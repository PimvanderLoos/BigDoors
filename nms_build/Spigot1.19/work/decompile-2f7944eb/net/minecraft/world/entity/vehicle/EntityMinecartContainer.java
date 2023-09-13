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
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public abstract class EntityMinecartContainer extends EntityMinecartAbstract implements ContainerEntity {

    private NonNullList<ItemStack> itemStacks;
    @Nullable
    public MinecraftKey lootTable;
    public long lootTableSeed;

    protected EntityMinecartContainer(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    }

    protected EntityMinecartContainer(EntityTypes<?> entitytypes, double d0, double d1, double d2, World world) {
        super(entitytypes, world, d0, d1, d2);
        this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    }

    @Override
    public void destroy(DamageSource damagesource) {
        super.destroy(damagesource);
        this.chestVehicleDestroyed(damagesource, this.level, this);
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

    @Override
    public void remove(Entity.RemovalReason entity_removalreason) {
        if (!this.level.isClientSide && entity_removalreason.shouldDestroy()) {
            InventoryUtils.dropContents(this.level, (Entity) this, (IInventory) this);
        }

        super.remove(entity_removalreason);
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
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        return this.interactWithChestVehicle(this::gameEvent, entityhuman);
    }

    @Override
    protected void applyNaturalSlowdown() {
        float f = 0.98F;

        if (this.lootTable == null) {
            int i = 15 - Container.getRedstoneSignalFromContainer(this);

            f += (float) i * 0.001F;
        }

        if (this.isInWater()) {
            f *= 0.95F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply((double) f, 0.0D, (double) f));
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    public void setLootTable(MinecraftKey minecraftkey, long i) {
        this.lootTable = minecraftkey;
        this.lootTableSeed = i;
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        if (this.lootTable != null && entityhuman.isSpectator()) {
            return null;
        } else {
            this.unpackChestVehicleLootTable(playerinventory.player);
            return this.createMenu(i, playerinventory);
        }
    }

    protected abstract Container createMenu(int i, PlayerInventory playerinventory);

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
