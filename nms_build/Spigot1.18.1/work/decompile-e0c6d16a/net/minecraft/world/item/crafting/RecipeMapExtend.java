package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.saveddata.maps.WorldMap;

public class RecipeMapExtend extends ShapedRecipes {

    public RecipeMapExtend(MinecraftKey minecraftkey) {
        super(minecraftkey, "", 3, 3, NonNullList.of(RecipeItemStack.EMPTY, RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.FILLED_MAP), RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.PAPER), RecipeItemStack.of(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        if (!super.matches(inventorycrafting, world)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.EMPTY;

            for (int i = 0; i < inventorycrafting.getContainerSize() && itemstack.isEmpty(); ++i) {
                ItemStack itemstack1 = inventorycrafting.getItem(i);

                if (itemstack1.is(Items.FILLED_MAP)) {
                    itemstack = itemstack1;
                }
            }

            if (itemstack.isEmpty()) {
                return false;
            } else {
                WorldMap worldmap = ItemWorldMap.getSavedData(itemstack, world);

                return worldmap == null ? false : (worldmap.isExplorationMap() ? false : worldmap.scale < 4);
            }
        }
    }

    @Override
    public ItemStack assemble(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < inventorycrafting.getContainerSize() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (itemstack1.is(Items.FILLED_MAP)) {
                itemstack = itemstack1;
            }
        }

        itemstack = itemstack.copy();
        itemstack.setCount(1);
        itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
        return itemstack;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}
