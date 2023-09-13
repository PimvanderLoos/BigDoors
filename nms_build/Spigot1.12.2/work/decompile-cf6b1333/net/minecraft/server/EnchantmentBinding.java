package net.minecraft.server;

public class EnchantmentBinding extends Enchantment {

    public EnchantmentBinding(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.WEARABLE, aenumitemslot);
        this.c("binding_curse");
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

    public boolean isTreasure() {
        return true;
    }

    public boolean isCursed() {
        return true;
    }
}
