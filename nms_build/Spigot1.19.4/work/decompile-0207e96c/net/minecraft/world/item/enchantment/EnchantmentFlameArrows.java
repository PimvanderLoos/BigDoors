package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentFlameArrows extends Enchantment {

    public EnchantmentFlameArrows(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.BOW, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 20;
    }

    @Override
    public int getMaxCost(int i) {
        return 50;
    }
}
