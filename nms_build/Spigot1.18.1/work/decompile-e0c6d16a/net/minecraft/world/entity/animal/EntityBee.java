package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMoveFlying;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceRecord;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCrops;
import net.minecraft.world.level.block.BlockStem;
import net.minecraft.world.level.block.BlockSweetBerryBush;
import net.minecraft.world.level.block.BlockTallPlant;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IBlockFragilePlantElement;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityBee extends EntityAnimal implements IEntityAngerable, EntityBird {

    public static final float FLAP_DEGREES_PER_TICK = 120.32113F;
    public static final int TICKS_PER_FLAP = MathHelper.ceil(1.4959966F);
    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.defineId(EntityBee.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Integer> DATA_REMAINING_ANGER_TIME = DataWatcher.defineId(EntityBee.class, DataWatcherRegistry.INT);
    private static final int FLAG_ROLL = 2;
    private static final int FLAG_HAS_STUNG = 4;
    private static final int FLAG_HAS_NECTAR = 8;
    private static final int STING_DEATH_COUNTDOWN = 1200;
    private static final int TICKS_BEFORE_GOING_TO_KNOWN_FLOWER = 2400;
    private static final int TICKS_WITHOUT_NECTAR_BEFORE_GOING_HOME = 3600;
    private static final int MIN_ATTACK_DIST = 4;
    private static final int MAX_CROPS_GROWABLE = 10;
    private static final int POISON_SECONDS_NORMAL = 10;
    private static final int POISON_SECONDS_HARD = 18;
    private static final int TOO_FAR_DISTANCE = 32;
    private static final int HIVE_CLOSE_ENOUGH_DISTANCE = 2;
    private static final int PATHFIND_TO_HIVE_WHEN_CLOSER_THAN = 16;
    private static final int HIVE_SEARCH_DISTANCE = 20;
    public static final String TAG_CROPS_GROWN_SINCE_POLLINATION = "CropsGrownSincePollination";
    public static final String TAG_CANNOT_ENTER_HIVE_TICKS = "CannotEnterHiveTicks";
    public static final String TAG_TICKS_SINCE_POLLINATION = "TicksSincePollination";
    public static final String TAG_HAS_STUNG = "HasStung";
    public static final String TAG_HAS_NECTAR = "HasNectar";
    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String TAG_HIVE_POS = "HivePos";
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;
    private float rollAmount;
    private float rollAmountO;
    private int timeSinceSting;
    int ticksWithoutNectarSinceExitingHive;
    public int stayOutOfHiveCountdown;
    private int numCropsGrownSincePollination;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_HIVE = 200;
    int remainingCooldownBeforeLocatingNewHive;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;
    int remainingCooldownBeforeLocatingNewFlower;
    @Nullable
    BlockPosition savedFlowerPos;
    @Nullable
    public BlockPosition hivePos;
    EntityBee.k beePollinateGoal;
    EntityBee.e goToHiveGoal;
    private EntityBee.f goToKnownFlowerGoal;
    private int underWaterTicks;

    public EntityBee(EntityTypes<? extends EntityBee> entitytypes, World world) {
        super(entitytypes, world);
        this.remainingCooldownBeforeLocatingNewFlower = MathHelper.nextInt(this.random, 20, 60);
        this.moveControl = new ControllerMoveFlying(this, 20, true);
        this.lookControl = new EntityBee.j(this);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityBee.DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(EntityBee.DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getBlockState(blockposition).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityBee.b(this, 1.399999976158142D, true));
        this.goalSelector.addGoal(1, new EntityBee.d());
        this.goalSelector.addGoal(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.addGoal(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.of((Tag) TagsItem.FLOWERS), false));
        this.beePollinateGoal = new EntityBee.k();
        this.goalSelector.addGoal(4, this.beePollinateGoal);
        this.goalSelector.addGoal(5, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.addGoal(5, new EntityBee.i());
        this.goToHiveGoal = new EntityBee.e();
        this.goalSelector.addGoal(5, this.goToHiveGoal);
        this.goToKnownFlowerGoal = new EntityBee.f();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);
        this.goalSelector.addGoal(7, new EntityBee.g());
        this.goalSelector.addGoal(8, new EntityBee.l());
        this.goalSelector.addGoal(9, new PathfinderGoalFloat(this));
        this.targetSelector.addGoal(1, (new EntityBee.h(this)).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new EntityBee.c(this));
        this.targetSelector.addGoal(3, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        if (this.hasHive()) {
            nbttagcompound.put("HivePos", GameProfileSerializer.writeBlockPos(this.getHivePos()));
        }

        if (this.hasSavedFlowerPos()) {
            nbttagcompound.put("FlowerPos", GameProfileSerializer.writeBlockPos(this.getSavedFlowerPos()));
        }

        nbttagcompound.putBoolean("HasNectar", this.hasNectar());
        nbttagcompound.putBoolean("HasStung", this.hasStung());
        nbttagcompound.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
        nbttagcompound.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
        nbttagcompound.putInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
        this.addPersistentAngerSaveData(nbttagcompound);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.hivePos = null;
        if (nbttagcompound.contains("HivePos")) {
            this.hivePos = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("HivePos"));
        }

        this.savedFlowerPos = null;
        if (nbttagcompound.contains("FlowerPos")) {
            this.savedFlowerPos = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("FlowerPos"));
        }

        super.readAdditionalSaveData(nbttagcompound);
        this.setHasNectar(nbttagcompound.getBoolean("HasNectar"));
        this.setHasStung(nbttagcompound.getBoolean("HasStung"));
        this.ticksWithoutNectarSinceExitingHive = nbttagcompound.getInt("TicksSincePollination");
        this.stayOutOfHiveCountdown = nbttagcompound.getInt("CannotEnterHiveTicks");
        this.numCropsGrownSincePollination = nbttagcompound.getInt("CropsGrownSincePollination");
        this.readPersistentAngerSaveData(this.level, nbttagcompound);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = entity.hurt(DamageSource.sting(this), (float) ((int) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.doEnchantDamageEffects(this, entity);
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).setStingerCount(((EntityLiving) entity).getStingerCount() + 1);
                byte b0 = 0;

                if (this.level.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 10;
                } else if (this.level.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 18;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.POISON, b0 * 20, 0), this);
                }
            }

            this.setHasStung(true);
            this.stopBeingAngry();
            this.playSound(SoundEffects.BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.spawnFluidParticle(this.level, this.getX() - 0.30000001192092896D, this.getX() + 0.30000001192092896D, this.getZ() - 0.30000001192092896D, this.getZ() + 0.30000001192092896D, this.getY(0.5D), Particles.FALLING_NECTAR);
            }
        }

        this.updateRollAmount();
    }

    private void spawnFluidParticle(World world, double d0, double d1, double d2, double d3, double d4, ParticleParam particleparam) {
        world.addParticle(particleparam, MathHelper.lerp(world.random.nextDouble(), d0, d1), d4, MathHelper.lerp(world.random.nextDouble(), d2, d3), 0.0D, 0.0D, 0.0D);
    }

    void pathfindRandomlyTowards(BlockPosition blockposition) {
        Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition);
        byte b0 = 0;
        BlockPosition blockposition1 = this.blockPosition();
        int i = (int) vec3d.y - blockposition1.getY();

        if (i > 2) {
            b0 = 4;
        } else if (i < -2) {
            b0 = -4;
        }

        int j = 6;
        int k = 8;
        int l = blockposition1.distManhattan(blockposition);

        if (l < 15) {
            j = l / 2;
            k = l / 2;
        }

        Vec3D vec3d1 = AirRandomPos.getPosTowards(this, j, k, b0, vec3d, 0.3141592741012573D);

        if (vec3d1 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, 1.0D);
        }
    }

    @Nullable
    public BlockPosition getSavedFlowerPos() {
        return this.savedFlowerPos;
    }

    public boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    public void setSavedFlowerPos(BlockPosition blockposition) {
        this.savedFlowerPos = blockposition;
    }

    @VisibleForDebug
    public int getTravellingTicks() {
        return Math.max(this.goToHiveGoal.travellingTicks, this.goToKnownFlowerGoal.travellingTicks);
    }

    @VisibleForDebug
    public List<BlockPosition> getBlacklistedHives() {
        return this.goToHiveGoal.blacklistedTargets;
    }

    private boolean isTiredOfLookingForNectar() {
        return this.ticksWithoutNectarSinceExitingHive > 3600;
    }

    boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = this.isTiredOfLookingForNectar() || this.level.isRaining() || this.level.isNight() || this.hasNectar();

            return flag && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    public void setStayOutOfHiveCountdown(int i) {
        this.stayOutOfHiveCountdown = i;
    }

    public float getRollAmount(float f) {
        return MathHelper.lerp(f, this.rollAmountO, this.rollAmount);
    }

    private void updateRollAmount() {
        this.rollAmountO = this.rollAmount;
        if (this.isRolling()) {
            this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
        } else {
            this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
        }

    }

    @Override
    protected void customServerAiStep() {
        boolean flag = this.hasStung();

        if (this.isInWaterOrBubble()) {
            ++this.underWaterTicks;
        } else {
            this.underWaterTicks = 0;
        }

        if (this.underWaterTicks > 20) {
            this.hurt(DamageSource.DROWN, 1.0F);
        }

        if (flag) {
            ++this.timeSinceSting;
            if (this.timeSinceSting % 5 == 0 && this.random.nextInt(MathHelper.clamp(1200 - this.timeSinceSting, (int) 1, (int) 1200)) == 0) {
                this.hurt(DamageSource.GENERIC, this.getHealth());
            }
        }

        if (!this.hasNectar()) {
            ++this.ticksWithoutNectarSinceExitingHive;
        }

        if (!this.level.isClientSide) {
            this.updatePersistentAnger((WorldServer) this.level, false);
        }

    }

    public void resetTicksWithoutNectarSinceExitingHive() {
        this.ticksWithoutNectarSinceExitingHive = 0;
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        } else {
            TileEntity tileentity = this.level.getBlockEntity(this.hivePos);

            return tileentity instanceof TileEntityBeehive && ((TileEntityBeehive) tileentity).isFireNearby();
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return (Integer) this.entityData.get(EntityBee.DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        this.entityData.set(EntityBee.DATA_REMAINING_ANGER_TIME, i);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(EntityBee.PERSISTENT_ANGER_TIME.sample(this.random));
    }

    private boolean doesHiveHaveSpace(BlockPosition blockposition) {
        TileEntity tileentity = this.level.getBlockEntity(blockposition);

        return tileentity instanceof TileEntityBeehive ? !((TileEntityBeehive) tileentity).isFull() : false;
    }

    @VisibleForDebug
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPosition getHivePos() {
        return this.hivePos;
    }

    @VisibleForDebug
    public PathfinderGoalSelector getGoalSelector() {
        return this.goalSelector;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendBeeInfo(this);
    }

    int getCropsGrownSincePollination() {
        return this.numCropsGrownSincePollination;
    }

    private void resetNumCropsGrownSincePollination() {
        this.numCropsGrownSincePollination = 0;
    }

    void incrementNumCropsGrownSincePollination() {
        ++this.numCropsGrownSincePollination;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            if (this.stayOutOfHiveCountdown > 0) {
                --this.stayOutOfHiveCountdown;
            }

            if (this.remainingCooldownBeforeLocatingNewHive > 0) {
                --this.remainingCooldownBeforeLocatingNewHive;
            }

            if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
                --this.remainingCooldownBeforeLocatingNewFlower;
            }

            boolean flag = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().distanceToSqr((Entity) this) < 4.0D;

            this.setRolling(flag);
            if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
        }

    }

    boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else {
            TileEntity tileentity = this.level.getBlockEntity(this.hivePos);

            return tileentity != null && tileentity.getType() == TileEntityTypes.BEEHIVE;
        }
    }

    public boolean hasNectar() {
        return this.getFlag(8);
    }

    public void setHasNectar(boolean flag) {
        if (flag) {
            this.resetTicksWithoutNectarSinceExitingHive();
        }

        this.setFlag(8, flag);
    }

    public boolean hasStung() {
        return this.getFlag(4);
    }

    public void setHasStung(boolean flag) {
        this.setFlag(4, flag);
    }

    private boolean isRolling() {
        return this.getFlag(2);
    }

    private void setRolling(boolean flag) {
        this.setFlag(2, flag);
    }

    boolean isTooFarAway(BlockPosition blockposition) {
        return !this.closerThan(blockposition, 32);
    }

    private void setFlag(int i, boolean flag) {
        if (flag) {
            this.entityData.set(EntityBee.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityBee.DATA_FLAGS_ID) | i));
        } else {
            this.entityData.set(EntityBee.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityBee.DATA_FLAGS_ID) & ~i));
        }

    }

    private boolean getFlag(int i) {
        return ((Byte) this.entityData.get(EntityBee.DATA_FLAGS_ID) & i) != 0;
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.FLYING_SPEED, 0.6000000238418579D).add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.ATTACK_DAMAGE, 2.0D).add(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world) {
            @Override
            public boolean isStableDestination(BlockPosition blockposition) {
                return !this.level.getBlockState(blockposition.below()).isAir();
            }

            @Override
            public void tick() {
                if (!EntityBee.this.beePollinateGoal.isPollinating()) {
                    super.tick();
                }
            }
        };

        navigationflying.setCanOpenDoors(false);
        navigationflying.setCanFloat(false);
        navigationflying.setCanPassDoors(true);
        return navigationflying;
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return itemstack.is((Tag) TagsItem.FLOWERS);
    }

    boolean isFlowerValid(BlockPosition blockposition) {
        return this.level.isLoaded(blockposition) && this.level.getBlockState(blockposition).is((Tag) TagsBlock.FLOWERS);
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {}

    @Override
    protected SoundEffect getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.BEE_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public EntityBee getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityBee) EntityTypes.BEE.create(worldserver);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.5F : entitysize.height * 0.5F;
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % EntityBee.TICKS_PER_FLAP == 0;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }

    public void dropOffNectar() {
        this.setHasNectar(false);
        this.resetNumCropsGrownSincePollination();
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            if (!this.level.isClientSide) {
                this.beePollinateGoal.stopPollinating();
            }

            return super.hurt(damagesource, f);
        }
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.ARTHROPOD;
    }

    @Override
    protected void jumpInLiquid(Tag<FluidType> tag) {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.01D, 0.0D));
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.5F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.2F));
    }

    boolean closerThan(BlockPosition blockposition, int i) {
        return blockposition.closerThan((BaseBlockPosition) this.blockPosition(), (double) i);
    }

    private class k extends EntityBee.a {

        private static final int MIN_POLLINATION_TICKS = 400;
        private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
        private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
        private final Predicate<IBlockData> VALID_POLLINATION_BLOCKS = (iblockdata) -> {
            return iblockdata.is((Tag) TagsBlock.FLOWERS) ? (iblockdata.is(Blocks.SUNFLOWER) ? iblockdata.getValue(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.UPPER : true) : false;
        };
        private static final double ARRIVAL_THRESHOLD = 0.1D;
        private static final int POSITION_CHANGE_CHANCE = 25;
        private static final float SPEED_MODIFIER = 0.35F;
        private static final float HOVER_HEIGHT_WITHIN_FLOWER = 0.6F;
        private static final float HOVER_POS_OFFSET = 0.33333334F;
        private int successfulPollinatingTicks;
        private int lastSoundPlayedTick;
        private boolean pollinating;
        @Nullable
        private Vec3D hoverPos;
        private int pollinatingTicks;
        private static final int MAX_POLLINATING_TICKS = 600;

        k() {
            super();
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            if (EntityBee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            } else if (EntityBee.this.hasNectar()) {
                return false;
            } else if (EntityBee.this.level.isRaining()) {
                return false;
            } else {
                Optional<BlockPosition> optional = this.findNearbyFlower();

                if (optional.isPresent()) {
                    EntityBee.this.savedFlowerPos = (BlockPosition) optional.get();
                    EntityBee.this.navigation.moveTo((double) EntityBee.this.savedFlowerPos.getX() + 0.5D, (double) EntityBee.this.savedFlowerPos.getY() + 0.5D, (double) EntityBee.this.savedFlowerPos.getZ() + 0.5D, 1.2000000476837158D);
                    return true;
                } else {
                    EntityBee.this.remainingCooldownBeforeLocatingNewFlower = MathHelper.nextInt(EntityBee.this.random, 20, 60);
                    return false;
                }
            }
        }

        @Override
        public boolean canBeeContinueToUse() {
            if (!this.pollinating) {
                return false;
            } else if (!EntityBee.this.hasSavedFlowerPos()) {
                return false;
            } else if (EntityBee.this.level.isRaining()) {
                return false;
            } else if (this.hasPollinatedLongEnough()) {
                return EntityBee.this.random.nextFloat() < 0.2F;
            } else if (EntityBee.this.tickCount % 20 == 0 && !EntityBee.this.isFlowerValid(EntityBee.this.savedFlowerPos)) {
                EntityBee.this.savedFlowerPos = null;
                return false;
            } else {
                return true;
            }
        }

        private boolean hasPollinatedLongEnough() {
            return this.successfulPollinatingTicks > 400;
        }

        boolean isPollinating() {
            return this.pollinating;
        }

        void stopPollinating() {
            this.pollinating = false;
        }

        @Override
        public void start() {
            this.successfulPollinatingTicks = 0;
            this.pollinatingTicks = 0;
            this.lastSoundPlayedTick = 0;
            this.pollinating = true;
            EntityBee.this.resetTicksWithoutNectarSinceExitingHive();
        }

        @Override
        public void stop() {
            if (this.hasPollinatedLongEnough()) {
                EntityBee.this.setHasNectar(true);
            }

            this.pollinating = false;
            EntityBee.this.navigation.stop();
            EntityBee.this.remainingCooldownBeforeLocatingNewFlower = 200;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.pollinatingTicks;
            if (this.pollinatingTicks > 600) {
                EntityBee.this.savedFlowerPos = null;
            } else {
                Vec3D vec3d = Vec3D.atBottomCenterOf(EntityBee.this.savedFlowerPos).add(0.0D, 0.6000000238418579D, 0.0D);

                if (vec3d.distanceTo(EntityBee.this.position()) > 1.0D) {
                    this.hoverPos = vec3d;
                    this.setWantedPos();
                } else {
                    if (this.hoverPos == null) {
                        this.hoverPos = vec3d;
                    }

                    boolean flag = EntityBee.this.position().distanceTo(this.hoverPos) <= 0.1D;
                    boolean flag1 = true;

                    if (!flag && this.pollinatingTicks > 600) {
                        EntityBee.this.savedFlowerPos = null;
                    } else {
                        if (flag) {
                            boolean flag2 = EntityBee.this.random.nextInt(25) == 0;

                            if (flag2) {
                                this.hoverPos = new Vec3D(vec3d.x() + (double) this.getOffset(), vec3d.y(), vec3d.z() + (double) this.getOffset());
                                EntityBee.this.navigation.stop();
                            } else {
                                flag1 = false;
                            }

                            EntityBee.this.getLookControl().setLookAt(vec3d.x(), vec3d.y(), vec3d.z());
                        }

                        if (flag1) {
                            this.setWantedPos();
                        }

                        ++this.successfulPollinatingTicks;
                        if (EntityBee.this.random.nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                            this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                            EntityBee.this.playSound(SoundEffects.BEE_POLLINATE, 1.0F, 1.0F);
                        }

                    }
                }
            }
        }

        private void setWantedPos() {
            EntityBee.this.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), 0.3499999940395355D);
        }

        private float getOffset() {
            return (EntityBee.this.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
        }

        private Optional<BlockPosition> findNearbyFlower() {
            return this.findNearestBlock(this.VALID_POLLINATION_BLOCKS, 5.0D);
        }

        private Optional<BlockPosition> findNearestBlock(Predicate<IBlockData> predicate, double d0) {
            BlockPosition blockposition = EntityBee.this.blockPosition();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = 0; (double) i <= d0; i = i > 0 ? -i : 1 - i) {
                for (int j = 0; (double) j < d0; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.setWithOffset(blockposition, k, i - 1, l);
                            if (blockposition.closerThan((BaseBlockPosition) blockposition_mutableblockposition, d0) && predicate.test(EntityBee.this.level.getBlockState(blockposition_mutableblockposition))) {
                                return Optional.of(blockposition_mutableblockposition);
                            }
                        }
                    }
                }
            }

            return Optional.empty();
        }
    }

    private class j extends ControllerLook {

        j(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void tick() {
            if (!EntityBee.this.isAngry()) {
                super.tick();
            }
        }

        @Override
        protected boolean resetXRotOnTick() {
            return !EntityBee.this.beePollinateGoal.isPollinating();
        }
    }

    private class b extends PathfinderGoalMeleeAttack {

        b(EntityCreature entitycreature, double d0, boolean flag) {
            super(entitycreature, d0, flag);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
        }
    }

    private class d extends EntityBee.a {

        d() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (EntityBee.this.hasHive() && EntityBee.this.wantsToEnterHive() && EntityBee.this.hivePos.closerThan((IPosition) EntityBee.this.position(), 2.0D)) {
                TileEntity tileentity = EntityBee.this.level.getBlockEntity(EntityBee.this.hivePos);

                if (tileentity instanceof TileEntityBeehive) {
                    TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                    if (!tileentitybeehive.isFull()) {
                        return true;
                    }

                    EntityBee.this.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            TileEntity tileentity = EntityBee.this.level.getBlockEntity(EntityBee.this.hivePos);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.addOccupant(EntityBee.this, EntityBee.this.hasNectar());
            }

        }
    }

    private class i extends EntityBee.a {

        i() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return EntityBee.this.remainingCooldownBeforeLocatingNewHive == 0 && !EntityBee.this.hasHive() && EntityBee.this.wantsToEnterHive();
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            EntityBee.this.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPosition> list = this.findNearbyHivesWithSpace();

            if (!list.isEmpty()) {
                Iterator iterator = list.iterator();

                BlockPosition blockposition;

                do {
                    if (!iterator.hasNext()) {
                        EntityBee.this.goToHiveGoal.clearBlacklist();
                        EntityBee.this.hivePos = (BlockPosition) list.get(0);
                        return;
                    }

                    blockposition = (BlockPosition) iterator.next();
                } while (EntityBee.this.goToHiveGoal.isTargetBlacklisted(blockposition));

                EntityBee.this.hivePos = blockposition;
            }
        }

        private List<BlockPosition> findNearbyHivesWithSpace() {
            BlockPosition blockposition = EntityBee.this.blockPosition();
            VillagePlace villageplace = ((WorldServer) EntityBee.this.level).getPoiManager();
            Stream<VillagePlaceRecord> stream = villageplace.getInRange((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.BEEHIVE || villageplacetype == VillagePlaceType.BEE_NEST;
            }, blockposition, 20, VillagePlace.Occupancy.ANY);

            return (List) stream.map(VillagePlaceRecord::getPos).filter(EntityBee.this::doesHiveHaveSpace).sorted(Comparator.comparingDouble((blockposition1) -> {
                return blockposition1.distSqr(blockposition);
            })).collect(Collectors.toList());
        }
    }

    @VisibleForDebug
    public class e extends EntityBee.a {

        public static final int MAX_TRAVELLING_TICKS = 600;
        int travellingTicks;
        private static final int MAX_BLACKLISTED_TARGETS = 3;
        final List<BlockPosition> blacklistedTargets;
        @Nullable
        private PathEntity lastPath;
        private static final int TICKS_BEFORE_HIVE_DROP = 60;
        private int ticksStuck;

        e() {
            super();
            this.travellingTicks = EntityBee.this.level.random.nextInt(10);
            this.blacklistedTargets = Lists.newArrayList();
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            return EntityBee.this.hivePos != null && !EntityBee.this.hasRestriction() && EntityBee.this.wantsToEnterHive() && !this.hasReachedTarget(EntityBee.this.hivePos) && EntityBee.this.level.getBlockState(EntityBee.this.hivePos).is((Tag) TagsBlock.BEEHIVES);
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void start() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            EntityBee.this.navigation.stop();
            EntityBee.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (EntityBee.this.hivePos != null) {
                ++this.travellingTicks;
                if (this.travellingTicks > this.adjustedTickDelay(600)) {
                    this.dropAndBlacklistHive();
                } else if (!EntityBee.this.navigation.isInProgress()) {
                    if (!EntityBee.this.closerThan(EntityBee.this.hivePos, 16)) {
                        if (EntityBee.this.isTooFarAway(EntityBee.this.hivePos)) {
                            this.dropHive();
                        } else {
                            EntityBee.this.pathfindRandomlyTowards(EntityBee.this.hivePos);
                        }
                    } else {
                        boolean flag = this.pathfindDirectlyTowards(EntityBee.this.hivePos);

                        if (!flag) {
                            this.dropAndBlacklistHive();
                        } else if (this.lastPath != null && EntityBee.this.navigation.getPath().sameAs(this.lastPath)) {
                            ++this.ticksStuck;
                            if (this.ticksStuck > 60) {
                                this.dropHive();
                                this.ticksStuck = 0;
                            }
                        } else {
                            this.lastPath = EntityBee.this.navigation.getPath();
                        }

                    }
                }
            }
        }

        private boolean pathfindDirectlyTowards(BlockPosition blockposition) {
            EntityBee.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
            EntityBee.this.navigation.moveTo((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
            return EntityBee.this.navigation.getPath() != null && EntityBee.this.navigation.getPath().canReach();
        }

        boolean isTargetBlacklisted(BlockPosition blockposition) {
            return this.blacklistedTargets.contains(blockposition);
        }

        private void blacklistTarget(BlockPosition blockposition) {
            this.blacklistedTargets.add(blockposition);

            while (this.blacklistedTargets.size() > 3) {
                this.blacklistedTargets.remove(0);
            }

        }

        void clearBlacklist() {
            this.blacklistedTargets.clear();
        }

        private void dropAndBlacklistHive() {
            if (EntityBee.this.hivePos != null) {
                this.blacklistTarget(EntityBee.this.hivePos);
            }

            this.dropHive();
        }

        private void dropHive() {
            EntityBee.this.hivePos = null;
            EntityBee.this.remainingCooldownBeforeLocatingNewHive = 200;
        }

        private boolean hasReachedTarget(BlockPosition blockposition) {
            if (EntityBee.this.closerThan(blockposition, 2)) {
                return true;
            } else {
                PathEntity pathentity = EntityBee.this.navigation.getPath();

                return pathentity != null && pathentity.getTarget().equals(blockposition) && pathentity.canReach() && pathentity.isDone();
            }
        }
    }

    public class f extends EntityBee.a {

        private static final int MAX_TRAVELLING_TICKS = 600;
        int travellingTicks;

        f() {
            super();
            this.travellingTicks = EntityBee.this.level.random.nextInt(10);
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            return EntityBee.this.savedFlowerPos != null && !EntityBee.this.hasRestriction() && this.wantsToGoToKnownFlower() && EntityBee.this.isFlowerValid(EntityBee.this.savedFlowerPos) && !EntityBee.this.closerThan(EntityBee.this.savedFlowerPos, 2);
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void start() {
            this.travellingTicks = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.travellingTicks = 0;
            EntityBee.this.navigation.stop();
            EntityBee.this.navigation.resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (EntityBee.this.savedFlowerPos != null) {
                ++this.travellingTicks;
                if (this.travellingTicks > this.adjustedTickDelay(600)) {
                    EntityBee.this.savedFlowerPos = null;
                } else if (!EntityBee.this.navigation.isInProgress()) {
                    if (EntityBee.this.isTooFarAway(EntityBee.this.savedFlowerPos)) {
                        EntityBee.this.savedFlowerPos = null;
                    } else {
                        EntityBee.this.pathfindRandomlyTowards(EntityBee.this.savedFlowerPos);
                    }
                }
            }
        }

        private boolean wantsToGoToKnownFlower() {
            return EntityBee.this.ticksWithoutNectarSinceExitingHive > 2400;
        }
    }

    private class g extends EntityBee.a {

        static final int GROW_CHANCE = 30;

        g() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return EntityBee.this.getCropsGrownSincePollination() >= 10 ? false : (EntityBee.this.random.nextFloat() < 0.3F ? false : EntityBee.this.hasNectar() && EntityBee.this.isHiveValid());
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void tick() {
            if (EntityBee.this.random.nextInt(this.adjustedTickDelay(30)) == 0) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPosition blockposition = EntityBee.this.blockPosition().below(i);
                    IBlockData iblockdata = EntityBee.this.level.getBlockState(blockposition);
                    Block block = iblockdata.getBlock();
                    boolean flag = false;
                    BlockStateInteger blockstateinteger = null;

                    if (iblockdata.is((Tag) TagsBlock.BEE_GROWABLES)) {
                        if (block instanceof BlockCrops) {
                            BlockCrops blockcrops = (BlockCrops) block;

                            if (!blockcrops.isMaxAge(iblockdata)) {
                                flag = true;
                                blockstateinteger = blockcrops.getAgeProperty();
                            }
                        } else {
                            int j;

                            if (block instanceof BlockStem) {
                                j = (Integer) iblockdata.getValue(BlockStem.AGE);
                                if (j < 7) {
                                    flag = true;
                                    blockstateinteger = BlockStem.AGE;
                                }
                            } else if (iblockdata.is(Blocks.SWEET_BERRY_BUSH)) {
                                j = (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE);
                                if (j < 3) {
                                    flag = true;
                                    blockstateinteger = BlockSweetBerryBush.AGE;
                                }
                            } else if (iblockdata.is(Blocks.CAVE_VINES) || iblockdata.is(Blocks.CAVE_VINES_PLANT)) {
                                ((IBlockFragilePlantElement) iblockdata.getBlock()).performBonemeal((WorldServer) EntityBee.this.level, EntityBee.this.random, blockposition, iblockdata);
                            }
                        }

                        if (flag) {
                            EntityBee.this.level.levelEvent(2005, blockposition, 0);
                            EntityBee.this.level.setBlockAndUpdate(blockposition, (IBlockData) iblockdata.setValue(blockstateinteger, (Integer) iblockdata.getValue(blockstateinteger) + 1));
                            EntityBee.this.incrementNumCropsGrownSincePollination();
                        }
                    }
                }

            }
        }
    }

    private class l extends PathfinderGoal {

        private static final int WANDER_THRESHOLD = 22;

        l() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntityBee.this.navigation.isDone() && EntityBee.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return EntityBee.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3D vec3d = this.findPos();

            if (vec3d != null) {
                EntityBee.this.navigation.moveTo(EntityBee.this.navigation.createPath(new BlockPosition(vec3d), 1), 1.0D);
            }

        }

        @Nullable
        private Vec3D findPos() {
            Vec3D vec3d;

            if (EntityBee.this.isHiveValid() && !EntityBee.this.closerThan(EntityBee.this.hivePos, 22)) {
                Vec3D vec3d1 = Vec3D.atCenterOf(EntityBee.this.hivePos);

                vec3d = vec3d1.subtract(EntityBee.this.position()).normalize();
            } else {
                vec3d = EntityBee.this.getViewVector(0.0F);
            }

            boolean flag = true;
            Vec3D vec3d2 = HoverRandomPos.getPos(EntityBee.this, 8, 7, vec3d.x, vec3d.z, 1.5707964F, 3, 1);

            return vec3d2 != null ? vec3d2 : AirAndWaterRandomPos.getPos(EntityBee.this, 8, 4, -2, vec3d.x, vec3d.z, 1.5707963705062866D);
        }
    }

    private class h extends PathfinderGoalHurtByTarget {

        h(EntityBee entitybee) {
            super(entitybee);
        }

        @Override
        public boolean canContinueToUse() {
            return EntityBee.this.isAngry() && super.canContinueToUse();
        }

        @Override
        protected void alertOther(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityBee && this.mob.hasLineOfSight(entityliving)) {
                entityinsentient.setTarget(entityliving);
            }

        }
    }

    private static class c extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        c(EntityBee entitybee) {
            Objects.requireNonNull(entitybee);
            super(entitybee, EntityHuman.class, 10, true, false, entitybee::isAngryAt);
        }

        @Override
        public boolean canUse() {
            return this.beeCanTarget() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            boolean flag = this.beeCanTarget();

            if (flag && this.mob.getTarget() != null) {
                return super.canContinueToUse();
            } else {
                this.targetMob = null;
                return false;
            }
        }

        private boolean beeCanTarget() {
            EntityBee entitybee = (EntityBee) this.mob;

            return entitybee.isAngry() && !entitybee.hasStung();
        }
    }

    private abstract class a extends PathfinderGoal {

        a() {}

        public abstract boolean canBeeUse();

        public abstract boolean canBeeContinueToUse();

        @Override
        public boolean canUse() {
            return this.canBeeUse() && !EntityBee.this.isAngry();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canBeeContinueToUse() && !EntityBee.this.isAngry();
        }
    }
}
