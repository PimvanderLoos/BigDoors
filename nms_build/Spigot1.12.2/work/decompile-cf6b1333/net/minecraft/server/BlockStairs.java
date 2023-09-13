package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockStairs extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockStairs.EnumHalf> HALF = BlockStateEnum.of("half", BlockStairs.EnumHalf.class);
    public static final BlockStateEnum<BlockStairs.EnumStairShape> SHAPE = BlockStateEnum.of("shape", BlockStairs.EnumStairShape.class);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 1.0D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB C = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D);
    protected static final AxisAlignedBB D = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    protected static final AxisAlignedBB E = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D);
    protected static final AxisAlignedBB F = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB G = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB H = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 1.0D);
    protected static final AxisAlignedBB I = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB J = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D);
    protected static final AxisAlignedBB K = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB L = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 0.5D);
    protected static final AxisAlignedBB M = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D);
    protected static final AxisAlignedBB N = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 0.5D, 0.5D, 1.0D);
    protected static final AxisAlignedBB O = new AxisAlignedBB(0.5D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
    private final Block P;
    private final IBlockData Q;

    protected BlockStairs(IBlockData iblockdata) {
        super(iblockdata.getBlock().material);
        this.w(this.blockStateList.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH).set(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.STRAIGHT));
        this.P = iblockdata.getBlock();
        this.Q = iblockdata;
        this.c(this.P.strength);
        this.b(this.P.durability / 3.0F);
        this.a(this.P.stepSound);
        this.e(255);
        this.a(CreativeModeTab.b);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        if (!flag) {
            iblockdata = this.updateState(iblockdata, world, blockposition);
        }

        Iterator iterator = y(iblockdata).iterator();

        while (iterator.hasNext()) {
            AxisAlignedBB axisalignedbb1 = (AxisAlignedBB) iterator.next();

            a(blockposition, axisalignedbb, list, axisalignedbb1);
        }

    }

    private static List<AxisAlignedBB> y(IBlockData iblockdata) {
        ArrayList arraylist = Lists.newArrayList();
        boolean flag = iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;

        arraylist.add(flag ? BlockStairs.d : BlockStairs.G);
        BlockStairs.EnumStairShape blockstairs_enumstairshape = (BlockStairs.EnumStairShape) iblockdata.get(BlockStairs.SHAPE);

        if (blockstairs_enumstairshape == BlockStairs.EnumStairShape.STRAIGHT || blockstairs_enumstairshape == BlockStairs.EnumStairShape.INNER_LEFT || blockstairs_enumstairshape == BlockStairs.EnumStairShape.INNER_RIGHT) {
            arraylist.add(z(iblockdata));
        }

        if (blockstairs_enumstairshape != BlockStairs.EnumStairShape.STRAIGHT) {
            arraylist.add(A(iblockdata));
        }

        return arraylist;
    }

    private static AxisAlignedBB z(IBlockData iblockdata) {
        boolean flag = iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;

        switch ((EnumDirection) iblockdata.get(BlockStairs.FACING)) {
        case NORTH:
        default:
            return flag ? BlockStairs.J : BlockStairs.g;

        case SOUTH:
            return flag ? BlockStairs.K : BlockStairs.B;

        case WEST:
            return flag ? BlockStairs.H : BlockStairs.e;

        case EAST:
            return flag ? BlockStairs.I : BlockStairs.f;
        }
    }

    private static AxisAlignedBB A(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        EnumDirection enumdirection1;

        switch ((BlockStairs.EnumStairShape) iblockdata.get(BlockStairs.SHAPE)) {
        case OUTER_LEFT:
        default:
            enumdirection1 = enumdirection;
            break;

        case OUTER_RIGHT:
            enumdirection1 = enumdirection.e();
            break;

        case INNER_RIGHT:
            enumdirection1 = enumdirection.opposite();
            break;

        case INNER_LEFT:
            enumdirection1 = enumdirection.f();
        }

        boolean flag = iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;

        switch (enumdirection1) {
        case NORTH:
        default:
            return flag ? BlockStairs.L : BlockStairs.C;

        case SOUTH:
            return flag ? BlockStairs.O : BlockStairs.F;

        case WEST:
            return flag ? BlockStairs.N : BlockStairs.E;

        case EAST:
            return flag ? BlockStairs.M : BlockStairs.D;
        }
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            return enumdirection == EnumDirection.UP == (iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
        } else {
            BlockStairs.EnumStairShape blockstairs_enumstairshape = (BlockStairs.EnumStairShape) iblockdata.get(BlockStairs.SHAPE);

            if (blockstairs_enumstairshape != BlockStairs.EnumStairShape.OUTER_LEFT && blockstairs_enumstairshape != BlockStairs.EnumStairShape.OUTER_RIGHT) {
                EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockStairs.FACING);

                switch (blockstairs_enumstairshape) {
                case INNER_RIGHT:
                    return enumdirection1 != enumdirection && enumdirection1 != enumdirection.f() ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;

                case INNER_LEFT:
                    return enumdirection1 != enumdirection && enumdirection1 != enumdirection.e() ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;

                case STRAIGHT:
                    return enumdirection1 == enumdirection ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;

                default:
                    return EnumBlockFaceShape.UNDEFINED;
                }
            } else {
                return EnumBlockFaceShape.UNDEFINED;
            }
        }
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.P.attack(world, blockposition, entityhuman);
    }

    public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.P.postBreak(world, blockposition, iblockdata);
    }

    public float a(Entity entity) {
        return this.P.a(entity);
    }

    public int a(World world) {
        return this.P.a(world);
    }

    public Vec3D a(World world, BlockPosition blockposition, Entity entity, Vec3D vec3d) {
        return this.P.a(world, blockposition, entity, vec3d);
    }

    public boolean m() {
        return this.P.m();
    }

    public boolean a(IBlockData iblockdata, boolean flag) {
        return this.P.a(iblockdata, flag);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return this.P.canPlace(world, blockposition);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.Q.doPhysics(world, blockposition, Blocks.AIR, blockposition);
        this.P.onPlace(world, blockposition, this.Q);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.P.remove(world, blockposition, this.Q);
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        this.P.stepOn(world, blockposition, entity);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.P.b(world, blockposition, iblockdata, random);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        return this.P.interact(world, blockposition, this.Q, entityhuman, enumhand, EnumDirection.DOWN, 0.0F, 0.0F, 0.0F);
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        this.P.wasExploded(world, blockposition, explosion);
    }

    public boolean k(IBlockData iblockdata) {
        return iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.P.c(this.Q, iblockaccess, blockposition);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityliving);

        iblockdata = iblockdata.set(BlockStairs.FACING, entityliving.getDirection()).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.STRAIGHT);
        return enumdirection != EnumDirection.DOWN && (enumdirection == EnumDirection.UP || (double) f1 <= 0.5D) ? iblockdata.set(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM) : iblockdata.set(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
    }

    @Nullable
    public MovingObjectPosition a(IBlockData iblockdata, World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = y(this.updateState(iblockdata, world, blockposition)).iterator();

        while (iterator.hasNext()) {
            AxisAlignedBB axisalignedbb = (AxisAlignedBB) iterator.next();

            arraylist.add(this.a(blockposition, vec3d, vec3d1, axisalignedbb));
        }

        MovingObjectPosition movingobjectposition = null;
        double d0 = 0.0D;
        Iterator iterator1 = arraylist.iterator();

        while (iterator1.hasNext()) {
            MovingObjectPosition movingobjectposition1 = (MovingObjectPosition) iterator1.next();

            if (movingobjectposition1 != null) {
                double d1 = movingobjectposition1.pos.distanceSquared(vec3d1);

                if (d1 > d0) {
                    movingobjectposition = movingobjectposition1;
                    d0 = d1;
                }
            }
        }

        return movingobjectposition;
    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData().set(BlockStairs.HALF, (i & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);

        iblockdata = iblockdata.set(BlockStairs.FACING, EnumDirection.fromType1(5 - (i & 3)));
        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        if (iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) {
            i |= 4;
        }

        i |= 5 - ((EnumDirection) iblockdata.get(BlockStairs.FACING)).a();
        return i;
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockStairs.SHAPE, g(iblockdata, iblockaccess, blockposition));
    }

    private static BlockStairs.EnumStairShape g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift(enumdirection));

        if (x(iblockdata1) && iblockdata.get(BlockStairs.HALF) == iblockdata1.get(BlockStairs.HALF)) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata1.get(BlockStairs.FACING);

            if (enumdirection1.k() != ((EnumDirection) iblockdata.get(BlockStairs.FACING)).k() && d(iblockdata, iblockaccess, blockposition, enumdirection1.opposite())) {
                if (enumdirection1 == enumdirection.f()) {
                    return BlockStairs.EnumStairShape.OUTER_LEFT;
                }

                return BlockStairs.EnumStairShape.OUTER_RIGHT;
            }
        }

        IBlockData iblockdata2 = iblockaccess.getType(blockposition.shift(enumdirection.opposite()));

        if (x(iblockdata2) && iblockdata.get(BlockStairs.HALF) == iblockdata2.get(BlockStairs.HALF)) {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata2.get(BlockStairs.FACING);

            if (enumdirection2.k() != ((EnumDirection) iblockdata.get(BlockStairs.FACING)).k() && d(iblockdata, iblockaccess, blockposition, enumdirection2)) {
                if (enumdirection2 == enumdirection.f()) {
                    return BlockStairs.EnumStairShape.INNER_LEFT;
                }

                return BlockStairs.EnumStairShape.INNER_RIGHT;
            }
        }

        return BlockStairs.EnumStairShape.STRAIGHT;
    }

    private static boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift(enumdirection));

        return !x(iblockdata1) || iblockdata1.get(BlockStairs.FACING) != iblockdata.get(BlockStairs.FACING) || iblockdata1.get(BlockStairs.HALF) != iblockdata.get(BlockStairs.HALF);
    }

    public static boolean x(IBlockData iblockdata) {
        return iblockdata.getBlock() instanceof BlockStairs;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockStairs.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockStairs.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        BlockStairs.EnumStairShape blockstairs_enumstairshape = (BlockStairs.EnumStairShape) iblockdata.get(BlockStairs.SHAPE);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            if (enumdirection.k() == EnumDirection.EnumAxis.Z) {
                switch (blockstairs_enumstairshape) {
                case OUTER_LEFT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.OUTER_RIGHT);

                case OUTER_RIGHT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.OUTER_LEFT);

                case INNER_RIGHT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.INNER_LEFT);

                case INNER_LEFT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.INNER_RIGHT);

                default:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180);
                }
            }
            break;

        case FRONT_BACK:
            if (enumdirection.k() == EnumDirection.EnumAxis.X) {
                switch (blockstairs_enumstairshape) {
                case OUTER_LEFT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.OUTER_RIGHT);

                case OUTER_RIGHT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.OUTER_LEFT);

                case INNER_RIGHT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.INNER_RIGHT);

                case INNER_LEFT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180).set(BlockStairs.SHAPE, BlockStairs.EnumStairShape.INNER_LEFT);

                case STRAIGHT:
                    return iblockdata.a(EnumBlockRotation.CLOCKWISE_180);
                }
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockStairs.FACING, BlockStairs.HALF, BlockStairs.SHAPE});
    }

    public static enum EnumStairShape implements INamable {

        STRAIGHT("straight"), INNER_LEFT("inner_left"), INNER_RIGHT("inner_right"), OUTER_LEFT("outer_left"), OUTER_RIGHT("outer_right");

        private final String f;

        private EnumStairShape(String s) {
            this.f = s;
        }

        public String toString() {
            return this.f;
        }

        public String getName() {
            return this.f;
        }
    }

    public static enum EnumHalf implements INamable {

        TOP("top"), BOTTOM("bottom");

        private final String c;

        private EnumHalf(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
