package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySquid extends EntityWaterAnimal {

    public float a;
    public float b;
    public float c;
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

    public EntitySquid(World world) {
        super(world);
        this.setSize(0.8F, 0.8F);
        this.random.setSeed((long) (1 + this.getId()));
        this.bB = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySquid.class);
    }

    protected void r() {
        this.goalSelector.a(0, new EntitySquid.PathfinderGoalSquid(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
    }

    public float getHeadHeight() {
        return this.length * 0.5F;
    }

    protected SoundEffect F() {
        return SoundEffects.hF;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.hH;
    }

    protected SoundEffect cf() {
        return SoundEffects.hG;
    }

    protected float cq() {
        return 0.4F;
    }

    protected boolean playStepSound() {
        return false;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ak;
    }

    public void n() {
        super.n();
        this.b = this.a;
        this.bv = this.c;
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

        if (this.inWater) {
            float f;

            if (this.bw < 3.1415927F) {
                f = this.bw / 3.1415927F;
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
                this.motX = (double) (this.bD * this.bA);
                this.motY = (double) (this.bE * this.bA);
                this.motZ = (double) (this.bF * this.bA);
            }

            f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            this.aN += (-((float) MathHelper.c(this.motX, this.motZ)) * 57.295776F - this.aN) * 0.1F;
            this.yaw = this.aN;
            this.c = (float) ((double) this.c + 3.141592653589793D * (double) this.bC * 1.5D);
            this.a += (-((float) MathHelper.c((double) f, this.motY)) * 57.295776F - this.a) * 0.1F;
        } else {
            this.by = MathHelper.e(MathHelper.sin(this.bw)) * 3.1415927F * 0.25F;
            if (!this.world.isClientSide) {
                this.motX = 0.0D;
                this.motZ = 0.0D;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    this.motY += 0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motY;
                } else if (!this.isNoGravity()) {
                    this.motY -= 0.08D;
                }

                this.motY *= 0.9800000190734863D;
            }

            this.a = (float) ((double) this.a + (double) (-90.0F - this.a) * 0.02D);
        }

    }

    public void a(float f, float f1, float f2) {
        this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
    }

    public boolean P() {
        return this.locY > 45.0D && this.locY < (double) this.world.getSeaLevel() && super.P();
    }

    public void b(float f, float f1, float f2) {
        this.bD = f;
        this.bE = f1;
        this.bF = f2;
    }

    public boolean p() {
        return this.bD != 0.0F || this.bE != 0.0F || this.bF != 0.0F;
    }

    static class PathfinderGoalSquid extends PathfinderGoal {

        private final EntitySquid a;

        public PathfinderGoalSquid(EntitySquid entitysquid) {
            this.a = entitysquid;
        }

        public boolean a() {
            return true;
        }

        public void e() {
            int i = this.a.bW();

            if (i > 100) {
                this.a.b(0.0F, 0.0F, 0.0F);
            } else if (this.a.getRandom().nextInt(50) == 0 || !this.a.inWater || !this.a.p()) {
                float f = this.a.getRandom().nextFloat() * 6.2831855F;
                float f1 = MathHelper.cos(f) * 0.2F;
                float f2 = -0.1F + this.a.getRandom().nextFloat() * 0.2F;
                float f3 = MathHelper.sin(f) * 0.2F;

                this.a.b(f1, f2, f3);
            }

        }
    }
}
