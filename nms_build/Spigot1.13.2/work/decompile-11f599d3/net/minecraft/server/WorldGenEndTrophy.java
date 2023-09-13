package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenEndTrophy extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public static final BlockPosition a = BlockPosition.ZERO;
    private final boolean b;

    public WorldGenEndTrophy(boolean flag) {
        this.b = flag;
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        Iterator iterator = BlockPosition.b(new BlockPosition(blockposition.getX() - 4, blockposition.getY() - 1, blockposition.getZ() - 4), new BlockPosition(blockposition.getX() + 4, blockposition.getY() + 32, blockposition.getZ() + 4)).iterator();

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();
            double d0 = blockposition_mutableblockposition.h(blockposition.getX(), blockposition_mutableblockposition.getY(), blockposition.getZ());

            if (d0 <= 3.5D) {
                if (blockposition_mutableblockposition.getY() < blockposition.getY()) {
                    if (d0 <= 2.5D) {
                        this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
                    } else if (blockposition_mutableblockposition.getY() < blockposition.getY()) {
                        this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.END_STONE.getBlockData());
                    }
                } else if (blockposition_mutableblockposition.getY() > blockposition.getY()) {
                    this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.AIR.getBlockData());
                } else if (d0 > 2.5D) {
                    this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
                } else if (this.b) {
                    this.a(generatoraccess, new BlockPosition(blockposition_mutableblockposition), Blocks.END_PORTAL.getBlockData());
                } else {
                    this.a(generatoraccess, new BlockPosition(blockposition_mutableblockposition), Blocks.AIR.getBlockData());
                }
            }
        }

        for (int i = 0; i < 4; ++i) {
            this.a(generatoraccess, blockposition.up(i), Blocks.BEDROCK.getBlockData());
        }

        BlockPosition blockposition1 = blockposition.up(2);
        Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator1.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator1.next();

            this.a(generatoraccess, blockposition1.shift(enumdirection), (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.a, enumdirection));
        }

        return true;
    }
}
