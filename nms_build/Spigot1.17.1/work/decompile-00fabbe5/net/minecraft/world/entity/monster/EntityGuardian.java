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
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityGuardian extends EntityMonster {

    protected static final int ATTACK_TIME = 80;
    private static final DataWatcherObject<Boolean> DATA_ID_MOVING = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_ID_ATTACK_TARGET = DataWatcher.a(EntityGuardian.class, DataWatcherRegistry.INT);
    private float clientSideTailAnimation;
    private float clientSideTailAnimationO;
    private float clientSideTailAnimationSpeed;
    private float clientSideSpikesAnimation;
    private float clientSideSpikesAnimationO;
    private EntityLiving clientSideCachedAttackTarget;
    private int clientSideAttackTime;
    private boolean clientSideTouchedGround;
    public PathfinderGoalRandomStroll randomStrollGoal;

    public EntityGuardian(EntityTypes<? extends EntityGuardian> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 10;
        this.a(PathType.WATER, 0.0F);
        this.moveControl = new EntityGuardian.ControllerMoveGuardian(this);
        this.clientSideTailAnimation = this.random.nextFloat();
        this.clientSideTailAnimationO = this.clientSideTailAnimation;
    }

    @Override
    protected void initPathfinder() {
        PathfinderGoalMoveTowardsRestriction pathfindergoalmovetowardsrestriction = new PathfinderGoalMoveTowardsRestriction(this, 1.0D);

        this.randomStrollGoal = new PathfinderGoalRandomStroll(this, 1.0D, 80);
        this.goalSelector.a(4, new EntityGuardian.PathfinderGoalGuardianAttack(this));
        this.goalSelector.a(5, pathfindergoalmovetowardsrestriction);
        this.goalSelector.a(7, this.randomStrollGoal);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityGuardian.class, 12.0F, 0.01F));
        this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
        this.randomStrollGoal.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        pathfindergoalmovetowardsrestriction.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, new EntityGuardian.EntitySelectorGuardianTargetHumanSquid(this)));
    }

    public static AttributeProvider.Builder fw() {
        return EntityMonster.fB().a(GenericAttributes.ATTACK_DAMAGE, 6.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.5D).a(GenericAttributes.FOLLOW_RANGE, 16.0D).a(GenericAttributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityGuardian.DATA_ID_MOVING, false);
        this.entityData.register(EntityGuardian.DATA_ID_ATTACK_TARGET, 0);
    }

    @Override
    public boolean dr() {
        return true;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.WATER;
    }

    public boolean fx() {
        return (Boolean) this.entityData.get(EntityGuardian.DATA_ID_MOVING);
    }

    void v(boolean flag) {
        this.entityData.set(EntityGuardian.DATA_ID_MOVING, flag);
    }

    public int p() {
        return 80;
    }

    void a(int i) {
        this.entityData.set(EntityGuardian.DATA_ID_ATTACK_TARGET, i);
    }

    public boolean fy() {
        return (Integer) this.entityData.get(EntityGuardian.DATA_ID_ATTACK_TARGET) != 0;
    }

    @Nullable
    public EntityLiving fz() {
        if (!this.fy()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level.getEntity((Integer) this.entityData.get(EntityGuardian.DATA_ID_ATTACK_TARGET));

                if (entity instanceof EntityLiving) {
                    this.clientSideCachedAttackTarget = (EntityLiving) entity;
                    return this.clientSideCachedAttackTarget;
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
        if (EntityGuardian.DATA_ID_ATTACK_TARGET.equals(datawatcherobject)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }

    }

    @Override
    public int J() {
        return 160;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.aO() ? SoundEffects.GUARDIAN_AMBIENT : SoundEffects.GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.aO() ? SoundEffects.GUARDIAN_HURT : SoundEffects.GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.aO() ? SoundEffects.GUARDIAN_DEATH : SoundEffects.GUARDIAN_DEATH_LAND;
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getFluid(blockposition).a((Tag) TagsFluid.WATER) ? 10.0F + iworldreader.z(blockposition) - 0.5F : super.a(blockposition, iworldreader);
    }

    @Override
    public void movementTick() {
        if (this.isAlive()) {
            if (this.level.isClientSide) {
                this.clientSideTailAnimationO = this.clientSideTailAnimation;
                Vec3D vec3d;

                if (!this.isInWater()) {
                    this.clientSideTailAnimationSpeed = 2.0F;
                    vec3d = this.getMot();
                    if (vec3d.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                        this.level.a(this.locX(), this.locY(), this.locZ(), this.getSoundFlop(), this.getSoundCategory(), 1.0F, 1.0F, false);
                    }

                    this.clientSideTouchedGround = vec3d.y < 0.0D && this.level.a(this.getChunkCoordinates().down(), (Entity) this);
                } else if (this.fx()) {
                    if (this.clientSideTailAnimationSpeed < 0.5F) {
                        this.clientSideTailAnimationSpeed = 4.0F;
                    } else {
                        this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
                    }
                } else {
                    this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
                }

                this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
                this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
                if (!this.aO()) {
                    this.clientSideSpikesAnimation = this.random.nextFloat();
                } else if (this.fx()) {
                    this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
                } else {
                    this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
                }

                if (this.fx() && this.isInWater()) {
                    vec3d = this.e(0.0F);

                    for (int i = 0; i < 2; ++i) {
                        this.level.addParticle(Particles.BUBBLE, this.d(0.5D) - vec3d.x * 1.5D, this.da() - vec3d.y * 1.5D, this.g(0.5D) - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D);
                    }
                }

                if (this.fy()) {
                    if (this.clientSideAttackTime < this.p()) {
                        ++this.clientSideAttackTime;
                    }

                    EntityLiving entityliving = this.fz();

                    if (entityliving != null) {
                        this.getControllerLook().a(entityliving, 90.0F, 90.0F);
                        this.getControllerLook().a();
                        double d0 = (double) this.B(0.0F);
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
                            this.level.addParticle(Particles.BUBBLE, this.locX() + d1 * d5, this.getHeadY() + d2 * d5, this.locZ() + d3 * d5, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            }

            if (this.aO()) {
                this.setAirTicks(300);
            } else if (this.onGround) {
                this.setMot(this.getMot().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.onGround = false;
                this.hasImpulse = true;
            }

            if (this.fy()) {
                this.setYRot(this.yHeadRot);
            }
        }

        super.movementTick();
    }

    protected SoundEffect getSoundFlop() {
        return SoundEffects.GUARDIAN_FLOP;
    }

    public float z(float f) {
        return MathHelper.h(f, this.clientSideTailAnimationO, this.clientSideTailAnimation);
    }

    public float A(float f) {
        return MathHelper.h(f, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
    }

    public float B(float f) {
        return ((float) this.clientSideAttackTime + f) / (float) this.p();
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.f((Entity) this);
    }

    public static boolean b(EntityTypes<? extends EntityGuardian> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return (random.nextInt(20) == 0 || !generatoraccess.y(blockposition)) && generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && (enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getFluid(blockposition).a((Tag) TagsFluid.WATER));
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.fx() && !damagesource.isMagic() && damagesource.k() instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) damagesource.k();

            if (!damagesource.isExplosion()) {
                entityliving.damageEntity(DamageSource.a(this), 2.0F);
            }
        }

        if (this.randomStrollGoal != null) {
            this.randomStrollGoal.h();
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    public int eZ() {
        return 180;
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater()) {
            this.a(0.1F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (!this.fx() && this.getGoalTarget() == null) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.g(vec3d);
        }

    }

    private static class ControllerMoveGuardian extends ControllerMove {

        private final EntityGuardian guardian;

        public ControllerMoveGuardian(EntityGuardian entityguardian) {
            super(entityguardian);
            this.guardian = entityguardian;
        }

        @Override
        public void a() {
            if (this.operation == ControllerMove.Operation.MOVE_TO && !this.guardian.getNavigation().m()) {
                Vec3D vec3d = new Vec3D(this.wantedX - this.guardian.locX(), this.wantedY - this.guardian.locY(), this.wantedZ - this.guardian.locZ());
                double d0 = vec3d.f();
                double d1 = vec3d.x / d0;
                double d2 = vec3d.y / d0;
                double d3 = vec3d.z / d0;
                float f = (float) (MathHelper.d(vec3d.z, vec3d.x) * 57.2957763671875D) - 90.0F;

                this.guardian.setYRot(this.a(this.guardian.getYRot(), f, 90.0F));
                this.guardian.yBodyRot = this.guardian.getYRot();
                float f1 = (float) (this.speedModifier * this.guardian.b(GenericAttributes.MOVEMENT_SPEED));
                float f2 = MathHelper.h(0.125F, this.guardian.ew(), f1);

                this.guardian.r(f2);
                double d4 = Math.sin((double) (this.guardian.tickCount + this.guardian.getId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (this.guardian.getYRot() * 0.017453292F));
                double d6 = Math.sin((double) (this.guardian.getYRot() * 0.017453292F));
                double d7 = Math.sin((double) (this.guardian.tickCount + this.guardian.getId()) * 0.75D) * 0.05D;

                this.guardian.setMot(this.guardian.getMot().add(d4 * d5, d7 * (d6 + d5) * 0.25D + (double) f2 * d2 * 0.1D, d4 * d6));
                ControllerLook controllerlook = this.guardian.getControllerLook();
                double d8 = this.guardian.locX() + d1 * 2.0D;
                double d9 = this.guardian.getHeadY() + d2 / d0;
                double d10 = this.guardian.locZ() + d3 * 2.0D;
                double d11 = controllerlook.e();
                double d12 = controllerlook.f();
                double d13 = controllerlook.g();

                if (!controllerlook.d()) {
                    d11 = d8;
                    d12 = d9;
                    d13 = d10;
                }

                this.guardian.getControllerLook().a(MathHelper.d(0.125D, d11, d8), MathHelper.d(0.125D, d12, d9), MathHelper.d(0.125D, d13, d10), 10.0F, 40.0F);
                this.guardian.v(true);
            } else {
                this.guardian.r(0.0F);
                this.guardian.v(false);
            }
        }
    }

    private static class PathfinderGoalGuardianAttack extends PathfinderGoal {

        private final EntityGuardian guardian;
        private int attackTime;
        private final boolean elder;

        public PathfinderGoalGuardianAttack(EntityGuardian entityguardian) {
            this.guardian = entityguardian;
            this.elder = entityguardian instanceof EntityGuardianElder;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.guardian.getGoalTarget();

            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean b() {
            return super.b() && (this.elder || this.guardian.f((Entity) this.guardian.getGoalTarget()) > 9.0D);
        }

        @Override
        public void c() {
            this.attackTime = -10;
            this.guardian.getNavigation().o();
            this.guardian.getControllerLook().a(this.guardian.getGoalTarget(), 90.0F, 90.0F);
            this.guardian.hasImpulse = true;
        }

        @Override
        public void d() {
            this.guardian.a((int) 0);
            this.guardian.setGoalTarget((EntityLiving) null);
            this.guardian.randomStrollGoal.h();
        }

        @Override
        public void e() {
            EntityLiving entityliving = this.guardian.getGoalTarget();

            this.guardian.getNavigation().o();
            this.guardian.getControllerLook().a(entityliving, 90.0F, 90.0F);
            if (!this.guardian.hasLineOfSight(entityliving)) {
                this.guardian.setGoalTarget((EntityLiving) null);
            } else {
                ++this.attackTime;
                if (this.attackTime == 0) {
                    this.guardian.a(this.guardian.getGoalTarget().getId());
                    if (!this.guardian.isSilent()) {
                        this.guardian.level.broadcastEntityEffect(this.guardian, (byte) 21);
                    }
                } else if (this.attackTime >= this.guardian.p()) {
                    float f = 1.0F;

                    if (this.guardian.level.getDifficulty() == EnumDifficulty.HARD) {
                        f += 2.0F;
                    }

                    if (this.elder) {
                        f += 2.0F;
                    }

                    entityliving.damageEntity(DamageSource.c(this.guardian, this.guardian), f);
                    entityliving.damageEntity(DamageSource.mobAttack(this.guardian), (float) this.guardian.b(GenericAttributes.ATTACK_DAMAGE));
                    this.guardian.setGoalTarget((EntityLiving) null);
                }

                super.e();
            }
        }
    }

    private static class EntitySelectorGuardianTargetHumanSquid implements Predicate<EntityLiving> {

        private final EntityGuardian guardian;

        public EntitySelectorGuardianTargetHumanSquid(EntityGuardian entityguardian) {
            this.guardian = entityguardian;
        }

        public boolean test(@Nullable EntityLiving entityliving) {
            return (entityliving instanceof EntityHuman || entityliving instanceof EntitySquid || entityliving instanceof Axolotl) && entityliving.f((Entity) this.guardian) > 9.0D;
        }
    }
}
