package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockRedstoneTorchWall.FACING, EnumDirection.NORTH)).set(BlockRedstoneTorchWall.LIT, true));
    }

    @Override
    public String h() {
        return this.getItem().getName();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockTorchWall.h(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return Blocks.WALL_TORCH.canPlace(iblockdata, iworldreader, blockposition);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return Blocks.WALL_TORCH.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = Blocks.WALL_TORCH.getPlacedState(blockactioncontext);

        return iblockdata == null ? null : (IBlockData) this.getBlockData().set(BlockRedstoneTorchWall.FACING, (EnumDirection) iblockdata.get(BlockRedstoneTorchWall.FACING));
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockRedstoneTorchWall.LIT)) {
            EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockRedstoneTorchWall.FACING)).opposite();
            double d0 = 0.27D;
            double d1 = (double) blockposition.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double) enumdirection.getAdjacentX();
            double d2 = (double) blockposition.getY() + 0.7D + (random.nextDouble() - 0.5D) * 0.2D + 0.22D;
            double d3 = (double) blockposition.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double) enumdirection.getAdjacentZ();

            world.addParticle(this.flameParticle, d1, d2, d3, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockRedstoneTorchWall.FACING)).opposite();

        return world.isBlockFacePowered(blockposition.shift(enumdirection), enumdirection);
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockRedstoneTorchWall.LIT) && iblockdata.get(BlockRedstoneTorchWall.FACING) != enumdirection ? 15 : 0;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return Blocks.WALL_TORCH.a(iblockdata, enumblockrotation);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return Blocks.WALL_TORCH.a(iblockdata, enumblockmirror);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneTorchWall.FACING, BlockRedstoneTorchWall.LIT);
    }
}
