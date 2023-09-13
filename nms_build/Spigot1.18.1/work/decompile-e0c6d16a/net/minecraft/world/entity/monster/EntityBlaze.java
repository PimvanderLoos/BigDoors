package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityBlaze extends EntityMonster {

    private float allowedHeightOffset = 0.5F;
    private int nextHeightOffsetChangeTick;
    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.defineId(EntityBlaze.class, DataWatcherRegistry.BYTE);

    public EntityBlaze(EntityTypes<? extends EntityBlaze> entitytypes, World world) {
        super(entitytypes, world);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new EntityBlaze.PathfinderGoalBlazeFireball(this));
        this.goalSelector.addGoal(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).setAlertOthers());
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.ATTACK_DAMAGE, 6.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).add(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityBlaze.DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.BLAZE_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.BLAZE_DEATH;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public void aiStep() {
        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEffects.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i) {
                this.level.addParticle(Particles.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.aiStep();
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        --this.nextHeightOffsetChangeTick;
        if (this.nextHeightOffsetChangeTick <= 0) {
            this.nextHeightOffsetChangeTick = 100;
            this.allowedHeightOffset = 0.5F + (float) this.random.nextGaussian() * 3.0F;
        }

        EntityLiving entityliving = this.getTarget();

        if (entityliving != null && entityliving.getEyeY() > this.getEyeY() + (double) this.allowedHeightOffset && this.canAttack(entityliving)) {
            Vec3D vec3d = this.getDeltaMovement();

            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (0.30000001192092896D - vec3d.y) * 0.30000001192092896D, 0.0D));
            this.hasImpulse = true;
        }

        super.customServerAiStep();
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return this.isCharged();
    }

    private boolean isCharged() {
        return ((Byte) this.entityData.get(EntityBlaze.DATA_FLAGS_ID) & 1) != 0;
    }

    void setCharged(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityBlaze.DATA_FLAGS_ID);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.entityData.set(EntityBlaze.DATA_FLAGS_ID, b0);
    }

    private static class PathfinderGoalBlazeFireball extends PathfinderGoal {

        private final EntityBlaze blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public PathfinderGoalBlazeFireball(EntityBlaze entityblaze) {
            this.blaze = entityblaze;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            EntityLiving entityliving = this.blaze.getTarget();

            return entityliving != null && entityliving.isAlive() && this.blaze.canAttack(entityliving);
        }

        @Override
        public void start() {
            this.attackStep = 0;
        }

        @Override
        public void stop() {
            this.blaze.setCharged(false);
            this.lastSeen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            --this.attackTime;
            EntityLiving entityliving = this.blaze.getTarget();

            if (entityliving != null) {
                boolean flag = this.blaze.getSensing().hasLineOfSight(entityliving);

                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.blaze.distanceToSqr((Entity) entityliving);

                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.blaze.doHurtTarget(entityliving);
                    }

                    this.blaze.getMoveControl().setWantedPosition(entityliving.getX(), entityliving.getY(), entityliving.getZ(), 1.0D);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = entityliving.getX() - this.blaze.getX();
                    double d2 = entityliving.getY(0.5D) - this.blaze.getY(0.5D);
                    double d3 = entityliving.getZ() - this.blaze.getZ();

                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.blaze.setCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            this.blaze.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;

                            if (!this.blaze.isSilent()) {
                                this.blaze.level.levelEvent((EntityHuman) null, 1018, this.blaze.blockPosition(), 0);
                            }

                            for (int i = 0; i < 1; ++i) {
                                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.blaze.level, this.blaze, d1 + this.blaze.getRandom().nextGaussian() * d4, d2, d3 + this.blaze.getRandom().nextGaussian() * d4);

                                entitysmallfireball.setPos(entitysmallfireball.getX(), this.blaze.getY(0.5D) + 0.5D, entitysmallfireball.getZ());
                                this.blaze.level.addFreshEntity(entitysmallfireball);
                            }
                        }
                    }

                    this.blaze.getLookControl().setLookAt(entityliving, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.blaze.getMoveControl().setWantedPosition(entityliving.getX(), entityliving.getY(), entityliving.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(GenericAttributes.FOLLOW_RANGE);
        }
    }
}
