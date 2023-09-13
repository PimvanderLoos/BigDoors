package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureHugeFungiConfiguration;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFungi extends BlockPlant implements IBlockFragilePlantElement {

    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4D;
    private final Supplier<Holder<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>>> feature;

    protected BlockFungi(BlockBase.Info blockbase_info, Supplier<Holder<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>>> supplier) {
        super(blockbase_info);
        this.feature = supplier;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFungi.SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.is(TagsBlock.NYLIUM) || iblockdata.is(Blocks.MYCELIUM) || iblockdata.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(iblockdata, iblockaccess, blockposition);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Block block = ((WorldGenFeatureHugeFungiConfiguration) ((WorldGenFeatureConfigured) ((Holder) this.feature.get()).value()).config()).validBaseState.getBlock();
        IBlockData iblockdata1 = iblockaccess.getBlockState(blockposition.below());

        return iblockdata1.is(block);
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) randomsource.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        ((WorldGenFeatureConfigured) ((Holder) this.feature.get()).value()).place(worldserver, worldserver.getChunkSource().getGenerator(), randomsource, blockposition);
    }
}
