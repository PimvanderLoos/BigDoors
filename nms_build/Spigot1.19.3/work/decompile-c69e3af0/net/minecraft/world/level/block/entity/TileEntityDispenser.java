package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerDispenser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityDispenser extends TileEntityLootable {

    public static final int CONTAINER_SIZE = 9;
    private NonNullList<ItemStack> items;

    protected TileEntityDispenser(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        super(tileentitytypes, blockposition, iblockdata);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
    }

    public TileEntityDispenser(BlockPosition blockposition, IBlockData iblockdata) {
        this(TileEntityTypes.DISPENSER, blockposition, iblockdata);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    public int getRandomSlot(RandomSource randomsource) {
        this.unpackLootTable((EntityHuman) null);
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.items.size(); ++k) {
            if (!((ItemStack) this.items.get(k)).isEmpty() && randomsource.nextInt(j++) == 0) {
                i = k;
            }
        }

        return i;
    }

    public int addItem(ItemStack itemstack) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                this.setItem(i, itemstack);
                return i;
            }
        }

        return -1;
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.dispenser");
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbttagcompound)) {
            ContainerUtil.loadAllItems(nbttagcompound, this.items);
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.trySaveLootTable(nbttagcompound)) {
            ContainerUtil.saveAllItems(nbttagcompound, this.items);
        }

    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerDispenser(i, playerinventory, this);
    }
}
