package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class SwiftSneakEnchantment extends Enchantment {

    public SwiftSneakEnchantment(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_LEGS, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return i * 25;
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
