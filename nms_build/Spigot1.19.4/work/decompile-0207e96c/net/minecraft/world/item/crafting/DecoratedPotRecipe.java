package net.minecraft.world.item.crafting;

import java.util.List;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;

public class DecoratedPotRecipe extends IRecipeComplex {

    public DecoratedPotRecipe(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        if (!this.canCraftInDimensions(inventorycrafting.getWidth(), inventorycrafting.getHeight())) {
            return false;
        } else {
            for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                switch (i) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                        if (!itemstack.is(TagsItem.DECORATED_POT_SHARDS)) {
                            return false;
                        }
                        break;
                    case 2:
                    case 4:
                    case 6:
                    default:
                        if (!itemstack.is(Items.AIR)) {
                            return false;
                        }
                }
            }

            return true;
        }
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        DecoratedPotBlockEntity.saveShards(List.of(inventorycrafting.getItem(1).getItem(), inventorycrafting.getItem(3).getItem(), inventorycrafting.getItem(5).getItem(), inventorycrafting.getItem(7).getItem()), nbttagcompound);
        ItemBlock.setBlockEntityData(itemstack, TileEntityTypes.DECORATED_POT, nbttagcompound);
        return itemstack;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i == 3 && j == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.DECORATED_POT_RECIPE;
    }
}
