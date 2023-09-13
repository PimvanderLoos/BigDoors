package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.phys.Vec3D;

public class Frog extends EntityAnimal {

    public static final RecipeItemStack TEMPTATION_ITEM = RecipeItemStack.of(Items.SLIME_BALL);
    protected static final ImmutableList<SensorType<? extends Sensor<? super Frog>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.IS_IN_WATER, MemoryModuleType.IS_PREGNANT, MemoryModuleType.IS_PANICKING, MemoryModuleType.UNREACHABLE_TONGUE_TARGETS});
    private static final DataWatcherObject<FrogVariant> DATA_VARIANT_ID = DataWatcher.defineId(Frog.class, DataWatcherRegistry.FROG_VARIANT);
    private static final DataWatcherObject<OptionalInt> DATA_TONGUE_TARGET_ID = DataWatcher.defineId(Frog.class, DataWatcherRegistry.OPTIONAL_UNSIGNED_INT);
    private static final int FROG_FALL_DAMAGE_REDUCTION = 5;
    public static final String VARIANT_KEY = "variant";
    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState croakAnimationState = new AnimationState();
    public final AnimationState tongueAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();

    public Frog(EntityTypes<? extends EntityAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.lookControl = new Frog.a(this);
        this.setPathfindingMalus(PathType.WATER, 4.0F);
        this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.maxUpStep = 1.0F;
    }

    @Override
    protected BehaviorController.b<Frog> brainProvider() {
        return BehaviorController.provider(Frog.MEMORY_TYPES, Frog.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return FrogAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public BehaviorController<Frog> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Frog.DATA_VARIANT_ID, FrogVariant.TEMPERATE);
        this.entityData.define(Frog.DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    public void eraseTongueTarget() {
        this.entityData.set(Frog.DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    public Optional<Entity> getTongueTarget() {
        IntStream intstream = ((OptionalInt) this.entityData.get(Frog.DATA_TONGUE_TARGET_ID)).stream();
        World world = this.level;

        Objects.requireNonNull(this.level);
        return intstream.mapToObj(world::getEntity).filter(Objects::nonNull).findFirst();
    }

    public void setTongueTarget(Entity entity) {
        this.entityData.set(Frog.DATA_TONGUE_TARGET_ID, OptionalInt.of(entity.getId()));
    }

    @Override
    public int getHeadRotSpeed() {
        return 35;
    }

    @Override
    public int getMaxHeadYRot() {
        return 5;
    }

    public FrogVariant getVariant() {
        return (FrogVariant) this.entityData.get(Frog.DATA_VARIANT_ID);
    }

    public void setVariant(FrogVariant frogvariant) {
        this.entityData.set(Frog.DATA_VARIANT_ID, frogvariant);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putString("variant", IRegistry.FROG_VARIANT.getKey(this.getVariant()).toString());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        FrogVariant frogvariant = (FrogVariant) IRegistry.FROG_VARIANT.get(MinecraftKey.tryParse(nbttagcompound.getString("variant")));

        if (frogvariant != null) {
            this.setVariant(frogvariant);
        }

    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    private boolean isMovingOnLand() {
        return this.onGround && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && !this.isInWaterOrBubble();
    }

    private boolean isMovingInWater() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && this.isInWaterOrBubble();
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("frogBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("frogActivityUpdate");
        FrogAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            if (this.isMovingOnLand()) {
                this.walkAnimationState.startIfStopped(this.tickCount);
            } else {
                this.walkAnimationState.stop();
            }

            if (this.isMovingInWater()) {
                this.swimIdleAnimationState.stop();
                this.swimAnimationState.startIfStopped(this.tickCount);
            } else if (this.isInWaterOrBubble()) {
                this.swimAnimationState.stop();
                this.swimIdleAnimationState.startIfStopped(this.tickCount);
            } else {
                this.swimAnimationState.stop();
                this.swimIdleAnimationState.stop();
            }
        }

        super.tick();
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (Frog.DATA_POSE.equals(datawatcherobject)) {
            EntityPose entitypose = this.getPose();

            if (entitypose == EntityPose.LONG_JUMPING) {
                this.jumpAnimationState.start(this.tickCount);
            } else {
                this.jumpAnimationState.stop();
            }

            if (entitypose == EntityPose.CROAKING) {
                this.croakAnimationState.start(this.tickCount);
            } else {
                this.croakAnimationState.stop();
            }

            if (entitypose == EntityPose.USING_TONGUE) {
                this.tongueAnimationState.start(this.tickCount);
            } else {
                this.tongueAnimationState.stop();
            }
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        Frog frog = (Frog) EntityTypes.FROG.create(worldserver);

        if (frog != null) {
            FrogAi.initMemories(frog, worldserver.getRandom());
        }

        return frog;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean flag) {}

    @Override
    public void spawnChildFromBreeding(WorldServer worldserver, EntityAnimal entityanimal) {
        EntityPlayer entityplayer = this.getLoveCause();

        if (entityplayer == null) {
            entityplayer = entityanimal.getLoveCause();
        }

        if (entityplayer != null) {
            entityplayer.awardStat(StatisticList.ANIMALS_BRED);
            CriterionTriggers.BRED_ANIMALS.trigger(entityplayer, this, entityanimal, (EntityAgeable) null);
        }

        this.setAge(6000);
        entityanimal.setAge(6000);
        this.resetLove();
        entityanimal.resetLove();
        this.getBrain().setMemory(MemoryModuleType.IS_PREGNANT, (Object) Unit.INSTANCE);
        worldserver.broadcastEntityEvent(this, (byte) 18);
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            worldserver.addFreshEntity(new EntityExperienceOrb(worldserver, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Holder<BiomeBase> holder = worldaccess.getBiome(this.blockPosition());

        if (holder.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
            this.setVariant(FrogVariant.COLD);
        } else if (holder.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
            this.setVariant(FrogVariant.WARM);
        } else {
            this.setVariant(FrogVariant.TEMPERATE);
        }

        FrogAi.initMemories(this, worldaccess.getRandom());
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 1.0D).add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.ATTACK_DAMAGE, 10.0D);
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.FROG_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.FROG_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.FROG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.FROG_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    protected int calculateFallDamage(float f, float f1) {
        return super.calculateFallDamage(f, f1) - 5;
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(vec3d);
        }

    }

    @Override
    public boolean canCutCorner(PathType pathtype) {
        return super.canCutCorner(pathtype) && pathtype != PathType.WATER_BORDER;
    }

    public static boolean canEat(EntityLiving entityliving) {
        if (entityliving instanceof EntitySlime) {
            EntitySlime entityslime = (EntitySlime) entityliving;

            if (entityslime.getSize() != 1) {
                return false;
            }
        }

        return entityliving.getType().is(TagsEntity.FROG_FOOD);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new Frog.c(this, world);
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return Frog.TEMPTATION_ITEM.test(itemstack);
    }

    public static boolean checkFrogSpawnRules(EntityTypes<? extends EntityAnimal> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, RandomSource randomsource) {
        return generatoraccess.getBlockState(blockposition.below()).is(TagsBlock.FROGS_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    private class a extends ControllerLook {

        a(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        protected boolean resetXRotOnTick() {
            return Frog.this.getTongueTarget().isEmpty();
        }
    }

    private static class c extends AmphibiousPathNavigation {

        c(Frog frog, World world) {
            super(frog, world);
        }

        @Override
        protected Pathfinder createPathFinder(int i) {
            this.nodeEvaluator = new Frog.b(true);
            this.nodeEvaluator.setCanPassDoors(true);
            return new Pathfinder(this.nodeEvaluator, i);
        }
    }

    private static class b extends AmphibiousNodeEvaluator {

        private final BlockPosition.MutableBlockPosition belowPos = new BlockPosition.MutableBlockPosition();

        public b(boolean flag) {
            super(flag);
        }

        @Nullable
        @Override
        public PathPoint getStart() {
            return this.getStartNode(new BlockPosition(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY), MathHelper.floor(this.mob.getBoundingBox().minZ)));
        }

        @Override
        public PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k) {
            this.belowPos.set(i, j - 1, k);
            IBlockData iblockdata = iblockaccess.getBlockState(this.belowPos);

            return iblockdata.is(TagsBlock.FROG_PREFER_JUMP_TO) ? PathType.OPEN : super.getBlockPathType(iblockaccess, i, j, k);
        }
    }
}
