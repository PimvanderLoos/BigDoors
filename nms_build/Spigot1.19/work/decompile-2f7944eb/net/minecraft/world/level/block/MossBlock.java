package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class MossBlock extends Block implements IBlockFragilePlantElement {

    public MossBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
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
        ((WorldGenFeatureConfigured) CaveFeatures.MOSS_PATCH_BONEMEAL.value()).place(worldserver, worldserver.getChunkSource().getGenerator(), randomsource, blockposition.above());
    }
}
