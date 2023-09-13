package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentOxygen extends Enchantment {

    public EnchantmentOxygen(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_HEAD, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 10 * i;
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
