package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFlatLayerInfo {

    public static final Codec<WorldGenFlatLayerInfo> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codecs.a(0, 256).fieldOf("height").forGetter(WorldGenFlatLayerInfo::a), IRegistry.BLOCK.fieldOf("block").withDefault(Blocks.AIR).forGetter((worldgenflatlayerinfo) -> {
            return worldgenflatlayerinfo.b().getBlock();
        })).apply(instance, WorldGenFlatLayerInfo::new);
    });
    private final IBlockData b;
    private final int c;
    private int d;

    public WorldGenFlatLayerInfo(int i, Block block) {
        this.c = i;
        this.b = block.getBlockData();
    }

    public int a() {
        return this.c;
    }

    public IBlockData b() {
        return this.b;
    }

    public int c() {
        return this.d;
    }

    public void a(int i) {
        this.d = i;
    }

    public String toString() {
        return (this.c != 1 ? this.c + "*" : "") + IRegistry.BLOCK.getKey(this.b.getBlock());
    }
}
