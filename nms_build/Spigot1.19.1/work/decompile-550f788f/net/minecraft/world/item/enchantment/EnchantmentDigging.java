package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnchantmentDigging extends Enchantment {

    protected EnchantmentDigging(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.DIGGER, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 1 + 10 * (i - 1);
    }

    @Override
    public int getMaxCost(int i) {
        return super.getMinCost(i) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(ItemStack itemstack) {
        return itemstack.is(Items.SHEARS) ? true : super.canEnchant(itemstack);
    }
}
