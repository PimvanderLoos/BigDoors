package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class EntityGuardian extends EntityMonster {

    private static final DataWatcherObject<Boolean> bz = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> bA = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.b);
    protected float a;
    protected float b;
    protected float c;
    protected float bw;
    protected float bx;
    private EntityLiving bB;
    private int bC;
    private boolean bD;
    public PathfinderGoalRandomStroll goalRandomStroll;

    public EntityGuardian(World world) {
        super(world);
        this.b_ = 10;
        this.setSize(0.85F, 0.85F);
        this.moveController = new EntityGuardian.ControllerMoveGuardian(this);
        this.a = this.random.nextFloat();
        this.b = this.a;
    }

    protected void r() {
        PathfinderGoalMoveTowardsRestriction pathfindergoalmovetowardsrestriction = new PathfinderGoalMoveTowardsRestriction(this, 1.0D);

        this.goalRandomStroll = new PathfinderGoalRandomStroll(this, 1.0D, 80);
        this.goalSelector.a(4, new EntityGuardian.PathfinderGoalGuardianAttack(this));
        this.goalSelector.a(5, pathfindergoalmovetowardsrestriction);
        this.goalSelector.a(7, this.goalRandomStroll);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityGuardian.class, 12.0F, 0.01F));
        this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
        this.goalRandomStroll.a(3);
        pathfindergoalmovetowardsrestriction.a(3);
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class, 10, true, false, new EntityGuardian.EntitySelectorGuardianTargetHumanSquid(this)));
    }

    public void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(16.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
    }

    public static void c(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityGuardian.class);
    }

    protected NavigationAbstract b(World world) {
        return new NavigationGuardian(this, world);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityGuardian.bz, Boolean.valueOf(false));
        this.datawatcher.register(EntityGuardian.bA, Integer.valueOf(0));
    }

    public boolean dk() {
        return ((Boolean) this.datawatcher.get(EntityGuardian.bz)).booleanValue();
    }

    private void a(boolean flag) {
        this.datawatcher.set(EntityGuardian.bz, Boolean.valueOf(flag));
    }

    public int o() {
        return 80;
    }

    private void a(int i) {
        this.datawatcher.set(EntityGuardian.bA, Integer.valueOf(i));
    }

    public boolean dl() {
        return ((Integer) this.datawatcher.get(EntityGuardian.bA)).intValue() != 0;
    }

    @Nullable
    public EntityLiving dm() {
        if (!this.dl()) {
            return null;
        } else if (this.world.isClientSide) {
            if (this.bB != null) {
                return this.bB;
            } else {
                Entity entity = this.world.getEntity(((Integer) this.datawatcher.get(EntityGuardian.bA)).intValue());

                if (entity instanceof EntityLiving) {
                    this.bB = (EntityLiving) entity;
                    return this.bB;
                } else {
                    return null;
                }
            }
        } else {
            return this.getGoalTarget();
        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityGuardian.bA.equals(datawatcherobject)) {
            this.bC = 0;
            this.bB = null;
        }

    }

    public int C() {
        return 160;
    }

    protected SoundEffect G() {
        return this.isInWater() ? SoundEffects.cq : SoundEffects.cr;
    }

    protected SoundEffect bW() {
        return this.isInWater() ? SoundEffects.cw : SoundEffects.cx;
    }

    protected SoundEffect bX() {
        return this.isInWater() ? SoundEffects.ct : SoundEffects.cu;
    }

    protected boolean playStepSound() {
        return false;
    }

    public float getHeadHeight() {
        return this.length * 0.5F;
    }

    public float a(BlockPosition blockposition) {
        return this.world.getType(blockposition).getMaterial() == Material.WATER ? 10.0F + this.world.n(blockposition) - 0.5F : super.a(blockposition);
    }

    public void n() {
        if (this.world.isClientSide) {
            this.b = this.a;
            if (!this.isInWater()) {
                this.c = 2.0F;
                if (this.motY > 0.0D && this.bD && !this.isSilent()) {
                    this.world.a(this.locX, this.locY, this.locZ, this.dj(), this.bC(), 1.0F, 1.0F, false);
                }

                this.bD = this.motY < 0.0D && this.world.d((new BlockPosition(this)).down(), false);
            } else if (this.dk()) {
                if (this.c < 0.5F) {
                    this.c = 4.0F;
                } else {
                    this.c += (0.5F - this.c) * 0.1F;
                }
            } else {
                this.c += (0.125F - this.c) * 0.2F;
            }

            this.a += this.c;
            this.bx = this.bw;
            if (!this.isInWater()) {
                this.bw = this.random.nextFloat();
            } else if (this.dk()) {
                this.bw += (0.0F - this.bw) * 0.25F;
            } else {
                this.bw += (1.0F - this.bw) * 0.06F;
            }

            if (this.dk() && this.isInWater()) {
                Vec3D vec3d = this.f(0.0F);

                for (int i = 0; i < 2; ++i) {
                    this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width - vec3d.x * 1.5D, this.locY + this.random.nextDouble() * (double) this.length - vec3d.y * 1.5D, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (this.dl()) {
                if (this.bC < this.o()) {
                    ++this.bC;
                }

                EntityLiving entityliving = this.dm();

                if (entityliving != null) {
                    this.getControllerLook().a(entityliving, 90.0F, 90.0F);
                    this.getControllerLook().a();
                    double d0 = (double) this.s(0.0F);
                    double d1 = entityliving.locX - this.locX;
                    double d2 = entityliving.locY + (double) (entityliving.length * 0.5F) - (this.locY + (double) this.getHeadHeight());
                    double d3 = entityliving.locZ - this.locZ;
                    double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

                    d1 /= d4;
                    d2 /= d4;
                    d3 /= d4;
                    double d5 = this.random.nextDouble();

                    while (d5 < d4) {
                        d5 += 1.8D - d0 + this.random.nextDouble() * (1.7D - d0);
                        this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX + d1 * d5, this.locY + d2 * d5 + (double) this.getHeadHeight(), this.locZ + d3 * d5, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
            }
        }

        if (this.inWater) {
            this.setAirTicks(300);
        } else if (this.onGround) {
            this.motY += 0.5D;
            this.motX += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.motZ += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.yaw = this.random.nextFloat() * 360.0F;
            this.onGround = false;
            this.impulse = true;
        }

        if (this.dl()) {
            this.yaw = this.aP;
        }

        super.n();
    }

    protected SoundEffect dj() {
        return SoundEffects.cv;
    }

    public float s(float f) {
        return ((float) this.bC + f) / (float) this.o();
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.x;
    }

    protected boolean r_() {
        return true;
    }

    public boolean canSpawn() {
        return this.world.a(this.getBoundingBox(), (Entity) this) && this.world.getCubes(this, this.getBoundingBox()).isEmpty();
    }

    public boolean cM() {
        return (this.random.nextInt(20) == 0 || !this.world.i(new BlockPosition(this))) && super.cM();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.dk() && !damagesource.isMagic() && damagesource.i() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.i();

            if (!damagesource.isExplosion()) {
                entityliving.damageEntity(DamageSource.a(this), 2.0F);
            }
        }

        if (this.goalRandomStroll != null) {
            this.goalRandomStroll.i();
        }

        return super.damageEntity(damagesource, f);
    }

    public int N() {
        return 180;
    }

    public void g(float f, float f1) {
        if (this.cu() && this.isInWater()) {
            this.a(f, f1, 0.1F);
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.8999999761581421D;
            this.motY *= 0.8999999761581421D;
            this.motZ *= 0.8999999761581421D;
            if (!this.dk() && this.getGoalTarget() == null) {
                this.motY -= 0.005D;
            }
        } else {
            super.g(f, f1);
        }

    }

    static class ControllerMoveGuardian extends ControllerMove {

        private final EntityGuardian i;

        public ControllerMoveGuardian(EntityGuardian entityguardian) {
            super(entityguardian);
            this.i = entityguardian;
        }

        public void c() {
            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().n()) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aN = this.i.yaw;
                float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                this.i.l(this.i.cq() + (f1 - this.i.cq()) * 0.125F);
                double d4 = Math.sin((double) (this.i.ticksLived + this.i.getId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (this.i.yaw * 0.017453292F));
                double d6 = Math.sin((double) (this.i.yaw * 0.017453292F));

                this.i.motX += d4 * d5;
                this.i.motZ += d4 * d6;
                d4 = Math.sin((double) (this.i.ticksLived + this.i.getId()) * 0.75D) * 0.05D;
                this.i.motY += d4 * (d6 + d5) * 0.25D;
                this.i.motY += (double) this.i.cq() * d1 * 0.1D;
                ControllerLook controllerlook = this.i.getControllerLook();
                double d7 = this.i.locX + d0 / d3 * 2.0D;
                double d8 = (double) this.i.getHeadHeight() + this.i.locY + d1 / d3;
                double d9 = this.i.locZ + d2 / d3 * 2.0D;
                double d10 = controllerlook.e();
                double d11 = controllerlook.f();
                double d12 = controllerlook.g();

                if (!controllerlook.b()) {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                this.i.getControllerLook().a(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
                this.i.a(true);
            } else {
                this.i.l(0.0F);
                this.i.a(false);
            }
        }
    }

    static class PathfinderGoalGuardianAttack extends PathfinderGoal {

        private final EntityGuardian a;
        private int b;
        private final boolean c;

        public PathfinderGoalGuardianAttack(EntityGuardian entityguardian) {
            this.a = entityguardian;
            this.c = entityguardian instanceof EntityGuardianElder;
            this.a(3);
        }

        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving != null && entityliving.isAlive();
        }

        public boolean b() {
            return super.b() && (this.c || this.a.h(this.a.getGoalTarget()) > 9.0D);
        }

        public void c() {
            this.b = -10;
            this.a.getNavigation().o();
            this.a.getControllerLook().a(this.a.getGoalTarget(), 90.0F, 90.0F);
            this.a.impulse = true;
        }

        public void d() {
            this.a.a(0);
            this.a.setGoalTarget((EntityLiving) null);
            this.a.goalRandomStroll.i();
        }

        public void e() {
            EntityLiving entityliving = this.a.getGoalTarget();

            this.a.getNavigation().o();
            this.a.getControllerLook().a(entityliving, 90.0F, 90.0F);
            if (!this.a.hasLineOfSight(entityliving)) {
                this.a.setGoalTarget((EntityLiving) null);
            } else {
                ++this.b;
                if (this.b == 0) {
                    this.a.a(this.a.getGoalTarget().getId());
                    this.a.world.broadcastEntityEffect(this.a, (byte) 21);
                } else if (this.b >= this.a.o()) {
                    float f = 1.0F;

                    if (this.a.world.getDifficulty() == EnumDifficulty.HARD) {
                        f += 2.0F;
                    }

                    if (this.c) {
                        f += 2.0F;
                    }

                    entityliving.damageEntity(DamageSource.b(this.a, this.a), f);
                    entityliving.damageEntity(DamageSource.mobAttack(this.a), (float) this.a.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue());
                    this.a.setGoalTarget((EntityLiving) null);
                }

                super.e();
            }
        }
    }

    static class EntitySelectorGuardianTargetHumanSquid implements Predicate<EntityLiving> {

        private final EntityGuardian a;

        public EntitySelectorGuardianTargetHumanSquid(EntityGuardian entityguardian) {
            this.a = entityguardian;
        }

        public boolean a(@Nullable EntityLiving entityliving) {
            return (entityliving instanceof EntityHuman || entityliving instanceof EntitySquid) && entityliving.h(this.a) > 9.0D;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EntityLiving) object);
        }
    }
}
