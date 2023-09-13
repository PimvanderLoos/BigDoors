package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
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
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iworldreader.getBlockState(blockposition.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = Blocks.GRASS.defaultBlockState();
        Optional<Holder.c<PlacedFeature>> optional = worldserver.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);
        int i = 0;

        while (i < 128) {
            BlockPosition blockposition2 = blockposition1;
            int j = 0;

            while (true) {
                if (j < i / 16) {
                    blockposition2 = blockposition2.offset(randomsource.nextInt(3) - 1, (randomsource.nextInt(3) - 1) * randomsource.nextInt(3) / 2, randomsource.nextInt(3) - 1);
                    if (worldserver.getBlockState(blockposition2.below()).is((Block) this) && !worldserver.getBlockState(blockposition2).isCollisionShapeFullBlock(worldserver, blockposition2)) {
                        ++j;
                        continue;
                    }
                } else {
                    IBlockData iblockdata2 = worldserver.getBlockState(blockposition2);

                    if (iblockdata2.is(iblockdata1.getBlock()) && randomsource.nextInt(10) == 0) {
                        ((IBlockFragilePlantElement) iblockdata1.getBlock()).performBonemeal(worldserver, randomsource, blockposition2, iblockdata2);
                    }

                    if (iblockdata2.isAir()) {
                        label39:
                        {
                            Holder holder;

                            if (randomsource.nextInt(8) == 0) {
                                List<WorldGenFeatureConfigured<?, ?>> list = ((BiomeBase) worldserver.getBiome(blockposition2).value()).getGenerationSettings().getFlowerFeatures();

                                if (list.isEmpty()) {
                                    break label39;
                                }

                                holder = ((WorldGenFeatureRandomPatchConfiguration) ((WorldGenFeatureConfigured) list.get(0)).config()).feature();
                            } else {
                                if (!optional.isPresent()) {
                                    break label39;
                                }

                                holder = (Holder) optional.get();
                            }

                            ((PlacedFeature) holder.value()).place(worldserver, worldserver.getChunkSource().getGenerator(), randomsource, blockposition2);
                        }
                    }
                }

                ++i;
                break;
            }
        }

    }
}
