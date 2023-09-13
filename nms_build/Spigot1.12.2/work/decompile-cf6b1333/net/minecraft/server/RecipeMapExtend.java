package net.minecraft.server;

import java.util.Iterator;

public class RecipeMapExtend extends ShapedRecipes {

    public RecipeMapExtend() {
        super("", 3, 3, NonNullList.a(RecipeItemStack.a, new RecipeItemStack[] { RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a((Item) Items.FILLED_MAP), RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a(new Item[] { Items.PAPER}), RecipeItemStack.a(new Item[] { Items.PAPER})}), new ItemStack(Items.MAP));
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        if (!super.a(inventorycrafting, world)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.a;

            for (int i = 0; i < inventorycrafting.getSize() && itemstack.isEmpty(); ++i) {
                ItemStack itemstack1 = inventorycrafting.getItem(i);

                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    itemstack = itemstack1;
                }
            }

            if (itemstack.isEmpty()) {
                return false;
            } else {
                WorldMap worldmap = Items.FILLED_MAP.getSavedMap(itemstack, world);

                return worldmap == null ? false : (this.a(worldmap) ? false : worldmap.scale < 4);
            }
        }
    }

    private boolean a(WorldMap worldmap) {
        if (worldmap.decorations != null) {
            Iterator iterator = worldmap.decorations.values().iterator();

            while (iterator.hasNext()) {
                MapIcon mapicon = (MapIcon) iterator.next();

                if (mapicon.b() == MapIcon.Type.MANSION || mapicon.b() == MapIcon.Type.MONUMENT) {
                    return true;
                }
            }
        }

        return false;
    }

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.a;

        for (int i = 0; i < inventorycrafting.getSize() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (itemstack1.getItem() == Items.FILLED_MAP) {
                itemstack = itemstack1;
            }
        }

        itemstack = itemstack.cloneItemStack();
        itemstack.setCount(1);
        if (itemstack.getTag() == null) {
            itemstack.setTag(new NBTTagCompound());
        }

        itemstack.getTag().setInt("map_scale_direction", 1);
        return itemstack;
    }

    public boolean c() {
        return true;
    }
}
