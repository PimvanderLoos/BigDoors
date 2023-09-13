package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenDecoratorHeightmapWorldSurface extends WorldGenDecoratorHeight<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorHeightmapWorldSurface(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected HeightMap.Type a(WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2) {
        return HeightMap.Type.WORLD_SURFACE_WG;
    }
}
