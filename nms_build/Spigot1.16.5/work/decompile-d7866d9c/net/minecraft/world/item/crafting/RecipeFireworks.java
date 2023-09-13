package net.minecraft.world.item.crafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class RecipeFireworks extends IRecipeComplex {

    private static final RecipeItemStack a = RecipeItemStack.a(Items.PAPER);
    private static final RecipeItemStack b = RecipeItemStack.a(Items.GUNPOWDER);
    private static final RecipeItemStack c = RecipeItemStack.a(Items.FIREWORK_STAR);

    public RecipeFireworks(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        int i = 0;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack = inventorycrafting.getItem(j);

            if (!itemstack.isEmpty()) {
                if (RecipeFireworks.a.test(itemstack)) {
                    if (flag) {
                        return false;
                    }

                    flag = true;
                } else if (RecipeFireworks.b.test(itemstack)) {
                    ++i;
                    if (i > 3) {
                        return false;
                    }
                } else if (!RecipeFireworks.c.test(itemstack)) {
                    return false;
                }
            }
        }

        return flag && i >= 1;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        NBTTagCompound nbttagcompound = itemstack.a("Fireworks");
        NBTTagList nbttaglist = new NBTTagList();
        int i = 0;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworks.b.test(itemstack1)) {
                    ++i;
                } else if (RecipeFireworks.c.test(itemstack1)) {
                    NBTTagCompound nbttagcompound1 = itemstack1.b("Explosion");

                    if (nbttagcompound1 != null) {
                        nbttaglist.add(nbttagcompound1);
                    }
                }
            }
        }

        nbttagcompound.setByte("Flight", (byte) i);
        if (!nbttaglist.isEmpty()) {
            nbttagcompound.set("Explosions", nbttaglist);
        }

        return itemstack;
    }

    @Override
    public ItemStack getResult() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.g;
    }
}
