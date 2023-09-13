package net.minecraft.server;

import java.util.Iterator;

public class RecipeMapExtend extends ShapedRecipes {

    public RecipeMapExtend(MinecraftKey minecraftkey) {
        super(minecraftkey, "", 3, 3, NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.FILLED_MAP), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER), RecipeItemStack.a(Items.PAPER)), new ItemStack(Items.MAP));
    }

    public boolean a(IInventory iinventory, World world) {
        if (!super.a(iinventory, world)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.a;

            for (int i = 0; i < iinventory.getSize() && itemstack.isEmpty(); ++i) {
                ItemStack itemstack1 = iinventory.getItem(i);

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

                if (mapicon.b() == MapIcon.Type.MANSION || mapicon.b() == MapIcon.Type.MONUMENT) {
                    return true;
                }
            }
        }

        return false;
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = ItemStack.a;

        for (int i = 0; i < iinventory.getSize() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = iinventory.getItem(i);

            if (itemstack1.getItem() == Items.FILLED_MAP) {
                itemstack = itemstack1;
            }
        }

        itemstack = itemstack.cloneItemStack();
        itemstack.setCount(1);
        itemstack.getOrCreateTag().setInt("map_scale_direction", 1);
        return itemstack;
    }

    public boolean c() {
        return true;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.f;
    }
}
