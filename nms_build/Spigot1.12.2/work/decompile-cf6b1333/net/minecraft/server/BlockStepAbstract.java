package net.minecraft.server;

import java.util.Random;

public abstract class BlockStepAbstract extends Block {

    public static final BlockStateEnum<BlockStepAbstract.EnumSlabHalf> HALF = BlockStateEnum.of("half", BlockStepAbstract.EnumSlabHalf.class);
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockStepAbstract(Material material) {
        this(material, material.r());
    }

    public BlockStepAbstract(Material material, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
        this.l = this.e();
        this.e(255);
    }

    protected boolean n() {
        return false;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.e() ? BlockStepAbstract.j : (iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP ? BlockStepAbstract.c : BlockStepAbstract.b);
    }

    public boolean k(IBlockData iblockdata) {
        return ((BlockStepAbstract) iblockdata.getBlock()).e() || iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((BlockStepAbstract) iblockdata.getBlock()).e() ? EnumBlockFaceShape.SOLID : (enumdirection == EnumDirection.UP && iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP ? EnumBlockFaceShape.SOLID : (enumdirection == EnumDirection.DOWN && iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.BOTTOM ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED));
    }

    public boolean b(IBlockData iblockdata) {
        return this.e();
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityliving).set(BlockStepAbstract.HALF, BlockStepAbstract.EnumSlabHalf.BOTTOM);

        return this.e() ? iblockdata : (enumdirection != EnumDirection.DOWN && (enumdirection == EnumDirection.UP || (double) f1 <= 0.5D) ? iblockdata : iblockdata.set(BlockStepAbstract.HALF, BlockStepAbstract.EnumSlabHalf.TOP));
    }

    public int a(Random random) {
        return this.e() ? 2 : 1;
    }

    public boolean c(IBlockData iblockdata) {
        return this.e();
    }

    public abstract String b(int i);

    public abstract boolean e();

    public abstract IBlockState<?> g();

    public abstract Comparable<?> a(ItemStack itemstack);

    public static enum EnumSlabHalf implements INamable {

        TOP("top"), BOTTOM("bottom");

        private final String c;

        private EnumSlabHalf(String s) {
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
