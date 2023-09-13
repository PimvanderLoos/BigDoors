package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;

public class WorldGenFlatLayerInfo {

    public static final Codec<WorldGenFlatLayerInfo> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter(WorldGenFlatLayerInfo::getHeight), BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").orElse(Blocks.AIR).forGetter((worldgenflatlayerinfo) -> {
            return worldgenflatlayerinfo.getBlockState().getBlock();
        })).apply(instance, WorldGenFlatLayerInfo::new);
    });
    private final Block block;
    private final int height;

    public WorldGenFlatLayerInfo(int i, Block block) {
        this.height = i;
        this.block = block;
    }

    public int getHeight() {
        return this.height;
    }

    public IBlockData getBlockState() {
        return this.block.defaultBlockState();
    }

    public String toString() {
        String s = this.height != 1 ? this.height + "*" : "";

        return s + BuiltInRegistries.BLOCK.getKey(this.block);
    }
}
