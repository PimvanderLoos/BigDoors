package net.minecraft.world.item.enchantment;

import net.minecraft.util.random.WeightedEntry;

public class WeightedRandomEnchant extends WeightedEntry.a {

    public final Enchantment enchantment;
    public final int level;

    public WeightedRandomEnchant(Enchantment enchantment, int i) {
        super(enchantment.getRarity().getWeight());
        this.enchantment = enchantment;
        this.level = i;
    }
}
