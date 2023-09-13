package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class BlockCobbleWall extends Block {

    public static final BlockStateBoolean UP = BlockStateBoolean.of("up");
    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
    public static final BlockStateEnum<BlockCobbleWall.EnumCobbleVariant> VARIANT = BlockStateEnum.of("variant", BlockCobbleWall.EnumCobbleVariant.class);
    protected static final AxisAlignedBB[] g = new AxisAlignedBB[] { new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
    protected static final AxisAlignedBB[] B = new AxisAlignedBB[] { BlockCobbleWall.g[0].e(1.5D), BlockCobbleWall.g[1].e(1.5D), BlockCobbleWall.g[2].e(1.5D), BlockCobbleWall.g[3].e(1.5D), BlockCobbleWall.g[4].e(1.5D), BlockCobbleWall.g[5].e(1.5D), BlockCobbleWall.g[6].e(1.5D), BlockCobbleWall.g[7].e(1.5D), BlockCobbleWall.g[8].e(1.5D), BlockCobbleWall.g[9].e(1.5D), BlockCobbleWall.g[10].e(1.5D), BlockCobbleWall.g[11].e(1.5D), BlockCobbleWall.g[12].e(1.5D), BlockCobbleWall.g[13].e(1.5D), BlockCobbleWall.g[14].e(1.5D), BlockCobbleWall.g[15].e(1.5D)};

    public BlockCobbleWall(Block block) {
        super(block.material);
        this.w(this.blockStateList.getBlockData().set(BlockCobbleWall.UP, Boolean.valueOf(false)).set(BlockCobbleWall.NORTH, Boolean.valueOf(false)).set(BlockCobbleWall.EAST, Boolean.valueOf(false)).set(BlockCobbleWall.SOUTH, Boolean.valueOf(false)).set(BlockCobbleWall.WEST, Boolean.valueOf(false)).set(BlockCobbleWall.VARIANT, BlockCobbleWall.EnumCobbleVariant.NORMAL));
        this.c(block.strength);
        this.b(block.durability / 3.0F);
        this.a(block.stepSound);
        this.a(CreativeModeTab.c);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        return BlockCobbleWall.g[x(iblockdata)];
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        if (!flag) {
            iblockdata = this.updateState(iblockdata, world, blockposition);
        }

        a(blockposition, axisalignedbb, list, BlockCobbleWall.B[x(iblockdata)]);
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        return BlockCobbleWall.B[x(iblockdata)];
    }

    private static int x(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockCobbleWall.NORTH)).booleanValue()) {
            i |= 1 << EnumDirection.NORTH.get2DRotationValue();
        }

        if (((Boolean) iblockdata.get(BlockCobbleWall.EAST)).booleanValue()) {
            i |= 1 << EnumDirection.EAST.get2DRotationValue();
        }

        if (((Boolean) iblockdata.get(BlockCobbleWall.SOUTH)).booleanValue()) {
            i |= 1 << EnumDirection.SOUTH.get2DRotationValue();
        }

        if (((Boolean) iblockdata.get(BlockCobbleWall.WEST)).booleanValue()) {
            i |= 1 << EnumDirection.WEST.get2DRotationValue();
        }

        return i;
    }

    public String getName() {
        return LocaleI18n.get(this.a() + "." + BlockCobbleWall.EnumCobbleVariant.NORMAL.c() + ".name");
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        Block block = iblockdata.getBlock();
        EnumBlockFaceShape enumblockfaceshape = iblockdata.d(iblockaccess, blockposition, enumdirection);
        boolean flag = enumblockfaceshape == EnumBlockFaceShape.MIDDLE_POLE_THICK || enumblockfaceshape == EnumBlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate;

        return !e(block) && enumblockfaceshape == EnumBlockFaceShape.SOLID || flag;
    }

    protected static boolean e(Block block) {
        return Block.c(block) || block == Blocks.BARRIER || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN;
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        BlockCobbleWall.EnumCobbleVariant[] ablockcobblewall_enumcobblevariant = BlockCobbleWall.EnumCobbleVariant.values();
        int i = ablockcobblewall_enumcobblevariant.length;

        for (int j = 0; j < i; ++j) {
            BlockCobbleWall.EnumCobbleVariant blockcobblewall_enumcobblevariant = ablockcobblewall_enumcobblevariant[j];

            nonnulllist.add(new ItemStack(this, 1, blockcobblewall_enumcobblevariant.a()));
        }

    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockCobbleWall.EnumCobbleVariant) iblockdata.get(BlockCobbleWall.VARIANT)).a();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCobbleWall.VARIANT, BlockCobbleWall.EnumCobbleVariant.a(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((BlockCobbleWall.EnumCobbleVariant) iblockdata.get(BlockCobbleWall.VARIANT)).a();
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = this.a(iblockaccess, blockposition.north(), EnumDirection.SOUTH);
        boolean flag1 = this.a(iblockaccess, blockposition.east(), EnumDirection.WEST);
        boolean flag2 = this.a(iblockaccess, blockposition.south(), EnumDirection.NORTH);
        boolean flag3 = this.a(iblockaccess, blockposition.west(), EnumDirection.EAST);
        boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;

        return iblockdata.set(BlockCobbleWall.UP, Boolean.valueOf(!flag4 || !iblockaccess.isEmpty(blockposition.up()))).set(BlockCobbleWall.NORTH, Boolean.valueOf(flag)).set(BlockCobbleWall.EAST, Boolean.valueOf(flag1)).set(BlockCobbleWall.SOUTH, Boolean.valueOf(flag2)).set(BlockCobbleWall.WEST, Boolean.valueOf(flag3));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockCobbleWall.UP, BlockCobbleWall.NORTH, BlockCobbleWall.EAST, BlockCobbleWall.WEST, BlockCobbleWall.SOUTH, BlockCobbleWall.VARIANT});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? EnumBlockFaceShape.MIDDLE_POLE_THICK : EnumBlockFaceShape.CENTER_BIG;
    }

    public static enum EnumCobbleVariant implements INamable {

        NORMAL(0, "cobblestone", "normal"), MOSSY(1, "mossy_cobblestone", "mossy");

        private static final BlockCobbleWall.EnumCobbleVariant[] c = new BlockCobbleWall.EnumCobbleVariant[values().length];
        private final int d;
        private final String e;
        private final String f;

        private EnumCobbleVariant(int i, String s, String s1) {
            this.d = i;
            this.e = s;
            this.f = s1;
        }

        public int a() {
            return this.d;
        }

        public String toString() {
            return this.e;
        }

        public static BlockCobbleWall.EnumCobbleVariant a(int i) {
            if (i < 0 || i >= BlockCobbleWall.EnumCobbleVariant.c.length) {
                i = 0;
            }

            return BlockCobbleWall.EnumCobbleVariant.c[i];
        }

        public String getName() {
            return this.e;
        }

        public String c() {
            return this.f;
        }

        static {
            BlockCobbleWall.EnumCobbleVariant[] ablockcobblewall_enumcobblevariant = values();
            int i = ablockcobblewall_enumcobblevariant.length;

            for (int j = 0; j < i; ++j) {
                BlockCobbleWall.EnumCobbleVariant blockcobblewall_enumcobblevariant = ablockcobblewall_enumcobblevariant[j];

                BlockCobbleWall.EnumCobbleVariant.c[blockcobblewall_enumcobblevariant.a()] = blockcobblewall_enumcobblevariant;
            }

        }
    }
}
