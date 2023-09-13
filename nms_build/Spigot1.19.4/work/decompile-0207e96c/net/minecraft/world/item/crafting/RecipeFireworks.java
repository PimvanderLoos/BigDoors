package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class RecipeFireworks extends IRecipeComplex {

    private static final RecipeItemStack PAPER_INGREDIENT = RecipeItemStack.of(Items.PAPER);
    private static final RecipeItemStack GUNPOWDER_INGREDIENT = RecipeItemStack.of(Items.GUNPOWDER);
    private static final RecipeItemStack STAR_INGREDIENT = RecipeItemStack.of(Items.FIREWORK_STAR);

    public RecipeFireworks(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        int i = 0;

        for (int j = 0; j < inventorycrafting.getContainerSize(); ++j) {
            ItemStack itemstack = inventorycrafting.getItem(j);

            if (!itemstack.isEmpty()) {
                if (RecipeFireworks.PAPER_INGREDIENT.test(itemstack)) {
                    if (flag) {
                        return false;
                    }

                    flag = true;
                } else if (RecipeFireworks.GUNPOWDER_INGREDIENT.test(itemstack)) {
                    ++i;
                    if (i > 3) {
                        return false;
                    }
                } else if (!RecipeFireworks.STAR_INGREDIENT.test(itemstack)) {
                    return false;
                }
            }
        }

        return flag && i >= 1;
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTagElement("Fireworks");
        NBTTagList nbttaglist = new NBTTagList();
        int i = 0;

        for (int j = 0; j < inventorycrafting.getContainerSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworks.GUNPOWDER_INGREDIENT.test(itemstack1)) {
                    ++i;
                } else if (RecipeFireworks.STAR_INGREDIENT.test(itemstack1)) {
                    NBTTagCompound nbttagcompound1 = itemstack1.getTagElement("Explosion");

                    if (nbttagcompound1 != null) {
                        nbttaglist.add(nbttagcompound1);
                    }
                }
            }
        }

        nbttagcompound.putByte("Flight", (byte) i);
        if (!nbttaglist.isEmpty()) {
            nbttagcompound.put("Explosions", nbttaglist);
        }

        return itemstack;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public ItemStack getResultItem(IRegistryCustom iregistrycustom) {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}
