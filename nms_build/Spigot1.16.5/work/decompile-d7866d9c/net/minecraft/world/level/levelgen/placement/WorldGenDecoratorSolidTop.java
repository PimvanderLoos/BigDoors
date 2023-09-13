package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorSolidTop extends WorldGenDecoratorHeight<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorSolidTop(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected HeightMap.Type a(WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2) {
        return HeightMap.Type.OCEAN_FLOOR_WG;
    }
}
