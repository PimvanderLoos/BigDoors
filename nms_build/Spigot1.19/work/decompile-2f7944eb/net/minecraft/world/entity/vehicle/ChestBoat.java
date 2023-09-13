package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAI;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;

public class ChestBoat extends EntityBoat implements HasCustomInventoryScreen, ContainerEntity {

    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> itemStacks;
    @Nullable
    private MinecraftKey lootTable;
    private long lootTableSeed;

    public ChestBoat(EntityTypes<? extends EntityBoat> entitytypes, World world) {
        super(entitytypes, world);
        this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    public ChestBoat(World world, double d0, double d1, double d2) {
        this(EntityTypes.CHEST_BOAT, world);
        this.setPos(d0, d1, d2);
        this.xo = d0;
        this.yo = d1;
        this.zo = d2;
    }

    @Override
    protected float getSinglePassengerXOffset() {
        return 0.15F;
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        this.addChestVehicleSaveData(nbttagcompound);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.readChestVehicleSaveData(nbttagcompound);
    }

    @Override
    public void destroy(DamageSource damagesource) {
        super.destroy(damagesource);
        this.chestVehicleDestroyed(damagesource, this.level, this);
    }

    @Override
    public void remove(Entity.RemovalReason entity_removalreason) {
        if (!this.level.isClientSide && entity_removalreason.shouldDestroy()) {
            InventoryUtils.dropContents(this.level, (Entity) this, (IInventory) this);
        }

        super.remove(entity_removalreason);
    }

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        return this.canAddPassenger(entityhuman) && !entityhuman.isSecondaryUseActive() ? super.interact(entityhuman, enumhand) : this.interactWithChestVehicle(this::gameEvent, entityhuman);
    }

    @Override
    public void openCustomInventoryScreen(EntityHuman entityhuman) {
        entityhuman.openMenu(this);
        if (!entityhuman.level.isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, entityhuman);
            PiglinAI.angerNearbyPiglins(entityhuman, true);
        }

    }

    @Override
    public Item getDropItem() {
        Item item;

        switch (this.getBoatType()) {
            case SPRUCE:
                item = Items.SPRUCE_CHEST_BOAT;
                break;
            case BIRCH:
                item = Items.BIRCH_CHEST_BOAT;
                break;
            case JUNGLE:
                item = Items.JUNGLE_CHEST_BOAT;
                break;
            case ACACIA:
                item = Items.ACACIA_CHEST_BOAT;
                break;
            case DARK_OAK:
                item = Items.DARK_OAK_CHEST_BOAT;
                break;
            case MANGROVE:
                item = Items.MANGROVE_CHEST_BOAT;
                break;
            default:
                item = Items.OAK_CHEST_BOAT;
        }

        return item;
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public ItemStack getItem(int i) {
        return this.getChestVehicleItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return this.removeChestVehicleItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return this.removeChestVehicleItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.setChestVehicleItem(i, itemstack);
    }

    @Override
    public SlotAccess getSlot(int i) {
        return this.getChestVehicleSlot(i);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.isChestVehicleStillValid(entityhuman);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        if (this.lootTable != null && entityhuman.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(playerinventory.player);
            return ContainerChest.threeRows(i, playerinventory, this);
        }
    }

    public void unpackLootTable(@Nullable EntityHuman entityhuman) {
        this.unpackChestVehicleLootTable(entityhuman);
    }

    @Nullable
    @Override
    public MinecraftKey getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable MinecraftKey minecraftkey) {
        this.lootTable = minecraftkey;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long i) {
        this.lootTableSeed = i;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }
}
