package net.minecraft.server;

public class WorldGenFeatureOceanRuinConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenFeatureOceanRuin.Temperature a;
    public final float b;
    public final float c;

    public WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature worldgenfeatureoceanruin_temperature, float f, float f1) {
        this.a = worldgenfeatureoceanruin_temperature;
        this.b = f;
        this.c = f1;
    }
}
