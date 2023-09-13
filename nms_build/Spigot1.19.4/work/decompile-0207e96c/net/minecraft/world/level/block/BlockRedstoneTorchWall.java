package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockRedstoneTorchWall extends BlockRedstoneTorch {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean LIT = BlockRedstoneTorch.LIT;

    protected BlockRedstoneTorchWall(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockRedstoneTorchWall.FACING, EnumDirection.NORTH)).setValue(BlockRedstoneTorchWall.LIT, true));
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockTorchWall.getShape(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return Blocks.WALL_TORCH.canSurvive(iblockdata, iworldreader, blockposition);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return Blocks.WALL_TORCH.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = Blocks.WALL_TORCH.getStateForPlacement(blockactioncontext);

        return iblockdata == null ? null : (IBlockData) this.defaultBlockState().setValue(BlockRedstoneTorchWall.FACING, (EnumDirection) iblockdata.getValue(BlockRedstoneTorchWall.FACING));
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockRedstoneTorchWall.LIT)) {
            EnumDirection enumdirection = ((EnumDirection) iblockdata.getValue(BlockRedstoneTorchWall.FACING)).getOpposite();
            double d0 = 0.27D;
            double d1 = (double) blockposition.getX() + 0.5D + (randomsource.nextDouble() - 0.5D) * 0.2D + 0.27D * (double) enumdirection.getStepX();
            double d2 = (double) blockposition.getY() + 0.7D + (randomsource.nextDouble() - 0.5D) * 0.2D + 0.22D;
            double d3 = (double) blockposition.getZ() + 0.5D + (randomsource.nextDouble() - 0.5D) * 0.2D + 0.27D * (double) enumdirection.getStepZ();

            world.addParticle(this.flameParticle, d1, d2, d3, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected boolean hasNeighborSignal(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.getValue(BlockRedstoneTorchWall.FACING)).getOpposite();

        return world.hasSignal(blockposition.relative(enumdirection), enumdirection);
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockRedstoneTorchWall.LIT) && iblockdata.getValue(BlockRedstoneTorchWall.FACING) != enumdirection ? 15 : 0;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return Blocks.WALL_TORCH.rotate(iblockdata, enumblockrotation);
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return Blocks.WALL_TORCH.mirror(iblockdata, enumblockmirror);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockRedstoneTorchWall.FACING, BlockRedstoneTorchWall.LIT);
    }
}
