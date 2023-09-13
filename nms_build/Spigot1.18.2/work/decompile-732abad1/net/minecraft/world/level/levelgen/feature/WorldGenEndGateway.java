package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;

public class WorldGenEndGateway extends WorldGenerator<WorldGenEndGatewayConfiguration> {

    public WorldGenEndGateway(Codec<WorldGenEndGatewayConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenEndGatewayConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        WorldGenEndGatewayConfiguration worldgenendgatewayconfiguration = (WorldGenEndGatewayConfiguration) featureplacecontext.config();
        Iterator iterator = BlockPosition.betweenClosed(blockposition.offset(-1, -2, -1), blockposition.offset(1, 2, 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            boolean flag = blockposition1.getX() == blockposition.getX();
            boolean flag1 = blockposition1.getY() == blockposition.getY();
            boolean flag2 = blockposition1.getZ() == blockposition.getZ();
            boolean flag3 = Math.abs(blockposition1.getY() - blockposition.getY()) == 2;

            if (flag && flag1 && flag2) {
                BlockPosition blockposition2 = blockposition1.immutable();

                this.setBlock(generatoraccessseed, blockposition2, Blocks.END_GATEWAY.defaultBlockState());
                worldgenendgatewayconfiguration.getExit().ifPresent((blockposition3) -> {
                    TileEntity tileentity = generatoraccessseed.getBlockEntity(blockposition2);

                    if (tileentity instanceof TileEntityEndGateway) {
                        TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                        tileentityendgateway.setExitPosition(blockposition3, worldgenendgatewayconfiguration.isExitExact());
                        tileentity.setChanged();
                    }

                });
            } else if (flag1) {
                this.setBlock(generatoraccessseed, blockposition1, Blocks.AIR.defaultBlockState());
            } else if (flag3 && flag && flag2) {
                this.setBlock(generatoraccessseed, blockposition1, Blocks.BEDROCK.defaultBlockState());
            } else if ((flag || flag2) && !flag3) {
                this.setBlock(generatoraccessseed, blockposition1, Blocks.BEDROCK.defaultBlockState());
            } else {
                this.setBlock(generatoraccessseed, blockposition1, Blocks.AIR.defaultBlockState());
            }
        }

        return true;
    }
}
