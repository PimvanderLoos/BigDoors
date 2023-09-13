package net.minecraft.server;

public enum EnchantmentSlotType {

    ALL {;
        public boolean canEnchant(Item item) {
            EnchantmentSlotType[] aenchantmentslottype = EnchantmentSlotType.values();
            int i = aenchantmentslottype.length;

            for (int j = 0; j < i; ++j) {
                EnchantmentSlotType enchantmentslottype = aenchantmentslottype[j];

                if (enchantmentslottype != EnchantmentSlotType.ALL && enchantmentslottype.canEnchant(item)) {
                    return true;
                }
            }

            return false;
        }
    }, ARMOR {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemArmor;
    }
}, ARMOR_FEET {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).c == EnumItemSlot.FEET;
    }
}, ARMOR_LEGS {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).c == EnumItemSlot.LEGS;
    }
}, ARMOR_CHEST {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).c == EnumItemSlot.CHEST;
    }
}, ARMOR_HEAD {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).c == EnumItemSlot.HEAD;
    }
}, WEAPON {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemSword;
    }
}, DIGGER {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemTool;
    }
}, FISHING_ROD {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemFishingRod;
    }
}, BREAKABLE {;
    public boolean canEnchant(Item item) {
        return item.usesDurability();
    }
}, BOW {;
    public boolean canEnchant(Item item) {
        return item instanceof ItemBow;
    }
}, WEARABLE {;
    public boolean canEnchant(Item item) {
        boolean flag = item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof BlockPumpkin;

        return item instanceof ItemArmor || item instanceof ItemElytra || item instanceof ItemSkull || flag;
    }
};

    private EnchantmentSlotType() {}

    public abstract boolean canEnchant(Item item);

    EnchantmentSlotType(Object object) {
        this();
    }
}
