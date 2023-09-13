package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class TorchflowerCropBlock extends BlockCrops {

    public static final int MAX_AGE = 2;
    public static final BlockStateInteger AGE = BlockProperties.AGE_2;
    private static final float AABB_OFFSET = 3.0F;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D), Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D), Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D)};

    public TorchflowerCropBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(TorchflowerCropBlock.AGE);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return TorchflowerCropBlock.SHAPE_BY_AGE[(Integer) iblockdata.getValue(this.getAgeProperty())];
    }

    @Override
    public BlockStateInteger getAgeProperty() {
        return TorchflowerCropBlock.AGE;
    }

    @Override
    public int getMaxAge() {
        return 2;
    }

    @Override
    protected IMaterial getBaseSeedId() {
        return Items.TORCHFLOWER_SEEDS;
    }

    @Override
    public IBlockData getStateForAge(int i) {
        return i == 2 ? Blocks.TORCHFLOWER.defaultBlockState() : super.getStateForAge(i);
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (randomsource.nextInt(3) != 0) {
            super.randomTick(iblockdata, worldserver, blockposition, randomsource);
        }

    }

    @Override
    protected int getBonemealAgeIncrease(World world) {
        return super.getBonemealAgeIncrease(world) / 3;
    }
}
