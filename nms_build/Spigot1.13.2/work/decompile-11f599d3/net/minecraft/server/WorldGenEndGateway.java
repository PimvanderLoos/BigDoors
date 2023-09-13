package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenEndGateway extends WorldGenerator<WorldGenEndGatewayConfiguration> {

    public WorldGenEndGateway() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenEndGatewayConfiguration worldgenendgatewayconfiguration) {
        Iterator iterator = BlockPosition.b(blockposition.a(-1, -2, -1), blockposition.a(1, 2, 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();
            boolean flag = blockposition_mutableblockposition.getX() == blockposition.getX();
            boolean flag1 = blockposition_mutableblockposition.getY() == blockposition.getY();
            boolean flag2 = blockposition_mutableblockposition.getZ() == blockposition.getZ();
            boolean flag3 = Math.abs(blockposition_mutableblockposition.getY() - blockposition.getY()) == 2;

            if (flag && flag1 && flag2) {
                BlockPosition blockposition1 = blockposition_mutableblockposition.h();

                this.a(generatoraccess, blockposition1, Blocks.END_GATEWAY.getBlockData());
                if (worldgenendgatewayconfiguration.a()) {
                    TileEntity tileentity = generatoraccess.getTileEntity(blockposition1);

                    if (tileentity instanceof TileEntityEndGateway) {
                        TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                        tileentityendgateway.b(WorldProviderTheEnd.g);
                    }
                }
            } else if (flag1) {
                this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.AIR.getBlockData());
            } else if (flag3 && flag && flag2) {
                this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
            } else if ((flag || flag2) && !flag3) {
                this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.BEDROCK.getBlockData());
            } else {
                this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, Blocks.AIR.getBlockData());
            }
        }

        return true;
    }
}
