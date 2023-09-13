package net.minecraft.world.level.block;

import com.google.common.base.Predicates;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetector;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockEnderPortalFrame extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean HAS_EYE = BlockProperties.EYE;
    protected static final VoxelShape BASE_SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    protected static final VoxelShape EYE_SHAPE = Block.a(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape FULL_SHAPE = VoxelShapes.a(BlockEnderPortalFrame.BASE_SHAPE, BlockEnderPortalFrame.EYE_SHAPE);
    private static ShapeDetector portalShape;

    public BlockEnderPortalFrame(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockEnderPortalFrame.FACING, EnumDirection.NORTH)).set(BlockEnderPortalFrame.HAS_EYE, false));
    }

    @Override
    public boolean g_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Boolean) iblockdata.get(BlockEnderPortalFrame.HAS_EYE) ? BlockEnderPortalFrame.FULL_SHAPE : BlockEnderPortalFrame.BASE_SHAPE;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockEnderPortalFrame.FACING, blockactioncontext.g().opposite())).set(BlockEnderPortalFrame.HAS_EYE, false);
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Boolean) iblockdata.get(BlockEnderPortalFrame.HAS_EYE) ? 15 : 0;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockEnderPortalFrame.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEnderPortalFrame.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockEnderPortalFrame.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockEnderPortalFrame.FACING, BlockEnderPortalFrame.HAS_EYE);
    }

    public static ShapeDetector c() {
        if (BlockEnderPortalFrame.portalShape == null) {
            BlockEnderPortalFrame.portalShape = ShapeDetectorBuilder.a().a("?vvv?", ">???<", ">???<", ">???<", "?^^^?").a('?', ShapeDetectorBlock.a(BlockStatePredicate.ANY)).a('^', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.HAS_EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.SOUTH)))).a('>', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.HAS_EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.WEST)))).a('v', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.HAS_EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.NORTH)))).a('<', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.HAS_EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.EAST)))).b();
        }

        return BlockEnderPortalFrame.portalShape;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
