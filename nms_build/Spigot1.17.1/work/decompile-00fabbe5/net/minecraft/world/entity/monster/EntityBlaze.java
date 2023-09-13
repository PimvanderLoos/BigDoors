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
    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntityBlaze.class, DataWatcherRegistry.BYTE);

    public EntityBlaze(EntityTypes<? extends EntityBlaze> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.LAVA, 8.0F);
        this.a(PathType.DANGER_FIRE, 0.0F);
        this.a(PathType.DAMAGE_FIRE, 0.0F);
        this.xpReward = 10;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(4, new EntityBlaze.PathfinderGoalBlazeFireball(this));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.ATTACK_DAMAGE, 6.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityBlaze.DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.BLAZE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.BLAZE_DEATH;
    }

    @Override
    public float aY() {
        return 1.0F;
    }

    @Override
    public void movementTick() {
        if (!this.onGround && this.getMot().y < 0.0D) {
            this.setMot(this.getMot().d(1.0D, 0.6D, 1.0D));
        }

        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.a(this.locX() + 0.5D, this.locY() + 0.5D, this.locZ() + 0.5D, SoundEffects.BLAZE_BURN, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i) {
                this.level.addParticle(Particles.LARGE_SMOKE, this.d(0.5D), this.da(), this.g(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.movementTick();
    }

    @Override
    public boolean ex() {
        return true;
    }

    @Override
    protected void mobTick() {
        --this.nextHeightOffsetChangeTick;
        if (this.nextHeightOffsetChangeTick <= 0) {
            this.nextHeightOffsetChangeTick = 100;
            this.allowedHeightOffset = 0.5F + (float) this.random.nextGaussian() * 3.0F;
        }

        EntityLiving entityliving = this.getGoalTarget();

        if (entityliving != null && entityliving.getHeadY() > this.getHeadY() + (double) this.allowedHeightOffset && this.c(entityliving)) {
            Vec3D vec3d = this.getMot();

            this.setMot(this.getMot().add(0.0D, (0.30000001192092896D - vec3d.y) * 0.30000001192092896D, 0.0D));
            this.hasImpulse = true;
        }

        super.mobTick();
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    public boolean isBurning() {
        return this.p();
    }

    private boolean p() {
        return ((Byte) this.entityData.get(EntityBlaze.DATA_FLAGS_ID) & 1) != 0;
    }

    void v(boolean flag) {
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
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.blaze.getGoalTarget();

            return entityliving != null && entityliving.isAlive() && this.blaze.c(entityliving);
        }

        @Override
        public void c() {
            this.attackStep = 0;
        }

        @Override
        public void d() {
            this.blaze.v(false);
            this.lastSeen = 0;
        }

        @Override
        public void e() {
            --this.attackTime;
            EntityLiving entityliving = this.blaze.getGoalTarget();

            if (entityliving != null) {
                boolean flag = this.blaze.getEntitySenses().a(entityliving);

                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.blaze.f((Entity) entityliving);

                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.blaze.attackEntity(entityliving);
                    }

                    this.blaze.getControllerMove().a(entityliving.locX(), entityliving.locY(), entityliving.locZ(), 1.0D);
                } else if (d0 < this.g() * this.g() && flag) {
                    double d1 = entityliving.locX() - this.blaze.locX();
                    double d2 = entityliving.e(0.5D) - this.blaze.e(0.5D);
                    double d3 = entityliving.locZ() - this.blaze.locZ();

                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.blaze.v(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            this.blaze.v(false);
                        }

                        if (this.attackStep > 1) {
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;

                            if (!this.blaze.isSilent()) {
                                this.blaze.level.a((EntityHuman) null, 1018, this.blaze.getChunkCoordinates(), 0);
                            }

                            for (int i = 0; i < 1; ++i) {
                                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.blaze.level, this.blaze, d1 + this.blaze.getRandom().nextGaussian() * d4, d2, d3 + this.blaze.getRandom().nextGaussian() * d4);

                                entitysmallfireball.setPosition(entitysmallfireball.locX(), this.blaze.e(0.5D) + 0.5D, entitysmallfireball.locZ());
                                this.blaze.level.addEntity(entitysmallfireball);
                            }
                        }
                    }

                    this.blaze.getControllerLook().a(entityliving, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.blaze.getControllerMove().a(entityliving.locX(), entityliving.locY(), entityliving.locZ(), 1.0D);
                }

                super.e();
            }
        }

        private double g() {
            return this.blaze.b(GenericAttributes.FOLLOW_RANGE);
        }
    }
}
