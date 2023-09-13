package net.minecraft.world.item.crafting;

import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.saveddata.maps.MapIcon;
import net.minecraft.world.level.saveddata.maps.WorldMap;

public class RecipeMapExtend extends ShapedRecipes {

    public RecipeMapExtend(MinecraftKey minecraftkey) {
        super(minecraftkey, "", 3, 3, NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.FILLED_MAP), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean a(InventoryCrafting inventorycrafting, World world) {
        if (!super.a(inventorycrafting, world)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.b;

            for (int i = 0; i < inventorycrafting.getSize() && itemstack.isEmpty(); ++i) {
                ItemStack itemstack1 = inventorycrafting.getItem(i);

                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    itemstack = itemstack1;
                }
            }

            if (itemstack.isEmpty()) {
                return false;
            } else {
                WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, world);

                return worldmap == null ? false : (this.a(worldmap) ? false : worldmap.scale < 4);
            }
        }
    }

    private boolean a(WorldMap worldmap) {
        if (worldmap.decorations != null) {
            Iterator iterator = worldmap.decorations.values().iterator();

            while (iterator.hasNext()) {
                MapIcon mapicon = (MapIcon) iterator.next();

                if (mapicon.getType() == MapIcon.Type.MANSION || mapicon.getType() == MapIcon.Type.MONUMENT) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.b;

        for (int i = 0; i < inventorycrafting.getSize() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (itemstack1.getItem() == Items.FILLED_MAP) {
                itemstack = itemstack1;
            }
        }

        itemstack = itemstack.cloneItemStack();
        itemstack.setCount(1);
        itemstack.getOrCreateTag().setInt("map_scale_direction", 1);
        return itemstack;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.f;
    }
}
