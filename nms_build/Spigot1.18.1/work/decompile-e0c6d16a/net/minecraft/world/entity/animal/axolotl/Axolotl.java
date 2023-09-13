package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3fa;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.LerpingModel;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.phys.Vec3D;

public class Axolotl extends EntityAnimal implements LerpingModel, Bucketable {

    public static final int TOTAL_PLAYDEAD_TIME = 200;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Axolotl>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN});
    private static final DataWatcherObject<Integer> DATA_VARIANT = DataWatcher.defineId(Axolotl.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_PLAYING_DEAD = DataWatcher.defineId(Axolotl.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> FROM_BUCKET = DataWatcher.defineId(Axolotl.class, DataWatcherRegistry.BOOLEAN);
    public static final double PLAYER_REGEN_DETECTION_RANGE = 20.0D;
    public static final int RARE_VARIANT_CHANCE = 1200;
    private static final int AXOLOTL_TOTAL_AIR_SUPPLY = 6000;
    public static final String VARIANT_TAG = "Variant";
    private static final int REHYDRATE_AIR_SUPPLY = 1800;
    private static final int REGEN_BUFF_MAX_DURATION = 2400;
    private final Map<String, Vector3fa> modelRotationValues = Maps.newHashMap();
    private static final int REGEN_BUFF_BASE_DURATION = 100;

    public Axolotl(EntityTypes<? extends Axolotl> entitytypes, World world) {
        super(entitytypes, world);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new Axolotl.c(this);
        this.lookControl = new Axolotl.b(this, 20);
        this.maxUpStep = 1.0F;
    }

    @Override
    public Map<String, Vector3fa> getModelRotationValues() {
        return this.modelRotationValues;
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.0F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Axolotl.DATA_VARIANT, 0);
        this.entityData.define(Axolotl.DATA_PLAYING_DEAD, false);
        this.entityData.define(Axolotl.FROM_BUCKET, false);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Variant", this.getVariant().getId());
        nbttagcompound.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setVariant(Axolotl.Variant.BY_ID[nbttagcompound.getInt("Variant")]);
        this.setFromBucket(nbttagcompound.getBoolean("FromBucket"));
    }

    @Override
    public void playAmbientSound() {
        if (!this.isPlayingDead()) {
            super.playAmbientSound();
        }
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        boolean flag = false;

        if (enummobspawn == EnumMobSpawn.BUCKET) {
            return (GroupDataEntity) groupdataentity;
        } else {
            if (groupdataentity instanceof Axolotl.a) {
                if (((Axolotl.a) groupdataentity).getGroupSize() >= 2) {
                    flag = true;
                }
            } else {
                groupdataentity = new Axolotl.a(new Axolotl.Variant[]{Axolotl.Variant.getCommonSpawnVariant(this.level.random), Axolotl.Variant.getCommonSpawnVariant(this.level.random)});
            }

            this.setVariant(((Axolotl.a) groupdataentity).getVariant(this.level.random));
            if (flag) {
                this.setAge(-24000);
            }

            return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        if (!this.isNoAi()) {
            this.handleAirSupply(i);
        }

    }

    protected void handleAirSupply(int i) {
        if (this.isAlive() && !this.isInWaterRainOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DRY_OUT, 2.0F);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }

    }

    public void rehydrate() {
        int i = this.getAirSupply() + 1800;

        this.setAirSupply(Math.min(i, this.getMaxAirSupply()));
    }

    @Override
    public int getMaxAirSupply() {
        return 6000;
    }

    public Axolotl.Variant getVariant() {
        return Axolotl.Variant.BY_ID[(Integer) this.entityData.get(Axolotl.DATA_VARIANT)];
    }

    public void setVariant(Axolotl.Variant axolotl_variant) {
        this.entityData.set(Axolotl.DATA_VARIANT, axolotl_variant.getId());
    }

    private static boolean useRareVariant(Random random) {
        return random.nextInt(1200) == 0;
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return iworldreader.isUnobstructed(this);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.WATER;
    }

    public void setPlayingDead(boolean flag) {
        this.entityData.set(Axolotl.DATA_PLAYING_DEAD, flag);
    }

    public boolean isPlayingDead() {
        return (Boolean) this.entityData.get(Axolotl.DATA_PLAYING_DEAD);
    }

    @Override
    public boolean fromBucket() {
        return (Boolean) this.entityData.get(Axolotl.FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean flag) {
        this.entityData.set(Axolotl.FROM_BUCKET, flag);
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        Axolotl axolotl = (Axolotl) EntityTypes.AXOLOTL.create(worldserver);

        if (axolotl != null) {
            Axolotl.Variant axolotl_variant;

            if (useRareVariant(this.random)) {
                axolotl_variant = Axolotl.Variant.getRareSpawnVariant(this.random);
            } else {
                axolotl_variant = this.random.nextBoolean() ? this.getVariant() : ((Axolotl) entityageable).getVariant();
            }

            axolotl.setVariant(axolotl_variant);
            axolotl.setPersistenceRequired();
        }

        return axolotl;
    }

    @Override
    public double getMeleeAttackRangeSqr(EntityLiving entityliving) {
        return 1.5D + (double) entityliving.getBbWidth() * 2.0D;
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return TagsItem.AXOLOTL_TEMPT_ITEMS.contains(itemstack.getItem());
    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("axolotlBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("axolotlActivityUpdate");
        AxolotlAi.updateActivity(this);
        this.level.getProfiler().pop();
        if (!this.isNoAi()) {
            Optional<Integer> optional = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);

            this.setPlayingDead(optional.isPresent() && (Integer) optional.get() > 0);
        }

    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 14.0D).add(GenericAttributes.MOVEMENT_SPEED, 1.0D).add(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new Axolotl.d(this, world);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = entity.hurt(DamageSource.mobAttack(this), (float) ((int) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.doEnchantDamageEffects(this, entity);
            this.playSound(SoundEffects.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        float f1 = this.getHealth();

        if (!this.level.isClientSide && !this.isNoAi() && this.level.random.nextInt(3) == 0 && ((float) this.level.random.nextInt(3) < f || f1 / this.getMaxHealth() < 0.5F) && f < f1 && this.isInWater() && (damagesource.getEntity() != null || damagesource.getDirectEntity() != null) && !this.isPlayingDead()) {
            this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, (int) 200);
        }

        return super.hurt(damagesource, f);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.655F;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        return (EnumInteractionResult) Bucketable.bucketMobPickup(entityhuman, enumhand, this).orElse(super.mobInteract(entityhuman, enumhand));
    }

    @Override
    public void saveToBucketTag(ItemStack itemstack) {
        Bucketable.saveDefaultDataToBucketTag(this, itemstack);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.putInt("Variant", this.getVariant().getId());
        nbttagcompound.putInt("Age", this.getAge());
        BehaviorController<?> behaviorcontroller = this.getBrain();

        if (behaviorcontroller.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            nbttagcompound.putLong("HuntingCooldown", behaviorcontroller.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
        }

    }

    @Override
    public void loadFromBucketTag(NBTTagCompound nbttagcompound) {
        Bucketable.loadDefaultDataFromBucketTag(this, nbttagcompound);
        this.setVariant(Axolotl.Variant.BY_ID[nbttagcompound.getInt("Variant")]);
        if (nbttagcompound.contains("Age")) {
            this.setAge(nbttagcompound.getInt("Age"));
        }

        if (nbttagcompound.contains("HuntingCooldown")) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, nbttagcompound.getLong("HuntingCooldown"));
        }

    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.AXOLOTL_BUCKET);
    }

    @Override
    public SoundEffect getPickupSound() {
        return SoundEffects.BUCKET_FILL_AXOLOTL;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isPlayingDead() && super.canBeSeenAsEnemy();
    }

    public static void onStopAttacking(Axolotl axolotl) {
        Optional<EntityLiving> optional = axolotl.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);

        if (optional.isPresent()) {
            World world = axolotl.level;
            EntityLiving entityliving = (EntityLiving) optional.get();

            if (entityliving.isDeadOrDying()) {
                DamageSource damagesource = entityliving.getLastDamageSource();

                if (damagesource != null) {
                    Entity entity = damagesource.getEntity();

                    if (entity != null && entity.getType() == EntityTypes.PLAYER) {
                        EntityHuman entityhuman = (EntityHuman) entity;
                        List<EntityHuman> list = world.getEntitiesOfClass(EntityHuman.class, axolotl.getBoundingBox().inflate(20.0D));

                        if (list.contains(entityhuman)) {
                            axolotl.applySupportingEffects(entityhuman);
                        }
                    }
                }
            }

        }
    }

    public void applySupportingEffects(EntityHuman entityhuman) {
        MobEffect mobeffect = entityhuman.getEffect(MobEffects.REGENERATION);
        int i = mobeffect != null ? mobeffect.getDuration() : 0;

        if (i < 2400) {
            i = Math.min(2400, 100 + i);
            entityhuman.addEffect(new MobEffect(MobEffects.REGENERATION, i, 0), this);
        }

        entityhuman.removeEffect(MobEffects.DIG_SLOWDOWN);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.AXOLOTL_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.AXOLOTL_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return this.isInWater() ? SoundEffects.AXOLOTL_IDLE_WATER : SoundEffects.AXOLOTL_IDLE_AIR;
    }

    @Override
    protected SoundEffect getSwimSplashSound() {
        return SoundEffects.AXOLOTL_SPLASH;
    }

    @Override
    protected SoundEffect getSwimSound() {
        return SoundEffects.AXOLOTL_SWIM;
    }

    @Override
    protected BehaviorController.b<Axolotl> brainProvider() {
        return BehaviorController.provider(Axolotl.MEMORY_TYPES, Axolotl.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return AxolotlAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public BehaviorController<Axolotl> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(vec3d);
        }

    }

    @Override
    protected void usePlayerItem(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (itemstack.is(Items.TROPICAL_FISH_BUCKET)) {
            entityhuman.setItemInHand(enumhand, new ItemStack(Items.WATER_BUCKET));
        } else {
            super.usePlayerItem(entityhuman, enumhand, itemstack);
        }

    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public static boolean checkAxolotlSpawnRules(EntityTypes<? extends EntityLiving> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return worldaccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.AXOLOTLS_SPAWNABLE_ON);
    }

    private static class c extends SmoothSwimmingMoveControl {

        private final Axolotl axolotl;

        public c(Axolotl axolotl) {
            super(axolotl, 85, 10, 0.1F, 0.5F, false);
            this.axolotl = axolotl;
        }

        @Override
        public void tick() {
            if (!this.axolotl.isPlayingDead()) {
                super.tick();
            }

        }
    }

    private class b extends SmoothSwimmingLookControl {

        public b(Axolotl axolotl, int i) {
            super(axolotl, i);
        }

        @Override
        public void tick() {
            if (!Axolotl.this.isPlayingDead()) {
                super.tick();
            }

        }
    }

    public static enum Variant {

        LUCY(0, "lucy", true), WILD(1, "wild", true), GOLD(2, "gold", true), CYAN(3, "cyan", true), BLUE(4, "blue", false);

        public static final Axolotl.Variant[] BY_ID = (Axolotl.Variant[]) Arrays.stream(values()).sorted(Comparator.comparingInt(Axolotl.Variant::getId)).toArray((i) -> {
            return new Axolotl.Variant[i];
        });
        private final int id;
        private final String name;
        private final boolean common;

        private Variant(int i, String s, boolean flag) {
            this.id = i;
            this.name = s;
            this.common = flag;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Axolotl.Variant getCommonSpawnVariant(Random random) {
            return getSpawnVariant(random, true);
        }

        public static Axolotl.Variant getRareSpawnVariant(Random random) {
            return getSpawnVariant(random, false);
        }

        private static Axolotl.Variant getSpawnVariant(Random random, boolean flag) {
            Axolotl.Variant[] aaxolotl_variant = (Axolotl.Variant[]) Arrays.stream(Axolotl.Variant.BY_ID).filter((axolotl_variant) -> {
                return axolotl_variant.common == flag;
            }).toArray((i) -> {
                return new Axolotl.Variant[i];
            });

            return (Axolotl.Variant) SystemUtils.getRandom((Object[]) aaxolotl_variant, random);
        }
    }

    public static class a extends EntityAgeable.a {

        public final Axolotl.Variant[] types;

        public a(Axolotl.Variant... aaxolotl_variant) {
            super(false);
            this.types = aaxolotl_variant;
        }

        public Axolotl.Variant getVariant(Random random) {
            return this.types[random.nextInt(this.types.length)];
        }
    }

    private static class d extends NavigationGuardian {

        d(Axolotl axolotl, World world) {
            super(axolotl, world);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        protected Pathfinder createPathFinder(int i) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
            return new Pathfinder(this.nodeEvaluator, i);
        }

        @Override
        public boolean isStableDestination(BlockPosition blockposition) {
            return !this.level.getBlockState(blockposition.below()).isAir();
        }
    }
}
