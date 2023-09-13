package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;

public class WorldGenFeatureBastionRemnant extends WorldGenFeatureJigsaw {

    private static final int BASTION_SPAWN_HEIGHT = 33;

    public WorldGenFeatureBastionRemnant(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 33, false, false, (piecegeneratorsupplier_a) -> {
            return true;
        });
    }
}
