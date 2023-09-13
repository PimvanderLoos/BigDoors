package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;

public class RecipeRepair extends IRecipeComplex {

    public RecipeRepair(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = (ItemStack) list.get(0);

                    if (!itemstack.is(itemstack1.getItem()) || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().canBeDepleted()) {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        List<ItemStack> list = Lists.newArrayList();

        ItemStack itemstack;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            itemstack = inventorycrafting.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = (ItemStack) list.get(0);

                    if (!itemstack.is(itemstack1.getItem()) || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().canBeDepleted()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (list.size() == 2) {
            ItemStack itemstack2 = (ItemStack) list.get(0);

            itemstack = (ItemStack) list.get(1);
            if (itemstack2.is(itemstack.getItem()) && itemstack2.getCount() == 1 && itemstack.getCount() == 1 && itemstack2.getItem().canBeDepleted()) {
                Item item = itemstack2.getItem();
                int j = item.getMaxDamage() - itemstack2.getDamageValue();
                int k = item.getMaxDamage() - itemstack.getDamageValue();
                int l = j + k + item.getMaxDamage() * 5 / 100;
                int i1 = item.getMaxDamage() - l;

                if (i1 < 0) {
                    i1 = 0;
                }

                ItemStack itemstack3 = new ItemStack(itemstack2.getItem());

                itemstack3.setDamageValue(i1);
                Map<Enchantment, Integer> map = Maps.newHashMap();
                Map<Enchantment, Integer> map1 = EnchantmentManager.getEnchantments(itemstack2);
                Map<Enchantment, Integer> map2 = EnchantmentManager.getEnchantments(itemstack);

                BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach((enchantment) -> {
                    int j1 = Math.max((Integer) map1.getOrDefault(enchantment, 0), (Integer) map2.getOrDefault(enchantment, 0));

                    if (j1 > 0) {
                        map.put(enchantment, j1);
                    }

                });
                if (!map.isEmpty()) {
                    EnchantmentManager.setEnchantments(map, itemstack3);
                }

                return itemstack3;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}
