package net.minecraft.server;

import java.util.Iterator;

public class ItemEnchantedBook extends Item {

    public ItemEnchantedBook() {}

    public boolean g_(ItemStack itemstack) {
        return false;
    }

    public EnumItemRarity g(ItemStack itemstack) {
        return h(itemstack).isEmpty() ? super.g(itemstack) : EnumItemRarity.UNCOMMON;
    }

    public static NBTTagList h(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null ? nbttagcompound.getList("StoredEnchantments", 10) : new NBTTagList();
    }

    public static void a(ItemStack itemstack, WeightedRandomEnchant weightedrandomenchant) {
        NBTTagList nbttaglist = h(itemstack);
        boolean flag = true;

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);

            if (Enchantment.c(nbttagcompound.getShort("id")) == weightedrandomenchant.enchantment) {
                if (nbttagcompound.getShort("lvl") < weightedrandomenchant.level) {
                    nbttagcompound.setShort("lvl", (short) weightedrandomenchant.level);
                }

                flag = false;
                break;
            }
        }

        if (flag) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.setShort("id", (short) Enchantment.getId(weightedrandomenchant.enchantment));
            nbttagcompound1.setShort("lvl", (short) weightedrandomenchant.level);
            nbttaglist.add(nbttagcompound1);
        }

        if (!itemstack.hasTag()) {
            itemstack.setTag(new NBTTagCompound());
        }

        itemstack.getTag().set("StoredEnchantments", nbttaglist);
    }

    public static ItemStack a(WeightedRandomEnchant weightedrandomenchant) {
        ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);

        a(itemstack, weightedrandomenchant);
        return itemstack;
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        Iterator iterator;
        Enchantment enchantment;

        if (creativemodetab == CreativeModeTab.g) {
            iterator = Enchantment.enchantments.iterator();

            while (iterator.hasNext()) {
                enchantment = (Enchantment) iterator.next();
                if (enchantment.itemTarget != null) {
                    for (int i = enchantment.getStartLevel(); i <= enchantment.getMaxLevel(); ++i) {
                        nonnulllist.add(a(new WeightedRandomEnchant(enchantment, i)));
                    }
                }
            }
        } else if (creativemodetab.n().length != 0) {
            iterator = Enchantment.enchantments.iterator();

            while (iterator.hasNext()) {
                enchantment = (Enchantment) iterator.next();
                if (creativemodetab.a(enchantment.itemTarget)) {
                    nonnulllist.add(a(new WeightedRandomEnchant(enchantment, enchantment.getMaxLevel())));
                }
            }
        }

    }
}
