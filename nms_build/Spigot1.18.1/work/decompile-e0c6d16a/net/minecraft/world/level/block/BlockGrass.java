package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BlockGrass extends BlockDirtSnowSpreadable implements IBlockFragilePlantElement {

    public BlockGrass(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iblockaccess.getBlockState(blockposition.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = Blocks.GRASS.defaultBlockState();
        int i = 0;

        while (i < 128) {
            BlockPosition blockposition2 = blockposition1;
            int j = 0;

            while (true) {
                if (j < i / 16) {
                    blockposition2 = blockposition2.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (worldserver.getBlockState(blockposition2.below()).is((Block) this) && !worldserver.getBlockState(blockposition2).isCollisionShapeFullBlock(worldserver, blockposition2)) {
                        ++j;
                        continue;
                    }
                } else {
                    IBlockData iblockdata2 = worldserver.getBlockState(blockposition2);

                    if (iblockdata2.is(iblockdata1.getBlock()) && random.nextInt(10) == 0) {
                        ((IBlockFragilePlantElement) iblockdata1.getBlock()).performBonemeal(worldserver, random, blockposition2, iblockdata2);
                    }

                    if (iblockdata2.isAir()) {
                        label36:
                        {
                            PlacedFeature placedfeature;

                            if (random.nextInt(8) == 0) {
                                List<WorldGenFeatureConfigured<?, ?>> list = worldserver.getBiome(blockposition2).getGenerationSettings().getFlowerFeatures();

                                if (list.isEmpty()) {
                                    break label36;
                                }

                                placedfeature = (PlacedFeature) ((WorldGenFeatureRandomPatchConfiguration) ((WorldGenFeatureConfigured) list.get(0)).config()).feature().get();
                            } else {
                                placedfeature = VegetationPlacements.GRASS_BONEMEAL;
                            }

                            placedfeature.place(worldserver, worldserver.getChunkSource().getGenerator(), random, blockposition2);
                        }
                    }
                }

                ++i;
                break;
            }
        }

    }
}
