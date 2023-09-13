package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityPolarBear extends EntityAnimal implements IEntityAngerable {

    private static final DataWatcherObject<Boolean> DATA_STANDING_ID = DataWatcher.defineId(EntityPolarBear.class, DataWatcherRegistry.BOOLEAN);
    private static final float STAND_ANIMATION_TICKS = 6.0F;
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public EntityPolarBear(EntityTypes<? extends EntityPolarBear> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.POLAR_BEAR.create(worldserver);
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new EntityPolarBear.c());
        this.goalSelector.addGoal(1, new EntityPolarBear.d());
        this.goalSelector.addGoal(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.addGoal(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.addGoal(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new EntityPolarBear.b());
        this.targetSelector.addGoal(2, new EntityPolarBear.a());
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityFox.class, 10, true, true, (Predicate) null));
        this.targetSelector.addGoal(5, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 30.0D).add(GenericAttributes.FOLLOW_RANGE, 20.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.25D).add(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    public static boolean checkPolarBearSpawnRules(EntityTypes<EntityPolarBear> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        Optional<ResourceKey<BiomeBase>> optional = generatoraccess.getBiomeName(blockposition);

        return !Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN)) ? checkAnimalSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, random) : isBrightEnoughToSpawn(generatoraccess, blockposition) && generatoraccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.POLAR_BEARS_SPAWNABLE_ON_IN_FROZEN_OCEAN);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.readPersistentAngerSaveData(this.level, nbttagcompound);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        this.addPersistentAngerSaveData(nbttagcompound);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(EntityPolarBear.PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        this.remainingPersistentAngerTime = i;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isBaby() ? SoundEffects.POLAR_BEAR_AMBIENT_BABY : SoundEffects.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEffects.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityPolarBear.DATA_STANDING_ID, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }

            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }

        if (!this.level.isClientSide) {
            this.updatePersistentAnger((WorldServer) this.level, true);
        }

    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        if (this.clientSideStandAnimation > 0.0F) {
            float f = this.clientSideStandAnimation / 6.0F;
            float f1 = 1.0F + f;

            return super.getDimensions(entitypose).scale(1.0F, f1);
        } else {
            return super.getDimensions(entitypose);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = entity.hurt(DamageSource.mobAttack(this), (float) ((int) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.doEnchantDamageEffects(this, entity);
        }

        return flag;
    }

    public boolean isStanding() {
        return (Boolean) this.entityData.get(EntityPolarBear.DATA_STANDING_ID);
    }

    public void setStanding(boolean flag) {
        this.entityData.set(EntityPolarBear.DATA_STANDING_ID, flag);
    }

    public float getStandingAnimationScale(float f) {
        return MathHelper.lerp(f, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(1.0F);
        }

        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    private class c extends PathfinderGoalMeleeAttack {

        public c() {
            super(EntityPolarBear.this, 1.25D, true);
        }

        @Override
        protected void checkAndPerformAttack(EntityLiving entityliving, double d0) {
            double d1 = this.getAttackReachSqr(entityliving);

            if (d0 <= d1 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(entityliving);
                EntityPolarBear.this.setStanding(false);
            } else if (d0 <= d1 * 2.0D) {
                if (this.isTimeToAttack()) {
                    EntityPolarBear.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    EntityPolarBear.this.setStanding(true);
                    EntityPolarBear.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                EntityPolarBear.this.setStanding(false);
            }

        }

        @Override
        public void stop() {
            EntityPolarBear.this.setStanding(false);
            super.stop();
        }

        @Override
        protected double getAttackReachSqr(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getBbWidth());
        }
    }

    private class d extends PathfinderGoalPanic {

        public d() {
            super(EntityPolarBear.this, 2.0D);
        }

        @Override
        public boolean canUse() {
            return !EntityPolarBear.this.isBaby() && !EntityPolarBear.this.isOnFire() ? false : super.canUse();
        }
    }

    private class b extends PathfinderGoalHurtByTarget {

        public b() {
            super(EntityPolarBear.this);
        }

        @Override
        public void start() {
            super.start();
            if (EntityPolarBear.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        @Override
        protected void alertOther(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPolarBear && !entityinsentient.isBaby()) {
                super.alertOther(entityinsentient, entityliving);
            }

        }
    }

    private class a extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public a() {
            super(EntityPolarBear.this, EntityHuman.class, 20, true, true, (Predicate) null);
        }

        @Override
        public boolean canUse() {
            if (EntityPolarBear.this.isBaby()) {
                return false;
            } else {
                if (super.canUse()) {
                    List<EntityPolarBear> list = EntityPolarBear.this.level.getEntitiesOfClass(EntityPolarBear.class, EntityPolarBear.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityPolarBear entitypolarbear = (EntityPolarBear) iterator.next();

                        if (entitypolarbear.isBaby()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }
}
