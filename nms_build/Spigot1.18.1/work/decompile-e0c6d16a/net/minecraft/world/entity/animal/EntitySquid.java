package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3D;

public class EntitySquid extends EntityWaterAnimal {

    public float xBodyRot;
    public float xBodyRotO;
    public float zBodyRot;
    public float zBodyRotO;
    public float tentacleMovement;
    public float oldTentacleMovement;
    public float tentacleAngle;
    public float oldTentacleAngle;
    private float speed;
    private float tentacleSpeed;
    private float rotateSpeed;
    private float tx;
    private float ty;
    private float tz;

    public EntitySquid(EntityTypes<? extends EntitySquid> entitytypes, World world) {
        super(entitytypes, world);
        this.random.setSeed((long) this.getId());
        this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntitySquid.PathfinderGoalSquid(this));
        this.goalSelector.addGoal(1, new EntitySquid.a());
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.SQUID_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.SQUID_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.SQUID_DEATH;
    }

    protected SoundEffect getSquirtSound() {
        return SoundEffects.SQUID_SQUIRT;
    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.xBodyRotO = this.xBodyRot;
        this.zBodyRotO = this.zBodyRot;
        this.oldTentacleMovement = this.tentacleMovement;
        this.oldTentacleAngle = this.tentacleAngle;
        this.tentacleMovement += this.tentacleSpeed;
        if ((double) this.tentacleMovement > 6.283185307179586D) {
            if (this.level.isClientSide) {
                this.tentacleMovement = 6.2831855F;
            } else {
                this.tentacleMovement = (float) ((double) this.tentacleMovement - 6.283185307179586D);
                if (this.random.nextInt(10) == 0) {
                    this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
                }

                this.level.broadcastEntityEvent(this, (byte) 19);
            }
        }

        if (this.isInWaterOrBubble()) {
            if (this.tentacleMovement < 3.1415927F) {
                float f = this.tentacleMovement / 3.1415927F;

                this.tentacleAngle = MathHelper.sin(f * f * 3.1415927F) * 3.1415927F * 0.25F;
                if ((double) f > 0.75D) {
                    this.speed = 1.0F;
                    this.rotateSpeed = 1.0F;
                } else {
                    this.rotateSpeed *= 0.8F;
                }
            } else {
                this.tentacleAngle = 0.0F;
                this.speed *= 0.9F;
                this.rotateSpeed *= 0.99F;
            }

            if (!this.level.isClientSide) {
                this.setDeltaMovement((double) (this.tx * this.speed), (double) (this.ty * this.speed), (double) (this.tz * this.speed));
            }

            Vec3D vec3d = this.getDeltaMovement();
            double d0 = vec3d.horizontalDistance();

            this.yBodyRot += (-((float) MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F - this.yBodyRot) * 0.1F;
            this.setYRot(this.yBodyRot);
            this.zBodyRot = (float) ((double) this.zBodyRot + 3.141592653589793D * (double) this.rotateSpeed * 1.5D);
            this.xBodyRot += (-((float) MathHelper.atan2(d0, vec3d.y)) * 57.295776F - this.xBodyRot) * 0.1F;
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.tentacleMovement)) * 3.1415927F * 0.25F;
            if (!this.level.isClientSide) {
                double d1 = this.getDeltaMovement().y;

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d1 = 0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
                } else if (!this.isNoGravity()) {
                    d1 -= 0.08D;
                }

                this.setDeltaMovement(0.0D, d1 * 0.9800000190734863D, 0.0D);
            }

            this.xBodyRot = (float) ((double) this.xBodyRot + (double) (-90.0F - this.xBodyRot) * 0.02D);
        }

    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (super.hurt(damagesource, f) && this.getLastHurtByMob() != null) {
            this.spawnInk();
            return true;
        } else {
            return false;
        }
    }

    private Vec3D rotateVector(Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.xRot(this.xBodyRotO * 0.017453292F);

        vec3d1 = vec3d1.yRot(-this.yBodyRotO * 0.017453292F);
        return vec3d1;
    }

    private void spawnInk() {
        this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
        Vec3D vec3d = this.rotateVector(new Vec3D(0.0D, -1.0D, 0.0D)).add(this.getX(), this.getY(), this.getZ());

        for (int i = 0; i < 30; ++i) {
            Vec3D vec3d1 = this.rotateVector(new Vec3D((double) this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double) this.random.nextFloat() * 0.6D - 0.3D));
            Vec3D vec3d2 = vec3d1.scale(0.3D + (double) (this.random.nextFloat() * 2.0F));

            ((WorldServer) this.level).sendParticles(this.getInkParticle(), vec3d.x, vec3d.y + 0.5D, vec3d.z, 0, vec3d2.x, vec3d2.y, vec3d2.z, 0.10000000149011612D);
        }

    }

    protected ParticleParam getInkParticle() {
        return Particles.SQUID_INK;
    }

    @Override
    public void travel(Vec3D vec3d) {
        this.move(EnumMoveType.SELF, this.getDeltaMovement());
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 19) {
            this.tentacleMovement = 0.0F;
        } else {
            super.handleEntityEvent(b0);
        }

    }

    public void setMovementVector(float f, float f1, float f2) {
        this.tx = f;
        this.ty = f1;
        this.tz = f2;
    }

    public boolean hasMovementVector() {
        return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
    }

    private class PathfinderGoalSquid extends PathfinderGoal {

        private final EntitySquid squid;

        public PathfinderGoalSquid(EntitySquid entitysquid) {
            this.squid = entitysquid;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            int i = this.squid.getNoActionTime();

            if (i > 100) {
                this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
            } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
                float f = this.squid.getRandom().nextFloat() * 6.2831855F;
                float f1 = MathHelper.cos(f) * 0.2F;
                float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
                float f3 = MathHelper.sin(f) * 0.2F;

                this.squid.setMovementVector(f1, f2, f3);
            }

        }
    }

    private class a extends PathfinderGoal {

        private static final float SQUID_FLEE_SPEED = 3.0F;
        private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
        private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
        private int fleeTicks;

        a() {}

        @Override
        public boolean canUse() {
            EntityLiving entityliving = EntitySquid.this.getLastHurtByMob();

            return EntitySquid.this.isInWater() && entityliving != null ? EntitySquid.this.distanceToSqr((Entity) entityliving) < 100.0D : false;
        }

        @Override
        public void start() {
            this.fleeTicks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.fleeTicks;
            EntityLiving entityliving = EntitySquid.this.getLastHurtByMob();

            if (entityliving != null) {
                Vec3D vec3d = new Vec3D(EntitySquid.this.getX() - entityliving.getX(), EntitySquid.this.getY() - entityliving.getY(), EntitySquid.this.getZ() - entityliving.getZ());
                IBlockData iblockdata = EntitySquid.this.level.getBlockState(new BlockPosition(EntitySquid.this.getX() + vec3d.x, EntitySquid.this.getY() + vec3d.y, EntitySquid.this.getZ() + vec3d.z));
                Fluid fluid = EntitySquid.this.level.getFluidState(new BlockPosition(EntitySquid.this.getX() + vec3d.x, EntitySquid.this.getY() + vec3d.y, EntitySquid.this.getZ() + vec3d.z));

                if (fluid.is((Tag) TagsFluid.WATER) || iblockdata.isAir()) {
                    double d0 = vec3d.length();

                    if (d0 > 0.0D) {
                        vec3d.normalize();
                        float f = 3.0F;

                        if (d0 > 5.0D) {
                            f = (float) ((double) f - (d0 - 5.0D) / 5.0D);
                        }

                        if (f > 0.0F) {
                            vec3d = vec3d.scale((double) f);
                        }
                    }

                    if (iblockdata.isAir()) {
                        vec3d = vec3d.subtract(0.0D, vec3d.y, 0.0D);
                    }

                    EntitySquid.this.setMovementVector((float) vec3d.x / 20.0F, (float) vec3d.y / 20.0F, (float) vec3d.z / 20.0F);
                }

                if (this.fleeTicks % 10 == 5) {
                    EntitySquid.this.level.addParticle(Particles.BUBBLE, EntitySquid.this.getX(), EntitySquid.this.getY(), EntitySquid.this.getZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }
}
