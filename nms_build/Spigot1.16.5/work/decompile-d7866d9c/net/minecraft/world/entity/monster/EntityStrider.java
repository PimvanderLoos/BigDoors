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

    private static final RecipeItemStack bo = RecipeItemStack.a(Items.bx);
    private static final RecipeItemStack bp = RecipeItemStack.a(Items.bx, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final DataWatcherObject<Integer> bq = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> br = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bs = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.i);
    public final SaddleStorage saddleStorage;
    private PathfinderGoalTempt bu;
    private PathfinderGoalPanic bv;

    public EntityStrider(EntityTypes<? extends EntityStrider> entitytypes, World world) {
        super(entitytypes, world);
        this.saddleStorage = new SaddleStorage(this.datawatcher, EntityStrider.bq, EntityStrider.bs);
        this.i = true;
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
        if (EntityStrider.bq.equals(datawatcherobject) && this.world.isClientSide) {
            this.saddleStorage.a();
        }

        super.a(datawatcherobject);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityStrider.bq, 0);
        this.datawatcher.register(EntityStrider.br, false);
        this.datawatcher.register(EntityStrider.bs, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.saddleStorage.a(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.saddleStorage.b(nbttagcompound);
    }

    @Override
    public boolean hasSaddle() {
        return this.saddleStorage.hasSaddle();
    }

    @Override
    public boolean canSaddle() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void saddle(@Nullable SoundCategory soundcategory) {
        this.saddleStorage.setSaddle(true);
        if (soundcategory != null) {
            this.world.playSound((EntityHuman) null, (Entity) this, SoundEffects.ENTITY_STRIDER_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    protected void initPathfinder() {
        this.bv = new PathfinderGoalPanic(this, 1.65D);
        this.goalSelector.a(1, this.bv);
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.bu = new PathfinderGoalTempt(this, 1.4D, false, EntityStrider.bp);
        this.goalSelector.a(3, this.bu);
        this.goalSelector.a(4, new EntityStrider.a(this, 1.5D));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D, 60));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityStrider.class, 8.0F));
    }

    public void setShivering(boolean flag) {
        this.datawatcher.set(EntityStrider.br, flag);
    }

    public boolean isShivering() {
        return this.getVehicle() instanceof EntityStrider ? ((EntityStrider) this.getVehicle()).isShivering() : (Boolean) this.datawatcher.get(EntityStrider.br);
    }

    @Override
    public boolean a(FluidType fluidtype) {
        return fluidtype.a((Tag) TagsFluid.LAVA);
    }

    @Override
    public double bc() {
        float f = Math.min(0.25F, this.av);
        float f1 = this.aw;

        return (double) this.getHeight() - 0.19D + (double) (0.12F * MathHelper.cos(f1 * 1.5F) * 2.0F * f);
    }

    @Override
    public boolean er() {
        Entity entity = this.getRidingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
        }
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.j((Entity) this);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public Vec3D b(EntityLiving entityliving) {
        Vec3D[] avec3d = new Vec3D[]{a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw - 22.5F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw + 22.5F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw - 45.0F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw + 45.0F)};
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

            if (!this.world.getFluid(blockposition).a((Tag) TagsFluid.LAVA)) {
                double d3 = this.world.h(blockposition);

                if (DismountUtil.a(d3)) {
                    Vec3D vec3d1 = Vec3D.a((BaseBlockPosition) blockposition, d3);
                    UnmodifiableIterator unmodifiableiterator = entityliving.ej().iterator();

                    while (unmodifiableiterator.hasNext()) {
                        EntityPose entitypose = (EntityPose) unmodifiableiterator.next();
                        AxisAlignedBB axisalignedbb = entityliving.f(entitypose);

                        if (DismountUtil.a(this.world, entityliving, axisalignedbb.c(vec3d1))) {
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
        this.q(this.eL());
        this.a((EntityInsentient) this, this.saddleStorage, vec3d);
    }

    public float eL() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * (this.isShivering() ? 0.66F : 1.0F);
    }

    @Override
    public float N_() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * (this.isShivering() ? 0.23F : 0.55F);
    }

    @Override
    public void a_(Vec3D vec3d) {
        super.g(vec3d);
    }

    @Override
    protected float at() {
        return this.B + 0.6F;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.aQ() ? SoundEffects.ENTITY_STRIDER_STEP_LAVA : SoundEffects.ENTITY_STRIDER_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean O_() {
        return this.saddleStorage.a(this.getRandom());
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.checkBlockCollisions();
        if (this.aQ()) {
            this.fallDistance = 0.0F;
        } else {
            super.a(d0, flag, iblockdata, blockposition);
        }
    }

    @Override
    public void tick() {
        if (this.eO() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEffects.ENTITY_STRIDER_HAPPY, 1.0F, this.dH());
        } else if (this.eN() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEffects.ENTITY_STRIDER_RETREAT, 1.0F, this.dH());
        }

        IBlockData iblockdata = this.world.getType(this.getChunkCoordinates());
        IBlockData iblockdata1 = this.aN();
        boolean flag = iblockdata.a((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || iblockdata1.a((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || this.b((Tag) TagsFluid.LAVA) > 0.0D;

        this.setShivering(!flag);
        super.tick();
        this.eU();
        this.checkBlockCollisions();
    }

    private boolean eN() {
        return this.bv != null && this.bv.h();
    }

    private boolean eO() {
        return this.bu != null && this.bu.h();
    }

    @Override
    protected boolean q() {
        return true;
    }

    private void eU() {
        if (this.aQ()) {
            VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.a((Entity) this);

            if (voxelshapecollision.a(BlockFluids.c, this.getChunkCoordinates(), true) && !this.world.getFluid(this.getChunkCoordinates().up()).a((Tag) TagsFluid.LAVA)) {
                this.onGround = true;
            } else {
                this.setMot(this.getMot().a(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public static AttributeProvider.Builder eM() {
        return EntityInsentient.p().a(GenericAttributes.MOVEMENT_SPEED, 0.17499999701976776D).a(GenericAttributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return !this.eN() && !this.eO() ? SoundEffects.ENTITY_STRIDER_AMBIENT : null;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_STRIDER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_STRIDER_DEATH;
    }

    @Override
    protected boolean q(Entity entity) {
        return this.getPassengers().isEmpty() && !this.a((Tag) TagsFluid.LAVA);
    }

    @Override
    public boolean dO() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new EntityStrider.b(this, world);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition).getFluid().a((Tag) TagsFluid.LAVA) ? 10.0F : (this.aQ() ? Float.NEGATIVE_INFINITY : 0.0F);
    }

    @Override
    public EntityStrider createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityStrider) EntityTypes.STRIDER.a((World) worldserver);
    }

    @Override
    public boolean k(ItemStack itemstack) {
        return EntityStrider.bo.test(itemstack);
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
        boolean flag = this.k(entityhuman.b(enumhand));

        if (!flag && this.hasSaddle() && !this.isVehicle() && !entityhuman.eq()) {
            if (!this.world.isClientSide) {
                entityhuman.startRiding(this);
            }

            return EnumInteractionResult.a(this.world.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

            if (!enuminteractionresult.a()) {
                ItemStack itemstack = entityhuman.b(enumhand);

                return itemstack.getItem() == Items.SADDLE ? itemstack.a(entityhuman, (EntityLiving) this, enumhand) : EnumInteractionResult.PASS;
            } else {
                if (flag && !this.isSilent()) {
                    this.world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_STRIDER_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                }

                return enuminteractionresult;
            }
        }
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (this.isBaby()) {
            return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        } else {
            Object object;

            if (this.random.nextInt(30) == 0) {
                EntityInsentient entityinsentient = (EntityInsentient) EntityTypes.ZOMBIFIED_PIGLIN.a((World) worldaccess.getMinecraftWorld());

                object = this.a(worldaccess, difficultydamagescaler, entityinsentient, new EntityZombie.GroupDataZombie(EntityZombie.a(this.random), false));
                entityinsentient.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
                this.saddle((SoundCategory) null);
            } else if (this.random.nextInt(10) == 0) {
                EntityAgeable entityageable = (EntityAgeable) EntityTypes.STRIDER.a((World) worldaccess.getMinecraftWorld());

                entityageable.setAgeRaw(-24000);
                object = this.a(worldaccess, difficultydamagescaler, entityageable, (GroupDataEntity) null);
            } else {
                object = new EntityAgeable.a(0.5F);
            }

            return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) object, nbttagcompound);
        }
    }

    private GroupDataEntity a(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EntityInsentient entityinsentient, @Nullable GroupDataEntity groupdataentity) {
        entityinsentient.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.yaw, 0.0F);
        entityinsentient.prepare(worldaccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, groupdataentity, (NBTTagCompound) null);
        entityinsentient.a((Entity) this, true);
        return new EntityAgeable.a(0.0F);
    }

    static class a extends PathfinderGoalGotoTarget {

        private final EntityStrider g;

        private a(EntityStrider entitystrider, double d0) {
            super(entitystrider, d0, 8, 2);
            this.g = entitystrider;
        }

        @Override
        public BlockPosition j() {
            return this.e;
        }

        @Override
        public boolean b() {
            return !this.g.aQ() && this.a(this.g.world, this.e);
        }

        @Override
        public boolean a() {
            return !this.g.aQ() && super.a();
        }

        @Override
        public boolean k() {
            return this.d % 20 == 0;
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            return iworldreader.getType(blockposition).a(Blocks.LAVA) && iworldreader.getType(blockposition.up()).a((IBlockAccess) iworldreader, blockposition, PathMode.LAND);
        }
    }

    static class b extends Navigation {

        b(EntityStrider entitystrider, World world) {
            super(entitystrider, world);
        }

        @Override
        protected Pathfinder a(int i) {
            this.o = new PathfinderNormal();
            return new Pathfinder(this.o, i);
        }

        @Override
        protected boolean a(PathType pathtype) {
            return pathtype != PathType.LAVA && pathtype != PathType.DAMAGE_FIRE && pathtype != PathType.DANGER_FIRE ? super.a(pathtype) : true;
        }

        @Override
        public boolean a(BlockPosition blockposition) {
            return this.b.getType(blockposition).a(Blocks.LAVA) || super.a(blockposition);
        }
    }
}
