package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockSkull extends BlockSkullAbstract {

    public static final int MAX = 15;
    private static final int ROTATIONS = 16;
    public static final BlockStateInteger ROTATION = BlockProperties.ROTATION_16;
    protected static final VoxelShape SHAPE = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

    protected BlockSkull(BlockSkull.a blockskull_a, BlockBase.Info blockbase_info) {
        super(blockskull_a, blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockSkull.ROTATION, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSkull.SHAPE;
    }

    @Override
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockSkull.ROTATION, MathHelper.floor((double) (blockactioncontext.i() * 16.0F / 360.0F) + 0.5D) & 15);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockSkull.ROTATION, enumblockrotation.a((Integer) iblockdata.get(BlockSkull.ROTATION), 16));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.set(BlockSkull.ROTATION, enumblockmirror.a((Integer) iblockdata.get(BlockSkull.ROTATION), 16));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSkull.ROTATION);
    }

    public interface a {}

    public static enum Type implements BlockSkull.a {

        SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, DRAGON;

        private Type() {}
    }
}
