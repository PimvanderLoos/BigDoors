package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemBow;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemFishingRod;
import net.minecraft.world.item.ItemSword;
import net.minecraft.world.item.ItemTool;
import net.minecraft.world.item.ItemTrident;
import net.minecraft.world.item.ItemVanishable;
import net.minecraft.world.level.block.Block;

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
            boolean flag;

            if (item instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor) item;

                if (itemarmor.getEquipmentSlot() == EnumItemSlot.FEET) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    },
    ARMOR_LEGS {
        @Override
        public boolean canEnchant(Item item) {
            boolean flag;

            if (item instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor) item;

                if (itemarmor.getEquipmentSlot() == EnumItemSlot.LEGS) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    },
    ARMOR_CHEST {
        @Override
        public boolean canEnchant(Item item) {
            boolean flag;

            if (item instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor) item;

                if (itemarmor.getEquipmentSlot() == EnumItemSlot.CHEST) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    },
    ARMOR_HEAD {
        @Override
        public boolean canEnchant(Item item) {
            boolean flag;

            if (item instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor) item;

                if (itemarmor.getEquipmentSlot() == EnumItemSlot.HEAD) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
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
            return item.canBeDepleted();
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
            return item instanceof Equipable || Block.byItem(item) instanceof Equipable;
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
            return item instanceof ItemVanishable || Block.byItem(item) instanceof ItemVanishable || null.BREAKABLE.canEnchant(item);
        }
    };

    EnchantmentSlotType() {}

    public abstract boolean canEnchant(Item item);
}
