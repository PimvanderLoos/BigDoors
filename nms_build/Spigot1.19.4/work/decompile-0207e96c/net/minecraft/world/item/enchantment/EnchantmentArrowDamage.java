package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentArrowDamage extends Enchantment {

    public EnchantmentArrowDamage(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.BOW, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 1 + (i - 1) * 10;
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
