package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;

public class WorldGenCarverConfigurationEmpty implements WorldGenCarverConfiguration {

    public static final Codec<WorldGenCarverConfigurationEmpty> b = Codec.unit(() -> {
        return WorldGenCarverConfigurationEmpty.c;
    });
    public static final WorldGenCarverConfigurationEmpty c = new WorldGenCarverConfigurationEmpty();

    public WorldGenCarverConfigurationEmpty() {}
}
