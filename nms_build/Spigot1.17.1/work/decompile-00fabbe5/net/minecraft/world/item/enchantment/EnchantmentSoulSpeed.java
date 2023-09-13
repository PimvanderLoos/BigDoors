package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentSoulSpeed extends Enchantment {

    public EnchantmentSoulSpeed(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_FEET, aenumitemslot);
    }

    @Override
    public int a(int i) {
        return i * 10;
    }

    @Override
    public int b(int i) {
        return this.a(i) + 15;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean h() {
        return false;
    }

    @Override
    public boolean i() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
