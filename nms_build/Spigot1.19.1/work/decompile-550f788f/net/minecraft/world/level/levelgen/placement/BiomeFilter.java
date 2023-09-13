package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeBase;

public class BiomeFilter extends PlacementFilter {

    private static final BiomeFilter INSTANCE = new BiomeFilter();
    public static Codec<BiomeFilter> CODEC = Codec.unit(() -> {
        return BiomeFilter.INSTANCE;
    });

    private BiomeFilter() {}

    public static BiomeFilter biome() {
        return BiomeFilter.INSTANCE;
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        PlacedFeature placedfeature = (PlacedFeature) placementcontext.topFeature().orElseThrow(() -> {
            return new IllegalStateException("Tried to biome check an unregistered feature, or a feature that should not restrict the biome");
        });
        Holder<BiomeBase> holder = placementcontext.getLevel().getBiome(blockposition);

        return placementcontext.generator().getBiomeGenerationSettings(holder).hasFeature(placedfeature);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.BIOME_FILTER;
    }
}
