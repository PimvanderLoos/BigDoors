package net.minecraft.server;

public class WorldGenFeatureBlockOffsetConfiguration implements WorldGenFeatureConfiguration {

    public final Block a;
    public final int b;

    public WorldGenFeatureBlockOffsetConfiguration(Block block, int i) {
        this.a = block;
        this.b = i;
    }
}
