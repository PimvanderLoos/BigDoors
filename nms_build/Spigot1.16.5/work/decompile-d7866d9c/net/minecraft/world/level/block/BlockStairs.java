package net.minecraft.world.level.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyHalf;
import net.minecraft.world.level.block.state.properties.BlockPropertyStairsShape;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockStairs extends Block implements IBlockWaterlogged {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockPropertyHalf> HALF = BlockProperties.ab;
    public static final BlockStateEnum<BlockPropertyStairsShape> SHAPE = BlockProperties.aL;
    public static final BlockStateBoolean d = BlockProperties.C;
    protected static final VoxelShape e = BlockStepAbstract.d;
    protected static final VoxelShape f = BlockStepAbstract.c;
    protected static final VoxelShape g = Block.a(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
    protected static final VoxelShape h = Block.a(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape i = Block.a(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape j = Block.a(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape k = Block.a(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape o = Block.a(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape p = Block.a(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape q = Block.a(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape[] r = a(BlockStairs.e, BlockStairs.g, BlockStairs.k, BlockStairs.h, BlockStairs.o);
    protected static final VoxelShape[] s = a(BlockStairs.f, BlockStairs.i, BlockStairs.p, BlockStairs.j, BlockStairs.q);
    private static final int[] t = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
    private final Block u;
    private final IBlockData v;

    private static VoxelShape[] a(VoxelShape voxelshape, VoxelShape voxelshape1, VoxelShape voxelshape2, VoxelShape voxelshape3, VoxelShape voxelshape4) {
        return (VoxelShape[]) IntStream.range(0, 16).mapToObj((i) -> {
            return a(i, voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        }).toArray((i) -> {
            return new VoxelShape[i];
        });
    }

    private static VoxelShape a(int i, VoxelShape voxelshape, VoxelShape voxelshape1, VoxelShape voxelshape2, VoxelShape voxelshape3, VoxelShape voxelshape4) {
        VoxelShape voxelshape5 = voxelshape;

        if ((i & 1) != 0) {
            voxelshape5 = VoxelShapes.a(voxelshape, voxelshape1);
        }

        if ((i & 2) != 0) {
            voxelshape5 = VoxelShapes.a(voxelshape5, voxelshape2);
        }

        if ((i & 4) != 0) {
            voxelshape5 = VoxelShapes.a(voxelshape5, voxelshape3);
        }

        if ((i & 8) != 0) {
            voxelshape5 = VoxelShapes.a(voxelshape5, voxelshape4);
        }

        return voxelshape5;
    }

    protected BlockStairs(IBlockData iblockdata, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockStairs.FACING, EnumDirection.NORTH)).set(BlockStairs.HALF, BlockPropertyHalf.BOTTOM)).set(BlockStairs.SHAPE, BlockPropertyStairsShape.STRAIGHT)).set(BlockStairs.d, false));
        this.u = iblockdata.getBlock();
        this.v = iblockdata;
    }

    @Override
    public boolean c_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (iblockdata.get(BlockStairs.HALF) == BlockPropertyHalf.TOP ? BlockStairs.r : BlockStairs.s)[BlockStairs.t[this.l(iblockdata)]];
    }

    private int l(IBlockData iblockdata) {
        return ((BlockPropertyStairsShape) iblockdata.get(BlockStairs.SHAPE)).ordinal() * 4 + ((EnumDirection) iblockdata.get(BlockStairs.FACING)).get2DRotationValue();
    }

    @Override
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.v.attack(world, blockposition, entityhuman);
    }

    @Override
    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        this.u.postBreak(generatoraccess, blockposition, iblockdata);
    }

    @Override
    public float getDurability() {
        return this.u.getDurability();
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata.getBlock())) {
            this.v.doPhysics(world, blockposition, Blocks.AIR, blockposition, false);
            this.u.onPlace(this.v, world, blockposition, iblockdata1, false);
        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            this.v.remove(world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        this.u.stepOn(world, blockposition, entity);
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return this.u.isTicking(iblockdata);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        this.u.tick(iblockdata, worldserver, blockposition, random);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        this.u.tickAlways(iblockdata, worldserver, blockposition, random);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return this.v.interact(world, entityhuman, enumhand, movingobjectpositionblock);
    }

    @Override
    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        this.u.wasExploded(world, blockposition, explosion);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockposition);
        IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockStairs.FACING, blockactioncontext.f())).set(BlockStairs.HALF, enumdirection != EnumDirection.DOWN && (enumdirection == EnumDirection.UP || blockactioncontext.getPos().y - (double) blockposition.getY() <= 0.5D) ? BlockPropertyHalf.BOTTOM : BlockPropertyHalf.TOP)).set(BlockStairs.d, fluid.getType() == FluidTypes.WATER);

        return (IBlockData) iblockdata.set(BlockStairs.SHAPE, g(iblockdata, blockactioncontext.getWorld(), blockposition));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockStairs.d)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection.n().d() ? (IBlockData) iblockdata.set(BlockStairs.SHAPE, g(iblockdata, generatoraccess, blockposition)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private static BlockPropertyStairsShape g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift(enumdirection));

        if (h(iblockdata1) && iblockdata.get(BlockStairs.HALF) == iblockdata1.get(BlockStairs.HALF)) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata1.get(BlockStairs.FACING);

            if (enumdirection1.n() != ((EnumDirection) iblockdata.get(BlockStairs.FACING)).n() && d(iblockdata, iblockaccess, blockposition, enumdirection1.opposite())) {
                if (enumdirection1 == enumdirection.h()) {
                    return BlockPropertyStairsShape.OUTER_LEFT;
                }

                return BlockPropertyStairsShape.OUTER_RIGHT;
            }
        }

        IBlockData iblockdata2 = iblockaccess.getType(blockposition.shift(enumdirection.opposite()));

        if (h(iblockdata2) && iblockdata.get(BlockStairs.HALF) == iblockdata2.get(BlockStairs.HALF)) {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata2.get(BlockStairs.FACING);

            if (enumdirection2.n() != ((EnumDirection) iblockdata.get(BlockStairs.FACING)).n() && d(iblockdata, iblockaccess, blockposition, enumdirection2)) {
                if (enumdirection2 == enumdirection.h()) {
                    return BlockPropertyStairsShape.INNER_LEFT;
                }

                return BlockPropertyStairsShape.INNER_RIGHT;
            }
        }

        return BlockPropertyStairsShape.STRAIGHT;
    }

    private static boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift(enumdirection));

        return !h(iblockdata1) || iblockdata1.get(BlockStairs.FACING) != iblockdata.get(BlockStairs.FACING) || iblockdata1.get(BlockStairs.HALF) != iblockdata.get(BlockStairs.HALF);
    }

    public static boolean h(IBlockData iblockdata) {
        return iblockdata.getBlock() instanceof BlockStairs;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockStairs.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockStairs.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        BlockPropertyStairsShape blockpropertystairsshape = (BlockPropertyStairsShape) iblockdata.get(BlockStairs.SHAPE);

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                if (enumdirection.n() == EnumDirection.EnumAxis.Z) {
                    switch (blockpropertystairsshape) {
                        case INNER_LEFT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.INNER_RIGHT);
                        case INNER_RIGHT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.INNER_LEFT);
                        case OUTER_LEFT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT);
                        default:
                            return iblockdata.a(EnumBlockRotation.CLOCKWISE_180);
                    }
                }
                break;
            case FRONT_BACK:
                if (enumdirection.n() == EnumDirection.EnumAxis.X) {
                    switch (blockpropertystairsshape) {
                        case INNER_LEFT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.INNER_LEFT);
                        case INNER_RIGHT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.INNER_RIGHT);
                        case OUTER_LEFT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT);
                        case STRAIGHT:
                            return iblockdata.a(EnumBlockRotation.CLOCKWISE_180);
                    }
                }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStairs.FACING, BlockStairs.HALF, BlockStairs.SHAPE, BlockStairs.d);
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockStairs.d) ? FluidTypes.WATER.a(false) : super.d(iblockdata);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
