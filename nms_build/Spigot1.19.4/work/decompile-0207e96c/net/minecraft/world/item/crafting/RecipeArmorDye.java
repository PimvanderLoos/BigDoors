package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.IDyeable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class RecipeArmorDye extends IRecipeComplex {

    public RecipeArmorDye(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        ItemStack itemstack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() instanceof IDyeable) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!(itemstack1.getItem() instanceof ItemDye)) {
                        return false;
                    }

                    list.add(itemstack1);
                }
            }
        }

        return !itemstack.isEmpty() && !list.isEmpty();
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        List<ItemDye> list = Lists.newArrayList();
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (item instanceof IDyeable) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemstack = itemstack1.copy();
                } else {
                    if (!(item instanceof ItemDye)) {
                        return ItemStack.EMPTY;
                    }

                    list.add((ItemDye) item);
                }
            }
        }

        if (!itemstack.isEmpty() && !list.isEmpty()) {
            return IDyeable.dyeArmor(itemstack, list);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}
