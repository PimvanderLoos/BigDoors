package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
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
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.of(Items.WARPED_FUNGUS);
    private static final RecipeItemStack TEMPT_ITEMS = RecipeItemStack.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final DataWatcherObject<Integer> DATA_BOOST_TIME = DataWatcher.defineId(EntityStrider.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_SUFFOCATING = DataWatcher.defineId(EntityStrider.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_SADDLE_ID = DataWatcher.defineId(EntityStrider.class, DataWatcherRegistry.BOOLEAN);
    public final SaddleStorage steering;
    @Nullable
    private PathfinderGoalTempt temptGoal;
    @Nullable
    private PathfinderGoalPanic panicGoal;

    public EntityStrider(EntityTypes<? extends EntityStrider> entitytypes, World world) {
        super(entitytypes, world);
        this.steering = new SaddleStorage(this.entityData, EntityStrider.DATA_BOOST_TIME, EntityStrider.DATA_SADDLE_ID);
        this.blocksBuilding = true;
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
    }

    public static boolean checkStriderSpawnRules(EntityTypes<EntityStrider> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        do {
            blockposition_mutableblockposition.move(EnumDirection.UP);
        } while (generatoraccess.getFluidState(blockposition_mutableblockposition).is((Tag) TagsFluid.LAVA));

        return generatoraccess.getBlockState(blockposition_mutableblockposition).isAir();
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityStrider.DATA_BOOST_TIME.equals(datawatcherobject) && this.level.isClientSide) {
            this.steering.onSynced();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityStrider.DATA_BOOST_TIME, 0);
        this.entityData.define(EntityStrider.DATA_SUFFOCATING, false);
        this.entityData.define(EntityStrider.DATA_SADDLE_ID, false);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        this.steering.addAdditionalSaveData(nbttagcompound);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.steering.readAdditionalSaveData(nbttagcompound);
    }

    @Override
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void equipSaddle(@Nullable SoundCategory soundcategory) {
        this.steering.setSaddle(true);
        if (soundcategory != null) {
            this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.STRIDER_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    protected void registerGoals() {
        this.panicGoal = new PathfinderGoalPanic(this, 1.65D);
        this.goalSelector.addGoal(1, this.panicGoal);
        this.goalSelector.addGoal(2, new PathfinderGoalBreed(this, 1.0D));
        this.temptGoal = new PathfinderGoalTempt(this, 1.4D, EntityStrider.TEMPT_ITEMS, false);
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(4, new EntityStrider.a(this, 1.5D));
        this.goalSelector.addGoal(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomStroll(this, 1.0D, 60));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.addGoal(9, new PathfinderGoalLookAtPlayer(this, EntityStrider.class, 8.0F));
    }

    public void setSuffocating(boolean flag) {
        this.entityData.set(EntityStrider.DATA_SUFFOCATING, flag);
    }

    public boolean isSuffocating() {
        return this.getVehicle() instanceof EntityStrider ? ((EntityStrider) this.getVehicle()).isSuffocating() : (Boolean) this.entityData.get(EntityStrider.DATA_SUFFOCATING);
    }

    @Override
    public boolean canStandOnFluid(FluidType fluidtype) {
        return fluidtype.is(TagsFluid.LAVA);
    }

    @Override
    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.animationSpeed);
        float f1 = this.animationPosition;

        return (double) this.getBbHeight() - 0.19D + (double) (0.12F * MathHelper.cos(f1 * 1.5F) * 2.0F * f);
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = this.getControllingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getMainHandItem().is(Items.WARPED_FUNGUS_ON_A_STICK) || entityhuman.getOffhandItem().is(Items.WARPED_FUNGUS_ON_A_STICK);
        }
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return iworldreader.isUnobstructed(this);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public Vec3D getDismountLocationForPassenger(EntityLiving entityliving) {
        Vec3D[] avec3d = new Vec3D[]{getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), entityliving.getYRot()), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), entityliving.getYRot() - 22.5F), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), entityliving.getYRot() + 22.5F), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), entityliving.getYRot() - 45.0F), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), entityliving.getYRot() + 45.0F)};
        Set<BlockPosition> set = Sets.newLinkedHashSet();
        double d0 = this.getBoundingBox().maxY;
        double d1 = this.getBoundingBox().minY - 0.5D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Vec3D[] avec3d1 = avec3d;
        int i = avec3d.length;

        for (int j = 0; j < i; ++j) {
            Vec3D vec3d = avec3d1[j];

            blockposition_mutableblockposition.set(this.getX() + vec3d.x, d0, this.getZ() + vec3d.z);

            for (double d2 = d0; d2 > d1; --d2) {
                set.add(blockposition_mutableblockposition.immutable());
                blockposition_mutableblockposition.move(EnumDirection.DOWN);
            }
        }

        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (!this.level.getFluidState(blockposition).is((Tag) TagsFluid.LAVA)) {
                double d3 = this.level.getBlockFloorHeight(blockposition);

                if (DismountUtil.isBlockFloorValid(d3)) {
                    Vec3D vec3d1 = Vec3D.upFromBottomCenterOf(blockposition, d3);
                    UnmodifiableIterator unmodifiableiterator = entityliving.getDismountPoses().iterator();

                    while (unmodifiableiterator.hasNext()) {
                        EntityPose entitypose = (EntityPose) unmodifiableiterator.next();
                        AxisAlignedBB axisalignedbb = entityliving.getLocalBoundsForPose(entitypose);

                        if (DismountUtil.canDismountTo(this.level, entityliving, axisalignedbb.move(vec3d1))) {
                            entityliving.setPose(entitypose);
                            return vec3d1;
                        }
                    }
                }
            }
        }

        return new Vec3D(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Override
    public void travel(Vec3D vec3d) {
        this.setSpeed(this.getMoveSpeed());
        this.travel(this, this.steering, vec3d);
    }

    public float getMoveSpeed() {
        return (float) this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.66F : 1.0F);
    }

    @Override
    public float getSteeringSpeed() {
        return (float) this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.23F : 0.55F);
    }

    @Override
    public void travelWithInput(Vec3D vec3d) {
        super.travel(vec3d);
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.6F;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.isInLava() ? SoundEffects.STRIDER_STEP_LAVA : SoundEffects.STRIDER_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.checkInsideBlocks();
        if (this.isInLava()) {
            this.resetFallDistance();
        } else {
            super.checkFallDamage(d0, flag, iblockdata, blockposition);
        }
    }

    @Override
    public void tick() {
        if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEffects.STRIDER_HAPPY, 1.0F, this.getVoicePitch());
        } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEffects.STRIDER_RETREAT, 1.0F, this.getVoicePitch());
        }

        IBlockData iblockdata = this.level.getBlockState(this.blockPosition());
        IBlockData iblockdata1 = this.getBlockStateOn();
        boolean flag = iblockdata.is((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || iblockdata1.is((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || this.getFluidHeight(TagsFluid.LAVA) > 0.0D;

        this.setSuffocating(!flag);
        super.tick();
        this.floatStrider();
        this.checkInsideBlocks();
    }

    private boolean isPanicking() {
        return this.panicGoal != null && this.panicGoal.isRunning();
    }

    private boolean isBeingTempted() {
        return this.temptGoal != null && this.temptGoal.isRunning();
    }

    @Override
    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    private void floatStrider() {
        if (this.isInLava()) {
            VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.of(this);

            if (voxelshapecollision.isAbove(BlockFluids.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is((Tag) TagsFluid.LAVA)) {
                this.onGround = true;
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.17499999701976776D).add(GenericAttributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return !this.isPanicking() && !this.isBeingTempted() ? SoundEffects.STRIDER_AMBIENT : null;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.STRIDER_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.STRIDER_DEATH;
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return !this.isVehicle() && !this.isEyeInFluid(TagsFluid.LAVA);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new EntityStrider.b(this, world);
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getBlockState(blockposition).getFluidState().is((Tag) TagsFluid.LAVA) ? 10.0F : (this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F);
    }

    @Override
    public EntityStrider getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityStrider) EntityTypes.STRIDER.create(worldserver);
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return EntityStrider.FOOD_ITEMS.test(itemstack);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation((IMaterial) Items.SADDLE);
        }

    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        boolean flag = this.isFood(entityhuman.getItemInHand(enumhand));

        if (!flag && this.isSaddled() && !this.isVehicle() && !entityhuman.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                entityhuman.startRiding(this);
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = super.mobInteract(entityhuman, enumhand);

            if (!enuminteractionresult.consumesAction()) {
                ItemStack itemstack = entityhuman.getItemInHand(enumhand);

                return itemstack.is(Items.SADDLE) ? itemstack.interactLivingEntity(entityhuman, this, enumhand) : EnumInteractionResult.PASS;
            } else {
                if (flag && !this.isSilent()) {
                    this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.STRIDER_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                }

                return enuminteractionresult;
            }
        }
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.6F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (this.isBaby()) {
            return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        } else {
            Object object;

            if (this.random.nextInt(30) == 0) {
                EntityInsentient entityinsentient = (EntityInsentient) EntityTypes.ZOMBIFIED_PIGLIN.create(worldaccess.getLevel());

                object = this.spawnJockey(worldaccess, difficultydamagescaler, entityinsentient, new EntityZombie.GroupDataZombie(EntityZombie.getSpawnAsBabyOdds(this.random), false));
                entityinsentient.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
                this.equipSaddle((SoundCategory) null);
            } else if (this.random.nextInt(10) == 0) {
                EntityAgeable entityageable = (EntityAgeable) EntityTypes.STRIDER.create(worldaccess.getLevel());

                entityageable.setAge(-24000);
                object = this.spawnJockey(worldaccess, difficultydamagescaler, entityageable, (GroupDataEntity) null);
            } else {
                object = new EntityAgeable.a(0.5F);
            }

            return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) object, nbttagcompound);
        }
    }

    private GroupDataEntity spawnJockey(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EntityInsentient entityinsentient, @Nullable GroupDataEntity groupdataentity) {
        entityinsentient.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
        entityinsentient.finalizeSpawn(worldaccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, groupdataentity, (NBTTagCompound) null);
        entityinsentient.startRiding(this, true);
        return new EntityAgeable.a(0.0F);
    }

    private static class a extends PathfinderGoalGotoTarget {

        private final EntityStrider strider;

        a(EntityStrider entitystrider, double d0) {
            super(entitystrider, d0, 8, 2);
            this.strider = entitystrider;
        }

        @Override
        public BlockPosition getMoveToTarget() {
            return this.blockPos;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.strider.isInLava() && this.isValidTarget(this.strider.level, this.blockPos);
        }

        @Override
        public boolean canUse() {
            return !this.strider.isInLava() && super.canUse();
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        @Override
        protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
            return iworldreader.getBlockState(blockposition).is(Blocks.LAVA) && iworldreader.getBlockState(blockposition.above()).isPathfindable(iworldreader, blockposition, PathMode.LAND);
        }
    }

    private static class b extends Navigation {

        b(EntityStrider entitystrider, World world) {
            super(entitystrider, world);
        }

        @Override
        protected Pathfinder createPathFinder(int i) {
            this.nodeEvaluator = new PathfinderNormal();
            return new Pathfinder(this.nodeEvaluator, i);
        }

        @Override
        protected boolean hasValidPathType(PathType pathtype) {
            return pathtype != PathType.LAVA && pathtype != PathType.DAMAGE_FIRE && pathtype != PathType.DANGER_FIRE ? super.hasValidPathType(pathtype) : true;
        }

        @Override
        public boolean isStableDestination(BlockPosition blockposition) {
            return this.level.getBlockState(blockposition).is(Blocks.LAVA) || super.isStableDestination(blockposition);
        }
    }
}
