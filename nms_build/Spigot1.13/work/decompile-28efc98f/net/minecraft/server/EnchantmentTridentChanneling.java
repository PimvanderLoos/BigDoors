package net.minecraft.server;

public class EnchantmentTridentChanneling extends Enchantment {

    public EnchantmentTridentChanneling(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.TRIDENT, aenumitemslot);
    }

    public int a(int i) {
        return 25;
    }

    public int b(int i) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean a(Enchantment enchantment) {
        return super.a(enchantment);
    }
}
