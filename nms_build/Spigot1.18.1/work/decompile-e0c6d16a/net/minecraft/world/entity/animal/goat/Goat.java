package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
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
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemLiquidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;

public class Goat extends EntityAnimal {

    public static final EntitySize LONG_JUMPING_DIMENSIONS = EntitySize.scalable(0.9F, 1.3F).scale(0.7F);
    private static final int ADULT_ATTACK_DAMAGE = 2;
    private static final int BABY_ATTACK_DAMAGE = 1;
    protected static final ImmutableList<SensorType<? extends Sensor<? super Goat>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET});
    public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
    public static final double GOAT_SCREAMING_CHANCE = 0.02D;
    private static final DataWatcherObject<Boolean> DATA_IS_SCREAMING_GOAT = DataWatcher.defineId(Goat.class, DataWatcherRegistry.BOOLEAN);
    private boolean isLoweringHead;
    private int lowerHeadTick;

    public Goat(EntityTypes<? extends Goat> entitytypes, World world) {
        super(entitytypes, world);
        this.getNavigation().setCanFloat(true);
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
        } else {
            this.getAttribute(GenericAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
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
            GoatAi.initMemories(goat);
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
        GoatAi.initMemories(this);
        this.setScreamingGoat(worldaccess.getRandom().nextDouble() < 0.02D);
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
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setScreamingGoat(nbttagcompound.getBoolean("IsScreamingGoat"));
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

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new Goat.b(this, world);
    }

    public static boolean checkGoatSpawnRules(EntityTypes<? extends EntityAnimal> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.GOATS_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    private static class b extends Navigation {

        b(Goat goat, World world) {
            super(goat, world);
        }

        @Override
        protected Pathfinder createPathFinder(int i) {
            this.nodeEvaluator = new Goat.a();
            return new Pathfinder(this.nodeEvaluator, i);
        }
    }

    private static class a extends PathfinderNormal {

        private final BlockPosition.MutableBlockPosition belowPos = new BlockPosition.MutableBlockPosition();

        a() {}

        @Override
        public PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k) {
            this.belowPos.set(i, j - 1, k);
            PathType pathtype = getBlockPathTypeRaw(iblockaccess, this.belowPos);

            return pathtype == PathType.POWDER_SNOW ? PathType.BLOCKED : getBlockPathTypeStatic(iblockaccess, this.belowPos.move(EnumDirection.UP));
        }
    }
}
