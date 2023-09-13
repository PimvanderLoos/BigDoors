package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class BlockRedstoneWire extends Block {

    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> NORTH = BlockStateEnum.of("north", BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> EAST = BlockStateEnum.of("east", BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> SOUTH = BlockStateEnum.of("south", BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> WEST = BlockStateEnum.of("west", BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
    protected static final AxisAlignedBB[] f = new AxisAlignedBB[] { new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)};
    private boolean g = true;
    private final Set<BlockPosition> B = Sets.newHashSet();

    public BlockRedstoneWire() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockRedstoneWire.NORTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE).set(BlockRedstoneWire.EAST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE).set(BlockRedstoneWire.SOUTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE).set(BlockRedstoneWire.WEST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE).set(BlockRedstoneWire.POWER, Integer.valueOf(0)));
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockRedstoneWire.f[y(iblockdata.c(iblockaccess, blockposition))];
    }

    private static int y(IBlockData iblockdata) {
        int i = 0;
        boolean flag = iblockdata.get(BlockRedstoneWire.NORTH) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
        boolean flag1 = iblockdata.get(BlockRedstoneWire.EAST) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
        boolean flag2 = iblockdata.get(BlockRedstoneWire.SOUTH) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
        boolean flag3 = iblockdata.get(BlockRedstoneWire.WEST) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;

        if (flag || flag2 && !flag && !flag1 && !flag3) {
            i |= 1 << EnumDirection.NORTH.get2DRotationValue();
        }

        if (flag1 || flag3 && !flag && !flag1 && !flag2) {
            i |= 1 << EnumDirection.EAST.get2DRotationValue();
        }

        if (flag2 || flag && !flag1 && !flag2 && !flag3) {
            i |= 1 << EnumDirection.SOUTH.get2DRotationValue();
        }

        if (flag3 || flag1 && !flag && !flag2 && !flag3) {
            i |= 1 << EnumDirection.WEST.get2DRotationValue();
        }

        return i;
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = iblockdata.set(BlockRedstoneWire.WEST, this.a(iblockaccess, blockposition, EnumDirection.WEST));
        iblockdata = iblockdata.set(BlockRedstoneWire.EAST, this.a(iblockaccess, blockposition, EnumDirection.EAST));
        iblockdata = iblockdata.set(BlockRedstoneWire.NORTH, this.a(iblockaccess, blockposition, EnumDirection.NORTH));
        iblockdata = iblockdata.set(BlockRedstoneWire.SOUTH, this.a(iblockaccess, blockposition, EnumDirection.SOUTH));
        return iblockdata;
    }

    private BlockRedstoneWire.EnumRedstoneWireConnection a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition.shift(enumdirection));

        if (!a(iblockaccess.getType(blockposition1), enumdirection) && (iblockdata.l() || !x(iblockaccess.getType(blockposition1.down())))) {
            IBlockData iblockdata1 = iblockaccess.getType(blockposition.up());

            if (!iblockdata1.l()) {
                boolean flag = iblockaccess.getType(blockposition1).q() || iblockaccess.getType(blockposition1).getBlock() == Blocks.GLOWSTONE;

                if (flag && x(iblockaccess.getType(blockposition1.up()))) {
                    if (iblockdata.k()) {
                        return BlockRedstoneWire.EnumRedstoneWireConnection.UP;
                    }

                    return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
                }
            }

            return BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
        } else {
            return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
        }
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockRedstoneWire.k;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).q() || world.getType(blockposition.down()).getBlock() == Blocks.GLOWSTONE;
    }

    private IBlockData e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata = this.a(world, blockposition, blockposition, iblockdata);
        ArrayList arraylist = Lists.newArrayList(this.B);

        this.B.clear();
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            world.applyPhysics(blockposition1, this, false);
        }

        return iblockdata;
    }

    private IBlockData a(World world, BlockPosition blockposition, BlockPosition blockposition1, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        int i = ((Integer) iblockdata.get(BlockRedstoneWire.POWER)).intValue();
        byte b0 = 0;
        int j = this.getPower(world, blockposition1, b0);

        this.g = false;
        int k = world.z(blockposition);

        this.g = true;
        if (k > 0 && k > j - 1) {
            j = k;
        }

        int l = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition2 = blockposition.shift(enumdirection);
            boolean flag = blockposition2.getX() != blockposition1.getX() || blockposition2.getZ() != blockposition1.getZ();

            if (flag) {
                l = this.getPower(world, blockposition2, l);
            }

            if (world.getType(blockposition2).l() && !world.getType(blockposition.up()).l()) {
                if (flag && blockposition.getY() >= blockposition1.getY()) {
                    l = this.getPower(world, blockposition2.up(), l);
                }
            } else if (!world.getType(blockposition2).l() && flag && blockposition.getY() <= blockposition1.getY()) {
                l = this.getPower(world, blockposition2.down(), l);
            }
        }

        if (l > j) {
            j = l - 1;
        } else if (j > 0) {
            --j;
        } else {
            j = 0;
        }

        if (k > j - 1) {
            j = k;
        }

        if (i != j) {
            iblockdata = iblockdata.set(BlockRedstoneWire.POWER, Integer.valueOf(j));
            if (world.getType(blockposition) == iblockdata1) {
                world.setTypeAndData(blockposition, iblockdata, 2);
            }

            this.B.add(blockposition);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i1 = aenumdirection.length;

            for (int j1 = 0; j1 < i1; ++j1) {
                EnumDirection enumdirection1 = aenumdirection[j1];

                this.B.add(blockposition.shift(enumdirection1));
            }
        }

        return iblockdata;
    }

    private void b(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() == this) {
            world.applyPhysics(blockposition, this, false);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }

        }
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            this.e(world, blockposition, iblockdata);
            Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

            EnumDirection enumdirection;

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                this.b(world, blockposition.shift(enumdirection));
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (world.getType(blockposition1).l()) {
                    this.b(world, blockposition1.up());
                } else {
                    this.b(world, blockposition1.down());
                }
            }

        }
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        if (!world.isClientSide) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }

            this.e(world, blockposition, iblockdata);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection1;

            while (iterator.hasNext()) {
                enumdirection1 = (EnumDirection) iterator.next();
                this.b(world, blockposition.shift(enumdirection1));
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection1 = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection1);

                if (world.getType(blockposition1).l()) {
                    this.b(world, blockposition1.up());
                } else {
                    this.b(world, blockposition1.down());
                }
            }

        }
    }

    public int getPower(World world, BlockPosition blockposition, int i) {
        if (world.getType(blockposition).getBlock() != this) {
            return i;
        } else {
            int j = ((Integer) world.getType(blockposition).get(BlockRedstoneWire.POWER)).intValue();

            return j > i ? j : i;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            if (this.canPlace(world, blockposition)) {
                this.e(world, blockposition, iblockdata);
            } else {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
            }

        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REDSTONE;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !this.g ? 0 : iblockdata.a(iblockaccess, blockposition, enumdirection);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.g) {
            return 0;
        } else {
            int i = ((Integer) iblockdata.get(BlockRedstoneWire.POWER)).intValue();

            if (i == 0) {
                return 0;
            } else if (enumdirection == EnumDirection.UP) {
                return i;
            } else {
                EnumSet enumset = EnumSet.noneOf(EnumDirection.class);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection1 = (EnumDirection) iterator.next();

                    if (this.b(iblockaccess, blockposition, enumdirection1)) {
                        enumset.add(enumdirection1);
                    }
                }

                if (enumdirection.k().c() && enumset.isEmpty()) {
                    return i;
                } else if (enumset.contains(enumdirection) && !enumset.contains(enumdirection.f()) && !enumset.contains(enumdirection.e())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition1);
        boolean flag = iblockdata.l();
        boolean flag1 = iblockaccess.getType(blockposition.up()).l();

        return !flag1 && flag && c(iblockaccess, blockposition1.up()) ? true : (a(iblockdata, enumdirection) ? true : (iblockdata.getBlock() == Blocks.POWERED_REPEATER && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection ? true : !flag && c(iblockaccess, blockposition1.down())));
    }

    protected static boolean c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return x(iblockaccess.getType(blockposition));
    }

    protected static boolean x(IBlockData iblockdata) {
        return a(iblockdata, (EnumDirection) null);
    }

    protected static boolean a(IBlockData iblockdata, @Nullable EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        } else if (Blocks.UNPOWERED_REPEATER.D(iblockdata)) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockRepeater.FACING);

            return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
        } else {
            return Blocks.dk == iblockdata.getBlock() ? enumdirection == iblockdata.get(BlockObserver.FACING) : iblockdata.m() && enumdirection != null;
        }
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return this.g;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.REDSTONE);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockRedstoneWire.POWER, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockRedstoneWire.POWER)).intValue();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH)).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST)).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH)).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));

        case COUNTERCLOCKWISE_90:
            return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.EAST)).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.SOUTH)).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.WEST)).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.NORTH));

        case CLOCKWISE_90:
            return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.WEST)).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.NORTH)).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.EAST)).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.SOUTH));

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH)).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH));

        case FRONT_BACK:
            return iblockdata.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST)).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));

        default:
            return super.a(iblockdata, enumblockmirror);
        }
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST, BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    static enum EnumRedstoneWireConnection implements INamable {

        UP("up"), SIDE("side"), NONE("none");

        private final String d;

        private EnumRedstoneWireConnection(String s) {
            this.d = s;
        }

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this.d;
        }
    }
}
