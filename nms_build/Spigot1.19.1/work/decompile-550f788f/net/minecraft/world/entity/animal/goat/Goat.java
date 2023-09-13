package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemLiquidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class Goat extends EntityAnimal {

    public static final EntitySize LONG_JUMPING_DIMENSIONS = EntitySize.scalable(0.9F, 1.3F).scale(0.7F);
    private static final int ADULT_ATTACK_DAMAGE = 2;
    private static final int BABY_ATTACK_DAMAGE = 1;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Goat>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET, MemoryModuleType.IS_PANICKING});
    public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
    public static final double GOAT_SCREAMING_CHANCE = 0.02D;
    public static final double UNIHORN_CHANCE = 0.10000000149011612D;
    private static final DataWatcherObject<Boolean> DATA_IS_SCREAMING_GOAT = DataWatcher.defineId(Goat.class, DataWatcherRegistry.BOOLEAN);
    public static final DataWatcherObject<Boolean> DATA_HAS_LEFT_HORN = DataWatcher.defineId(Goat.class, DataWatcherRegistry.BOOLEAN);
    public static final DataWatcherObject<Boolean> DATA_HAS_RIGHT_HORN = DataWatcher.defineId(Goat.class, DataWatcherRegistry.BOOLEAN);
    private boolean isLoweringHead;
    private int lowerHeadTick;

    public Goat(EntityTypes<? extends Goat> entitytypes, World world) {
        super(entitytypes, world);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
    }

    public ItemStack createHorn() {
        RandomSource randomsource = RandomSource.create((long) this.getUUID().hashCode());
        TagKey<Instrument> tagkey = this.isScreamingGoat() ? InstrumentTags.SCREAMING_GOAT_HORNS : InstrumentTags.REGULAR_GOAT_HORNS;
        HolderSet<Instrument> holderset = IRegistry.INSTRUMENT.getOrCreateTag(tagkey);

        return InstrumentItem.create(Items.GOAT_HORN, (Holder) holderset.getRandomElement(randomsource).get());
    }

    @Override
    protected BehaviorController.b<Goat> brainProvider() {
        return BehaviorController.provider(Goat.MEMORY_TYPES, Goat.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return GoatAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D).add(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void ageBoundaryReached() {
        if (this.isBaby()) {
            this.getAttribute(GenericAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
            this.removeHorns();
        } else {
            this.getAttribute(GenericAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
            this.addHorns();
        }

    }

    @Override
    protected int calculateFallDamage(float f, float f1) {
        return super.calculateFallDamage(f, f1) - 10;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_AMBIENT : SoundEffects.GOAT_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_HURT : SoundEffects.GOAT_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_DEATH : SoundEffects.GOAT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.GOAT_STEP, 0.15F, 1.0F);
    }

    protected SoundEffect getMilkingSound() {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_MILK : SoundEffects.GOAT_MILK;
    }

    @Override
    public Goat getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        Goat goat = (Goat) EntityTypes.GOAT.create(worldserver);

        if (goat != null) {
            GoatAi.initMemories(goat, worldserver.getRandom());
            boolean flag = entityageable instanceof Goat && ((Goat) entityageable).isScreamingGoat();

            goat.setScreamingGoat(flag || worldserver.getRandom().nextDouble() < 0.02D);
        }

        return goat;
    }

    @Override
    public BehaviorController<Goat> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("goatBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("goatActivityUpdate");
        GoatAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public int getMaxHeadYRot() {
        return 15;
    }

    @Override
    public void setYHeadRot(float f) {
        int i = this.getMaxHeadYRot();
        float f1 = MathHelper.degreesDifference(this.yBodyRot, f);
        float f2 = MathHelper.clamp(f1, (float) (-i), (float) i);

        super.setYHeadRot(this.yBodyRot + f2);
    }

    @Override
    public SoundEffect getEatingSound(ItemStack itemstack) {
        return this.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_EAT : SoundEffects.GOAT_EAT;
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
            entityhuman.playSound(this.getMilkingSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = ItemLiquidUtil.createFilledResult(itemstack, entityhuman, Items.MILK_BUCKET.getDefaultInstance());

            entityhuman.setItemInHand(enumhand, itemstack1);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = super.mobInteract(entityhuman, enumhand);

            if (enuminteractionresult.consumesAction() && this.isFood(itemstack)) {
                this.level.playSound((EntityHuman) null, (Entity) this, this.getEatingSound(itemstack), SoundCategory.NEUTRAL, 1.0F, MathHelper.randomBetween(this.level.random, 0.8F, 1.2F));
            }

            return enuminteractionresult;
        }
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        RandomSource randomsource = worldaccess.getRandom();

        GoatAi.initMemories(this, randomsource);
        this.setScreamingGoat(randomsource.nextDouble() < 0.02D);
        this.ageBoundaryReached();
        if (!this.isBaby() && (double) randomsource.nextFloat() < 0.10000000149011612D) {
            DataWatcherObject<Boolean> datawatcherobject = randomsource.nextBoolean() ? Goat.DATA_HAS_LEFT_HORN : Goat.DATA_HAS_RIGHT_HORN;

            this.entityData.set(datawatcherobject, false);
        }

        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return entitypose == EntityPose.LONG_JUMPING ? Goat.LONG_JUMPING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(entitypose);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("IsScreamingGoat", this.isScreamingGoat());
        nbttagcompound.putBoolean("HasLeftHorn", this.hasLeftHorn());
        nbttagcompound.putBoolean("HasRightHorn", this.hasRightHorn());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setScreamingGoat(nbttagcompound.getBoolean("IsScreamingGoat"));
        this.entityData.set(Goat.DATA_HAS_LEFT_HORN, nbttagcompound.getBoolean("HasLeftHorn"));
        this.entityData.set(Goat.DATA_HAS_RIGHT_HORN, nbttagcompound.getBoolean("HasRightHorn"));
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 58) {
            this.isLoweringHead = true;
        } else if (b0 == 59) {
            this.isLoweringHead = false;
        } else {
            super.handleEntityEvent(b0);
        }

    }

    @Override
    public void aiStep() {
        if (this.isLoweringHead) {
            ++this.lowerHeadTick;
        } else {
            this.lowerHeadTick -= 2;
        }

        this.lowerHeadTick = MathHelper.clamp(this.lowerHeadTick, (int) 0, (int) 20);
        super.aiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Goat.DATA_IS_SCREAMING_GOAT, false);
        this.entityData.define(Goat.DATA_HAS_LEFT_HORN, true);
        this.entityData.define(Goat.DATA_HAS_RIGHT_HORN, true);
    }

    public boolean hasLeftHorn() {
        return (Boolean) this.entityData.get(Goat.DATA_HAS_LEFT_HORN);
    }

    public boolean hasRightHorn() {
        return (Boolean) this.entityData.get(Goat.DATA_HAS_RIGHT_HORN);
    }

    public boolean dropHorn() {
        boolean flag = this.hasLeftHorn();
        boolean flag1 = this.hasRightHorn();

        if (!flag && !flag1) {
            return false;
        } else {
            DataWatcherObject datawatcherobject;

            if (!flag) {
                datawatcherobject = Goat.DATA_HAS_RIGHT_HORN;
            } else if (!flag1) {
                datawatcherobject = Goat.DATA_HAS_LEFT_HORN;
            } else {
                datawatcherobject = this.random.nextBoolean() ? Goat.DATA_HAS_LEFT_HORN : Goat.DATA_HAS_RIGHT_HORN;
            }

            this.entityData.set(datawatcherobject, false);
            Vec3D vec3d = this.position();
            ItemStack itemstack = this.createHorn();
            double d0 = (double) MathHelper.randomBetween(this.random, -0.2F, 0.2F);
            double d1 = (double) MathHelper.randomBetween(this.random, 0.3F, 0.7F);
            double d2 = (double) MathHelper.randomBetween(this.random, -0.2F, 0.2F);
            EntityItem entityitem = new EntityItem(this.level, vec3d.x(), vec3d.y(), vec3d.z(), itemstack, d0, d1, d2);

            this.level.addFreshEntity(entityitem);
            return true;
        }
    }

    public void addHorns() {
        this.entityData.set(Goat.DATA_HAS_LEFT_HORN, true);
        this.entityData.set(Goat.DATA_HAS_RIGHT_HORN, true);
    }

    public void removeHorns() {
        this.entityData.set(Goat.DATA_HAS_LEFT_HORN, false);
        this.entityData.set(Goat.DATA_HAS_RIGHT_HORN, false);
    }

    public boolean isScreamingGoat() {
        return (Boolean) this.entityData.get(Goat.DATA_IS_SCREAMING_GOAT);
    }

    public void setScreamingGoat(boolean flag) {
        this.entityData.set(Goat.DATA_IS_SCREAMING_GOAT, flag);
    }

    public float getRammingXHeadRot() {
        return (float) this.lowerHeadTick / 20.0F * 30.0F * 0.017453292F;
    }

    public static boolean checkGoatSpawnRules(EntityTypes<? extends EntityAnimal> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, RandomSource randomsource) {
        return generatoraccess.getBlockState(blockposition.below()).is(TagsBlock.GOATS_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }
}
