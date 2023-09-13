package net.minecraft.world.entity.ai.village.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.IBlockData;

public record VillagePlaceType(Set<IBlockData> matchingStates, int maxTickets, int validRange) {

    public static final Predicate<Holder<VillagePlaceType>> NONE = (holder) -> {
        return false;
    };

    public VillagePlaceType(Set<IBlockData> set, int i, int j) {
        set = Set.copyOf(set);
        this.matchingStates = set;
        this.maxTickets = i;
        this.validRange = j;
    }

    public boolean is(IBlockData iblockdata) {
        return this.matchingStates.contains(iblockdata);
    }
}
