package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFlying;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class EntityPhantom extends EntityFlying implements IMonster {

    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = MathHelper.ceil(24.166098F);
    private static final DataWatcherObject<Integer> ID_SIZE = DataWatcher.defineId(EntityPhantom.class, DataWatcherRegistry.INT);
    Vec3D moveTargetPoint;
    BlockPosition anchorPoint;
    EntityPhantom.AttackPhase attackPhase;

    public EntityPhantom(EntityTypes<? extends EntityPhantom> entitytypes, World world) {
        super(entitytypes, world);
        this.moveTargetPoint = Vec3D.ZERO;
        this.anchorPoint = BlockPosition.ZERO;
        this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
        this.xpReward = 5;
        this.moveControl = new EntityPhantom.g(this);
        this.lookControl = new EntityPhantom.f(this);
    }

    @Override
    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % EntityPhantom.TICKS_PER_FLAP == 0;
    }

    @Override
    protected EntityAIBodyControl createBodyControl() {
        return new EntityPhantom.d(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EntityPhantom.c());
        this.goalSelector.addGoal(2, new EntityPhantom.i());
        this.goalSelector.addGoal(3, new EntityPhantom.e());
        this.targetSelector.addGoal(1, new EntityPhantom.b());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityPhantom.ID_SIZE, 0);
    }

    public void setPhantomSize(int i) {
        this.entityData.set(EntityPhantom.ID_SIZE, MathHelper.clamp(i, (int) 0, (int) 64));
    }

    private void updatePhantomSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(GenericAttributes.ATTACK_DAMAGE).setBaseValue((double) (6 + this.getPhantomSize()));
    }

    public int getPhantomSize() {
        return (Integer) this.entityData.get(EntityPhantom.ID_SIZE);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.35F;
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityPhantom.ID_SIZE.equals(datawatcherobject)) {
            this.updatePhantomSizeInfo();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float f = MathHelper.cos((float) (this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * 0.017453292F + 3.1415927F);
            float f1 = MathHelper.cos((float) (this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * 0.017453292F + 3.1415927F);

            if (f > 0.0F && f1 <= 0.0F) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEffects.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomSize();
            float f2 = MathHelper.cos(this.getYRot() * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f3 = MathHelper.sin(this.getYRot() * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);

            this.level.addParticle(Particles.MYCELIUM, this.getX() + (double) f2, this.getY() + (double) f4, this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(Particles.MYCELIUM, this.getX() - (double) f2, this.getY() + (double) f4, this.getZ() - (double) f3, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public void aiStep() {
        if (this.isAlive() && this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }

        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("AX")) {
            this.anchorPoint = new BlockPosition(nbttagcompound.getInt("AX"), nbttagcompound.getInt("AY"), nbttagcompound.getInt("AZ"));
        }

        this.setPhantomSize(nbttagcompound.getInt("Size"));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("AX", this.anchorPoint.getX());
        nbttagcompound.putInt("AY", this.anchorPoint.getY());
        nbttagcompound.putInt("AZ", this.anchorPoint.getZ());
        nbttagcompound.putInt("Size", this.getPhantomSize());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        return true;
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.PHANTOM_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.PHANTOM_DEATH;
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public boolean canAttackType(EntityTypes<?> entitytypes) {
        return true;
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        int i = this.getPhantomSize();
        EntitySize entitysize = super.getDimensions(entitypose);
        float f = (entitysize.width + 0.2F * (float) i) / entitysize.width;

        return entitysize.scale(f);
    }

    private static enum AttackPhase {

        CIRCLE, SWOOP;

        private AttackPhase() {}
    }

    private class g extends ControllerMove {

        private float speed = 0.1F;

        public g(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void tick() {
            if (EntityPhantom.this.horizontalCollision) {
                EntityPhantom.this.setYRot(EntityPhantom.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            float f = (float) (EntityPhantom.this.moveTargetPoint.x - EntityPhantom.this.getX());
            float f1 = (float) (EntityPhantom.this.moveTargetPoint.y - EntityPhantom.this.getY());
            float f2 = (float) (EntityPhantom.this.moveTargetPoint.z - EntityPhantom.this.getZ());
            double d0 = (double) MathHelper.sqrt(f * f + f2 * f2);

            if (Math.abs(d0) > 9.999999747378752E-6D) {
                double d1 = 1.0D - (double) MathHelper.abs(f1 * 0.7F) / d0;

                f = (float) ((double) f * d1);
                f2 = (float) ((double) f2 * d1);
                d0 = (double) MathHelper.sqrt(f * f + f2 * f2);
                double d2 = (double) MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
                float f3 = EntityPhantom.this.getYRot();
                float f4 = (float) MathHelper.atan2((double) f2, (double) f);
                float f5 = MathHelper.wrapDegrees(EntityPhantom.this.getYRot() + 90.0F);
                float f6 = MathHelper.wrapDegrees(f4 * 57.295776F);

                EntityPhantom.this.setYRot(MathHelper.approachDegrees(f5, f6, 4.0F) - 90.0F);
                EntityPhantom.this.yBodyRot = EntityPhantom.this.getYRot();
                if (MathHelper.degreesDifferenceAbs(f3, EntityPhantom.this.getYRot()) < 3.0F) {
                    this.speed = MathHelper.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = MathHelper.approach(this.speed, 0.2F, 0.025F);
                }

                float f7 = (float) (-(MathHelper.atan2((double) (-f1), d0) * 57.2957763671875D));

                EntityPhantom.this.setXRot(f7);
                float f8 = EntityPhantom.this.getYRot() + 90.0F;
                double d3 = (double) (this.speed * MathHelper.cos(f8 * 0.017453292F)) * Math.abs((double) f / d2);
                double d4 = (double) (this.speed * MathHelper.sin(f8 * 0.017453292F)) * Math.abs((double) f2 / d2);
                double d5 = (double) (this.speed * MathHelper.sin(f7 * 0.017453292F)) * Math.abs((double) f1 / d2);
                Vec3D vec3d = EntityPhantom.this.getDeltaMovement();

                EntityPhantom.this.setDeltaMovement(vec3d.add((new Vec3D(d3, d5, d4)).subtract(vec3d).scale(0.2D)));
            }

        }
    }

    private class f extends ControllerLook {

        public f(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void tick() {}
    }

    private class d extends EntityAIBodyControl {

        public d(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void clientTick() {
            EntityPhantom.this.yHeadRot = EntityPhantom.this.yBodyRot;
            EntityPhantom.this.yBodyRot = EntityPhantom.this.getYRot();
        }
    }

    private class c extends PathfinderGoal {

        private int nextSweepTick;

        c() {}

        @Override
        public boolean canUse() {
            EntityLiving entityliving = EntityPhantom.this.getTarget();

            return entityliving != null ? EntityPhantom.this.canAttack(entityliving, PathfinderTargetCondition.DEFAULT) : false;
        }

        @Override
        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop() {
            EntityPhantom.this.anchorPoint = EntityPhantom.this.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING, EntityPhantom.this.anchorPoint).above(10 + EntityPhantom.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (EntityPhantom.this.attackPhase == EntityPhantom.AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + EntityPhantom.this.random.nextInt(4)) * 20);
                    EntityPhantom.this.playSound(SoundEffects.PHANTOM_SWOOP, 10.0F, 0.95F + EntityPhantom.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            EntityPhantom.this.anchorPoint = EntityPhantom.this.getTarget().blockPosition().above(20 + EntityPhantom.this.random.nextInt(20));
            if (EntityPhantom.this.anchorPoint.getY() < EntityPhantom.this.level.getSeaLevel()) {
                EntityPhantom.this.anchorPoint = new BlockPosition(EntityPhantom.this.anchorPoint.getX(), EntityPhantom.this.level.getSeaLevel() + 1, EntityPhantom.this.anchorPoint.getZ());
            }

        }
    }

    private class i extends EntityPhantom.h {

        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        i() {
            super();
        }

        @Override
        public boolean canUse() {
            return EntityPhantom.this.getTarget() != null && EntityPhantom.this.attackPhase == EntityPhantom.AttackPhase.SWOOP;
        }

        @Override
        public boolean canContinueToUse() {
            EntityLiving entityliving = EntityPhantom.this.getTarget();

            if (entityliving == null) {
                return false;
            } else if (!entityliving.isAlive()) {
                return false;
            } else {
                if (entityliving instanceof EntityHuman) {
                    EntityHuman entityhuman = (EntityHuman) entityliving;

                    if (entityliving.isSpectator() || entityhuman.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (EntityPhantom.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = EntityPhantom.this.tickCount + 20;
                        List<EntityCat> list = EntityPhantom.this.level.getEntitiesOfClass(EntityCat.class, EntityPhantom.this.getBoundingBox().inflate(16.0D), IEntitySelector.ENTITY_STILL_ALIVE);
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            EntityCat entitycat = (EntityCat) iterator.next();

                            entitycat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        @Override
        public void start() {}

        @Override
        public void stop() {
            EntityPhantom.this.setTarget((EntityLiving) null);
            EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
        }

        @Override
        public void tick() {
            EntityLiving entityliving = EntityPhantom.this.getTarget();

            if (entityliving != null) {
                EntityPhantom.this.moveTargetPoint = new Vec3D(entityliving.getX(), entityliving.getY(0.5D), entityliving.getZ());
                if (EntityPhantom.this.getBoundingBox().inflate(0.20000000298023224D).intersects(entityliving.getBoundingBox())) {
                    EntityPhantom.this.doHurtTarget(entityliving);
                    EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
                    if (!EntityPhantom.this.isSilent()) {
                        EntityPhantom.this.level.levelEvent(1039, EntityPhantom.this.blockPosition(), 0);
                    }
                } else if (EntityPhantom.this.horizontalCollision || EntityPhantom.this.hurtTime > 0) {
                    EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
                }

            }
        }
    }

    private class e extends EntityPhantom.h {

        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        e() {
            super();
        }

        @Override
        public boolean canUse() {
            return EntityPhantom.this.getTarget() == null || EntityPhantom.this.attackPhase == EntityPhantom.AttackPhase.CIRCLE;
        }

        @Override
        public void start() {
            this.distance = 5.0F + EntityPhantom.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + EntityPhantom.this.random.nextFloat() * 9.0F;
            this.clockwise = EntityPhantom.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick() {
            if (EntityPhantom.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + EntityPhantom.this.random.nextFloat() * 9.0F;
            }

            if (EntityPhantom.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (EntityPhantom.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = EntityPhantom.this.random.nextFloat() * 2.0F * 3.1415927F;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (EntityPhantom.this.moveTargetPoint.y < EntityPhantom.this.getY() && !EntityPhantom.this.level.isEmptyBlock(EntityPhantom.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (EntityPhantom.this.moveTargetPoint.y > EntityPhantom.this.getY() && !EntityPhantom.this.level.isEmptyBlock(EntityPhantom.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPosition.ZERO.equals(EntityPhantom.this.anchorPoint)) {
                EntityPhantom.this.anchorPoint = EntityPhantom.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * 0.017453292F;
            EntityPhantom.this.moveTargetPoint = Vec3D.atLowerCornerOf(EntityPhantom.this.anchorPoint).add((double) (this.distance * MathHelper.cos(this.angle)), (double) (-4.0F + this.height), (double) (this.distance * MathHelper.sin(this.angle)));
        }
    }

    private class b extends PathfinderGoal {

        private final PathfinderTargetCondition attackTargeting = PathfinderTargetCondition.forCombat().range(64.0D);
        private int nextScanTick = reducedTickDelay(20);

        b() {}

        @Override
        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<EntityHuman> list = EntityPhantom.this.level.getNearbyPlayers(this.attackTargeting, EntityPhantom.this, EntityPhantom.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));

                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::getY).reversed());
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityHuman entityhuman = (EntityHuman) iterator.next();

                        if (EntityPhantom.this.canAttack(entityhuman, PathfinderTargetCondition.DEFAULT)) {
                            EntityPhantom.this.setTarget(entityhuman);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            EntityLiving entityliving = EntityPhantom.this.getTarget();

            return entityliving != null ? EntityPhantom.this.canAttack(entityliving, PathfinderTargetCondition.DEFAULT) : false;
        }
    }

    private abstract class h extends PathfinderGoal {

        public h() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        protected boolean touchingTarget() {
            return EntityPhantom.this.moveTargetPoint.distanceToSqr(EntityPhantom.this.getX(), EntityPhantom.this.getY(), EntityPhantom.this.getZ()) < 4.0D;
        }
    }
}
