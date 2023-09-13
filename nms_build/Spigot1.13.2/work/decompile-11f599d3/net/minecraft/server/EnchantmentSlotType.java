package net.minecraft.server;

public enum EnchantmentSlotType {

    ALL {
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
    },
    ARMOR {
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor;
        }
    },
    ARMOR_FEET {
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.FEET;
        }
    },
    ARMOR_LEGS {
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.LEGS;
        }
    },
    ARMOR_CHEST {
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.CHEST;
        }
    },
    ARMOR_HEAD {
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.HEAD;
        }
    },
    WEAPON {
        public boolean canEnchant(Item item) {
            return item instanceof ItemSword;
        }
    },
    DIGGER {
        public boolean canEnchant(Item item) {
            return item instanceof ItemTool;
        }
    },
    FISHING_ROD {
        public boolean canEnchant(Item item) {
            return item instanceof ItemFishingRod;
        }
    },
    TRIDENT {
        public boolean canEnchant(Item item) {
            return item instanceof ItemTrident;
        }
    },
    BREAKABLE {
        public boolean canEnchant(Item item) {
            return item.usesDurability();
        }
    },
    BOW {
        public boolean canEnchant(Item item) {
            return item instanceof ItemBow;
        }
    },
    WEARABLE {
        public boolean canEnchant(Item item) {
            Block block = Block.asBlock(item);

            return item instanceof ItemArmor || item instanceof ItemElytra || block instanceof BlockSkullAbstract || block instanceof BlockPumpkin;
        }
    };

    private EnchantmentSlotType() {}

    public abstract boolean canEnchant(Item item);
}
