package net.minecraft.server;

public class EnchantmentWaterWorker extends Enchantment {

    public EnchantmentWaterWorker(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_HEAD, aenumitemslot);
        this.c("waterWorker");
    }

    public int a(int i) {
        return 1;
    }

    public int b(int i) {
        return this.a(i) + 40;
    }

    public int getMaxLevel() {
        return 1;
    }
}
