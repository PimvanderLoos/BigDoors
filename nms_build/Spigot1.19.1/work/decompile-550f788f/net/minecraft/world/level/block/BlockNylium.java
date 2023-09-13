package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.lighting.LightEngineLayer;

public class BlockNylium extends Block implements IBlockFragilePlantElement {

    protected BlockNylium(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    private static boolean canBeNylium(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);
        int i = LightEngineLayer.getLightBlockInto(iworldreader, iblockdata, blockposition, iblockdata1, blockposition1, EnumDirection.UP, iblockdata1.getLightBlock(iworldreader, blockposition1));

        return i < iworldreader.getMaxLightLevel();
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!canBeNylium(iblockdata, worldserver, blockposition)) {
            worldserver.setBlockAndUpdate(blockposition, Blocks.NETHERRACK.defaultBlockState());
        }

    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iblockaccess.getBlockState(blockposition.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = worldserver.getBlockState(blockposition);
        BlockPosition blockposition1 = blockposition.above();
        ChunkGenerator chunkgenerator = worldserver.getChunkSource().getGenerator();

        if (iblockdata1.is(Blocks.CRIMSON_NYLIUM)) {
            ((WorldGenFeatureConfigured) NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL.value()).place(worldserver, chunkgenerator, randomsource, blockposition1);
        } else if (iblockdata1.is(Blocks.WARPED_NYLIUM)) {
            ((WorldGenFeatureConfigured) NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL.value()).place(worldserver, chunkgenerator, randomsource, blockposition1);
            ((WorldGenFeatureConfigured) NetherFeatures.NETHER_SPROUTS_BONEMEAL.value()).place(worldserver, chunkgenerator, randomsource, blockposition1);
            if (randomsource.nextInt(8) == 0) {
                ((WorldGenFeatureConfigured) NetherFeatures.TWISTING_VINES_BONEMEAL.value()).place(worldserver, chunkgenerator, randomsource, blockposition1);
            }
        }

    }
}
