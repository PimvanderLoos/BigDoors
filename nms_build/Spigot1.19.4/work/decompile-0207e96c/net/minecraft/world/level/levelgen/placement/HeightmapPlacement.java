package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.HeightMap;

public class HeightmapPlacement extends PlacementModifier {

    public static final Codec<HeightmapPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightMap.Type.CODEC.fieldOf("heightmap").forGetter((heightmapplacement) -> {
            return heightmapplacement.heightmap;
        })).apply(instance, HeightmapPlacement::new);
    });
    private final HeightMap.Type heightmap;

    private HeightmapPlacement(HeightMap.Type heightmap_type) {
        this.heightmap = heightmap_type;
    }

    public static HeightmapPlacement onHeightmap(HeightMap.Type heightmap_type) {
        return new HeightmapPlacement(heightmap_type);
    }

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getZ();
        int k = placementcontext.getHeight(this.heightmap, i, j);

        return k > placementcontext.getMinBuildHeight() ? Stream.of(new BlockPosition(i, k, j)) : Stream.of();
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.HEIGHTMAP;
    }
}
