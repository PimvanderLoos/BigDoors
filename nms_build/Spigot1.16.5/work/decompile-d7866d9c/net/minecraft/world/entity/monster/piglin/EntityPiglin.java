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
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
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

public class EntityPiglin extends EntityPiglinAbstract implements ICrossbow {

    private static final DataWatcherObject<Boolean> bp = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bq = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> br = DataWatcher.a(EntityPiglin.class, DataWatcherRegistry.i);
    private static final UUID bs = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
    private static final AttributeModifier bt = new AttributeModifier(EntityPiglin.bs, "Baby speed boost", 0.20000000298023224D, AttributeModifier.Operation.MULTIPLY_BASE);
    private final InventorySubcontainer bu = new InventorySubcontainer(8);
    public boolean cannotHunt = false;
    protected static final ImmutableList<SensorType<? extends Sensor<? super EntityPiglin>>> d = ImmutableList.of(SensorType.c, SensorType.d, SensorType.b, SensorType.f, SensorType.k);
    protected static final ImmutableList<MemoryModuleType<?>> bo = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMSIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT});

    public EntityPiglin(EntityTypes<? extends EntityPiglinAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 5;
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

        nbttagcompound.set("Inventory", this.bu.g());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setBaby(nbttagcompound.getBoolean("IsBaby"));
        this.v(nbttagcompound.getBoolean("CannotHunt"));
        this.bu.a(nbttagcompound.getList("Inventory", 10));
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        this.bu.f().forEach(this::a);
    }

    protected ItemStack k(ItemStack itemstack) {
        return this.bu.a(itemstack);
    }

    protected boolean l(ItemStack itemstack) {
        return this.bu.b(itemstack);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPiglin.bp, false);
        this.datawatcher.register(EntityPiglin.bq, false);
        this.datawatcher.register(EntityPiglin.br, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityPiglin.bp.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public static AttributeProvider.Builder eT() {
        return EntityMonster.eR().a(GenericAttributes.MAX_HEALTH, 16.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D);
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
            } else if (this.eM()) {
                this.setSlot(EnumItemSlot.MAINHAND, this.eV());
            }
        }

        PiglinAI.a(this);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected boolean L() {
        return false;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isPersistent();
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.eM()) {
            this.d(EnumItemSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.d(EnumItemSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.d(EnumItemSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.d(EnumItemSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }

    }

    private void d(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (this.world.random.nextFloat() < 0.1F) {
            this.setSlot(enumitemslot, itemstack);
        }

    }

    @Override
    protected BehaviorController.b<EntityPiglin> cK() {
        return BehaviorController.a((Collection) EntityPiglin.bo, (Collection) EntityPiglin.d);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return PiglinAI.a(this, this.cK().a(dynamic));
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
        } else if (!this.world.isClientSide) {
            return PiglinAI.a(this, entityhuman, enumhand);
        } else {
            boolean flag = PiglinAI.b(this, entityhuman.b(enumhand)) && this.eN() != EntityPiglinArmPose.ADMIRING_ITEM;

            return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public double bc() {
        return (double) this.getHeight() * 0.92D;
    }

    @Override
    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityPiglin.bp, flag);
        if (!this.world.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityPiglin.bt);
            if (flag) {
                attributemodifiable.b(EntityPiglin.bt);
            }
        }

    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityPiglin.bp);
    }

    private void v(boolean flag) {
        this.cannotHunt = flag;
    }

    @Override
    protected boolean m() {
        return !this.cannotHunt;
    }

    @Override
    protected void mobTick() {
        this.world.getMethodProfiler().enter("piglinBrain");
        this.getBehaviorController().a((WorldServer) this.world, (EntityLiving) this);
        this.world.getMethodProfiler().exit();
        PiglinAI.b(this);
        super.mobTick();
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return this.f;
    }

    @Override
    protected void c(WorldServer worldserver) {
        PiglinAI.c(this);
        this.bu.f().forEach(this::a);
        super.c(worldserver);
    }

    private ItemStack eV() {
        return (double) this.random.nextFloat() < 0.5D ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    private boolean eW() {
        return (Boolean) this.datawatcher.get(EntityPiglin.bq);
    }

    @Override
    public void b(boolean flag) {
        this.datawatcher.set(EntityPiglin.bq, flag);
    }

    @Override
    public void U_() {
        this.ticksFarFromPlayer = 0;
    }

    public EntityPiglinArmPose eN() {
        return this.eU() ? EntityPiglinArmPose.DANCING : (PiglinAI.a(this.getItemInOffHand().getItem()) ? EntityPiglinArmPose.ADMIRING_ITEM : (this.isAggressive() && this.eO() ? EntityPiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : (this.eW() ? EntityPiglinArmPose.CROSSBOW_CHARGE : (this.isAggressive() && this.a(Items.CROSSBOW) ? EntityPiglinArmPose.CROSSBOW_HOLD : EntityPiglinArmPose.DEFAULT))));
    }

    public boolean eU() {
        return (Boolean) this.datawatcher.get(EntityPiglin.br);
    }

    public void u(boolean flag) {
        this.datawatcher.set(EntityPiglin.br, flag);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.world.isClientSide) {
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

    protected void m(ItemStack itemstack) {
        this.b(EnumItemSlot.MAINHAND, itemstack);
    }

    protected void n(ItemStack itemstack) {
        if (itemstack.getItem() == PiglinAI.a) {
            this.setSlot(EnumItemSlot.OFFHAND, itemstack);
            this.d(EnumItemSlot.OFFHAND);
        } else {
            this.b(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    @Override
    public boolean i(ItemStack itemstack) {
        return this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.canPickupLoot() && PiglinAI.a(this, itemstack);
    }

    protected boolean o(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.j(itemstack);
        ItemStack itemstack1 = this.getEquipment(enumitemslot);

        return this.a(itemstack, itemstack1);
    }

    @Override
    protected boolean a(ItemStack itemstack, ItemStack itemstack1) {
        if (EnchantmentManager.d(itemstack1)) {
            return false;
        } else {
            boolean flag = PiglinAI.a(itemstack.getItem()) || itemstack.getItem() == Items.CROSSBOW;
            boolean flag1 = PiglinAI.a(itemstack1.getItem()) || itemstack1.getItem() == Items.CROSSBOW;

            return flag && !flag1 ? true : (!flag && flag1 ? false : (this.eM() && itemstack.getItem() != Items.CROSSBOW && itemstack1.getItem() == Items.CROSSBOW ? false : super.a(itemstack, itemstack1)));
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
        return this.world.isClientSide ? null : (SoundEffect) PiglinAI.d(this).orElse((Object) null);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PIGLIN_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_PIGLIN_STEP, 0.15F, 1.0F);
    }

    protected void a(SoundEffect soundeffect) {
        this.playSound(soundeffect, this.getSoundVolume(), this.dH());
    }

    @Override
    protected void eP() {
        this.a(SoundEffects.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }
}
