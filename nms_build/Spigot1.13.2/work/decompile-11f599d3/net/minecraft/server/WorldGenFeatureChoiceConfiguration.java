package net.minecraft.server;

public class WorldGenFeatureChoiceConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenerator<?> a;
    public final WorldGenFeatureConfiguration b;
    public final WorldGenerator<?> c;
    public final WorldGenFeatureConfiguration d;

    public <FC extends WorldGenFeatureConfiguration> WorldGenFeatureChoiceConfiguration(WorldGenerator<?> worldgenerator, WorldGenFeatureConfiguration worldgenfeatureconfiguration, WorldGenerator<?> worldgenerator1, WorldGenFeatureConfiguration worldgenfeatureconfiguration1) {
        this.a = worldgenerator;
        this.b = worldgenfeatureconfiguration;
        this.c = worldgenerator1;
        this.d = worldgenfeatureconfiguration1;
    }
}
