package net.minecraft.server;

public enum EnchantmentSlotType {

    ARMOR {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor;
        }
    },
    ARMOR_FEET {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.FEET;
        }
    },
    ARMOR_LEGS {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.LEGS;
        }
    },
    ARMOR_CHEST {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.CHEST;
        }
    },
    ARMOR_HEAD {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemArmor && ((ItemArmor) item).b() == EnumItemSlot.HEAD;
        }
    },
    WEAPON {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemSword;
        }
    },
    DIGGER {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemTool;
        }
    },
    FISHING_ROD {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemFishingRod;
        }
    },
    TRIDENT {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemTrident;
        }
    },
    BREAKABLE {
        @Override
        public boolean canEnchant(Item item) {
            return item.usesDurability();
        }
    },
    BOW {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemBow;
        }
    },
    WEARABLE {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemWearable || Block.asBlock(item) instanceof ItemWearable;
        }
    },
    CROSSBOW {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemCrossbow;
        }
    },
    VANISHABLE {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemVanishable || Block.asBlock(item) instanceof ItemVanishable || null.BREAKABLE.canEnchant(item);
        }
    };

    private EnchantmentSlotType() {}

    public abstract boolean canEnchant(Item item);
}
