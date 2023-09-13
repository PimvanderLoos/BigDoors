package net.minecraft.server;

public class ItemEnchantedBook extends Item {

    public ItemEnchantedBook() {}

    public boolean g_(ItemStack itemstack) {
        return false;
    }

    public EnumItemRarity g(ItemStack itemstack) {
        return this.h(itemstack).isEmpty() ? super.g(itemstack) : EnumItemRarity.UNCOMMON;
    }

    public NBTTagList h(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("StoredEnchantments", 9) ? (NBTTagList) nbttagcompound.get("StoredEnchantments") : new NBTTagList();
    }

    public void a(ItemStack itemstack, WeightedRandomEnchant weightedrandomenchant) {
        NBTTagList nbttaglist = this.h(itemstack);
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

    public ItemStack a(WeightedRandomEnchant weightedrandomenchant) {
        ItemStack itemstack = new ItemStack(this);

        this.a(itemstack, weightedrandomenchant);
        return itemstack;
    }
}
