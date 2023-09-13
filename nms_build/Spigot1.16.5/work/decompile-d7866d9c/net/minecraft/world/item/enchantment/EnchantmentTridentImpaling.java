package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;

public class EnchantmentTridentImpaling extends Enchantment {

    public EnchantmentTridentImpaling(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.TRIDENT, aenumitemslot);
    }

    @Override
    public int a(int i) {
        return 1 + (i - 1) * 8;
    }

    @Override
    public int b(int i) {
        return this.a(i) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float a(int i, EnumMonsterType enummonstertype) {
        return enummonstertype == EnumMonsterType.WATER_MOB ? (float) i * 2.5F : 0.0F;
    }
}
