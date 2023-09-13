package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentWaterWorker extends Enchantment {

    public EnchantmentWaterWorker(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_HEAD, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 1;
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + 40;
    }
}
