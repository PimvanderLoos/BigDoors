package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityGuardian extends EntityMonster {

    protected static final int ATTACK_TIME = 80;
    private static final DataWatcherObject<Boolean> DATA_ID_MOVING = DataWatcher.defineId(EntityGuardian.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_ID_ATTACK_TARGET = DataWatcher.defineId(EntityGuardian.class, DataWatcherRegistry.INT);
    private float clientSideTailAnimation;
    private float clientSideTailAnimationO;
    private float clientSideTailAnimationSpeed;
    private float clientSideSpikesAnimation;
    private float clientSideSpikesAnimationO;
    @Nullable
    private EntityLiving clientSideCachedAttackTarget;
    private int clientSideAttackTime;
    private boolean clientSideTouchedGround;
    @Nullable
    public PathfinderGoalRandomStroll randomStrollGoal;

    public EntityGuardian(EntityTypes<? extends EntityGuardian> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 10;
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new EntityGuardian.ControllerMoveGuardian(this);
        this.clientSideTailAnimation = this.random.nextFloat();
        this.clientSideTailAnimationO = this.clientSideTailAnimation;
    }

    @Override
    protected void registerGoals() {
        PathfinderGoalMoveTowardsRestriction pathfindergoalmovetowardsrestriction = new PathfinderGoalMoveTowardsRestriction(this, 1.0D);

        this.randomStrollGoal = new PathfinderGoalRandomStroll(this, 1.0D, 80);
        this.goalSelector.addGoal(4, new EntityGuardian.PathfinderGoalGuardianAttack(this));
        this.goalSelector.addGoal(5, pathfindergoalmovetowardsrestriction);
        this.goalSelector.addGoal(7, this.randomStrollGoal);
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityGuardian.class, 12.0F, 0.01F));
        this.goalSelector.addGoal(9, new PathfinderGoalRandomLookaround(this));
        this.randomStrollGoal.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        pathfindergoalmovetowardsrestriction.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        this.targetSelector.addGoal(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, new EntityGuardian.EntitySelectorGuardianTargetHumanSquid(this)));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.ATTACK_DAMAGE, 6.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.5D).add(GenericAttributes.FOLLOW_RANGE, 16.0D).add(GenericAttributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityGuardian.DATA_ID_MOVING, false);
        this.entityData.define(EntityGuardian.DATA_ID_ATTACK_TARGET, 0);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.WATER;
    }

    public boolean isMoving() {
        return (Boolean) this.entityData.get(EntityGuardian.DATA_ID_MOVING);
    }

    void setMoving(boolean flag) {
        this.entityData.set(EntityGuardian.DATA_ID_MOVING, flag);
    }

    public int getAttackDuration() {
        return 80;
    }

    public void setActiveAttackTarget(int i) {
        this.entityData.set(EntityGuardian.DATA_ID_ATTACK_TARGET, i);
    }

    public boolean hasActiveAttackTarget() {
        return (Integer) this.entityData.get(EntityGuardian.DATA_ID_ATTACK_TARGET) != 0;
    }

    @Nullable
    public EntityLiving getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level.getEntity((Integer) this.entityData.get(EntityGuardian.DATA_ID_ATTACK_TARGET));

                if (entity instanceof EntityLiving) {
                    this.clientSideCachedAttackTarget = (EntityLiving) entity;
                    return this.clientSideCachedAttackTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        super.onSyncedDataUpdated(datawatcherobject);
        if (EntityGuardian.DATA_ID_ATTACK_TARGET.equals(datawatcherobject)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }

    }

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isInWaterOrBubble() ? SoundEffects.GUARDIAN_AMBIENT : SoundEffects.GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isInWaterOrBubble() ? SoundEffects.GUARDIAN_HURT : SoundEffects.GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return this.isInWaterOrBubble() ? SoundEffects.GUARDIAN_DEATH : SoundEffects.GUARDIAN_DEATH_LAND;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getFluidState(blockposition).is((Tag) TagsFluid.WATER) ? 10.0F + iworldreader.getBrightness(blockposition) - 0.5F : super.getWalkTargetValue(blockposition, iworldreader);
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            if (this.level.isClientSide) {
                this.clientSideTailAnimationO = this.clientSideTailAnimation;
                Vec3D vec3d;

                if (!this.isInWater()) {
                    this.clientSideTailAnimationSpeed = 2.0F;
                    vec3d = this.getDeltaMovement();
                    if (vec3d.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(), this.getSoundSource(), 1.0F, 1.0F, false);
                    }

                    this.clientSideTouchedGround = vec3d.y < 0.0D && this.level.loadedAndEntityCanStandOn(this.blockPosition().below(), this);
                } else if (this.isMoving()) {
                    if (this.clientSideTailAnimationSpeed < 0.5F) {
                        this.clientSideTailAnimationSpeed = 4.0F;
                    } else {
                        this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
                    }
                } else {
                    this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
                }

                this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
                this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
                if (!this.isInWaterOrBubble()) {
                    this.clientSideSpikesAnimation = this.random.nextFloat();
                } else if (this.isMoving()) {
                    this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
                } else {
                    this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
                }

                if (this.isMoving() && this.isInWater()) {
                    vec3d = this.getViewVector(0.0F);

                    for (int i = 0; i < 2; ++i) {
                        this.level.addParticle(Particles.BUBBLE, this.getRandomX(0.5D) - vec3d.x * 1.5D, this.getRandomY() - vec3d.y * 1.5D, this.getRandomZ(0.5D) - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D);
                    }
                }

                if (this.hasActiveAttackTarget()) {
                    if (this.clientSideAttackTime < this.getAttackDuration()) {
                        ++this.clientSideAttackTime;
                    }

                    EntityLiving entityliving = this.getActiveAttackTarget();

                    if (entityliving != null) {
                        this.getLookControl().setLookAt(entityliving, 90.0F, 90.0F);
                        this.getLookControl().tick();
                        double d0 = (double) this.getAttackAnimationScale(0.0F);
                        double d1 = entityliving.getX() - this.getX();
                        double d2 = entityliving.getY(0.5D) - this.getEyeY();
                        double d3 = entityliving.getZ() - this.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

                        d1 /= d4;
                        d2 /= d4;
                        d3 /= d4;
                        double d5 = this.random.nextDouble();

                        while (d5 < d4) {
                            d5 += 1.8D - d0 + this.random.nextDouble() * (1.7D - d0);
                            this.level.addParticle(Particles.BUBBLE, this.getX() + d1 * d5, this.getEyeY() + d2 * d5, this.getZ() + d3 * d5, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            }

            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            } else if (this.onGround) {
                this.setDeltaMovement(this.getDeltaMovement().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.onGround = false;
                this.hasImpulse = true;
            }

            if (this.hasActiveAttackTarget()) {
                this.setYRot(this.yHeadRot);
            }
        }

        super.aiStep();
    }

    protected SoundEffect getFlopSound() {
        return SoundEffects.GUARDIAN_FLOP;
    }

    public float getTailAnimation(float f) {
        return MathHelper.lerp(f, this.clientSideTailAnimationO, this.clientSideTailAnimation);
    }

    public float getSpikesAnimation(float f) {
        return MathHelper.lerp(f, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
    }

    public float getAttackAnimationScale(float f) {
        return ((float) this.clientSideAttackTime + f) / (float) this.getAttackDuration();
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return iworldreader.isUnobstructed(this);
    }

    public static boolean checkGuardianSpawnRules(EntityTypes<? extends EntityGuardian> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return (random.nextInt(20) == 0 || !generatoraccess.canSeeSkyFromBelowWater(blockposition)) && generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && (enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getFluidState(blockposition).is((Tag) TagsFluid.WATER)) && generatoraccess.getFluidState(blockposition.below()).is((Tag) TagsFluid.WATER);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (!this.isMoving() && !damagesource.isMagic() && damagesource.getDirectEntity() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.getDirectEntity();

            if (!damagesource.isExplosion()) {
                entityliving.hurt(DamageSource.thorns(this), 2.0F);
            }
        }

        if (this.randomStrollGoal != null) {
            this.randomStrollGoal.trigger();
        }

        return super.hurt(damagesource, f);
    }

    @Override
    public int getMaxHeadXRot() {
        return 180;
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1F, vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (!this.isMoving() && this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(vec3d);
        }

    }

    private static class ControllerMoveGuardian extends ControllerMove {

        private final EntityGuardian guardian;

        public ControllerMoveGuardian(EntityGuardian entityguardian) {
            super(entityguardian);
            this.guardian = entityguardian;
        }

        @Override
        public void tick() {
            if (this.operation == ControllerMove.Operation.MOVE_TO && !this.guardian.getNavigation().isDone()) {
                Vec3D vec3d = new Vec3D(this.wantedX - this.guardian.getX(), this.wantedY - this.guardian.getY(), this.wantedZ - this.guardian.getZ());
                double d0 = vec3d.length();
                double d1 = vec3d.x / d0;
                double d2 = vec3d.y / d0;
                double d3 = vec3d.z / d0;
                float f = (float) (MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875D) - 90.0F;

                this.guardian.setYRot(this.rotlerp(this.guardian.getYRot(), f, 90.0F));
                this.guardian.yBodyRot = this.guardian.getYRot();
                float f1 = (float) (this.speedModifier * this.guardian.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));
                float f2 = MathHelper.lerp(0.125F, this.guardian.getSpeed(), f1);

                this.guardian.setSpeed(f2);
                double d4 = Math.sin((double) (this.guardian.tickCount + this.guardian.getId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (this.guardian.getYRot() * 0.017453292F));
                double d6 = Math.sin((double) (this.guardian.getYRot() * 0.017453292F));
                double d7 = Math.sin((double) (this.guardian.tickCount + this.guardian.getId()) * 0.75D) * 0.05D;

                this.guardian.setDeltaMovement(this.guardian.getDeltaMovement().add(d4 * d5, d7 * (d6 + d5) * 0.25D + (double) f2 * d2 * 0.1D, d4 * d6));
                ControllerLook controllerlook = this.guardian.getLookControl();
                double d8 = this.guardian.getX() + d1 * 2.0D;
                double d9 = this.guardian.getEyeY() + d2 / d0;
                double d10 = this.guardian.getZ() + d3 * 2.0D;
                double d11 = controllerlook.getWantedX();
                double d12 = controllerlook.getWantedY();
                double d13 = controllerlook.getWantedZ();

                if (!controllerlook.isLookingAtTarget()) {
                    d11 = d8;
                    d12 = d9;
                    d13 = d10;
                }

                this.guardian.getLookControl().setLookAt(MathHelper.lerp(0.125D, d11, d8), MathHelper.lerp(0.125D, d12, d9), MathHelper.lerp(0.125D, d13, d10), 10.0F, 40.0F);
                this.guardian.setMoving(true);
            } else {
                this.guardian.setSpeed(0.0F);
                this.guardian.setMoving(false);
            }
        }
    }

    private static class PathfinderGoalGuardianAttack extends PathfinderGoal {

        private final EntityGuardian guardian;
        private int attackTime;
        private final boolean elder;

        public PathfinderGoalGuardianAttack(EntityGuardian entityguardian) {
            this.guardian = entityguardian;
            this.elder = entityguardian instanceof EntityGuardianElder;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            EntityLiving entityliving = this.guardian.getTarget();

            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && (this.elder || this.guardian.getTarget() != null && this.guardian.distanceToSqr((Entity) this.guardian.getTarget()) > 9.0D);
        }

        @Override
        public void start() {
            this.attackTime = -10;
            this.guardian.getNavigation().stop();
            EntityLiving entityliving = this.guardian.getTarget();

            if (entityliving != null) {
                this.guardian.getLookControl().setLookAt(entityliving, 90.0F, 90.0F);
            }

            this.guardian.hasImpulse = true;
        }

        @Override
        public void stop() {
            this.guardian.setActiveAttackTarget(0);
            this.guardian.setTarget((EntityLiving) null);
            this.guardian.randomStrollGoal.trigger();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            EntityLiving entityliving = this.guardian.getTarget();

            if (entityliving != null) {
                this.guardian.getNavigation().stop();
                this.guardian.getLookControl().setLookAt(entityliving, 90.0F, 90.0F);
                if (!this.guardian.hasLineOfSight(entityliving)) {
                    this.guardian.setTarget((EntityLiving) null);
                } else {
                    ++this.attackTime;
                    if (this.attackTime == 0) {
                        this.guardian.setActiveAttackTarget(entityliving.getId());
                        if (!this.guardian.isSilent()) {
                            this.guardian.level.broadcastEntityEvent(this.guardian, (byte) 21);
                        }
                    } else if (this.attackTime >= this.guardian.getAttackDuration()) {
                        float f = 1.0F;

                        if (this.guardian.level.getDifficulty() == EnumDifficulty.HARD) {
                            f += 2.0F;
                        }

                        if (this.elder) {
                            f += 2.0F;
                        }

                        entityliving.hurt(DamageSource.indirectMagic(this.guardian, this.guardian), f);
                        entityliving.hurt(DamageSource.mobAttack(this.guardian), (float) this.guardian.getAttributeValue(GenericAttributes.ATTACK_DAMAGE));
                        this.guardian.setTarget((EntityLiving) null);
                    }

                    super.tick();
                }
            }
        }
    }

    private static class EntitySelectorGuardianTargetHumanSquid implements Predicate<EntityLiving> {

        private final EntityGuardian guardian;

        public EntitySelectorGuardianTargetHumanSquid(EntityGuardian entityguardian) {
            this.guardian = entityguardian;
        }

        public boolean test(@Nullable EntityLiving entityliving) {
            return (entityliving instanceof EntityHuman || entityliving instanceof EntitySquid || entityliving instanceof Axolotl) && entityliving.distanceToSqr((Entity) this.guardian) > 9.0D;
        }
    }
}
