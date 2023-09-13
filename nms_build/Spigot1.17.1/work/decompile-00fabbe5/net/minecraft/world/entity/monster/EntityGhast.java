package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFlying;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityLargeFireball;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityGhast extends EntityFlying implements IMonster {

    private static final DataWatcherObject<Boolean> DATA_IS_CHARGING = DataWatcher.a(EntityGhast.class, DataWatcherRegistry.BOOLEAN);
    private int explosionPower = 1;

    public EntityGhast(EntityTypes<? extends EntityGhast> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
        this.moveControl = new EntityGhast.ControllerGhast(this);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(5, new EntityGhast.PathfinderGoalGhastIdleMove(this));
        this.goalSelector.a(7, new EntityGhast.PathfinderGoalGhastMoveTowardsTarget(this));
        this.goalSelector.a(7, new EntityGhast.PathfinderGoalGhastAttackTarget(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.locY() - this.locY()) <= 4.0D;
        }));
    }

    public boolean n() {
        return (Boolean) this.entityData.get(EntityGhast.DATA_IS_CHARGING);
    }

    public void v(boolean flag) {
        this.entityData.set(EntityGhast.DATA_IS_CHARGING, flag);
    }

    public int getPower() {
        return this.explosionPower;
    }

    @Override
    protected boolean Q() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource.k() instanceof EntityLargeFireball && damagesource.getEntity() instanceof EntityHuman) {
            super.damageEntity(damagesource, 1000.0F);
            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityGhast.DATA_IS_CHARGING, false);
    }

    public static AttributeProvider.Builder t() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.FOLLOW_RANGE, 100.0D);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.GHAST_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.GHAST_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.GHAST_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    public static boolean b(EntityTypes<EntityGhast> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && random.nextInt(20) == 0 && a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
    }

    @Override
    public int getMaxSpawnGroup() {
        return 1;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setByte("ExplosionPower", (byte) this.explosionPower);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("ExplosionPower", 99)) {
            this.explosionPower = nbttagcompound.getByte("ExplosionPower");
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 2.6F;
    }

    private static class ControllerGhast extends ControllerMove {

        private final EntityGhast ghast;
        private int floatDuration;

        public ControllerGhast(EntityGhast entityghast) {
            super(entityghast);
            this.ghast = entityghast;
        }

        @Override
        public void a() {
            if (this.operation == ControllerMove.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3D vec3d = new Vec3D(this.wantedX - this.ghast.locX(), this.wantedY - this.ghast.locY(), this.wantedZ - this.ghast.locZ());
                    double d0 = vec3d.f();

                    vec3d = vec3d.d();
                    if (this.a(vec3d, MathHelper.e(d0))) {
                        this.ghast.setMot(this.ghast.getMot().e(vec3d.a(0.1D)));
                    } else {
                        this.operation = ControllerMove.Operation.WAIT;
                    }
                }

            }
        }

        private boolean a(Vec3D vec3d, int i) {
            AxisAlignedBB axisalignedbb = this.ghast.getBoundingBox();

            for (int j = 1; j < i; ++j) {
                axisalignedbb = axisalignedbb.c(vec3d);
                if (!this.ghast.level.getCubes(this.ghast, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static class PathfinderGoalGhastIdleMove extends PathfinderGoal {

        private final EntityGhast ghast;

        public PathfinderGoalGhastIdleMove(EntityGhast entityghast) {
            this.ghast = entityghast;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            ControllerMove controllermove = this.ghast.getControllerMove();

            if (!controllermove.b()) {
                return true;
            } else {
                double d0 = controllermove.d() - this.ghast.locX();
                double d1 = controllermove.e() - this.ghast.locY();
                double d2 = controllermove.f() - this.ghast.locZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void c() {
            Random random = this.ghast.getRandom();
            double d0 = this.ghast.locX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.ghast.locY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.ghast.locZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);

            this.ghast.getControllerMove().a(d0, d1, d2, 1.0D);
        }
    }

    private static class PathfinderGoalGhastMoveTowardsTarget extends PathfinderGoal {

        private final EntityGhast ghast;

        public PathfinderGoalGhastMoveTowardsTarget(EntityGhast entityghast) {
            this.ghast = entityghast;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return true;
        }

        @Override
        public void e() {
            if (this.ghast.getGoalTarget() == null) {
                Vec3D vec3d = this.ghast.getMot();

                this.ghast.setYRot(-((float) MathHelper.d(vec3d.x, vec3d.z)) * 57.295776F);
                this.ghast.yBodyRot = this.ghast.getYRot();
            } else {
                EntityLiving entityliving = this.ghast.getGoalTarget();
                double d0 = 64.0D;

                if (entityliving.f((Entity) this.ghast) < 4096.0D) {
                    double d1 = entityliving.locX() - this.ghast.locX();
                    double d2 = entityliving.locZ() - this.ghast.locZ();

                    this.ghast.setYRot(-((float) MathHelper.d(d1, d2)) * 57.295776F);
                    this.ghast.yBodyRot = this.ghast.getYRot();
                }
            }

        }
    }

    private static class PathfinderGoalGhastAttackTarget extends PathfinderGoal {

        private final EntityGhast ghast;
        public int chargeTime;

        public PathfinderGoalGhastAttackTarget(EntityGhast entityghast) {
            this.ghast = entityghast;
        }

        @Override
        public boolean a() {
            return this.ghast.getGoalTarget() != null;
        }

        @Override
        public void c() {
            this.chargeTime = 0;
        }

        @Override
        public void d() {
            this.ghast.v(false);
        }

        @Override
        public void e() {
            EntityLiving entityliving = this.ghast.getGoalTarget();
            double d0 = 64.0D;

            if (entityliving.f((Entity) this.ghast) < 4096.0D && this.ghast.hasLineOfSight(entityliving)) {
                World world = this.ghast.level;

                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.ghast.isSilent()) {
                    world.a((EntityHuman) null, 1015, this.ghast.getChunkCoordinates(), 0);
                }

                if (this.chargeTime == 20) {
                    double d1 = 4.0D;
                    Vec3D vec3d = this.ghast.e(1.0F);
                    double d2 = entityliving.locX() - (this.ghast.locX() + vec3d.x * 4.0D);
                    double d3 = entityliving.e(0.5D) - (0.5D + this.ghast.e(0.5D));
                    double d4 = entityliving.locZ() - (this.ghast.locZ() + vec3d.z * 4.0D);

                    if (!this.ghast.isSilent()) {
                        world.a((EntityHuman) null, 1016, this.ghast.getChunkCoordinates(), 0);
                    }

                    EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.ghast, d2, d3, d4, this.ghast.getPower());

                    entitylargefireball.setPosition(this.ghast.locX() + vec3d.x * 4.0D, this.ghast.e(0.5D) + 0.5D, entitylargefireball.locZ() + vec3d.z * 4.0D);
                    world.addEntity(entitylargefireball);
                    this.chargeTime = -40;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }

            this.ghast.v(this.chargeTime > 10);
        }
    }
}
