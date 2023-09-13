package net.minecraft.world.level;

import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.level.biome.BiomeBase;

public interface ColorResolver {

    @DontObfuscate
    int getColor(BiomeBase biomebase, double d0, double d1);
}
