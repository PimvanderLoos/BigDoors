package net.minecraft.server;

public class EnchantmentMending extends Enchantment {

    public EnchantmentMending(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.BREAKABLE, aenumitemslot);
    }

    public int a(int i) {
        return i * 25;
    }

    public int b(int i) {
        return this.a(i) + 50;
    }

    public boolean isTreasure() {
        return true;
    }

    public int getMaxLevel() {
        return 1;
    }
}
