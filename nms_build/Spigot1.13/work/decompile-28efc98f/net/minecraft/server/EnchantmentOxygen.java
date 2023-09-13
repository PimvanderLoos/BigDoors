package net.minecraft.server;

public class EnchantmentOxygen extends Enchantment {

    public EnchantmentOxygen(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_HEAD, aenumitemslot);
    }

    public int a(int i) {
        return 10 * i;
    }

    public int b(int i) {
        return this.a(i) + 30;
    }

    public int getMaxLevel() {
        return 3;
    }
}
