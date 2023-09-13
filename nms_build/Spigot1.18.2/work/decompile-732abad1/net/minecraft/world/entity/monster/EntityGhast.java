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

    private static final DataWatcherObject<Boolean> DATA_IS_CHARGING = DataWatcher.defineId(EntityGhast.class, DataWatcherRegistry.BOOLEAN);
    private int explosionPower = 1;

    public EntityGhast(EntityTypes<? extends EntityGhast> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
        this.moveControl = new EntityGhast.ControllerGhast(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new EntityGhast.PathfinderGoalGhastIdleMove(this));
        this.goalSelector.addGoal(7, new EntityGhast.PathfinderGoalGhastMoveTowardsTarget(this));
        this.goalSelector.addGoal(7, new EntityGhast.PathfinderGoalGhastAttackTarget(this));
        this.targetSelector.addGoal(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.getY() - this.getY()) <= 4.0D;
        }));
    }

    public boolean isCharging() {
        return (Boolean) this.entityData.get(EntityGhast.DATA_IS_CHARGING);
    }

    public void setCharging(boolean flag) {
        this.entityData.set(EntityGhast.DATA_IS_CHARGING, flag);
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (damagesource.getDirectEntity() instanceof EntityLargeFireball && damagesource.getEntity() instanceof EntityHuman) {
            super.hurt(damagesource, 1000.0F);
            return true;
        } else {
            return super.hurt(damagesource, f);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityGhast.DATA_IS_CHARGING, false);
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.FOLLOW_RANGE, 100.0D);
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.GHAST_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.GHAST_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.GHAST_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    public static boolean checkGhastSpawnRules(EntityTypes<EntityGhast> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && random.nextInt(20) == 0 && checkMobSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, random);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putByte("ExplosionPower", (byte) this.explosionPower);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("ExplosionPower", 99)) {
            this.explosionPower = nbttagcompound.getByte("ExplosionPower");
        }

    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
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
        public void tick() {
            if (this.operation == ControllerMove.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3D vec3d = new Vec3D(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
                    double d0 = vec3d.length();

                    vec3d = vec3d.normalize();
                    if (this.canReach(vec3d, MathHelper.ceil(d0))) {
                        this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vec3d.scale(0.1D)));
                    } else {
                        this.operation = ControllerMove.Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3D vec3d, int i) {
            AxisAlignedBB axisalignedbb = this.ghast.getBoundingBox();

            for (int j = 1; j < i; ++j) {
                axisalignedbb = axisalignedbb.move(vec3d);
                if (!this.ghast.level.noCollision(this.ghast, axisalignedbb)) {
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
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            ControllerMove controllermove = this.ghast.getMoveControl();

            if (!controllermove.hasWanted()) {
                return true;
            } else {
                double d0 = controllermove.getWantedX() - this.ghast.getX();
                double d1 = controllermove.getWantedY() - this.ghast.getY();
                double d2 = controllermove.getWantedZ() - this.ghast.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Random random = this.ghast.getRandom();
            double d0 = this.ghast.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.ghast.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.ghast.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);

            this.ghast.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
        }
    }

    private static class PathfinderGoalGhastMoveTowardsTarget extends PathfinderGoal {

        private final EntityGhast ghast;

        public PathfinderGoalGhastMoveTowardsTarget(EntityGhast entityghast) {
            this.ghast = entityghast;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3D vec3d = this.ghast.getDeltaMovement();

                this.ghast.setYRot(-((float) MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F);
                this.ghast.yBodyRot = this.ghast.getYRot();
            } else {
                EntityLiving entityliving = this.ghast.getTarget();
                double d0 = 64.0D;

                if (entityliving.distanceToSqr((Entity) this.ghast) < 4096.0D) {
                    double d1 = entityliving.getX() - this.ghast.getX();
                    double d2 = entityliving.getZ() - this.ghast.getZ();

                    this.ghast.setYRot(-((float) MathHelper.atan2(d1, d2)) * 57.295776F);
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
        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        @Override
        public void start() {
            this.chargeTime = 0;
        }

        @Override
        public void stop() {
            this.ghast.setCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            EntityLiving entityliving = this.ghast.getTarget();

            if (entityliving != null) {
                double d0 = 64.0D;

                if (entityliving.distanceToSqr((Entity) this.ghast) < 4096.0D && this.ghast.hasLineOfSight(entityliving)) {
                    World world = this.ghast.level;

                    ++this.chargeTime;
                    if (this.chargeTime == 10 && !this.ghast.isSilent()) {
                        world.levelEvent((EntityHuman) null, 1015, this.ghast.blockPosition(), 0);
                    }

                    if (this.chargeTime == 20) {
                        double d1 = 4.0D;
                        Vec3D vec3d = this.ghast.getViewVector(1.0F);
                        double d2 = entityliving.getX() - (this.ghast.getX() + vec3d.x * 4.0D);
                        double d3 = entityliving.getY(0.5D) - (0.5D + this.ghast.getY(0.5D));
                        double d4 = entityliving.getZ() - (this.ghast.getZ() + vec3d.z * 4.0D);

                        if (!this.ghast.isSilent()) {
                            world.levelEvent((EntityHuman) null, 1016, this.ghast.blockPosition(), 0);
                        }

                        EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.ghast, d2, d3, d4, this.ghast.getExplosionPower());

                        entitylargefireball.setPos(this.ghast.getX() + vec3d.x * 4.0D, this.ghast.getY(0.5D) + 0.5D, entitylargefireball.getZ() + vec3d.z * 4.0D);
                        world.addFreshEntity(entitylargefireball);
                        this.chargeTime = -40;
                    }
                } else if (this.chargeTime > 0) {
                    --this.chargeTime;
                }

                this.ghast.setCharging(this.chargeTime > 10);
            }
        }
    }
}
