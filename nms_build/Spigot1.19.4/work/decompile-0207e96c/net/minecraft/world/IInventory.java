package net.minecraft.world;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;

public interface IInventory extends Clearable {

    int LARGE_MAX_STACK_SIZE = 64;
    int DEFAULT_DISTANCE_LIMIT = 8;

    int getContainerSize();

    boolean isEmpty();

    ItemStack getItem(int i);

    ItemStack removeItem(int i, int j);

    ItemStack removeItemNoUpdate(int i);

    void setItem(int i, ItemStack itemstack);

    default int getMaxStackSize() {
        return 64;
    }

    void setChanged();

    boolean stillValid(EntityHuman entityhuman);

    default void startOpen(EntityHuman entityhuman) {}

    default void stopOpen(EntityHuman entityhuman) {}

    default boolean canPlaceItem(int i, ItemStack itemstack) {
        return true;
    }

    default boolean canTakeItem(IInventory iinventory, int i, ItemStack itemstack) {
        return true;
    }

    default int countItem(Item item) {
        int i = 0;

        for (int j = 0; j < this.getContainerSize(); ++j) {
            ItemStack itemstack = this.getItem(j);

            if (itemstack.getItem().equals(item)) {
                i += itemstack.getCount();
            }
        }

        return i;
    }

    default boolean hasAnyOf(Set<Item> set) {
        return this.hasAnyMatching((itemstack) -> {
            return !itemstack.isEmpty() && set.contains(itemstack.getItem());
        });
    }

    default boolean hasAnyMatching(Predicate<ItemStack> predicate) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (predicate.test(itemstack)) {
                return true;
            }
        }

        return false;
    }

    static boolean stillValidBlockEntity(TileEntity tileentity, EntityHuman entityhuman) {
        return stillValidBlockEntity(tileentity, entityhuman, 8);
    }

    static boolean stillValidBlockEntity(TileEntity tileentity, EntityHuman entityhuman, int i) {
        World world = tileentity.getLevel();
        BlockPosition blockposition = tileentity.getBlockPos();

        return world == null ? false : (world.getBlockEntity(blockposition) != tileentity ? false : entityhuman.distanceToSqr((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) <= (double) (i * i));
    }
}
