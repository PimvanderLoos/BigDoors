package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenVillage extends WorldGenFeatureJigsaw {

    public WorldGenVillage(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 0, true, true);
    }
}
