package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;

public class WorldGenFlatLayerInfo {

    public static final Codec<WorldGenFlatLayerInfo> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter(WorldGenFlatLayerInfo::a), IRegistry.BLOCK.fieldOf("block").orElse(Blocks.AIR).forGetter((worldgenflatlayerinfo) -> {
            return worldgenflatlayerinfo.b().getBlock();
        })).apply(instance, WorldGenFlatLayerInfo::new);
    });
    private final Block block;
    private final int height;

    public WorldGenFlatLayerInfo(int i, Block block) {
        this.height = i;
        this.block = block;
    }

    public int a() {
        return this.height;
    }

    public IBlockData b() {
        return this.block.getBlockData();
    }

    public String toString() {
        String s = this.height != 1 ? this.height + "*" : "";

        return s + IRegistry.BLOCK.getKey(this.block);
    }
}
