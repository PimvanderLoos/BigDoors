package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class RecipeFireworksFade extends IRecipeComplex {

    private static final RecipeItemStack a = RecipeItemStack.a(Items.FIREWORK_STAR);

    public RecipeFireworksFade(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        boolean flag1 = false;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemDye) {
                    flag = true;
                } else {
                    if (!RecipeFireworksFade.a.test(itemstack)) {
                        return false;
                    }

                    if (flag1) {
                        return false;
                    }

                    flag1 = true;
                }
            }
        }

        return flag1 && flag;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        List<Integer> list = Lists.newArrayList();
        ItemStack itemstack = null;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);
            Item item = itemstack1.getItem();

            if (item instanceof ItemDye) {
                list.add(((ItemDye) item).d().getFireworksColor());
            } else if (RecipeFireworksFade.a.test(itemstack1)) {
                itemstack = itemstack1.cloneItemStack();
                itemstack.setCount(1);
            }
        }

        if (itemstack != null && !list.isEmpty()) {
            itemstack.a("Explosion").b("FadeColors", (List) list);
            return itemstack;
        } else {
            return ItemStack.b;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.i;
    }
}
