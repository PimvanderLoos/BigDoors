package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;

public class EnchantmentTridentImpaling extends Enchantment {

    public EnchantmentTridentImpaling(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.TRIDENT, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 1 + (i - 1) * 8;
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(int i, EnumMonsterType enummonstertype) {
        return enummonstertype == EnumMonsterType.WATER ? (float) i * 2.5F : 0.0F;
    }
}
