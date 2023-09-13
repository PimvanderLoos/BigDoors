package net.minecraft.server;

public class WeightedRandomEnchant extends WeightedRandom.WeightedRandomChoice {

    public final Enchantment enchantment;
    public final int level;

    public WeightedRandomEnchant(Enchantment enchantment, int i) {
        super(enchantment.e().a());
        this.enchantment = enchantment;
        this.level = i;
    }
}
