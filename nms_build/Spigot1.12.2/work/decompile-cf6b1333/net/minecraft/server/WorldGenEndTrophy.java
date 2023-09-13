package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenEndTrophy extends WorldGenerator {

    public static final BlockPosition a = BlockPosition.ZERO;
    public static final BlockPosition b = new BlockPosition(WorldGenEndTrophy.a.getX() - 4 & -16, 0, WorldGenEndTrophy.a.getZ() - 4 & -16);
    private final boolean c;

    public WorldGenEndTrophy(boolean flag) {
        this.c = flag;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.b(new BlockPosition(blockposition.getX() - 4, blockposition.getY() - 1, blockposition.getZ() - 4), new BlockPosition(blockposition.getX() + 4, blockposition.getY() + 32, blockposition.getZ() + 4)).iterator();

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();
            double d0 = blockposition_mutableblockposition.h(blockposition.getX(), blockposition_mutableblockposition.getY(), blockposition.getZ());

            if (d0 <= 3.5D) {
                if (blockposition_mutableblockposition.getY() < blockposition.getY()) {
                    if (d0 <= 2.5D) {
                        this.a(world, blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
                    } else if (blockposition_mutableblockposition.getY() < blockposition.getY()) {
                        this.a(world, blockposition_mutableblockposition, Blocks.END_STONE.getBlockData());
                    }
                } else if (blockposition_mutableblockposition.getY() > blockposition.getY()) {
                    this.a(world, blockposition_mutableblockposition, Blocks.AIR.getBlockData());
                } else if (d0 > 2.5D) {
                    this.a(world, blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
                } else if (this.c) {
                    this.a(world, new BlockPosition(blockposition_mutableblockposition), Blocks.END_PORTAL.getBlockData());
                } else {
                    this.a(world, new BlockPosition(blockposition_mutableblockposition), Blocks.AIR.getBlockData());
                }
            }
        }

        for (int i = 0; i < 4; ++i) {
            this.a(world, blockposition.up(i), Blocks.BEDROCK.getBlockData());
        }

        BlockPosition blockposition1 = blockposition.up(2);
        Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator1.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator1.next();

            this.a(world, blockposition1.shift(enumdirection), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, enumdirection));
        }

        return true;
    }
}
