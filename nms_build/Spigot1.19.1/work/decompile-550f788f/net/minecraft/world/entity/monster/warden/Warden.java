package net.minecraft.world.entity.monster.warden;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumRenderType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;

public class Warden extends EntityMonster implements VibrationListener.b {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int GAME_EVENT_LISTENER_RANGE = 16;
    private static final int VIBRATION_COOLDOWN_TICKS = 40;
    private static final int TIME_TO_USE_MELEE_UNTIL_SONIC_BOOM = 200;
    private static final int MAX_HEALTH = 500;
    private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
    private static final float KNOCKBACK_RESISTANCE = 1.0F;
    private static final float ATTACK_KNOCKBACK = 1.5F;
    private static final int ATTACK_DAMAGE = 30;
    private static final DataWatcherObject<Integer> CLIENT_ANGER_LEVEL = DataWatcher.defineId(Warden.class, DataWatcherRegistry.INT);
    private static final int DARKNESS_DISPLAY_LIMIT = 200;
    private static final int DARKNESS_DURATION = 260;
    private static final int DARKNESS_RADIUS = 20;
    private static final int DARKNESS_INTERVAL = 120;
    private static final int ANGERMANAGEMENT_TICK_DELAY = 20;
    private static final int DEFAULT_ANGER = 35;
    private static final int PROJECTILE_ANGER = 10;
    private static final int ON_HURT_ANGER_BOOST = 20;
    private static final int RECENT_PROJECTILE_TICK_THRESHOLD = 100;
    private static final int TOUCH_COOLDOWN_TICKS = 20;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final float DIGGING_PARTICLES_DURATION = 4.5F;
    private static final float DIGGING_PARTICLES_OFFSET = 0.7F;
    private static final int PROJECTILE_ANGER_DISTANCE = 30;
    private int tendrilAnimation;
    private int tendrilAnimationO;
    private int heartAnimation;
    private int heartAnimationO;
    public AnimationState roarAnimationState = new AnimationState();
    public AnimationState sniffAnimationState = new AnimationState();
    public AnimationState emergeAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState sonicBoomAnimationState = new AnimationState();
    private final DynamicGameEventListener<VibrationListener> dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), 16, this, (VibrationListener.a) null, 0.0F, 0));
    private AngerManagement angerManagement = new AngerManagement(this::canTargetEntity, Collections.emptyList());

    public Warden(EntityTypes<? extends EntityMonster> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this, this.hasPose(EntityPose.EMERGING) ? 1 : 0);
    }

    @Override
    public void recreateFromPacket(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.recreateFromPacket(packetplayoutspawnentity);
        if (packetplayoutspawnentity.getData() == 1) {
            this.setPose(EntityPose.EMERGING);
        }

    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return super.checkSpawnObstruction(iworldreader) && iworldreader.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.0F;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damagesource) {
        return this.isDiggingOrEmerging() && !damagesource.isBypassInvul() ? true : super.isInvulnerableTo(damagesource);
    }

    private boolean isDiggingOrEmerging() {
        return this.hasPose(EntityPose.DIGGING) || this.hasPose(EntityPose.EMERGING);
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.55F;
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MAX_HEALTH, 500.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.KNOCKBACK_RESISTANCE, 1.0D).add(GenericAttributes.ATTACK_KNOCKBACK, 1.5D).add(GenericAttributes.ATTACK_DAMAGE, 30.0D);
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 4.0F;
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return !this.hasPose(EntityPose.ROARING) && !this.isDiggingOrEmerging() ? this.getAngerLevel().getAmbientSound() : null;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.WARDEN_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.WARDEN_STEP, 10.0F, 1.0F);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.level.broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEffects.WARDEN_ATTACK_IMPACT, 10.0F, this.getVoicePitch());
        SonicBoom.setCooldown(this, 40);
        return super.doHurtTarget(entity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Warden.CLIENT_ANGER_LEVEL, 0);
    }

    public int getClientAngerLevel() {
        return (Integer) this.entityData.get(Warden.CLIENT_ANGER_LEVEL);
    }

    private void syncClientAngerLevel() {
        this.entityData.set(Warden.CLIENT_ANGER_LEVEL, this.getActiveAnger());
    }

    @Override
    public void tick() {
        World world = this.level;

        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;

            ((VibrationListener) this.dynamicGameEventListener.getListener()).tick(worldserver);
            if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
                WardenAi.setDigCooldown(this);
            }
        }

        super.tick();
        if (this.level.isClientSide()) {
            if (this.tickCount % this.getHeartBeatDelay() == 0) {
                this.heartAnimation = 10;
                if (!this.isSilent()) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEffects.WARDEN_HEARTBEAT, this.getSoundSource(), 5.0F, this.getVoicePitch(), false);
                }
            }

            this.tendrilAnimationO = this.tendrilAnimation;
            if (this.tendrilAnimation > 0) {
                --this.tendrilAnimation;
            }

            this.heartAnimationO = this.heartAnimation;
            if (this.heartAnimation > 0) {
                --this.heartAnimation;
            }

            switch (this.getPose()) {
                case EMERGING:
                    this.clientDiggingParticles(this.emergeAnimationState);
                    break;
                case DIGGING:
                    this.clientDiggingParticles(this.diggingAnimationState);
            }
        }

    }

    @Override
    protected void customServerAiStep() {
        WorldServer worldserver = (WorldServer) this.level;

        worldserver.getProfiler().push("wardenBrain");
        this.getBrain().tick(worldserver, this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
        if ((this.tickCount + this.getId()) % 120 == 0) {
            applyDarknessAround(worldserver, this.position(), this, 20);
        }

        if (this.tickCount % 20 == 0) {
            this.angerManagement.tick(worldserver, this::canTargetEntity);
            this.syncClientAngerLevel();
        }

        WardenAi.updateActivity(this);
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 4) {
            this.roarAnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if (b0 == 61) {
            this.tendrilAnimation = 10;
        } else if (b0 == 62) {
            this.sonicBoomAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(b0);
        }

    }

    private int getHeartBeatDelay() {
        float f = (float) this.getClientAngerLevel() / (float) AngerLevel.ANGRY.getMinimumAnger();

        return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0F, 1.0F) * 30.0F);
    }

    public float getTendrilAnimation(float f) {
        return MathHelper.lerp(f, (float) this.tendrilAnimationO, (float) this.tendrilAnimation) / 10.0F;
    }

    public float getHeartAnimation(float f) {
        return MathHelper.lerp(f, (float) this.heartAnimationO, (float) this.heartAnimation) / 10.0F;
    }

    private void clientDiggingParticles(AnimationState animationstate) {
        if ((float) animationstate.getAccumulatedTime() < 4500.0F) {
            RandomSource randomsource = this.getRandom();
            IBlockData iblockdata = this.getBlockStateOn();

            if (iblockdata.getRenderShape() != EnumRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    double d0 = this.getX() + (double) MathHelper.randomBetween(randomsource, -0.7F, 0.7F);
                    double d1 = this.getY();
                    double d2 = this.getZ() + (double) MathHelper.randomBetween(randomsource, -0.7F, 0.7F);

                    this.level.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }

    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (Warden.DATA_POSE.equals(datawatcherobject)) {
            switch (this.getPose()) {
                case EMERGING:
                    this.emergeAnimationState.start(this.tickCount);
                    break;
                case DIGGING:
                    this.diggingAnimationState.start(this.tickCount);
                    break;
                case ROARING:
                    this.roarAnimationState.start(this.tickCount);
                    break;
                case SNIFFING:
                    this.sniffAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isDiggingOrEmerging();
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return WardenAi.makeBrain(this, dynamic);
    }

    @Override
    public BehaviorController<Warden> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, WorldServer> biconsumer) {
        World world = this.level;

        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;

            biconsumer.accept(this.dynamicGameEventListener, worldserver);
        }

    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return GameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Contract("null->false")
    public boolean canTargetEntity(@Nullable Entity entity) {
        boolean flag;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            if (this.level == entity.level && IEntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isAlliedTo(entity) && entityliving.getType() != EntityTypes.ARMOR_STAND && entityliving.getType() != EntityTypes.WARDEN && !entityliving.isInvulnerable() && !entityliving.isDeadOrDying() && this.level.getWorldBorder().isWithinBounds(entityliving.getBoundingBox())) {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    public static void applyDarknessAround(WorldServer worldserver, Vec3D vec3d, @Nullable Entity entity, int i) {
        MobEffect mobeffect = new MobEffect(MobEffects.DARKNESS, 260, 0, false, false);

        MobEffectUtil.addEffectToPlayersAround(worldserver, entity, vec3d, (double) i, mobeffect, 200);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        DataResult dataresult = AngerManagement.codec(this::canTargetEntity).encodeStart(DynamicOpsNBT.INSTANCE, this.angerManagement);
        Logger logger = Warden.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("anger", nbtbase);
        });
        dataresult = VibrationListener.codec(this).encodeStart(DynamicOpsNBT.INSTANCE, (VibrationListener) this.dynamicGameEventListener.getListener());
        logger = Warden.LOGGER;
        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("listener", nbtbase);
        });
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        DataResult dataresult;
        Logger logger;

        if (nbttagcompound.contains("anger")) {
            dataresult = AngerManagement.codec(this::canTargetEntity).parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("anger")));
            logger = Warden.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((angermanagement) -> {
                this.angerManagement = angermanagement;
            });
            this.syncClientAngerLevel();
        }

        if (nbttagcompound.contains("listener", 10)) {
            dataresult = VibrationListener.codec(this).parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("listener")));
            logger = Warden.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((vibrationlistener) -> {
                this.dynamicGameEventListener.updateListener(vibrationlistener, this.level);
            });
        }

    }

    private void playListeningSound() {
        if (!this.hasPose(EntityPose.ROARING)) {
            this.playSound(this.getAngerLevel().getListeningSound(), 10.0F, this.getVoicePitch());
        }

    }

    public AngerLevel getAngerLevel() {
        return AngerLevel.byAnger(this.getActiveAnger());
    }

    private int getActiveAnger() {
        return this.angerManagement.getActiveAnger(this.getTarget());
    }

    public void clearAnger(Entity entity) {
        this.angerManagement.clearAnger(entity);
    }

    public void increaseAngerAt(@Nullable Entity entity) {
        this.increaseAngerAt(entity, 35, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int i, boolean flag) {
        if (!this.isNoAi() && this.canTargetEntity(entity)) {
            WardenAi.setDigCooldown(this);
            boolean flag1 = !(this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object) null) instanceof EntityHuman);
            int j = this.angerManagement.increaseAnger(entity, i);

            if (entity instanceof EntityHuman && flag1 && AngerLevel.byAnger(j).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }

            if (flag) {
                this.playListeningSound();
            }
        }

    }

    public Optional<EntityLiving> getEntityAngryAt() {
        return this.getAngerLevel().isAngry() ? this.angerManagement.getActiveEntity() : Optional.empty();
    }

    @Nullable
    @Override
    public EntityLiving getTarget() {
        return (EntityLiving) this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object) null);
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return false;
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        if (enummobspawn == EnumMobSpawn.TRIGGERED) {
            this.setPose(EntityPose.EMERGING);
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long) WardenAi.EMERGE_DURATION);
            this.playSound(SoundEffects.WARDEN_AGITATED, 5.0F, 1.0F);
        }

        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        boolean flag = super.hurt(damagesource, f);

        if (!this.level.isClientSide && !this.isNoAi() && !this.isDiggingOrEmerging()) {
            Entity entity = damagesource.getEntity();

            this.increaseAngerAt(entity, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!(damagesource instanceof EntityDamageSourceIndirect) || this.closerThan(entityliving, 5.0D)) {
                    this.setAttackTarget(entityliving);
                }
            }
        }

        return flag;
    }

    public void setAttackTarget(EntityLiving entityliving) {
        this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        BehaviorAttackTargetSet.setAttackTarget(this, entityliving);
        SonicBoom.setCooldown(this, 200);
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        EntitySize entitysize = super.getDimensions(entitypose);

        return this.isDiggingOrEmerging() ? EntitySize.fixed(entitysize.width, 1.0F) : entitysize;
    }

    @Override
    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            WardenAi.setDisturbanceLocation(this, entity.blockPosition());
        }

        super.doPush(entity);
    }

    @Override
    public boolean shouldListen(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, GameEvent.a gameevent_a) {
        if (!this.isNoAi() && !this.isDeadOrDying() && !this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && !this.isDiggingOrEmerging() && worldserver.getWorldBorder().isWithinBounds(blockposition) && !this.isRemoved() && this.level == worldserver) {
            Entity entity = gameevent_a.sourceEntity();
            boolean flag;

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!this.canTargetEntity(entityliving)) {
                    flag = false;
                    return flag;
                }
            }

            flag = true;
            return flag;
        } else {
            return false;
        }
    }

    @Override
    public void onSignalReceive(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity, @Nullable Entity entity1, float f) {
        if (!this.isDeadOrDying()) {
            this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
            worldserver.broadcastEntityEvent(this, (byte) 61);
            this.playSound(SoundEffects.WARDEN_TENDRIL_CLICKS, 5.0F, this.getVoicePitch());
            BlockPosition blockposition1 = blockposition;

            if (entity1 != null) {
                if (this.closerThan(entity1, 30.0D)) {
                    if (this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                        if (this.canTargetEntity(entity1)) {
                            blockposition1 = entity1.blockPosition();
                        }

                        this.increaseAngerAt(entity1);
                    } else {
                        this.increaseAngerAt(entity1, 10, true);
                    }
                }

                this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
            } else {
                this.increaseAngerAt(entity);
            }

            if (!this.getAngerLevel().isAngry()) {
                Optional<EntityLiving> optional = this.angerManagement.getActiveEntity();

                if (entity1 != null || optional.isEmpty() || optional.get() == entity) {
                    WardenAi.setDisturbanceLocation(this, blockposition1);
                }
            }

        }
    }

    @VisibleForTesting
    public AngerManagement getAngerManagement() {
        return this.angerManagement;
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new Navigation(this, world) {
            @Override
            protected Pathfinder createPathFinder(int i) {
                this.nodeEvaluator = new PathfinderNormal();
                this.nodeEvaluator.setCanPassDoors(true);
                return new Pathfinder(this.nodeEvaluator, i) {
                    @Override
                    protected float distance(PathPoint pathpoint, PathPoint pathpoint1) {
                        return pathpoint.distanceToXZ(pathpoint1);
                    }
                };
            }
        };
    }
}
