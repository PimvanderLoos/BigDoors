package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.ISteerable;
import net.minecraft.world.entity.SaddleStorage;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.DismountUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class EntityStrider extends EntityAnimal implements ISteerable, ISaddleable {

    private static final float SUFFOCATE_STEERING_MODIFIER = 0.23F;
    private static final float SUFFOCATE_SPEED_MODIFIER = 0.66F;
    private static final float STEERING_MODIFIER = 0.55F;
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.a(Items.WARPED_FUNGUS);
    private static final RecipeItemStack TEMPT_ITEMS = RecipeItemStack.a(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final DataWatcherObject<Integer> DATA_BOOST_TIME = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_SUFFOCATING = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_SADDLE_ID = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.BOOLEAN);
    public final SaddleStorage steering;
    private PathfinderGoalTempt temptGoal;
    private PathfinderGoalPanic panicGoal;

    public EntityStrider(EntityTypes<? extends EntityStrider> entitytypes, World world) {
        super(entitytypes, world);
        this.steering = new SaddleStorage(this.entityData, EntityStrider.DATA_BOOST_TIME, EntityStrider.DATA_SADDLE_ID);
        this.blocksBuilding = true;
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.LAVA, 0.0F);
        this.a(PathType.DANGER_FIRE, 0.0F);
        this.a(PathType.DAMAGE_FIRE, 0.0F);
    }

    public static boolean c(EntityTypes<EntityStrider> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        do {
            blockposition_mutableblockposition.c(EnumDirection.UP);
        } while (generatoraccess.getFluid(blockposition_mutableblockposition).a((Tag) TagsFluid.LAVA));

        return generatoraccess.getType(blockposition_mutableblockposition).isAir();
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityStrider.DATA_BOOST_TIME.equals(datawatcherobject) && this.level.isClientSide) {
            this.steering.a();
        }

        super.a(datawatcherobject);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityStrider.DATA_BOOST_TIME, 0);
        this.entityData.register(EntityStrider.DATA_SUFFOCATING, false);
        this.entityData.register(EntityStrider.DATA_SADDLE_ID, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.steering.a(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.steering.b(nbttagcompound);
    }

    @Override
    public boolean hasSaddle() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean canSaddle() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void saddle(@Nullable SoundCategory soundcategory) {
        this.steering.setSaddle(true);
        if (soundcategory != null) {
            this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.STRIDER_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    protected void initPathfinder() {
        this.panicGoal = new PathfinderGoalPanic(this, 1.65D);
        this.goalSelector.a(1, this.panicGoal);
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.temptGoal = new PathfinderGoalTempt(this, 1.4D, EntityStrider.TEMPT_ITEMS, false);
        this.goalSelector.a(3, this.temptGoal);
        this.goalSelector.a(4, new EntityStrider.a(this, 1.5D));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D, 60));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityStrider.class, 8.0F));
    }

    public void setShivering(boolean flag) {
        this.entityData.set(EntityStrider.DATA_SUFFOCATING, flag);
    }

    public boolean isShivering() {
        return this.getVehicle() instanceof EntityStrider ? ((EntityStrider) this.getVehicle()).isShivering() : (Boolean) this.entityData.get(EntityStrider.DATA_SUFFOCATING);
    }

    @Override
    public boolean a(FluidType fluidtype) {
        return fluidtype.a((Tag) TagsFluid.LAVA);
    }

    @Override
    public double bl() {
        float f = Math.min(0.25F, this.animationSpeed);
        float f1 = this.animationPosition;

        return (double) this.getHeight() - 0.19D + (double) (0.12F * MathHelper.cos(f1 * 1.5F) * 2.0F * f);
    }

    @Override
    public boolean fd() {
        Entity entity = this.getRidingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().a(Items.WARPED_FUNGUS_ON_A_STICK) || entityhuman.getItemInOffHand().a(Items.WARPED_FUNGUS_ON_A_STICK);
        }
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.f((Entity) this);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.cB();
    }

    @Override
    public Vec3D b(EntityLiving entityliving) {
        Vec3D[] avec3d = new Vec3D[]{a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.getYRot()), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.getYRot() - 22.5F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.getYRot() + 22.5F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.getYRot() - 45.0F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.getYRot() + 45.0F)};
        Set<BlockPosition> set = Sets.newLinkedHashSet();
        double d0 = this.getBoundingBox().maxY;
        double d1 = this.getBoundingBox().minY - 0.5D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Vec3D[] avec3d1 = avec3d;
        int i = avec3d.length;

        for (int j = 0; j < i; ++j) {
            Vec3D vec3d = avec3d1[j];

            blockposition_mutableblockposition.c(this.locX() + vec3d.x, d0, this.locZ() + vec3d.z);

            for (double d2 = d0; d2 > d1; --d2) {
                set.add(blockposition_mutableblockposition.immutableCopy());
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            }
        }

        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (!this.level.getFluid(blockposition).a((Tag) TagsFluid.LAVA)) {
                double d3 = this.level.i(blockposition);

                if (DismountUtil.a(d3)) {
                    Vec3D vec3d1 = Vec3D.a((BaseBlockPosition) blockposition, d3);
                    UnmodifiableIterator unmodifiableiterator = entityliving.eS().iterator();

                    while (unmodifiableiterator.hasNext()) {
                        EntityPose entitypose = (EntityPose) unmodifiableiterator.next();
                        AxisAlignedBB axisalignedbb = entityliving.f(entitypose);

                        if (DismountUtil.a(this.level, entityliving, axisalignedbb.c(vec3d1))) {
                            entityliving.setPose(entitypose);
                            return vec3d1;
                        }
                    }
                }
            }
        }

        return new Vec3D(this.locX(), this.getBoundingBox().maxY, this.locZ());
    }

    @Override
    public void g(Vec3D vec3d) {
        this.r(this.t());
        this.a((EntityInsentient) this, this.steering, vec3d);
    }

    public float t() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * (this.isShivering() ? 0.66F : 1.0F);
    }

    @Override
    public float b() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * (this.isShivering() ? 0.23F : 0.55F);
    }

    @Override
    public void a(Vec3D vec3d) {
        super.g(vec3d);
    }

    @Override
    protected float az() {
        return this.moveDist + 0.6F;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.aX() ? SoundEffects.STRIDER_STEP_LAVA : SoundEffects.STRIDER_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean a() {
        return this.steering.a(this.getRandom());
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.checkBlockCollisions();
        if (this.aX()) {
            this.fallDistance = 0.0F;
        } else {
            super.a(d0, flag, iblockdata, blockposition);
        }
    }

    @Override
    public void tick() {
        if (this.fy() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEffects.STRIDER_HAPPY, 1.0F, this.ep());
        } else if (this.fx() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEffects.STRIDER_RETREAT, 1.0F, this.ep());
        }

        IBlockData iblockdata = this.level.getType(this.getChunkCoordinates());
        IBlockData iblockdata1 = this.aU();
        boolean flag = iblockdata.a((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || iblockdata1.a((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || this.b((Tag) TagsFluid.LAVA) > 0.0D;

        this.setShivering(!flag);
        super.tick();
        this.fE();
        this.checkBlockCollisions();
    }

    private boolean fx() {
        return this.panicGoal != null && this.panicGoal.h();
    }

    private boolean fy() {
        return this.temptGoal != null && this.temptGoal.h();
    }

    @Override
    protected boolean x() {
        return true;
    }

    private void fE() {
        if (this.aX()) {
            VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.a((Entity) this);

            if (voxelshapecollision.a(BlockFluids.STABLE_SHAPE, this.getChunkCoordinates(), true) && !this.level.getFluid(this.getChunkCoordinates().up()).a((Tag) TagsFluid.LAVA)) {
                this.onGround = true;
            } else {
                this.setMot(this.getMot().a(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public static AttributeProvider.Builder fw() {
        return EntityInsentient.w().a(GenericAttributes.MOVEMENT_SPEED, 0.17499999701976776D).a(GenericAttributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return !this.fx() && !this.fy() ? SoundEffects.STRIDER_AMBIENT : null;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.STRIDER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.STRIDER_DEATH;
    }

    @Override
    protected boolean o(Entity entity) {
        return !this.isVehicle() && !this.a((Tag) TagsFluid.LAVA);
    }

    @Override
    public boolean ex() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new EntityStrider.b(this, world);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition).getFluid().a((Tag) TagsFluid.LAVA) ? 10.0F : (this.aX() ? Float.NEGATIVE_INFINITY : 0.0F);
    }

    @Override
    public EntityStrider createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityStrider) EntityTypes.STRIDER.a((World) worldserver);
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return EntityStrider.FOOD_ITEMS.test(itemstack);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasSaddle()) {
            this.a((IMaterial) Items.SADDLE);
        }

    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        boolean flag = this.isBreedItem(entityhuman.b(enumhand));

        if (!flag && this.hasSaddle() && !this.isVehicle() && !entityhuman.eZ()) {
            if (!this.level.isClientSide) {
                entityhuman.startRiding(this);
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

            if (!enuminteractionresult.a()) {
                ItemStack itemstack = entityhuman.b(enumhand);

                return itemstack.a(Items.SADDLE) ? itemstack.a(entityhuman, (EntityLiving) this, enumhand) : EnumInteractionResult.PASS;
            } else {
                if (flag && !this.isSilent()) {
                    this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.STRIDER_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                }

                return enuminteractionresult;
            }
        }
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.6F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (this.isBaby()) {
            return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        } else {
            Object object;

            if (this.random.nextInt(30) == 0) {
                EntityInsentient entityinsentient = (EntityInsentient) EntityTypes.ZOMBIFIED_PIGLIN.a((World) worldaccess.getLevel());

                object = this.a(worldaccess, difficultydamagescaler, entityinsentient, new EntityZombie.GroupDataZombie(EntityZombie.a(this.random), false));
                entityinsentient.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
                this.saddle((SoundCategory) null);
            } else if (this.random.nextInt(10) == 0) {
                EntityAgeable entityageable = (EntityAgeable) EntityTypes.STRIDER.a((World) worldaccess.getLevel());

                entityageable.setAgeRaw(-24000);
                object = this.a(worldaccess, difficultydamagescaler, entityageable, (GroupDataEntity) null);
            } else {
                object = new EntityAgeable.a(0.5F);
            }

            return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) object, nbttagcompound);
        }
    }

    private GroupDataEntity a(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EntityInsentient entityinsentient, @Nullable GroupDataEntity groupdataentity) {
        entityinsentient.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), 0.0F);
        entityinsentient.prepare(worldaccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, groupdataentity, (NBTTagCompound) null);
        entityinsentient.a((Entity) this, true);
        return new EntityAgeable.a(0.0F);
    }

    private static class a extends PathfinderGoalGotoTarget {

        private final EntityStrider strider;

        a(EntityStrider entitystrider, double d0) {
            super(entitystrider, d0, 8, 2);
            this.strider = entitystrider;
        }

        @Override
        public BlockPosition j() {
            return this.blockPos;
        }

        @Override
        public boolean b() {
            return !this.strider.aX() && this.a(this.strider.level, this.blockPos);
        }

        @Override
        public boolean a() {
            return !this.strider.aX() && super.a();
        }

        @Override
        public boolean k() {
            return this.tryTicks % 20 == 0;
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            return iworldreader.getType(blockposition).a(Blocks.LAVA) && iworldreader.getType(blockposition.up()).a((IBlockAccess) iworldreader, blockposition, PathMode.LAND);
        }
    }

    private static class b extends Navigation {

        b(EntityStrider entitystrider, World world) {
            super(entitystrider, world);
        }

        @Override
        protected Pathfinder a(int i) {
            this.nodeEvaluator = new PathfinderNormal();
            return new Pathfinder(this.nodeEvaluator, i);
        }

        @Override
        protected boolean a(PathType pathtype) {
            return pathtype != PathType.LAVA && pathtype != PathType.DAMAGE_FIRE && pathtype != PathType.DANGER_FIRE ? super.a(pathtype) : true;
        }

        @Override
        public boolean a(BlockPosition blockposition) {
            return this.level.getType(blockposition).a(Blocks.LAVA) || super.a(blockposition);
        }
    }
}
