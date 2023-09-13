package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFlatLayerInfo {

    public static final Codec<WorldGenFlatLayerInfo> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 256).fieldOf("height").forGetter(WorldGenFlatLayerInfo::a), IRegistry.BLOCK.fieldOf("block").orElse(Blocks.AIR).forGetter((worldgenflatlayerinfo) -> {
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
