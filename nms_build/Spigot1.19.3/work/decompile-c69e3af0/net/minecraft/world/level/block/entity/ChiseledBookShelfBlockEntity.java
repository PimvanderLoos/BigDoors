package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import org.slf4j.Logger;

public class ChiseledBookShelfBlockEntity extends TileEntity implements IInventory {

    public static final int MAX_BOOKS_IN_STORAGE = 6;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final NonNullList<ItemStack> items;
    public int lastInteractedSlot;

    public ChiseledBookShelfBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.CHISELED_BOOKSHELF, blockposition, iblockdata);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);
        this.lastInteractedSlot = -1;
    }

    private void updateState(int i) {
        if (i >= 0 && i < 6) {
            this.lastInteractedSlot = i;
            IBlockData iblockdata = this.getBlockState();

            for (int j = 0; j < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++j) {
                boolean flag = !this.getItem(j).isEmpty();
                BlockStateBoolean blockstateboolean = (BlockStateBoolean) ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(j);

                iblockdata = (IBlockData) iblockdata.setValue(blockstateboolean, flag);
            }

            ((World) Objects.requireNonNull(this.level)).setBlock(this.worldPosition, iblockdata, 3);
        } else {
            ChiseledBookShelfBlockEntity.LOGGER.error("Expected slot 0-5, got {}", i);
        }
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        this.items.clear();
        ContainerUtil.loadAllItems(nbttagcompound, this.items);
        this.lastInteractedSlot = nbttagcompound.getInt("last_interacted_slot");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        ContainerUtil.saveAllItems(nbttagcompound, this.items, true);
        nbttagcompound.putInt("last_interacted_slot", this.lastInteractedSlot);
    }

    public int count() {
        return (int) this.items.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return (ItemStack) this.items.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack itemstack = (ItemStack) Objects.requireNonNullElse((ItemStack) this.items.get(i), ItemStack.EMPTY);

        this.items.set(i, ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            this.updateState(i);
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return this.removeItem(i, 1);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        if (itemstack.is(TagsItem.BOOKSHELF_BOOKS)) {
            this.items.set(i, itemstack);
            this.updateState(i);
        }

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.level == null ? false : (this.level.getBlockEntity(this.worldPosition) != this ? false : entityhuman.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return itemstack.is(TagsItem.BOOKSHELF_BOOKS) && this.getItem(i).isEmpty();
    }

    public int getLastInteractedSlot() {
        return this.lastInteractedSlot;
    }
}
