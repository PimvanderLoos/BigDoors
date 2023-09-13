package net.minecraft.world.item.enchantment;

import net.minecraft.util.WeightedRandom;

public class WeightedRandomEnchant extends WeightedRandom.WeightedRandomChoice {

    public final Enchantment enchantment;
    public final int level;

    public WeightedRandomEnchant(Enchantment enchantment, int i) {
        super(enchantment.d().a());
        this.enchantment = enchantment;
        this.level = i;
    }
}
