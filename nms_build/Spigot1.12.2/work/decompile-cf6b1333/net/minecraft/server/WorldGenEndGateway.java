package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenEndGateway extends WorldGenerator {

    public WorldGenEndGateway() {}

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.b(blockposition.a(-1, -2, -1), blockposition.a(1, 2, 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();
            boolean flag = blockposition_mutableblockposition.getX() == blockposition.getX();
            boolean flag1 = blockposition_mutableblockposition.getY() == blockposition.getY();
            boolean flag2 = blockposition_mutableblockposition.getZ() == blockposition.getZ();
            boolean flag3 = Math.abs(blockposition_mutableblockposition.getY() - blockposition.getY()) == 2;

            if (flag && flag1 && flag2) {
                this.a(world, new BlockPosition(blockposition_mutableblockposition), Blocks.END_GATEWAY.getBlockData());
            } else if (flag1) {
                this.a(world, blockposition_mutableblockposition, Blocks.AIR.getBlockData());
            } else if (flag3 && flag && flag2) {
                this.a(world, blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
            } else if ((flag || flag2) && !flag3) {
                this.a(world, blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
            } else {
                this.a(world, blockposition_mutableblockposition, Blocks.AIR.getBlockData());
            }
        }

        return true;
    }
}
