package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenDecoratorSolidTop extends WorldGenDecoratorHeight<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorSolidTop(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected HeightMap.Type a(WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2) {
        return HeightMap.Type.OCEAN_FLOOR_WG;
    }
}
