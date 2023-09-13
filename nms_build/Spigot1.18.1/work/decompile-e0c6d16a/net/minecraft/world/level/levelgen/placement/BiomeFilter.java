package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
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
    protected boolean shouldPlace(PlacementContext placementcontext, Random random, BlockPosition blockposition) {
        PlacedFeature placedfeature = (PlacedFeature) placementcontext.topFeature().orElseThrow(() -> {
            return new IllegalStateException("Tried to biome check an unregistered feature");
        });
        BiomeBase biomebase = placementcontext.getLevel().getBiome(blockposition);

        return biomebase.getGenerationSettings().hasFeature(placedfeature);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.BIOME_FILTER;
    }
}
