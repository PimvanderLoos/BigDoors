package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;

public class WorldGenEndGateway extends WorldGenerator<WorldGenEndGatewayConfiguration> {

    public WorldGenEndGateway(Codec<WorldGenEndGatewayConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenEndGatewayConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        WorldGenEndGatewayConfiguration worldgenendgatewayconfiguration = (WorldGenEndGatewayConfiguration) featureplacecontext.e();
        Iterator iterator = BlockPosition.a(blockposition.c(-1, -2, -1), blockposition.c(1, 2, 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            boolean flag = blockposition1.getX() == blockposition.getX();
            boolean flag1 = blockposition1.getY() == blockposition.getY();
            boolean flag2 = blockposition1.getZ() == blockposition.getZ();
            boolean flag3 = Math.abs(blockposition1.getY() - blockposition.getY()) == 2;

            if (flag && flag1 && flag2) {
                BlockPosition blockposition2 = blockposition1.immutableCopy();

                this.a((IWorldWriter) generatoraccessseed, blockposition2, Blocks.END_GATEWAY.getBlockData());
                worldgenendgatewayconfiguration.c().ifPresent((blockposition3) -> {
                    TileEntity tileentity = generatoraccessseed.getTileEntity(blockposition2);

                    if (tileentity instanceof TileEntityEndGateway) {
                        TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                        tileentityendgateway.a(blockposition3, worldgenendgatewayconfiguration.d());
                        tileentity.update();
                    }

                });
            } else if (flag1) {
                this.a((IWorldWriter) generatoraccessseed, blockposition1, Blocks.AIR.getBlockData());
            } else if (flag3 && flag && flag2) {
                this.a((IWorldWriter) generatoraccessseed, blockposition1, Blocks.BEDROCK.getBlockData());
            } else if ((flag || flag2) && !flag3) {
                this.a((IWorldWriter) generatoraccessseed, blockposition1, Blocks.BEDROCK.getBlockData());
            } else {
                this.a((IWorldWriter) generatoraccessseed, blockposition1, Blocks.AIR.getBlockData());
            }
        }

        return true;
    }
}
