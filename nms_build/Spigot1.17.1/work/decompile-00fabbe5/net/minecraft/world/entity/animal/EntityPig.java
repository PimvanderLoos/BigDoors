package net.minecraft.world.entity.animal;

import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.ISteerable;
import net.minecraft.world.entity.SaddleStorage;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.DismountUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityPig extends EntityAnimal implements ISteerable, ISaddleable {

    private static final DataWatcherObject<Boolean> DATA_SADDLE_ID = DataWatcher.a(EntityPig.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_BOOST_TIME = DataWatcher.a(EntityPig.class, DataWatcherRegistry.INT);
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.a(Items.CARROT, Items.POTATO, Items.BEETROOT);
    public final SaddleStorage steering;

    public EntityPig(EntityTypes<? extends EntityPig> entitytypes, World world) {
        super(entitytypes, world);
        this.steering = new SaddleStorage(this.entityData, EntityPig.DATA_BOOST_TIME, EntityPig.DATA_SADDLE_ID);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, EntityPig.FOOD_ITEMS, false));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    public static AttributeProvider.Builder p() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.cB();
    }

    @Override
    public boolean fd() {
        Entity entity = this.getRidingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().a(Items.CARROT_ON_A_STICK) || entityhuman.getItemInOffHand().a(Items.CARROT_ON_A_STICK);
        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPig.DATA_BOOST_TIME.equals(datawatcherobject) && this.level.isClientSide) {
            this.steering.a();
        }

        super.a(datawatcherobject);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityPig.DATA_SADDLE_ID, false);
        this.entityData.register(EntityPig.DATA_BOOST_TIME, 0);
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
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.PIG_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PIG_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PIG_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PIG_STEP, 0.15F, 1.0F);
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
                return enuminteractionresult;
            }
        }
    }

    @Override
    public boolean canSaddle() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasSaddle()) {
            this.a((IMaterial) Items.SADDLE);
        }

    }

    @Override
    public boolean hasSaddle() {
        return this.steering.hasSaddle();
    }

    @Override
    public void saddle(@Nullable SoundCategory soundcategory) {
        this.steering.setSaddle(true);
        if (soundcategory != null) {
            this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.PIG_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    public Vec3D b(EntityLiving entityliving) {
        EnumDirection enumdirection = this.getAdjustedDirection();

        if (enumdirection.n() == EnumDirection.EnumAxis.Y) {
            return super.b(entityliving);
        } else {
            int[][] aint = DismountUtil.a(enumdirection);
            BlockPosition blockposition = this.getChunkCoordinates();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            UnmodifiableIterator unmodifiableiterator = entityliving.eS().iterator();

            while (unmodifiableiterator.hasNext()) {
                EntityPose entitypose = (EntityPose) unmodifiableiterator.next();
                AxisAlignedBB axisalignedbb = entityliving.f(entitypose);
                int[][] aint1 = aint;
                int i = aint.length;

                for (int j = 0; j < i; ++j) {
                    int[] aint2 = aint1[j];

                    blockposition_mutableblockposition.d(blockposition.getX() + aint2[0], blockposition.getY(), blockposition.getZ() + aint2[1]);
                    double d0 = this.level.i(blockposition_mutableblockposition);

                    if (DismountUtil.a(d0)) {
                        Vec3D vec3d = Vec3D.a((BaseBlockPosition) blockposition_mutableblockposition, d0);

                        if (DismountUtil.a(this.level, entityliving, axisalignedbb.c(vec3d))) {
                            entityliving.setPose(entitypose);
                            return vec3d;
                        }
                    }
                }
            }

            return super.b(entityliving);
        }
    }

    @Override
    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {
        if (worldserver.getDifficulty() != EnumDifficulty.PEACEFUL) {
            EntityPigZombie entitypigzombie = (EntityPigZombie) EntityTypes.ZOMBIFIED_PIGLIN.a((World) worldserver);

            entitypigzombie.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            entitypigzombie.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), this.getXRot());
            entitypigzombie.setNoAI(this.isNoAI());
            entitypigzombie.setBaby(this.isBaby());
            if (this.hasCustomName()) {
                entitypigzombie.setCustomName(this.getCustomName());
                entitypigzombie.setCustomNameVisible(this.getCustomNameVisible());
            }

            entitypigzombie.setPersistent();
            worldserver.addEntity(entitypigzombie);
            this.die();
        } else {
            super.onLightningStrike(worldserver, entitylightning);
        }

    }

    @Override
    public void g(Vec3D vec3d) {
        this.a((EntityInsentient) this, this.steering, vec3d);
    }

    @Override
    public float b() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * 0.225F;
    }

    @Override
    public void a(Vec3D vec3d) {
        super.g(vec3d);
    }

    @Override
    public boolean a() {
        return this.steering.a(this.getRandom());
    }

    @Override
    public EntityPig createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityPig) EntityTypes.PIG.a((World) worldserver);
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return EntityPig.FOOD_ITEMS.test(itemstack);
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.6F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }
}
