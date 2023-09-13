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
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityGuardian extends EntityMonster {

    private static final DataWatcherObject<Boolean> b = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.b);
    private float bo;
    private float bp;
    private float bq;
    private float br;
    private float bs;
    private EntityLiving bt;
    private int bu;
    private boolean bv;
    public PathfinderGoalRandomStroll goalRandomStroll;

    public EntityGuardian(EntityTypes<? extends EntityGuardian> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 10;
        this.a(PathType.WATER, 0.0F);
        this.moveController = new EntityGuardian.ControllerMoveGuardian(this);
        this.bo = this.random.nextFloat();
        this.bp = this.bo;
    }

    @Override
    protected void initPathfinder() {
        PathfinderGoalMoveTowardsRestriction pathfindergoalmovetowardsrestriction = new PathfinderGoalMoveTowardsRestriction(this, 1.0D);

        this.goalRandomStroll = new PathfinderGoalRandomStroll(this, 1.0D, 80);
        this.goalSelector.a(4, new EntityGuardian.PathfinderGoalGuardianAttack(this));
        this.goalSelector.a(5, pathfindergoalmovetowardsrestriction);
        this.goalSelector.a(7, this.goalRandomStroll);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityGuardian.class, 12.0F, 0.01F));
        this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
        this.goalRandomStroll.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        pathfindergoalmovetowardsrestriction.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, new EntityGuardian.EntitySelectorGuardianTargetHumanSquid(this)));
    }

    public static AttributeProvider.Builder eM() {
        return EntityMonster.eR().a(GenericAttributes.ATTACK_DAMAGE, 6.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.5D).a(GenericAttributes.FOLLOW_RANGE, 16.0D).a(GenericAttributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityGuardian.b, false);
        this.datawatcher.register(EntityGuardian.d, 0);
    }

    @Override
    public boolean cM() {
        return true;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.WATER_MOB;
    }

    public boolean eN() {
        return (Boolean) this.datawatcher.get(EntityGuardian.b);
    }

    private void t(boolean flag) {
        this.datawatcher.set(EntityGuardian.b, flag);
    }

    public int eK() {
        return 80;
    }

    private void a(int i) {
        this.datawatcher.set(EntityGuardian.d, i);
    }

    public boolean eO() {
        return (Integer) this.datawatcher.get(EntityGuardian.d) != 0;
    }

    @Nullable
    public EntityLiving eP() {
        if (!this.eO()) {
            return null;
        } else if (this.world.isClientSide) {
            if (this.bt != null) {
                return this.bt;
            } else {
                Entity entity = this.world.getEntity((Integer) this.datawatcher.get(EntityGuardian.d));

                if (entity instanceof EntityLiving) {
                    this.bt = (EntityLiving) entity;
                    return this.bt;
                } else {
                    return null;
                }
            }
        } else {
            return this.getGoalTarget();
        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityGuardian.d.equals(datawatcherobject)) {
            this.bu = 0;
            this.bt = null;
        }

    }

    @Override
    public int D() {
        return 160;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.aH() ? SoundEffects.ENTITY_GUARDIAN_AMBIENT : SoundEffects.ENTITY_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.aH() ? SoundEffects.ENTITY_GUARDIAN_HURT : SoundEffects.ENTITY_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.aH() ? SoundEffects.ENTITY_GUARDIAN_DEATH : SoundEffects.ENTITY_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getFluid(blockposition).a((Tag) TagsFluid.WATER) ? 10.0F + iworldreader.y(blockposition) - 0.5F : super.a(blockposition, iworldreader);
    }

    @Override
    public void movementTick() {
        if (this.isAlive()) {
            if (this.world.isClientSide) {
                this.bp = this.bo;
                Vec3D vec3d;

                if (!this.isInWater()) {
                    this.bq = 2.0F;
                    vec3d = this.getMot();
                    if (vec3d.y > 0.0D && this.bv && !this.isSilent()) {
                        this.world.a(this.locX(), this.locY(), this.locZ(), this.getSoundFlop(), this.getSoundCategory(), 1.0F, 1.0F, false);
                    }

                    this.bv = vec3d.y < 0.0D && this.world.a(this.getChunkCoordinates().down(), (Entity) this);
                } else if (this.eN()) {
                    if (this.bq < 0.5F) {
                        this.bq = 4.0F;
                    } else {
                        this.bq += (0.5F - this.bq) * 0.1F;
                    }
                } else {
                    this.bq += (0.125F - this.bq) * 0.2F;
                }

                this.bo += this.bq;
                this.bs = this.br;
                if (!this.aH()) {
                    this.br = this.random.nextFloat();
                } else if (this.eN()) {
                    this.br += (0.0F - this.br) * 0.25F;
                } else {
                    this.br += (1.0F - this.br) * 0.06F;
                }

                if (this.eN() && this.isInWater()) {
                    vec3d = this.f(0.0F);

                    for (int i = 0; i < 2; ++i) {
                        this.world.addParticle(Particles.BUBBLE, this.d(0.5D) - vec3d.x * 1.5D, this.cF() - vec3d.y * 1.5D, this.g(0.5D) - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D);
                    }
                }

                if (this.eO()) {
                    if (this.bu < this.eK()) {
                        ++this.bu;
                    }

                    EntityLiving entityliving = this.eP();

                    if (entityliving != null) {
                        this.getControllerLook().a(entityliving, 90.0F, 90.0F);
                        this.getControllerLook().a();
                        double d0 = (double) this.A(0.0F);
                        double d1 = entityliving.locX() - this.locX();
                        double d2 = entityliving.e(0.5D) - this.getHeadY();
                        double d3 = entityliving.locZ() - this.locZ();
                        double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

                        d1 /= d4;
                        d2 /= d4;
                        d3 /= d4;
                        double d5 = this.random.nextDouble();

                        while (d5 < d4) {
                            d5 += 1.8D - d0 + this.random.nextDouble() * (1.7D - d0);
                            this.world.addParticle(Particles.BUBBLE, this.locX() + d1 * d5, this.getHeadY() + d2 * d5, this.locZ() + d3 * d5, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            }

            if (this.aH()) {
                this.setAirTicks(300);
            } else if (this.onGround) {
                this.setMot(this.getMot().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
                this.yaw = this.random.nextFloat() * 360.0F;
                this.onGround = false;
                this.impulse = true;
            }

            if (this.eO()) {
                this.yaw = this.aC;
            }
        }

        super.movementTick();
    }

    protected SoundEffect getSoundFlop() {
        return SoundEffects.ENTITY_GUARDIAN_FLOP;
    }

    public float A(float f) {
        return ((float) this.bu + f) / (float) this.eK();
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.j((Entity) this);
    }

    public static boolean b(EntityTypes<? extends EntityGuardian> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return (random.nextInt(20) == 0 || !generatoraccess.x(blockposition)) && generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && (enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getFluid(blockposition).a((Tag) TagsFluid.WATER));
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.eN() && !damagesource.isMagic() && damagesource.j() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.j();

            if (!damagesource.isExplosion()) {
                entityliving.damageEntity(DamageSource.a(this), 2.0F);
            }
        }

        if (this.goalRandomStroll != null) {
            this.goalRandomStroll.h();
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    public int O() {
        return 180;
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater()) {
            this.a(0.1F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (!this.eN() && this.getGoalTarget() == null) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.g(vec3d);
        }

    }

    static class ControllerMoveGuardian extends ControllerMove {

        private final EntityGuardian i;

        public ControllerMoveGuardian(EntityGuardian entityguardian) {
            super(entityguardian);
            this.i = entityguardian;
        }

        @Override
        public void a() {
            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().m()) {
                Vec3D vec3d = new Vec3D(this.b - this.i.locX(), this.c - this.i.locY(), this.d - this.i.locZ());
                double d0 = vec3d.f();
                double d1 = vec3d.x / d0;
                double d2 = vec3d.y / d0;
                double d3 = vec3d.z / d0;
                float f = (float) (MathHelper.d(vec3d.z, vec3d.x) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aA = this.i.yaw;
                float f1 = (float) (this.e * this.i.b(GenericAttributes.MOVEMENT_SPEED));
                float f2 = MathHelper.g(0.125F, this.i.dN(), f1);

                this.i.q(f2);
                double d4 = Math.sin((double) (this.i.ticksLived + this.i.getId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (this.i.yaw * 0.017453292F));
                double d6 = Math.sin((double) (this.i.yaw * 0.017453292F));
                double d7 = Math.sin((double) (this.i.ticksLived + this.i.getId()) * 0.75D) * 0.05D;

                this.i.setMot(this.i.getMot().add(d4 * d5, d7 * (d6 + d5) * 0.25D + (double) f2 * d2 * 0.1D, d4 * d6));
                ControllerLook controllerlook = this.i.getControllerLook();
                double d8 = this.i.locX() + d1 * 2.0D;
                double d9 = this.i.getHeadY() + d2 / d0;
                double d10 = this.i.locZ() + d3 * 2.0D;
                double d11 = controllerlook.d();
                double d12 = controllerlook.e();
                double d13 = controllerlook.f();

                if (!controllerlook.c()) {
                    d11 = d8;
                    d12 = d9;
                    d13 = d10;
                }

                this.i.getControllerLook().a(MathHelper.d(0.125D, d11, d8), MathHelper.d(0.125D, d12, d9), MathHelper.d(0.125D, d13, d10), 10.0F, 40.0F);
                this.i.t(true);
            } else {
                this.i.q(0.0F);
                this.i.t(false);
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
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean b() {
            return super.b() && (this.c || this.a.h((Entity) this.a.getGoalTarget()) > 9.0D);
        }

        @Override
        public void c() {
            this.b = -10;
            this.a.getNavigation().o();
            this.a.getControllerLook().a(this.a.getGoalTarget(), 90.0F, 90.0F);
            this.a.impulse = true;
        }

        @Override
        public void d() {
            this.a.a(0);
            this.a.setGoalTarget((EntityLiving) null);
            this.a.goalRandomStroll.h();
        }

        @Override
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
                    if (!this.a.isSilent()) {
                        this.a.world.broadcastEntityEffect(this.a, (byte) 21);
                    }
                } else if (this.b >= this.a.eK()) {
                    float f = 1.0F;

                    if (this.a.world.getDifficulty() == EnumDifficulty.HARD) {
                        f += 2.0F;
                    }

                    if (this.c) {
                        f += 2.0F;
                    }

                    entityliving.damageEntity(DamageSource.c(this.a, this.a), f);
                    entityliving.damageEntity(DamageSource.mobAttack(this.a), (float) this.a.b(GenericAttributes.ATTACK_DAMAGE));
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
            return (entityliving instanceof EntityHuman || entityliving instanceof EntitySquid) && entityliving.h((Entity) this.a) > 9.0D;
        }
    }
}
