package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class RarityFilter extends PlacementFilter {

    public static final Codec<RarityFilter> CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("chance").xmap(RarityFilter::new, (rarityfilter) -> {
        return rarityfilter.chance;
    }).codec();
    private final int chance;

    private RarityFilter(int i) {
        this.chance = i;
    }

    public static RarityFilter onAverageOnceEvery(int i) {
        return new RarityFilter(i);
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        return randomsource.nextFloat() < 1.0F / (float) this.chance;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.RARITY_FILTER;
    }
}
