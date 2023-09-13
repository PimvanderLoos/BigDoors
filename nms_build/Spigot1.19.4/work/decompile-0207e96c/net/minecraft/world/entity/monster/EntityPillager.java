package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalCrossbowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemBanner;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;

public class EntityPillager extends EntityIllagerAbstract implements ICrossbow, InventoryCarrier {

    private static final DataWatcherObject<Boolean> IS_CHARGING_CROSSBOW = DataWatcher.defineId(EntityPillager.class, DataWatcherRegistry.BOOLEAN);
    private static final int INVENTORY_SIZE = 5;
    private static final int SLOT_OFFSET = 300;
    private static final float CROSSBOW_POWER = 1.6F;
    public final InventorySubcontainer inventory = new InventorySubcontainer(5);

    public EntityPillager(EntityTypes<? extends EntityPillager> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(2, new EntityRaider.a(this, 10.0F));
        this.goalSelector.addGoal(3, new PathfinderGoalCrossbowAttack<>(this, 1.0D, 8.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.addGoal(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 15.0F));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).setAlertOthers());
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).add(GenericAttributes.MAX_HEALTH, 24.0D).add(GenericAttributes.ATTACK_DAMAGE, 5.0D).add(GenericAttributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityPillager.IS_CHARGING_CROSSBOW, false);
    }

    @Override
    public boolean canFireProjectileWeapon(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.CROSSBOW;
    }

    public boolean isChargingCrossbow() {
        return (Boolean) this.entityData.get(EntityPillager.IS_CHARGING_CROSSBOW);
    }

    @Override
    public void setChargingCrossbow(boolean flag) {
        this.entityData.set(EntityPillager.IS_CHARGING_CROSSBOW, flag);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        this.writeInventoryToTag(nbttagcompound);
    }

    @Override
    public EntityIllagerAbstract.a getArmPose() {
        return this.isChargingCrossbow() ? EntityIllagerAbstract.a.CROSSBOW_CHARGE : (this.isHolding(Items.CROSSBOW) ? EntityIllagerAbstract.a.CROSSBOW_HOLD : (this.isAggressive() ? EntityIllagerAbstract.a.ATTACKING : EntityIllagerAbstract.a.NEUTRAL));
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.readInventoryFromTag(nbttagcompound);
        this.setCanPickUpLoot(true);
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.0F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        RandomSource randomsource = worldaccess.getRandom();

        this.populateDefaultEquipmentSlots(randomsource, difficultydamagescaler);
        this.populateDefaultEquipmentEnchantments(randomsource, difficultydamagescaler);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomsource, DifficultyDamageScaler difficultydamagescaler) {
        this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Override
    protected void enchantSpawnedWeapon(RandomSource randomsource, float f) {
        super.enchantSpawnedWeapon(randomsource, f);
        if (randomsource.nextInt(300) == 0) {
            ItemStack itemstack = this.getMainHandItem();

            if (itemstack.is(Items.CROSSBOW)) {
                Map<Enchantment, Integer> map = EnchantmentManager.getEnchantments(itemstack);

                map.putIfAbsent(Enchantments.PIERCING, 1);
                EnchantmentManager.setEnchantments(map, itemstack);
                this.setItemSlot(EnumItemSlot.MAINHAND, itemstack);
            }
        }

    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return super.isAlliedTo(entity) ? true : (entity instanceof EntityLiving && ((EntityLiving) entity).getMobType() == EnumMonsterType.ILLAGER ? this.getTeam() == null && entity.getTeam() == null : false);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.PILLAGER_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.PILLAGER_HURT;
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
    public InventorySubcontainer getInventory() {
        return this.inventory;
    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItem();

        if (itemstack.getItem() instanceof ItemBanner) {
            super.pickUpItem(entityitem);
        } else if (this.wantsItem(itemstack)) {
            this.onItemPickup(entityitem);
            ItemStack itemstack1 = this.inventory.addItem(itemstack);

            if (itemstack1.isEmpty()) {
                entityitem.discard();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }

    private boolean wantsItem(ItemStack itemstack) {
        return this.hasActiveRaid() && itemstack.is(Items.WHITE_BANNER);
    }

    @Override
    public SlotAccess getSlot(int i) {
        int j = i - 300;

        return j >= 0 && j < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, j) : super.getSlot(i);
    }

    @Override
    public void applyRaidBuffs(int i, boolean flag) {
        Raid raid = this.getCurrentRaid();
        boolean flag1 = this.random.nextFloat() <= raid.getEnchantOdds();

        if (flag1) {
            ItemStack itemstack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();

            if (i > raid.getNumGroups(EnumDifficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            } else if (i > raid.getNumGroups(EnumDifficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }

            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentManager.setEnchantments(map, itemstack);
            this.setItemSlot(EnumItemSlot.MAINHAND, itemstack);
        }

    }

    @Override
    public SoundEffect getCelebrateSound() {
        return SoundEffects.PILLAGER_CELEBRATE;
    }
}
