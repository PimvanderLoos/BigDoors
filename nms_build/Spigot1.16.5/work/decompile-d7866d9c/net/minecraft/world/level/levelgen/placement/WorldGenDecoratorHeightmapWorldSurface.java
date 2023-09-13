package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorHeightmapWorldSurface extends WorldGenDecoratorHeight<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorHeightmapWorldSurface(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected HeightMap.Type a(WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2) {
        return HeightMap.Type.WORLD_SURFACE_WG;
    }
}
