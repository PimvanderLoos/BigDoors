package net.minecraft.world.level.block.piston;

import java.util.Arrays;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDirectional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockPistonExtension extends BlockDirectional {

    public static final BlockStateEnum<BlockPropertyPistonType> TYPE = BlockProperties.aJ;
    public static final BlockStateBoolean SHORT = BlockProperties.x;
    protected static final VoxelShape d = Block.a(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape e = Block.a(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape f = Block.a(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape g = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape h = Block.a(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape i = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    protected static final VoxelShape j = Block.a(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape k = Block.a(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
    protected static final VoxelShape o = Block.a(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape p = Block.a(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
    protected static final VoxelShape q = Block.a(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape r = Block.a(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
    protected static final VoxelShape s = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape t = Block.a(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape u = Block.a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape v = Block.a(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape w = Block.a(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape x = Block.a(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    private static final VoxelShape[] y = a(true);
    private static final VoxelShape[] z = a(false);

    private static VoxelShape[] a(boolean flag) {
        return (VoxelShape[]) Arrays.stream(EnumDirection.values()).map((enumdirection) -> {
            return a(enumdirection, flag);
        }).toArray((i) -> {
            return new VoxelShape[i];
        });
    }

    private static VoxelShape a(EnumDirection enumdirection, boolean flag) {
        switch (enumdirection) {
            case DOWN:
            default:
                return VoxelShapes.a(BlockPistonExtension.i, flag ? BlockPistonExtension.t : BlockPistonExtension.k);
            case UP:
                return VoxelShapes.a(BlockPistonExtension.h, flag ? BlockPistonExtension.s : BlockPistonExtension.j);
            case NORTH:
                return VoxelShapes.a(BlockPistonExtension.g, flag ? BlockPistonExtension.v : BlockPistonExtension.p);
            case SOUTH:
                return VoxelShapes.a(BlockPistonExtension.f, flag ? BlockPistonExtension.u : BlockPistonExtension.o);
            case WEST:
                return VoxelShapes.a(BlockPistonExtension.e, flag ? BlockPistonExtension.x : BlockPistonExtension.r);
            case EAST:
                return VoxelShapes.a(BlockPistonExtension.d, flag ? BlockPistonExtension.w : BlockPistonExtension.q);
        }
    }

    public BlockPistonExtension(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPistonExtension.FACING, EnumDirection.NORTH)).set(BlockPistonExtension.TYPE, BlockPropertyPistonType.DEFAULT)).set(BlockPistonExtension.SHORT, false));
    }

    @Override
    public boolean c_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return ((Boolean) iblockdata.get(BlockPistonExtension.SHORT) ? BlockPistonExtension.y : BlockPistonExtension.z)[((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).ordinal()];
    }

    private boolean a(IBlockData iblockdata, IBlockData iblockdata1) {
        Block block = iblockdata.get(BlockPistonExtension.TYPE) == BlockPropertyPistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;

        return iblockdata1.a(block) && (Boolean) iblockdata1.get(BlockPiston.EXTENDED) && iblockdata1.get(BlockPistonExtension.FACING) == iblockdata.get(BlockPistonExtension.FACING);
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.abilities.canInstantlyBuild) {
            BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite());

            if (this.a(iblockdata, world.getType(blockposition1))) {
                world.b(blockposition1, false);
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite());

            if (this.a(iblockdata, world.getType(blockposition1))) {
                world.b(blockposition1, true);
            }

        }
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.opposite() == iblockdata.get(BlockPistonExtension.FACING) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite()));

        return this.a(iblockdata, iblockdata1) || iblockdata1.a(Blocks.MOVING_PISTON) && iblockdata1.get(BlockPistonExtension.FACING) == iblockdata.get(BlockPistonExtension.FACING);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (iblockdata.canPlace(world, blockposition)) {
            BlockPosition blockposition2 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite());

            world.getType(blockposition2).doPhysics(world, blockposition2, block, blockposition1, false);
        }

    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockPistonExtension.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPistonExtension.FACING, BlockPistonExtension.TYPE, BlockPistonExtension.SHORT);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
