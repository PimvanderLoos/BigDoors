package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentMending extends Enchantment {

    public EnchantmentMending(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.BREAKABLE, aenumitemslot);
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
}
