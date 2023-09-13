package net.minecraft.server;

import java.util.List;

public class WorldGenFeatureCircleConfiguration implements WorldGenFeatureConfiguration {

    public final Block a;
    public final int b;
    public final int c;
    public final List<Block> d;

    public WorldGenFeatureCircleConfiguration(Block block, int i, int j, List<Block> list) {
        this.a = block;
        this.b = i;
        this.c = j;
        this.d = list;
    }
}
