package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3fa;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Collection;
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
    private static final DataWatcherObject<Integer> DATA_VARIANT = DataWatcher.a(Axolotl.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_PLAYING_DEAD = DataWatcher.a(Axolotl.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> FROM_BUCKET = DataWatcher.a(Axolotl.class, DataWatcherRegistry.BOOLEAN);
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
        this.a(PathType.WATER, 0.0F);
        this.moveControl = new Axolotl.c(this);
        this.lookControl = new Axolotl.b(this, 20);
        this.maxUpStep = 1.0F;
    }

    @Override
    public Map<String, Vector3fa> a() {
        return this.modelRotationValues;
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.0F;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(Axolotl.DATA_VARIANT, 0);
        this.entityData.register(Axolotl.DATA_PLAYING_DEAD, false);
        this.entityData.register(Axolotl.FROM_BUCKET, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant().a());
        nbttagcompound.setBoolean("FromBucket", this.isFromBucket());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setVariant(Axolotl.Variant.BY_ID[nbttagcompound.getInt("Variant")]);
        this.setFromBucket(nbttagcompound.getBoolean("FromBucket"));
    }

    @Override
    public void K() {
        if (!this.isPlayingDead()) {
            super.K();
        }
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        boolean flag = false;

        if (enummobspawn == EnumMobSpawn.BUCKET) {
            return (GroupDataEntity) groupdataentity;
        } else {
            if (groupdataentity instanceof Axolotl.a) {
                if (((Axolotl.a) groupdataentity).a() >= 2) {
                    flag = true;
                }
            } else {
                groupdataentity = new Axolotl.a(new Axolotl.Variant[]{Axolotl.Variant.a(this.level.random), Axolotl.Variant.a(this.level.random)});
            }

            this.setVariant(((Axolotl.a) groupdataentity).a(this.level.random));
            if (flag) {
                this.setAgeRaw(-24000);
            }

            return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
        }
    }

    @Override
    public void entityBaseTick() {
        int i = this.getAirTicks();

        super.entityBaseTick();
        if (!this.isNoAI()) {
            this.u(i);
        }

    }

    protected void u(int i) {
        if (this.isAlive() && !this.aN()) {
            this.setAirTicks(i - 1);
            if (this.getAirTicks() == -20) {
                this.setAirTicks(0);
                this.damageEntity(DamageSource.DRY_OUT, 2.0F);
            }
        } else {
            this.setAirTicks(this.bS());
        }

    }

    public void fw() {
        int i = this.getAirTicks() + 1800;

        this.setAirTicks(Math.min(i, this.bS()));
    }

    @Override
    public int bS() {
        return 6000;
    }

    public Axolotl.Variant getVariant() {
        return Axolotl.Variant.BY_ID[(Integer) this.entityData.get(Axolotl.DATA_VARIANT)];
    }

    public void setVariant(Axolotl.Variant axolotl_variant) {
        this.entityData.set(Axolotl.DATA_VARIANT, axolotl_variant.a());
    }

    private static boolean a(Random random) {
        return random.nextInt(1200) == 0;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.f((Entity) this);
    }

    @Override
    public boolean dr() {
        return true;
    }

    @Override
    public boolean ck() {
        return false;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.WATER;
    }

    public void setPlayingDead(boolean flag) {
        this.entityData.set(Axolotl.DATA_PLAYING_DEAD, flag);
    }

    public boolean isPlayingDead() {
        return (Boolean) this.entityData.get(Axolotl.DATA_PLAYING_DEAD);
    }

    @Override
    public boolean isFromBucket() {
        return (Boolean) this.entityData.get(Axolotl.FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean flag) {
        this.entityData.set(Axolotl.FROM_BUCKET, flag);
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        Axolotl axolotl = (Axolotl) EntityTypes.AXOLOTL.a((World) worldserver);

        if (axolotl != null) {
            Axolotl.Variant axolotl_variant;

            if (a(this.random)) {
                axolotl_variant = Axolotl.Variant.b(this.random);
            } else {
                axolotl_variant = this.random.nextBoolean() ? this.getVariant() : ((Axolotl) entityageable).getVariant();
            }

            axolotl.setVariant(axolotl_variant);
            axolotl.setPersistent();
        }

        return axolotl;
    }

    @Override
    public double i(EntityLiving entityliving) {
        return 1.5D + (double) entityliving.getWidth() * 2.0D;
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return TagsItem.AXOLOTL_TEMPT_ITEMS.isTagged(itemstack.getItem());
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    protected void mobTick() {
        this.level.getMethodProfiler().enter("axolotlBrain");
        this.getBehaviorController().a((WorldServer) this.level, (EntityLiving) this);
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("axolotlActivityUpdate");
        AxolotlAi.a(this);
        this.level.getMethodProfiler().exit();
        if (!this.isNoAI()) {
            Optional<Integer> optional = this.getBehaviorController().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);

            this.setPlayingDead(optional.isPresent() && (Integer) optional.get() > 0);
        }

    }

    public static AttributeProvider.Builder fE() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 14.0D).a(GenericAttributes.MOVEMENT_SPEED, 1.0D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new Axolotl.d(this, world);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.b(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.a((EntityLiving) this, entity);
            this.playSound(SoundEffects.AXOLOTL_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        float f1 = this.getHealth();

        if (!this.level.isClientSide && !this.isNoAI() && this.level.random.nextInt(3) == 0 && ((float) this.level.random.nextInt(3) < f || f1 / this.getMaxHealth() < 0.5F) && f < f1 && this.isInWater() && (damagesource.getEntity() != null || damagesource.k() != null) && !this.isPlayingDead()) {
            this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, (int) 200);
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.655F;
    }

    @Override
    public int eZ() {
        return 1;
    }

    @Override
    public int fa() {
        return 1;
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        return (EnumInteractionResult) Bucketable.a(entityhuman, enumhand, this).orElse(super.b(entityhuman, enumhand));
    }

    @Override
    public void setBucketName(ItemStack itemstack) {
        Bucketable.a(this, itemstack);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.setInt("Variant", this.getVariant().a());
        nbttagcompound.setInt("Age", this.getAge());
        BehaviorController<?> behaviorcontroller = this.getBehaviorController();

        if (behaviorcontroller.hasMemory(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            nbttagcompound.setLong("HuntingCooldown", behaviorcontroller.d(MemoryModuleType.HAS_HUNTING_COOLDOWN));
        }

    }

    @Override
    public void c(NBTTagCompound nbttagcompound) {
        Bucketable.a(this, nbttagcompound);
        this.setVariant(Axolotl.Variant.BY_ID[nbttagcompound.getInt("Variant")]);
        if (nbttagcompound.hasKey("Age")) {
            this.setAgeRaw(nbttagcompound.getInt("Age"));
        }

        if (nbttagcompound.hasKey("HuntingCooldown")) {
            this.getBehaviorController().a(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, nbttagcompound.getLong("HuntingCooldown"));
        }

    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(Items.AXOLOTL_BUCKET);
    }

    @Override
    public SoundEffect t() {
        return SoundEffects.BUCKET_FILL_AXOLOTL;
    }

    @Override
    public boolean dN() {
        return !this.isPlayingDead() && super.dN();
    }

    public static void a(Axolotl axolotl) {
        Optional<EntityLiving> optional = axolotl.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET);

        if (optional.isPresent()) {
            World world = axolotl.level;
            EntityLiving entityliving = (EntityLiving) optional.get();

            if (entityliving.dV()) {
                DamageSource damagesource = entityliving.dW();

                if (damagesource != null) {
                    Entity entity = damagesource.getEntity();

                    if (entity != null && entity.getEntityType() == EntityTypes.PLAYER) {
                        EntityHuman entityhuman = (EntityHuman) entity;
                        List<EntityHuman> list = world.a(EntityHuman.class, axolotl.getBoundingBox().g(20.0D));

                        if (list.contains(entityhuman)) {
                            axolotl.f(entityhuman);
                        }
                    }
                }
            }

        }
    }

    public void f(EntityHuman entityhuman) {
        MobEffect mobeffect = entityhuman.getEffect(MobEffects.REGENERATION);
        int i = mobeffect != null ? mobeffect.getDuration() : 0;

        if (i < 2400) {
            i = Math.min(2400, 100 + i);
            entityhuman.addEffect(new MobEffect(MobEffects.REGENERATION, i, 0), this);
        }

        entityhuman.removeEffect(MobEffects.DIG_SLOWDOWN);
    }

    @Override
    public boolean isSpecialPersistence() {
        return super.isSpecialPersistence() || this.isFromBucket();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.AXOLOTL_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.AXOLOTL_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isInWater() ? SoundEffects.AXOLOTL_IDLE_WATER : SoundEffects.AXOLOTL_IDLE_AIR;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.AXOLOTL_SPLASH;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.AXOLOTL_SWIM;
    }

    @Override
    protected BehaviorController.b<Axolotl> dp() {
        return BehaviorController.a((Collection) Axolotl.MEMORY_TYPES, (Collection) Axolotl.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return AxolotlAi.a(this.dp().a(dynamic));
    }

    @Override
    public BehaviorController<Axolotl> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    protected void R() {
        super.R();
        PacketDebug.a((EntityLiving) this);
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater()) {
            this.a(this.ew(), vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
        } else {
            super.g(vec3d);
        }

    }

    @Override
    protected void a(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (itemstack.a(Items.TROPICAL_FISH_BUCKET)) {
            entityhuman.a(enumhand, new ItemStack(Items.WATER_BUCKET));
        } else {
            super.a(entityhuman, enumhand, itemstack);
        }

    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    private static class c extends SmoothSwimmingMoveControl {

        private final Axolotl axolotl;

        public c(Axolotl axolotl) {
            super(axolotl, 85, 10, 0.1F, 0.5F, false);
            this.axolotl = axolotl;
        }

        @Override
        public void a() {
            if (!this.axolotl.isPlayingDead()) {
                super.a();
            }

        }
    }

    private class b extends SmoothSwimmingLookControl {

        public b(Axolotl axolotl, int i) {
            super(axolotl, i);
        }

        @Override
        public void a() {
            if (!Axolotl.this.isPlayingDead()) {
                super.a();
            }

        }
    }

    public static enum Variant {

        LUCY(0, "lucy", true), WILD(1, "wild", true), GOLD(2, "gold", true), CYAN(3, "cyan", true), BLUE(4, "blue", false);

        public static final Axolotl.Variant[] BY_ID = (Axolotl.Variant[]) Arrays.stream(values()).sorted(Comparator.comparingInt(Axolotl.Variant::a)).toArray((i) -> {
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

        public int a() {
            return this.id;
        }

        public String b() {
            return this.name;
        }

        public static Axolotl.Variant a(Random random) {
            return a(random, true);
        }

        public static Axolotl.Variant b(Random random) {
            return a(random, false);
        }

        private static Axolotl.Variant a(Random random, boolean flag) {
            Axolotl.Variant[] aaxolotl_variant = (Axolotl.Variant[]) Arrays.stream(Axolotl.Variant.BY_ID).filter((axolotl_variant) -> {
                return axolotl_variant.common == flag;
            }).toArray((i) -> {
                return new Axolotl.Variant[i];
            });

            return (Axolotl.Variant) SystemUtils.a((Object[]) aaxolotl_variant, random);
        }
    }

    public static class a extends EntityAgeable.a {

        public final Axolotl.Variant[] types;

        public a(Axolotl.Variant... aaxolotl_variant) {
            super(false);
            this.types = aaxolotl_variant;
        }

        public Axolotl.Variant a(Random random) {
            return this.types[random.nextInt(this.types.length)];
        }
    }

    private static class d extends NavigationGuardian {

        d(Axolotl axolotl, World world) {
            super(axolotl, world);
        }

        @Override
        protected boolean a() {
            return true;
        }

        @Override
        protected Pathfinder a(int i) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
            return new Pathfinder(this.nodeEvaluator, i);
        }

        @Override
        public boolean a(BlockPosition blockposition) {
            return !this.level.getType(blockposition.down()).isAir();
        }
    }
}
