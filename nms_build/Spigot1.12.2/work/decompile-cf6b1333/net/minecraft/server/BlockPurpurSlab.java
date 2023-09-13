package net.minecraft.server;

import java.util.Random;

public abstract class BlockPurpurSlab extends BlockStepAbstract {

    public static final BlockStateEnum<BlockPurpurSlab.Type> d = BlockStateEnum.of("variant", BlockPurpurSlab.Type.class);

    public BlockPurpurSlab() {
        super(Material.STONE, MaterialMapColor.s);
        IBlockData iblockdata = this.blockStateList.getBlockData();

        if (!this.e()) {
            iblockdata = iblockdata.set(BlockPurpurSlab.HALF, BlockStepAbstract.EnumSlabHalf.BOTTOM);
        }

        this.w(iblockdata.set(BlockPurpurSlab.d, BlockPurpurSlab.Type.DEFAULT));
        this.a(CreativeModeTab.b);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.PURPUR_SLAB);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.PURPUR_SLAB);
    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData().set(BlockPurpurSlab.d, BlockPurpurSlab.Type.DEFAULT);

        if (!this.e()) {
            iblockdata = iblockdata.set(BlockPurpurSlab.HALF, (i & 8) == 0 ? BlockStepAbstract.EnumSlabHalf.BOTTOM : BlockStepAbstract.EnumSlabHalf.TOP);
        }

        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        if (!this.e() && iblockdata.get(BlockPurpurSlab.HALF) == BlockStepAbstract.EnumSlabHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return this.e() ? new BlockStateList(this, new IBlockState[] { BlockPurpurSlab.d}) : new BlockStateList(this, new IBlockState[] { BlockPurpurSlab.HALF, BlockPurpurSlab.d});
    }

    public String b(int i) {
        return super.a();
    }

    public IBlockState<?> g() {
        return BlockPurpurSlab.d;
    }

    public Comparable<?> a(ItemStack itemstack) {
        return BlockPurpurSlab.Type.DEFAULT;
    }

    public static enum Type implements INamable {

        DEFAULT;

        private Type() {}

        public String getName() {
            return "default";
        }
    }

    public static class Default extends BlockPurpurSlab {

        public Default() {}

        public boolean e() {
            return true;
        }
    }

    public static class Half extends BlockPurpurSlab {

        public Half() {}

        public boolean e() {
            return false;
        }
    }
}
