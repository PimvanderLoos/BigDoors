package net.minecraft.server;

import java.util.Random;
import java.util.stream.IntStream;

public class BlockStairs extends Block implements IFluidSource, IFluidContainer {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockPropertyHalf> HALF = BlockProperties.Q;
    public static final BlockStateEnum<BlockPropertyStairsShape> SHAPE = BlockProperties.av;
    public static final BlockStateBoolean o = BlockProperties.y;
    protected static final VoxelShape p = BlockStepAbstract.o;
    protected static final VoxelShape q = BlockStepAbstract.c;
    protected static final VoxelShape r = Block.a(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
    protected static final VoxelShape s = Block.a(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape t = Block.a(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape u = Block.a(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape v = Block.a(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape w = Block.a(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape x = Block.a(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape y = Block.a(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape[] z = a(BlockStairs.p, BlockStairs.r, BlockStairs.v, BlockStairs.s, BlockStairs.w);
    protected static final VoxelShape[] A = a(BlockStairs.q, BlockStairs.t, BlockStairs.x, BlockStairs.u, BlockStairs.y);
    private static final int[] B = new int[] { 12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
    private final Block C;
    private final IBlockData D;

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

    protected BlockStairs(IBlockData iblockdata, Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockStairs.FACING, EnumDirection.NORTH)).set(BlockStairs.HALF, BlockPropertyHalf.BOTTOM)).set(BlockStairs.SHAPE, BlockPropertyStairsShape.STRAIGHT)).set(BlockStairs.o, false));
        this.C = iblockdata.getBlock();
        this.D = iblockdata;
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.K();
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return (iblockdata.get(BlockStairs.HALF) == BlockPropertyHalf.TOP ? BlockStairs.z : BlockStairs.A)[BlockStairs.B[this.w(iblockdata)]];
    }

    private int w(IBlockData iblockdata) {
        return ((BlockPropertyStairsShape) iblockdata.get(BlockStairs.SHAPE)).ordinal() * 4 + ((EnumDirection) iblockdata.get(BlockStairs.FACING)).get2DRotationValue();
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            return enumdirection == EnumDirection.UP == (iblockdata.get(BlockStairs.HALF) == BlockPropertyHalf.TOP) ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
        } else {
            BlockPropertyStairsShape blockpropertystairsshape = (BlockPropertyStairsShape) iblockdata.get(BlockStairs.SHAPE);

            if (blockpropertystairsshape != BlockPropertyStairsShape.OUTER_LEFT && blockpropertystairsshape != BlockPropertyStairsShape.OUTER_RIGHT) {
                EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockStairs.FACING);

                switch (blockpropertystairsshape) {
                case STRAIGHT:
                    return enumdirection1 == enumdirection ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
                case INNER_LEFT:
                    return enumdirection1 != enumdirection && enumdirection1 != enumdirection.e() ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;
                case INNER_RIGHT:
                    return enumdirection1 != enumdirection && enumdirection1 != enumdirection.f() ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;
                default:
                    return EnumBlockFaceShape.UNDEFINED;
                }
            } else {
                return EnumBlockFaceShape.UNDEFINED;
            }
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.D.attack(world, blockposition, entityhuman);
    }

    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        this.C.postBreak(generatoraccess, blockposition, iblockdata);
    }

    public float getDurability() {
        return this.C.getDurability();
    }

    public TextureType c() {
        return this.C.c();
    }

    public int a(IWorldReader iworldreader) {
        return this.C.a(iworldreader);
    }

    public boolean j() {
        return this.C.j();
    }

    public boolean isCollidable(IBlockData iblockdata) {
        return this.C.isCollidable(iblockdata);
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata.getBlock() != iblockdata.getBlock()) {
            this.D.doPhysics(world, blockposition, Blocks.AIR, blockposition);
            this.C.onPlace(this.D, world, blockposition, iblockdata1);
        }
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            this.D.remove(world, blockposition, iblockdata1, flag);
        }
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        this.C.stepOn(world, blockposition, entity);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        this.C.a(iblockdata, world, blockposition, random);
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        return this.D.interact(world, blockposition, entityhuman, enumhand, EnumDirection.DOWN, 0.0F, 0.0F, 0.0F);
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        this.C.wasExploded(world, blockposition, explosion);
    }

    public boolean r(IBlockData iblockdata) {
        return iblockdata.get(BlockStairs.HALF) == BlockPropertyHalf.TOP;
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockStairs.FACING, blockactioncontext.f())).set(BlockStairs.HALF, enumdirection != EnumDirection.DOWN && (enumdirection == EnumDirection.UP || (double) blockactioncontext.n() <= 0.5D) ? BlockPropertyHalf.BOTTOM : BlockPropertyHalf.TOP)).set(BlockStairs.o, fluid.c() == FluidTypes.WATER);

        return (IBlockData) iblockdata.set(BlockStairs.SHAPE, m(iblockdata, blockactioncontext.getWorld(), blockactioncontext.getClickPosition()));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockStairs.o)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection.k().c() ? (IBlockData) iblockdata.set(BlockStairs.SHAPE, m(iblockdata, generatoraccess, blockposition)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private static BlockPropertyStairsShape m(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift(enumdirection));

        if (k(iblockdata1) && iblockdata.get(BlockStairs.HALF) == iblockdata1.get(BlockStairs.HALF)) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata1.get(BlockStairs.FACING);

            if (enumdirection1.k() != ((EnumDirection) iblockdata.get(BlockStairs.FACING)).k() && d(iblockdata, iblockaccess, blockposition, enumdirection1.opposite())) {
                if (enumdirection1 == enumdirection.f()) {
                    return BlockPropertyStairsShape.OUTER_LEFT;
                }

                return BlockPropertyStairsShape.OUTER_RIGHT;
            }
        }

        IBlockData iblockdata2 = iblockaccess.getType(blockposition.shift(enumdirection.opposite()));

        if (k(iblockdata2) && iblockdata.get(BlockStairs.HALF) == iblockdata2.get(BlockStairs.HALF)) {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata2.get(BlockStairs.FACING);

            if (enumdirection2.k() != ((EnumDirection) iblockdata.get(BlockStairs.FACING)).k() && d(iblockdata, iblockaccess, blockposition, enumdirection2)) {
                if (enumdirection2 == enumdirection.f()) {
                    return BlockPropertyStairsShape.INNER_LEFT;
                }

                return BlockPropertyStairsShape.INNER_RIGHT;
            }
        }

        return BlockPropertyStairsShape.STRAIGHT;
    }

    private static boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift(enumdirection));

        return !k(iblockdata1) || iblockdata1.get(BlockStairs.FACING) != iblockdata.get(BlockStairs.FACING) || iblockdata1.get(BlockStairs.HALF) != iblockdata.get(BlockStairs.HALF);
    }

    public static boolean k(IBlockData iblockdata) {
        return iblockdata.getBlock() instanceof BlockStairs;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockStairs.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockStairs.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        BlockPropertyStairsShape blockpropertystairsshape = (BlockPropertyStairsShape) iblockdata.get(BlockStairs.SHAPE);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            if (enumdirection.k() == EnumDirection.EnumAxis.Z) {
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
            if (enumdirection.k() == EnumDirection.EnumAxis.X) {
                switch (blockpropertystairsshape) {
                case STRAIGHT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180);
                case INNER_LEFT:
                    return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.INNER_LEFT);
                case INNER_RIGHT:
                    return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.INNER_RIGHT);
                case OUTER_LEFT:
                    return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT);
                case OUTER_RIGHT:
                    return (IBlockData) iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT);
                }
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStairs.FACING, BlockStairs.HALF, BlockStairs.SHAPE, BlockStairs.o);
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockStairs.o)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockStairs.o, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockStairs.o) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockStairs.o) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockStairs.o) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockStairs.o, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
