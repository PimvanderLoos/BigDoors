package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!canBeNylium(iblockdata, worldserver, blockposition)) {
            worldserver.setBlockAndUpdate(blockposition, Blocks.NETHERRACK.defaultBlockState());
        }

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
        IBlockData iblockdata1 = worldserver.getBlockState(blockposition);
        BlockPosition blockposition1 = blockposition.above();
        ChunkGenerator chunkgenerator = worldserver.getChunkSource().getGenerator();

        if (iblockdata1.is(Blocks.CRIMSON_NYLIUM)) {
            NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL.place(worldserver, chunkgenerator, random, blockposition1);
        } else if (iblockdata1.is(Blocks.WARPED_NYLIUM)) {
            NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL.place(worldserver, chunkgenerator, random, blockposition1);
            NetherFeatures.NETHER_SPROUTS_BONEMEAL.place(worldserver, chunkgenerator, random, blockposition1);
            if (random.nextInt(8) == 0) {
                NetherFeatures.TWISTING_VINES_BONEMEAL.place(worldserver, chunkgenerator, random, blockposition1);
            }
        }

    }
}
