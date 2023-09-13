package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.Vec3D;

public class EntitySlime extends EntityInsentient implements IMonster {

    private static final DataWatcherObject<Integer> ID_SIZE = DataWatcher.defineId(EntitySlime.class, DataWatcherRegistry.INT);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public EntitySlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new EntitySlime.ControllerMoveSlime(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.addGoal(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.addGoal(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.addGoal(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
        this.targetSelector.addGoal(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.getY() - this.getY()) <= 4.0D;
        }));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntitySlime.ID_SIZE, 1);
    }

    public void setSize(int i, boolean flag) {
        int j = MathHelper.clamp(i, (int) 1, (int) 127);

        this.entityData.set(EntitySlime.ID_SIZE, j);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(GenericAttributes.MAX_HEALTH).setBaseValue((double) (j * j));
        this.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue((double) (0.2F + 0.1F * (float) j));
        this.getAttribute(GenericAttributes.ATTACK_DAMAGE).setBaseValue((double) j);
        if (flag) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = j;
    }

    public int getSize() {
        return (Integer) this.entityData.get(EntitySlime.ID_SIZE);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Size", this.getSize() - 1);
        nbttagcompound.putBoolean("wasOnGround", this.wasOnGround);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.setSize(nbttagcompound.getInt("Size") + 1, false);
        super.readAdditionalSaveData(nbttagcompound);
        this.wasOnGround = nbttagcompound.getBoolean("wasOnGround");
    }

    public boolean isTiny() {
        return this.getSize() <= 1;
    }

    protected ParticleParam getParticleType() {
        return Particles.ITEM_SLIME;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return this.getSize() > 0;
    }

    @Override
    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;

                this.level.addParticle(this.getParticleType(), this.getX() + (double) f2, this.getY(), this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetSquish = -0.5F;
        } else if (!this.onGround && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.decreaseSquish();
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();

        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntitySlime.ID_SIZE.equals(datawatcherobject)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    public EntityTypes<? extends EntitySlime> getType() {
        return super.getType();
    }

    @Override
    public void remove(Entity.RemovalReason entity_removalreason) {
        int i = this.getSize();

        if (!this.level.isClientSide && i > 1 && this.isDeadOrDying()) {
            IChatBaseComponent ichatbasecomponent = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                EntitySlime entityslime = (EntitySlime) this.getType().create(this.level);

                if (this.isPersistenceRequired()) {
                    entityslime.setPersistenceRequired();
                }

                entityslime.setCustomName(ichatbasecomponent);
                entityslime.setNoAi(flag);
                entityslime.setInvulnerable(this.isInvulnerable());
                entityslime.setSize(j, true);
                entityslime.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.level.addFreshEntity(entityslime);
            }
        }

        super.remove(entity_removalreason);
    }

    @Override
    public void push(Entity entity) {
        super.push(entity);
        if (entity instanceof EntityIronGolem && this.isDealsDamage()) {
            this.dealDamage((EntityLiving) entity);
        }

    }

    @Override
    public void playerTouch(EntityHuman entityhuman) {
        if (this.isDealsDamage()) {
            this.dealDamage(entityhuman);
        }

    }

    protected void dealDamage(EntityLiving entityliving) {
        if (this.isAlive()) {
            int i = this.getSize();

            if (this.distanceToSqr((Entity) entityliving) < 0.6D * (double) i * 0.6D * (double) i && this.hasLineOfSight(entityliving) && entityliving.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEffects.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, entityliving);
            }
        }

    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.625F * entitysize.height;
    }

    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isTiny() ? SoundEffects.SLIME_HURT_SMALL : SoundEffects.SLIME_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return this.isTiny() ? SoundEffects.SLIME_DEATH_SMALL : SoundEffects.SLIME_DEATH;
    }

    protected SoundEffect getSquishSound() {
        return this.isTiny() ? SoundEffects.SLIME_SQUISH_SMALL : SoundEffects.SLIME_SQUISH;
    }

    @Override
    public MinecraftKey getDefaultLootTable() {
        return this.getSize() == 1 ? this.getType().getDefaultLootTable() : LootTables.EMPTY;
    }

    public static boolean checkSlimeSpawnRules(EntityTypes<EntitySlime> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL) {
            if (Objects.equals(generatoraccess.getBiomeName(blockposition), Optional.of(Biomes.SWAMP)) && blockposition.getY() > 50 && blockposition.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < generatoraccess.getMoonBrightness() && generatoraccess.getMaxLocalRawBrightness(blockposition) <= random.nextInt(8)) {
                return checkMobSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, random);
            }

            if (!(generatoraccess instanceof GeneratorAccessSeed)) {
                return false;
            }

            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
            boolean flag = SeededRandom.seedSlimeChunk(chunkcoordintpair.x, chunkcoordintpair.z, ((GeneratorAccessSeed) generatoraccess).getSeed(), 987234911L).nextInt(10) == 0;

            if (random.nextInt(10) == 0 && flag && blockposition.getY() < 40) {
                return checkMobSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, random);
            }
        }

        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    @Override
    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    @Override
    protected void jumpFromGround() {
        Vec3D vec3d = this.getDeltaMovement();

        this.setDeltaMovement(vec3d.x, (double) this.getJumpPower(), vec3d.z);
        this.hasImpulse = true;
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.random.nextInt(3);

        if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.getSpecialMultiplier()) {
            ++i;
        }

        int j = 1 << i;

        this.setSize(j, true);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;

        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEffect getJumpSound() {
        return this.isTiny() ? SoundEffects.SLIME_JUMP_SMALL : SoundEffects.SLIME_JUMP;
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return super.getDimensions(entitypose).scale(0.255F * (float) this.getSize());
    }

    private static class ControllerMoveSlime extends ControllerMove {

        private float yRot;
        private int jumpDelay;
        private final EntitySlime slime;
        private boolean isAggressive;

        public ControllerMoveSlime(EntitySlime entityslime) {
            super(entityslime);
            this.slime = entityslime;
            this.yRot = 180.0F * entityslime.getYRot() / 3.1415927F;
        }

        public void setDirection(float f, boolean flag) {
            this.yRot = f;
            this.isAggressive = flag;
        }

        public void setWantedMovement(double d0) {
            this.speedModifier = d0;
            this.operation = ControllerMove.Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != ControllerMove.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = ControllerMove.Operation.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    private static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {

        private final EntitySlime slime;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
            entityslime.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }

            ((EntitySlime.ControllerMoveSlime) this.slime.getMoveControl()).setWantedMovement(1.2D);
        }
    }

    private static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {

        private final EntitySlime slime;
        private int growTiredTimer;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            EntityLiving entityliving = this.slime.getTarget();

            return entityliving == null ? false : (!this.slime.canAttack(entityliving) ? false : this.slime.getMoveControl() instanceof EntitySlime.ControllerMoveSlime);
        }

        @Override
        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            EntityLiving entityliving = this.slime.getTarget();

            return entityliving == null ? false : (!this.slime.canAttack(entityliving) ? false : --this.growTiredTimer > 0);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            EntityLiving entityliving = this.slime.getTarget();

            if (entityliving != null) {
                this.slime.lookAt(entityliving, 10.0F, 10.0F);
            }

            ((EntitySlime.ControllerMoveSlime) this.slime.getMoveControl()).setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
        }
    }

    private static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {

        private final EntitySlime slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float) this.slime.getRandom().nextInt(360);
            }

            ((EntitySlime.ControllerMoveSlime) this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

    private static class PathfinderGoalSlimeIdle extends PathfinderGoal {

        private final EntitySlime slime;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        @Override
        public void tick() {
            ((EntitySlime.ControllerMoveSlime) this.slime.getMoveControl()).setWantedMovement(1.0D);
        }
    }
}
