package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.ICrossbow;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityPiglin extends EntityPiglinAbstract implements ICrossbow, InventoryCarrier {

    private static final DataWatcherObject<Boolean> DATA_BABY_ID = DataWatcher.defineId(EntityPiglin.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_IS_CHARGING_CROSSBOW = DataWatcher.defineId(EntityPiglin.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_IS_DANCING = DataWatcher.defineId(EntityPiglin.class, DataWatcherRegistry.BOOLEAN);
    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(EntityPiglin.SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.20000000298023224D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final int MAX_HEALTH = 16;
    private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.35F;
    private static final int ATTACK_DAMAGE = 5;
    private static final float CROSSBOW_POWER = 1.6F;
    private static final float CHANCE_OF_WEARING_EACH_ARMOUR_ITEM = 0.1F;
    private static final int MAX_PASSENGERS_ON_ONE_HOGLIN = 3;
    private static final float PROBABILITY_OF_SPAWNING_AS_BABY = 0.2F;
    private static final float BABY_EYE_HEIGHT_ADJUSTMENT = 0.81F;
    private static final double PROBABILITY_OF_SPAWNING_WITH_CROSSBOW_INSTEAD_OF_SWORD = 0.5D;
    public final InventorySubcontainer inventory = new InventorySubcontainer(8);
    public boolean cannotHunt;
    protected static final ImmutableList<SensorType<? extends Sensor<? super EntityPiglin>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT});

    public EntityPiglin(EntityTypes<? extends EntityPiglinAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.putBoolean("IsBaby", true);
        }

        if (this.cannotHunt) {
            nbttagcompound.putBoolean("CannotHunt", true);
        }

        nbttagcompound.put("Inventory", this.inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setBaby(nbttagcompound.getBoolean("IsBaby"));
        this.setCannotHunt(nbttagcompound.getBoolean("CannotHunt"));
        this.inventory.fromTag(nbttagcompound.getList("Inventory", 10));
    }

    @VisibleForDebug
    @Override
    public IInventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropCustomDeathLoot(damagesource, i, flag);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
    }

    protected ItemStack addToInventory(ItemStack itemstack) {
        return this.inventory.addItem(itemstack);
    }

    protected boolean canAddToInventory(ItemStack itemstack) {
        return this.inventory.canAddItem(itemstack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityPiglin.DATA_BABY_ID, false);
        this.entityData.define(EntityPiglin.DATA_IS_CHARGING_CROSSBOW, false);
        this.entityData.define(EntityPiglin.DATA_IS_DANCING, false);
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        super.onSyncedDataUpdated(datawatcherobject);
        if (EntityPiglin.DATA_BABY_ID.equals(datawatcherobject)) {
            this.refreshDimensions();
        }

    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MAX_HEALTH, 16.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).add(GenericAttributes.ATTACK_DAMAGE, 5.0D);
    }

    public static boolean checkPiglinSpawnRules(EntityTypes<EntityPiglin> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return !generatoraccess.getBlockState(blockposition.below()).is(Blocks.NETHER_WART_BLOCK);
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn != EnumMobSpawn.STRUCTURE) {
            if (worldaccess.getRandom().nextFloat() < 0.2F) {
                this.setBaby(true);
            } else if (this.isAdult()) {
                this.setItemSlot(EnumItemSlot.MAINHAND, this.createSpawnWeapon());
            }
        }

        PiglinAI.initMemories(this);
        this.populateDefaultEquipmentSlots(difficultydamagescaler);
        this.populateDefaultEquipmentEnchantments(difficultydamagescaler);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return !this.isPersistenceRequired();
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyDamageScaler difficultydamagescaler) {
        if (this.isAdult()) {
            this.maybeWearArmor(EnumItemSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.maybeWearArmor(EnumItemSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.maybeWearArmor(EnumItemSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.maybeWearArmor(EnumItemSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }

    }

    private void maybeWearArmor(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (this.level.random.nextFloat() < 0.1F) {
            this.setItemSlot(enumitemslot, itemstack);
        }

    }

    @Override
    protected BehaviorController.b<EntityPiglin> brainProvider() {
        return BehaviorController.provider(EntityPiglin.MEMORY_TYPES, EntityPiglin.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return PiglinAI.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public BehaviorController<EntityPiglin> getBrain() {
        return super.getBrain();
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        EnumInteractionResult enuminteractionresult = super.mobInteract(entityhuman, enumhand);

        if (enuminteractionresult.consumesAction()) {
            return enuminteractionresult;
        } else if (!this.level.isClientSide) {
            return PiglinAI.mobInteract(this, entityhuman, enumhand);
        } else {
            boolean flag = PiglinAI.canAdmire(this, entityhuman.getItemInHand(enumhand)) && this.getArmPose() != EntityPiglinArmPose.ADMIRING_ITEM;

            return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        }
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double) this.getBbHeight() * 0.92D;
    }

    @Override
    public void setBaby(boolean flag) {
        this.getEntityData().set(EntityPiglin.DATA_BABY_ID, flag);
        if (!this.level.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttribute(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityPiglin.SPEED_MODIFIER_BABY);
            if (flag) {
                attributemodifiable.addTransientModifier(EntityPiglin.SPEED_MODIFIER_BABY);
            }
        }

    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getEntityData().get(EntityPiglin.DATA_BABY_ID);
    }

    private void setCannotHunt(boolean flag) {
        this.cannotHunt = flag;
    }

    @Override
    protected boolean canHunt() {
        return !this.cannotHunt;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("piglinBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        PiglinAI.updateActivity(this);
        super.customServerAiStep();
    }

    @Override
    protected int getExperienceReward(EntityHuman entityhuman) {
        return this.xpReward;
    }

    @Override
    protected void finishConversion(WorldServer worldserver) {
        PiglinAI.cancelAdmiring(this);
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        super.finishConversion(worldserver);
    }

    private ItemStack createSpawnWeapon() {
        return (double) this.random.nextFloat() < 0.5D ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    private boolean isChargingCrossbow() {
        return (Boolean) this.entityData.get(EntityPiglin.DATA_IS_CHARGING_CROSSBOW);
    }

    @Override
    public void setChargingCrossbow(boolean flag) {
        this.entityData.set(EntityPiglin.DATA_IS_CHARGING_CROSSBOW, flag);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public EntityPiglinArmPose getArmPose() {
        return this.isDancing() ? EntityPiglinArmPose.DANCING : (PiglinAI.isLovedItem(this.getOffhandItem()) ? EntityPiglinArmPose.ADMIRING_ITEM : (this.isAggressive() && this.isHoldingMeleeWeapon() ? EntityPiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : (this.isChargingCrossbow() ? EntityPiglinArmPose.CROSSBOW_CHARGE : (this.isAggressive() && this.isHolding(Items.CROSSBOW) ? EntityPiglinArmPose.CROSSBOW_HOLD : EntityPiglinArmPose.DEFAULT))));
    }

    public boolean isDancing() {
        return (Boolean) this.entityData.get(EntityPiglin.DATA_IS_DANCING);
    }

    public void setDancing(boolean flag) {
        this.entityData.set(EntityPiglin.DATA_IS_DANCING, flag);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        boolean flag = super.hurt(damagesource, f);

        if (this.level.isClientSide) {
            return false;
        } else {
            if (flag && damagesource.getEntity() instanceof EntityLiving) {
                PiglinAI.wasHurtBy(this, (EntityLiving) damagesource.getEntity());
            }

            return flag;
        }
    }

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        this.performCrossbowAttack(this, 1.6F);
    }

    @Override
    public void shootCrossbowProjectile(EntityLiving entityliving, ItemStack itemstack, IProjectile iprojectile, float f) {
        this.shootCrossbowProjectile(this, entityliving, iprojectile, f, 1.6F);
    }

    @Override
    public boolean canFireProjectileWeapon(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.CROSSBOW;
    }

    protected void holdInMainHand(ItemStack itemstack) {
        this.setItemSlotAndDropWhenKilled(EnumItemSlot.MAINHAND, itemstack);
    }

    protected void holdInOffHand(ItemStack itemstack) {
        if (itemstack.is(PiglinAI.BARTERING_ITEM)) {
            this.setItemSlot(EnumItemSlot.OFFHAND, itemstack);
            this.setGuaranteedDrop(EnumItemSlot.OFFHAND);
        } else {
            this.setItemSlotAndDropWhenKilled(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.canPickUpLoot() && PiglinAI.wantsToPickup(this, itemstack);
    }

    protected boolean canReplaceCurrentItem(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = this.getItemBySlot(enumitemslot);

        return this.canReplaceCurrentItem(itemstack, itemstack1);
    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack itemstack, ItemStack itemstack1) {
        if (EnchantmentManager.hasBindingCurse(itemstack1)) {
            return false;
        } else {
            boolean flag = PiglinAI.isLovedItem(itemstack) || itemstack.is(Items.CROSSBOW);
            boolean flag1 = PiglinAI.isLovedItem(itemstack1) || itemstack1.is(Items.CROSSBOW);

            return flag && !flag1 ? true : (!flag && flag1 ? false : (this.isAdult() && !itemstack.is(Items.CROSSBOW) && itemstack1.is(Items.CROSSBOW) ? false : super.canReplaceCurrentItem(itemstack, itemstack1)));
        }
    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        this.onItemPickup(entityitem);
        PiglinAI.pickUpItem(this, entityitem);
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        if (this.isBaby() && entity.getType() == EntityTypes.HOGLIN) {
            entity = this.getTopPassenger(entity, 3);
        }

        return super.startRiding(entity, flag);
    }

    private Entity getTopPassenger(Entity entity, int i) {
        List<Entity> list = entity.getPassengers();

        return i != 1 && !list.isEmpty() ? this.getTopPassenger((Entity) list.get(0), i - 1) : entity;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.level.isClientSide ? null : (SoundEffect) PiglinAI.getSoundForCurrentActivity(this).orElse((Object) null);
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.PIGLIN_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.PIGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PIGLIN_STEP, 0.15F, 1.0F);
    }

    protected void playSound(SoundEffect soundeffect) {
        this.playSound(soundeffect, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    protected void playConvertedSound() {
        this.playSound(SoundEffects.PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }
}
