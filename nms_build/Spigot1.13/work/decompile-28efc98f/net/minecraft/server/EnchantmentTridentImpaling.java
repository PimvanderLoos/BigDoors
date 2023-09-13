package net.minecraft.server;

public class EnchantmentTridentImpaling extends Enchantment {

    public EnchantmentTridentImpaling(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.TRIDENT, aenumitemslot);
    }

    public int a(int i) {
        return 1 + (i - 1) * 8;
    }

    public int b(int i) {
        return this.a(i) + 20;
    }

    public int getMaxLevel() {
        return 5;
    }

    public float a(int i, EnumMonsterType enummonstertype) {
        return enummonstertype == EnumMonsterType.e ? (float) i * 2.5F : 0.0F;
    }
}
