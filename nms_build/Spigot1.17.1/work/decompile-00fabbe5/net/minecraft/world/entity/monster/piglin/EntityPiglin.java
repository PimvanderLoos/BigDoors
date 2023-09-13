package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
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

    private static final DataWatcherObject<Boolean> DATA_BABY_ID = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_IS_CHARGING_CROSSBOW = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_IS_DANCING = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.BOOLEAN);
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
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        if (this.cannotHunt) {
            nbttagcompound.setBoolean("CannotHunt", true);
        }

        nbttagcompound.set("Inventory", this.inventory.g());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setBaby(nbttagcompound.getBoolean("IsBaby"));
        this.x(nbttagcompound.getBoolean("CannotHunt"));
        this.inventory.a(nbttagcompound.getList("Inventory", 10));
    }

    @VisibleForDebug
    @Override
    public IInventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        this.inventory.f().forEach(this::b);
    }

    protected ItemStack m(ItemStack itemstack) {
        return this.inventory.a(itemstack);
    }

    protected boolean n(ItemStack itemstack) {
        return this.inventory.b(itemstack);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityPiglin.DATA_BABY_ID, false);
        this.entityData.register(EntityPiglin.DATA_IS_CHARGING_CROSSBOW, false);
        this.entityData.register(EntityPiglin.DATA_IS_DANCING, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityPiglin.DATA_BABY_ID.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public static AttributeProvider.Builder fC() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 16.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D);
    }

    public static boolean b(EntityTypes<EntityPiglin> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return !generatoraccess.getType(blockposition.down()).a(Blocks.NETHER_WART_BLOCK);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn != EnumMobSpawn.STRUCTURE) {
            if (worldaccess.getRandom().nextFloat() < 0.2F) {
                this.setBaby(true);
            } else if (this.fw()) {
                this.setSlot(EnumItemSlot.MAINHAND, this.fE());
            }
        }

        PiglinAI.a(this);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected boolean Q() {
        return false;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isPersistent();
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.fw()) {
            this.c(EnumItemSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.c(EnumItemSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.c(EnumItemSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.c(EnumItemSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }

    }

    private void c(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (this.level.random.nextFloat() < 0.1F) {
            this.setSlot(enumitemslot, itemstack);
        }

    }

    @Override
    protected BehaviorController.b<EntityPiglin> dp() {
        return BehaviorController.a((Collection) EntityPiglin.MEMORY_TYPES, (Collection) EntityPiglin.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return PiglinAI.a(this, this.dp().a(dynamic));
    }

    @Override
    public BehaviorController<EntityPiglin> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

        if (enuminteractionresult.a()) {
            return enuminteractionresult;
        } else if (!this.level.isClientSide) {
            return PiglinAI.a(this, entityhuman, enumhand);
        } else {
            boolean flag = PiglinAI.b(this, entityhuman.b(enumhand)) && this.fx() != EntityPiglinArmPose.ADMIRING_ITEM;

            return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public double bl() {
        return (double) this.getHeight() * 0.92D;
    }

    @Override
    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityPiglin.DATA_BABY_ID, flag);
        if (!this.level.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityPiglin.SPEED_MODIFIER_BABY);
            if (flag) {
                attributemodifiable.b(EntityPiglin.SPEED_MODIFIER_BABY);
            }
        }

    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityPiglin.DATA_BABY_ID);
    }

    private void x(boolean flag) {
        this.cannotHunt = flag;
    }

    @Override
    protected boolean n() {
        return !this.cannotHunt;
    }

    @Override
    protected void mobTick() {
        this.level.getMethodProfiler().enter("piglinBrain");
        this.getBehaviorController().a((WorldServer) this.level, (EntityLiving) this);
        this.level.getMethodProfiler().exit();
        PiglinAI.b(this);
        super.mobTick();
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return this.xpReward;
    }

    @Override
    protected void c(WorldServer worldserver) {
        PiglinAI.c(this);
        this.inventory.f().forEach(this::b);
        super.c(worldserver);
    }

    private ItemStack fE() {
        return (double) this.random.nextFloat() < 0.5D ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    private boolean fF() {
        return (Boolean) this.entityData.get(EntityPiglin.DATA_IS_CHARGING_CROSSBOW);
    }

    @Override
    public void b(boolean flag) {
        this.entityData.set(EntityPiglin.DATA_IS_CHARGING_CROSSBOW, flag);
    }

    @Override
    public void a() {
        this.noActionTime = 0;
    }

    @Override
    public EntityPiglinArmPose fx() {
        return this.fD() ? EntityPiglinArmPose.DANCING : (PiglinAI.a(this.getItemInOffHand()) ? EntityPiglinArmPose.ADMIRING_ITEM : (this.isAggressive() && this.fy() ? EntityPiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : (this.fF() ? EntityPiglinArmPose.CROSSBOW_CHARGE : (this.isAggressive() && this.a(Items.CROSSBOW) ? EntityPiglinArmPose.CROSSBOW_HOLD : EntityPiglinArmPose.DEFAULT))));
    }

    public boolean fD() {
        return (Boolean) this.entityData.get(EntityPiglin.DATA_IS_DANCING);
    }

    public void w(boolean flag) {
        this.entityData.set(EntityPiglin.DATA_IS_DANCING, flag);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.level.isClientSide) {
            return false;
        } else {
            if (flag && damagesource.getEntity() instanceof EntityLiving) {
                PiglinAI.a(this, (EntityLiving) damagesource.getEntity());
            }

            return flag;
        }
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        this.b(this, 1.6F);
    }

    @Override
    public void a(EntityLiving entityliving, ItemStack itemstack, IProjectile iprojectile, float f) {
        this.a(this, entityliving, iprojectile, f, 1.6F);
    }

    @Override
    public boolean a(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.CROSSBOW;
    }

    protected void o(ItemStack itemstack) {
        this.b(EnumItemSlot.MAINHAND, itemstack);
    }

    protected void p(ItemStack itemstack) {
        if (itemstack.a(PiglinAI.BARTERING_ITEM)) {
            this.setSlot(EnumItemSlot.OFFHAND, itemstack);
            this.d(EnumItemSlot.OFFHAND);
        } else {
            this.b(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    @Override
    public boolean l(ItemStack itemstack) {
        return this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.canPickupLoot() && PiglinAI.a(this, itemstack);
    }

    protected boolean q(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = this.getEquipment(enumitemslot);

        return this.a(itemstack, itemstack1);
    }

    @Override
    protected boolean a(ItemStack itemstack, ItemStack itemstack1) {
        if (EnchantmentManager.d(itemstack1)) {
            return false;
        } else {
            boolean flag = PiglinAI.a(itemstack) || itemstack.a(Items.CROSSBOW);
            boolean flag1 = PiglinAI.a(itemstack1) || itemstack1.a(Items.CROSSBOW);

            return flag && !flag1 ? true : (!flag && flag1 ? false : (this.fw() && !itemstack.a(Items.CROSSBOW) && itemstack1.a(Items.CROSSBOW) ? false : super.a(itemstack, itemstack1)));
        }
    }

    @Override
    protected void b(EntityItem entityitem) {
        this.a(entityitem);
        PiglinAI.a(this, entityitem);
    }

    @Override
    public boolean a(Entity entity, boolean flag) {
        if (this.isBaby() && entity.getEntityType() == EntityTypes.HOGLIN) {
            entity = this.b(entity, 3);
        }

        return super.a(entity, flag);
    }

    private Entity b(Entity entity, int i) {
        List<Entity> list = entity.getPassengers();

        return i != 1 && !list.isEmpty() ? this.b((Entity) list.get(0), i - 1) : entity;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.level.isClientSide ? null : (SoundEffect) PiglinAI.d(this).orElse((Object) null);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PIGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PIGLIN_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PIGLIN_STEP, 0.15F, 1.0F);
    }

    protected void a(SoundEffect soundeffect) {
        this.playSound(soundeffect, this.getSoundVolume(), this.ep());
    }

    @Override
    protected void fz() {
        this.a(SoundEffects.PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }
}
