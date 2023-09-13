package net.minecraft.server;

public class CraftingStatistic extends Statistic {

    private final Item g;

    public CraftingStatistic(String s, String s1, IChatBaseComponent ichatbasecomponent, Item item) {
        super(s + s1, ichatbasecomponent);
        this.g = item;
    }
}
