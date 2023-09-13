package net.minecraft.world.entity.animal;

import java.util.Random;
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
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
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
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntitySquid.PathfinderGoalSquid(this));
        this.goalSelector.a(1, new EntitySquid.a());
    }

    public static AttributeProvider.Builder fw() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.SQUID_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.SQUID_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.SQUID_DEATH;
    }

    protected SoundEffect p() {
        return SoundEffects.SQUID_SQUIRT;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public void movementTick() {
        super.movementTick();
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

                this.level.broadcastEntityEffect(this, (byte) 19);
            }
        }

        if (this.aO()) {
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
                this.setMot((double) (this.tx * this.speed), (double) (this.ty * this.speed), (double) (this.tz * this.speed));
            }

            Vec3D vec3d = this.getMot();
            double d0 = vec3d.h();

            this.yBodyRot += (-((float) MathHelper.d(vec3d.x, vec3d.z)) * 57.295776F - this.yBodyRot) * 0.1F;
            this.setYRot(this.yBodyRot);
            this.zBodyRot = (float) ((double) this.zBodyRot + 3.141592653589793D * (double) this.rotateSpeed * 1.5D);
            this.xBodyRot += (-((float) MathHelper.d(d0, vec3d.y)) * 57.295776F - this.xBodyRot) * 0.1F;
        } else {
            this.tentacleAngle = MathHelper.e(MathHelper.sin(this.tentacleMovement)) * 3.1415927F * 0.25F;
            if (!this.level.isClientSide) {
                double d1 = this.getMot().y;

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d1 = 0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
                } else if (!this.isNoGravity()) {
                    d1 -= 0.08D;
                }

                this.setMot(0.0D, d1 * 0.9800000190734863D, 0.0D);
            }

            this.xBodyRot = (float) ((double) this.xBodyRot + (double) (-90.0F - this.xBodyRot) * 0.02D);
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (super.damageEntity(damagesource, f) && this.getLastDamager() != null) {
            this.t();
            return true;
        } else {
            return false;
        }
    }

    private Vec3D i(Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.a(this.xBodyRotO * 0.017453292F);

        vec3d1 = vec3d1.b(-this.yBodyRotO * 0.017453292F);
        return vec3d1;
    }

    private void t() {
        this.playSound(this.p(), this.getSoundVolume(), this.ep());
        Vec3D vec3d = this.i(new Vec3D(0.0D, -1.0D, 0.0D)).add(this.locX(), this.locY(), this.locZ());

        for (int i = 0; i < 30; ++i) {
            Vec3D vec3d1 = this.i(new Vec3D((double) this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double) this.random.nextFloat() * 0.6D - 0.3D));
            Vec3D vec3d2 = vec3d1.a(0.3D + (double) (this.random.nextFloat() * 2.0F));

            ((WorldServer) this.level).a(this.n(), vec3d.x, vec3d.y + 0.5D, vec3d.z, 0, vec3d2.x, vec3d2.y, vec3d2.z, 0.10000000149011612D);
        }

    }

    protected ParticleParam n() {
        return Particles.SQUID_INK;
    }

    @Override
    public void g(Vec3D vec3d) {
        this.move(EnumMoveType.SELF, this.getMot());
    }

    public static boolean b(EntityTypes<EntitySquid> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() > 45 && blockposition.getY() < generatoraccess.getSeaLevel();
    }

    @Override
    public void a(byte b0) {
        if (b0 == 19) {
            this.tentacleMovement = 0.0F;
        } else {
            super.a(b0);
        }

    }

    public void a(float f, float f1, float f2) {
        this.tx = f;
        this.ty = f1;
        this.tz = f2;
    }

    public boolean fx() {
        return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
    }

    private class PathfinderGoalSquid extends PathfinderGoal {

        private final EntitySquid squid;

        public PathfinderGoalSquid(EntitySquid entitysquid) {
            this.squid = entitysquid;
        }

        @Override
        public boolean a() {
            return true;
        }

        @Override
        public void e() {
            int i = this.squid.dK();

            if (i > 100) {
                this.squid.a(0.0F, 0.0F, 0.0F);
            } else if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.wasTouchingWater || !this.squid.fx()) {
                float f = this.squid.getRandom().nextFloat() * 6.2831855F;
                float f1 = MathHelper.cos(f) * 0.2F;
                float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
                float f3 = MathHelper.sin(f) * 0.2F;

                this.squid.a(f1, f2, f3);
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
        public boolean a() {
            EntityLiving entityliving = EntitySquid.this.getLastDamager();

            return EntitySquid.this.isInWater() && entityliving != null ? EntitySquid.this.f((Entity) entityliving) < 100.0D : false;
        }

        @Override
        public void c() {
            this.fleeTicks = 0;
        }

        @Override
        public void e() {
            ++this.fleeTicks;
            EntityLiving entityliving = EntitySquid.this.getLastDamager();

            if (entityliving != null) {
                Vec3D vec3d = new Vec3D(EntitySquid.this.locX() - entityliving.locX(), EntitySquid.this.locY() - entityliving.locY(), EntitySquid.this.locZ() - entityliving.locZ());
                IBlockData iblockdata = EntitySquid.this.level.getType(new BlockPosition(EntitySquid.this.locX() + vec3d.x, EntitySquid.this.locY() + vec3d.y, EntitySquid.this.locZ() + vec3d.z));
                Fluid fluid = EntitySquid.this.level.getFluid(new BlockPosition(EntitySquid.this.locX() + vec3d.x, EntitySquid.this.locY() + vec3d.y, EntitySquid.this.locZ() + vec3d.z));

                if (fluid.a((Tag) TagsFluid.WATER) || iblockdata.isAir()) {
                    double d0 = vec3d.f();

                    if (d0 > 0.0D) {
                        vec3d.d();
                        float f = 3.0F;

                        if (d0 > 5.0D) {
                            f = (float) ((double) f - (d0 - 5.0D) / 5.0D);
                        }

                        if (f > 0.0F) {
                            vec3d = vec3d.a((double) f);
                        }
                    }

                    if (iblockdata.isAir()) {
                        vec3d = vec3d.a(0.0D, vec3d.y, 0.0D);
                    }

                    EntitySquid.this.a((float) vec3d.x / 20.0F, (float) vec3d.y / 20.0F, (float) vec3d.z / 20.0F);
                }

                if (this.fleeTicks % 10 == 5) {
                    EntitySquid.this.level.addParticle(Particles.BUBBLE, EntitySquid.this.locX(), EntitySquid.this.locY(), EntitySquid.this.locZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }
}
