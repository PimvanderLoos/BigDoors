package net.minecraft.server;

import java.util.Random;

public class EntitySquid extends EntityWaterAnimal {

    public float b;
    public float c;
    public float d;
    public float bv;
    public float bw;
    public float bx;
    public float by;
    public float bz;
    private float bA;
    private float bB;
    private float bC;
    private float bD;
    private float bE;
    private float bF;

    public EntitySquid(EntityTypes<? extends EntitySquid> entitytypes, World world) {
        super(entitytypes, world);
        this.random.setSeed((long) this.getId());
        this.bB = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntitySquid.PathfinderGoalSquid(this));
        this.goalSelector.a(1, new EntitySquid.a());
    }

    public static AttributeProvider.Builder m() {
        return EntityInsentient.p().a(GenericAttributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_SQUID_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_SQUID_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_SQUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        this.c = this.b;
        this.bv = this.d;
        this.bx = this.bw;
        this.bz = this.by;
        this.bw += this.bB;
        if ((double) this.bw > 6.283185307179586D) {
            if (this.world.isClientSide) {
                this.bw = 6.2831855F;
            } else {
                this.bw = (float) ((double) this.bw - 6.283185307179586D);
                if (this.random.nextInt(10) == 0) {
                    this.bB = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
                }

                this.world.broadcastEntityEffect(this, (byte) 19);
            }
        }

        if (this.aD()) {
            if (this.bw < 3.1415927F) {
                float f = this.bw / 3.1415927F;

                this.by = MathHelper.sin(f * f * 3.1415927F) * 3.1415927F * 0.25F;
                if ((double) f > 0.75D) {
                    this.bA = 1.0F;
                    this.bC = 1.0F;
                } else {
                    this.bC *= 0.8F;
                }
            } else {
                this.by = 0.0F;
                this.bA *= 0.9F;
                this.bC *= 0.99F;
            }

            if (!this.world.isClientSide) {
                this.setMot((double) (this.bD * this.bA), (double) (this.bE * this.bA), (double) (this.bF * this.bA));
            }

            Vec3D vec3d = this.getMot();
            float f1 = MathHelper.sqrt(b(vec3d));

            this.aH += (-((float) MathHelper.d(vec3d.x, vec3d.z)) * 57.295776F - this.aH) * 0.1F;
            this.yaw = this.aH;
            this.d = (float) ((double) this.d + 3.141592653589793D * (double) this.bC * 1.5D);
            this.b += (-((float) MathHelper.d((double) f1, vec3d.y)) * 57.295776F - this.b) * 0.1F;
        } else {
            this.by = MathHelper.e(MathHelper.sin(this.bw)) * 3.1415927F * 0.25F;
            if (!this.world.isClientSide) {
                double d0 = this.getMot().y;

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d0 = 0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
                } else if (!this.isNoGravity()) {
                    d0 -= 0.08D;
                }

                this.setMot(0.0D, d0 * 0.9800000190734863D, 0.0D);
            }

            this.b = (float) ((double) this.b + (double) (-90.0F - this.b) * 0.02D);
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (super.damageEntity(damagesource, f) && this.getLastDamager() != null) {
            this.eM();
            return true;
        } else {
            return false;
        }
    }

    private Vec3D g(Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.a(this.c * 0.017453292F);

        vec3d1 = vec3d1.b(-this.aI * 0.017453292F);
        return vec3d1;
    }

    private void eM() {
        this.playSound(SoundEffects.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.dG());
        Vec3D vec3d = this.g(new Vec3D(0.0D, -1.0D, 0.0D)).add(this.locX(), this.locY(), this.locZ());

        for (int i = 0; i < 30; ++i) {
            Vec3D vec3d1 = this.g(new Vec3D((double) this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double) this.random.nextFloat() * 0.6D - 0.3D));
            Vec3D vec3d2 = vec3d1.a(0.3D + (double) (this.random.nextFloat() * 2.0F));

            ((WorldServer) this.world).a(Particles.SQUID_INK, vec3d.x, vec3d.y + 0.5D, vec3d.z, 0, vec3d2.x, vec3d2.y, vec3d2.z, 0.10000000149011612D);
        }

    }

    @Override
    public void f(Vec3D vec3d) {
        this.move(EnumMoveType.SELF, this.getMot());
    }

    public static boolean b(EntityTypes<EntitySquid> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() > 45 && blockposition.getY() < generatoraccess.getSeaLevel();
    }

    public void a(float f, float f1, float f2) {
        this.bD = f;
        this.bE = f1;
        this.bF = f2;
    }

    public boolean eL() {
        return this.bD != 0.0F || this.bE != 0.0F || this.bF != 0.0F;
    }

    class a extends PathfinderGoal {

        private int b;

        private a() {}

        @Override
        public boolean a() {
            EntityLiving entityliving = EntitySquid.this.getLastDamager();

            return EntitySquid.this.isInWater() && entityliving != null ? EntitySquid.this.h((Entity) entityliving) < 100.0D : false;
        }

        @Override
        public void c() {
            this.b = 0;
        }

        @Override
        public void e() {
            ++this.b;
            EntityLiving entityliving = EntitySquid.this.getLastDamager();

            if (entityliving != null) {
                Vec3D vec3d = new Vec3D(EntitySquid.this.locX() - entityliving.locX(), EntitySquid.this.locY() - entityliving.locY(), EntitySquid.this.locZ() - entityliving.locZ());
                IBlockData iblockdata = EntitySquid.this.world.getType(new BlockPosition(EntitySquid.this.locX() + vec3d.x, EntitySquid.this.locY() + vec3d.y, EntitySquid.this.locZ() + vec3d.z));
                Fluid fluid = EntitySquid.this.world.getFluid(new BlockPosition(EntitySquid.this.locX() + vec3d.x, EntitySquid.this.locY() + vec3d.y, EntitySquid.this.locZ() + vec3d.z));

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

                if (this.b % 10 == 5) {
                    EntitySquid.this.world.addParticle(Particles.BUBBLE, EntitySquid.this.locX(), EntitySquid.this.locY(), EntitySquid.this.locZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }

    class PathfinderGoalSquid extends PathfinderGoal {

        private final EntitySquid b;

        public PathfinderGoalSquid(EntitySquid entitysquid) {
            this.b = entitysquid;
        }

        @Override
        public boolean a() {
            return true;
        }

        @Override
        public void e() {
            int i = this.b.dc();

            if (i > 100) {
                this.b.a(0.0F, 0.0F, 0.0F);
            } else if (this.b.getRandom().nextInt(50) == 0 || !this.b.inWater || !this.b.eL()) {
                float f = this.b.getRandom().nextFloat() * 6.2831855F;
                float f1 = MathHelper.cos(f) * 0.2F;
                float f2 = -0.1F + this.b.getRandom().nextFloat() * 0.2F;
                float f3 = MathHelper.sin(f) * 0.2F;

                this.b.a(f1, f2, f3);
            }

        }
    }
}
