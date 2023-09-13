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
    public static final BlockStateBoolean EYE = BlockProperties.h;
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    protected static final VoxelShape d = Block.a(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape e = VoxelShapes.a(BlockEnderPortalFrame.c, BlockEnderPortalFrame.d);
    private static ShapeDetector f;

    public BlockEnderPortalFrame(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockEnderPortalFrame.FACING, EnumDirection.NORTH)).set(BlockEnderPortalFrame.EYE, false));
    }

    @Override
    public boolean c_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Boolean) iblockdata.get(BlockEnderPortalFrame.EYE) ? BlockEnderPortalFrame.e : BlockEnderPortalFrame.c;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockEnderPortalFrame.FACING, blockactioncontext.f().opposite())).set(BlockEnderPortalFrame.EYE, false);
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Boolean) iblockdata.get(BlockEnderPortalFrame.EYE) ? 15 : 0;
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
        blockstatelist_a.a(BlockEnderPortalFrame.FACING, BlockEnderPortalFrame.EYE);
    }

    public static ShapeDetector c() {
        if (BlockEnderPortalFrame.f == null) {
            BlockEnderPortalFrame.f = ShapeDetectorBuilder.a().a("?vvv?", ">???<", ">???<", ">???<", "?^^^?").a('?', ShapeDetectorBlock.a(BlockStatePredicate.a)).a('^', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.SOUTH)))).a('>', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.WEST)))).a('v', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.NORTH)))).a('<', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(true)).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.EAST)))).b();
        }

        return BlockEnderPortalFrame.f;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
