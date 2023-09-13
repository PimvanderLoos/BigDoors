package net.minecraft.server;

public class WorldGenFeatureRandomConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenerator<?>[] a;
    public final WorldGenFeatureConfiguration[] b;
    public final int c;

    public WorldGenFeatureRandomConfiguration(WorldGenerator<?>[] aworldgenerator, WorldGenFeatureConfiguration[] aworldgenfeatureconfiguration, int i) {
        this.a = aworldgenerator;
        this.b = aworldgenfeatureconfiguration;
        this.c = i;
    }
}
