package net.minecraft.server;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityGuardian extends EntityMonster {

    private static final DataWatcherObject<Boolean> bF = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bG = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.b);
    protected float a;
    protected float b;
    protected float c;
    protected float bC;
    protected float bD;
    private EntityLiving bH;
    private int bI;
    private boolean bJ;
    public PathfinderGoalRandomStroll goalRandomStroll;

    protected EntityGuardian(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.b_ = 10;
        this.setSize(0.85F, 0.85F);
        this.moveController = new EntityGuardian.ControllerMoveGuardian(this);
        this.a = this.random.nextFloat();
        this.b = this.a;
    }

    public EntityGuardian(World world) {
        this(EntityTypes.GUARDIAN, world);
    }

    protected void n() {
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
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, new EntityGuardian.EntitySelectorGuardianTargetHumanSquid(this)));
    }

    public void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(16.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
    }

    protected NavigationAbstract b(World world) {
        return new NavigationGuardian(this, world);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityGuardian.bF, false);
        this.datawatcher.register(EntityGuardian.bG, 0);
    }

    public boolean ca() {
        return true;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.e;
    }

    public boolean dB() {
        return (Boolean) this.datawatcher.get(EntityGuardian.bF);
    }

    private void a(boolean flag) {
        this.datawatcher.set(EntityGuardian.bF, flag);
    }

    public int l() {
        return 80;
    }

    private void a(int i) {
        this.datawatcher.set(EntityGuardian.bG, i);
    }

    public boolean dC() {
        return (Integer) this.datawatcher.get(EntityGuardian.bG) != 0;
    }

    @Nullable
    public EntityLiving dD() {
        if (!this.dC()) {
            return null;
        } else if (this.world.isClientSide) {
            if (this.bH != null) {
                return this.bH;
            } else {
                Entity entity = this.world.getEntity((Integer) this.datawatcher.get(EntityGuardian.bG));

                if (entity instanceof EntityLiving) {
                    this.bH = (EntityLiving) entity;
                    return this.bH;
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
        if (EntityGuardian.bG.equals(datawatcherobject)) {
            this.bI = 0;
            this.bH = null;
        }

    }

    public int z() {
        return 160;
    }

    protected SoundEffect D() {
        return this.aq() ? SoundEffects.ENTITY_GUARDIAN_AMBIENT : SoundEffects.ENTITY_GUARDIAN_AMBIENT_LAND;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.aq() ? SoundEffects.ENTITY_GUARDIAN_HURT : SoundEffects.ENTITY_GUARDIAN_HURT_LAND;
    }

    protected SoundEffect cs() {
        return this.aq() ? SoundEffects.ENTITY_GUARDIAN_DEATH : SoundEffects.ENTITY_GUARDIAN_DEATH_LAND;
    }

    protected boolean playStepSound() {
        return false;
    }

    public float getHeadHeight() {
        return this.length * 0.5F;
    }

    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getFluid(blockposition).a(TagsFluid.WATER) ? 10.0F + iworldreader.A(blockposition) - 0.5F : super.a(blockposition, iworldreader);
    }

    public void movementTick() {
        if (this.world.isClientSide) {
            this.b = this.a;
            if (!this.isInWater()) {
                this.c = 2.0F;
                if (this.motY > 0.0D && this.bJ && !this.isSilent()) {
                    this.world.a(this.locX, this.locY, this.locZ, this.dA(), this.bV(), 1.0F, 1.0F, false);
                }

                this.bJ = this.motY < 0.0D && this.world.q((new BlockPosition(this)).down());
            } else if (this.dB()) {
                if (this.c < 0.5F) {
                    this.c = 4.0F;
                } else {
                    this.c += (0.5F - this.c) * 0.1F;
                }
            } else {
                this.c += (0.125F - this.c) * 0.2F;
            }

            this.a += this.c;
            this.bD = this.bC;
            if (!this.aq()) {
                this.bC = this.random.nextFloat();
            } else if (this.dB()) {
                this.bC += (0.0F - this.bC) * 0.25F;
            } else {
                this.bC += (1.0F - this.bC) * 0.06F;
            }

            if (this.dB() && this.isInWater()) {
                Vec3D vec3d = this.f(0.0F);

                for (int i = 0; i < 2; ++i) {
                    this.world.addParticle(Particles.e, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width - vec3d.x * 1.5D, this.locY + this.random.nextDouble() * (double) this.length - vec3d.y * 1.5D, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.dC()) {
                if (this.bI < this.l()) {
                    ++this.bI;
                }

                EntityLiving entityliving = this.dD();

                if (entityliving != null) {
                    this.getControllerLook().a(entityliving, 90.0F, 90.0F);
                    this.getControllerLook().a();
                    double d0 = (double) this.w(0.0F);
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
                        this.world.addParticle(Particles.e, this.locX + d1 * d5, this.locY + d2 * d5 + (double) this.getHeadHeight(), this.locZ + d3 * d5, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }

        if (this.aq()) {
            this.setAirTicks(300);
        } else if (this.onGround) {
            this.motY += 0.5D;
            this.motX += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.motZ += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.yaw = this.random.nextFloat() * 360.0F;
            this.onGround = false;
            this.impulse = true;
        }

        if (this.dC()) {
            this.yaw = this.aS;
        }

        super.movementTick();
    }

    protected SoundEffect dA() {
        return SoundEffects.ENTITY_GUARDIAN_FLOP;
    }

    public float w(float f) {
        return ((float) this.bI + f) / (float) this.l();
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.D;
    }

    protected boolean K_() {
        return true;
    }

    public boolean a(IWorldReader iworldreader) {
        return iworldreader.a_(this, this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox());
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return (this.random.nextInt(20) == 0 || !generatoraccess.z(new BlockPosition(this))) && super.a(generatoraccess, flag);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.dB() && !damagesource.isMagic() && damagesource.j() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.j();

            if (!damagesource.isExplosion()) {
                entityliving.damageEntity(DamageSource.a(this), 2.0F);
            }
        }

        if (this.goalRandomStroll != null) {
            this.goalRandomStroll.i();
        }

        return super.damageEntity(damagesource, f);
    }

    public int K() {
        return 180;
    }

    public void a(float f, float f1, float f2) {
        if (this.cP() && this.isInWater()) {
            this.a(f, f1, f2, 0.1F);
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.8999999761581421D;
            this.motY *= 0.8999999761581421D;
            this.motZ *= 0.8999999761581421D;
            if (!this.dB() && this.getGoalTarget() == null) {
                this.motY -= 0.005D;
            }
        } else {
            super.a(f, f1, f2);
        }

    }

    static class ControllerMoveGuardian extends ControllerMove {

        private final EntityGuardian i;

        public ControllerMoveGuardian(EntityGuardian entityguardian) {
            super(entityguardian);
            this.i = entityguardian;
        }

        public void a() {
            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().p()) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aQ = this.i.yaw;
                float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                this.i.o(this.i.cK() + (f1 - this.i.cK()) * 0.125F);
                double d4 = Math.sin((double) (this.i.ticksLived + this.i.getId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (this.i.yaw * 0.017453292F));
                double d6 = Math.sin((double) (this.i.yaw * 0.017453292F));

                this.i.motX += d4 * d5;
                this.i.motZ += d4 * d6;
                d4 = Math.sin((double) (this.i.ticksLived + this.i.getId()) * 0.75D) * 0.05D;
                this.i.motY += d4 * (d6 + d5) * 0.25D;
                this.i.motY += (double) this.i.cK() * d1 * 0.1D;
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
                this.i.o(0.0F);
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
            this.a.getNavigation().q();
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

            this.a.getNavigation().q();
            this.a.getControllerLook().a(entityliving, 90.0F, 90.0F);
            if (!this.a.hasLineOfSight(entityliving)) {
                this.a.setGoalTarget((EntityLiving) null);
            } else {
                ++this.b;
                if (this.b == 0) {
                    this.a.a(this.a.getGoalTarget().getId());
                    this.a.world.broadcastEntityEffect(this.a, (byte) 21);
                } else if (this.b >= this.a.l()) {
                    float f = 1.0F;

                    if (this.a.world.getDifficulty() == EnumDifficulty.HARD) {
                        f += 2.0F;
                    }

                    if (this.c) {
                        f += 2.0F;
                    }

                    entityliving.damageEntity(DamageSource.c(this.a, this.a), f);
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

        public boolean test(@Nullable EntityLiving entityliving) {
            return (entityliving instanceof EntityHuman || entityliving instanceof EntitySquid) && entityliving.h(this.a) > 9.0D;
        }
    }
}
