package net.minecraft.server;

public class WorldGenFeatureRandomChoiceConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenerator<?>[] a;
    public final WorldGenFeatureConfiguration[] b;
    public final float[] c;
    public final WorldGenerator<?> d;
    public final WorldGenFeatureConfiguration f;

    public <FC extends WorldGenFeatureConfiguration> WorldGenFeatureRandomChoiceConfiguration(WorldGenerator<?>[] aworldgenerator, WorldGenFeatureConfiguration[] aworldgenfeatureconfiguration, float[] afloat, WorldGenerator<FC> worldgenerator, FC fc) {
        this.a = aworldgenerator;
        this.b = aworldgenfeatureconfiguration;
        this.c = afloat;
        this.d = worldgenerator;
        this.f = fc;
    }
}
