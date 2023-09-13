package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentKnockback extends Enchantment {

    protected EnchantmentKnockback(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.WEAPON, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 5 + 20 * (i - 1);
    }

    @Override
    public int getMaxCost(int i) {
        return super.getMinCost(i) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
