package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityBlaze extends EntityMonster {

    private float a = 0.5F;
    private int b;
    private static final DataWatcherObject<Byte> c = DataWatcher.a(EntityBlaze.class, DataWatcherRegistry.a);

    public EntityBlaze(World world) {
        super(world);
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.LAVA, 8.0F);
        this.a(PathType.DANGER_FIRE, 0.0F);
        this.a(PathType.DAMAGE_FIRE, 0.0F);
        this.fireProof = true;
        this.b_ = 10;
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityBlaze.class);
    }

    protected void r() {
        this.goalSelector.a(4, new EntityBlaze.PathfinderGoalBlazeFireball(this));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(48.0D);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityBlaze.c, Byte.valueOf((byte) 0));
    }

    protected SoundEffect F() {
        return SoundEffects.C;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.F;
    }

    protected SoundEffect cf() {
        return SoundEffects.E;
    }

    public float aw() {
        return 1.0F;
    }

    public void n() {
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        if (this.world.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.world.a(this.locX + 0.5D, this.locY + 0.5D, this.locZ + 0.5D, SoundEffects.D, this.bK(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(EnumParticle.SMOKE_LARGE, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }

        super.n();
    }

    protected void M() {
        if (this.an()) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

        --this.b;
        if (this.b <= 0) {
            this.b = 100;
            this.a = 0.5F + (float) this.random.nextGaussian() * 3.0F;
        }

        EntityLiving entityliving = this.getGoalTarget();

        if (entityliving != null && entityliving.locY + (double) entityliving.getHeadHeight() > this.locY + (double) this.getHeadHeight() + (double) this.a) {
            this.motY += (0.30000001192092896D - this.motY) * 0.30000001192092896D;
            this.impulse = true;
        }

        super.M();
    }

    public void e(float f, float f1) {}

    public boolean isBurning() {
        return this.p();
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.q;
    }

    public boolean p() {
        return (((Byte) this.datawatcher.get(EntityBlaze.c)).byteValue() & 1) != 0;
    }

    public void a(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityBlaze.c)).byteValue();

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.datawatcher.set(EntityBlaze.c, Byte.valueOf(b0));
    }

    protected boolean s_() {
        return true;
    }

    static class PathfinderGoalBlazeFireball extends PathfinderGoal {

        private final EntityBlaze a;
        private int b;
        private int c;

        public PathfinderGoalBlazeFireball(EntityBlaze entityblaze) {
            this.a = entityblaze;
            this.a(3);
        }

        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving != null && entityliving.isAlive();
        }

        public void c() {
            this.b = 0;
        }

        public void d() {
            this.a.a(false);
        }

        public void e() {
            --this.c;
            EntityLiving entityliving = this.a.getGoalTarget();
            double d0 = this.a.h(entityliving);

            if (d0 < 4.0D) {
                if (this.c <= 0) {
                    this.c = 20;
                    this.a.B(entityliving);
                }

                this.a.getControllerMove().a(entityliving.locX, entityliving.locY, entityliving.locZ, 1.0D);
            } else if (d0 < this.f() * this.f()) {
                double d1 = entityliving.locX - this.a.locX;
                double d2 = entityliving.getBoundingBox().b + (double) (entityliving.length / 2.0F) - (this.a.locY + (double) (this.a.length / 2.0F));
                double d3 = entityliving.locZ - this.a.locZ;

                if (this.c <= 0) {
                    ++this.b;
                    if (this.b == 1) {
                        this.c = 60;
                        this.a.a(true);
                    } else if (this.b <= 4) {
                        this.c = 6;
                    } else {
                        this.c = 100;
                        this.b = 0;
                        this.a.a(false);
                    }

                    if (this.b > 1) {
                        float f = MathHelper.c(MathHelper.sqrt(d0)) * 0.5F;

                        this.a.world.a((EntityHuman) null, 1018, new BlockPosition((int) this.a.locX, (int) this.a.locY, (int) this.a.locZ), 0);

                        for (int i = 0; i < 1; ++i) {
                            EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.a.world, this.a, d1 + this.a.getRandom().nextGaussian() * (double) f, d2, d3 + this.a.getRandom().nextGaussian() * (double) f);

                            entitysmallfireball.locY = this.a.locY + (double) (this.a.length / 2.0F) + 0.5D;
                            this.a.world.addEntity(entitysmallfireball);
                        }
                    }
                }

                this.a.getControllerLook().a(entityliving, 10.0F, 10.0F);
            } else {
                this.a.getNavigation().p();
                this.a.getControllerMove().a(entityliving.locX, entityliving.locY, entityliving.locZ, 1.0D);
            }

            super.e();
        }

        private double f() {
            AttributeInstance attributeinstance = this.a.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);

            return attributeinstance == null ? 16.0D : attributeinstance.getValue();
        }
    }
}
