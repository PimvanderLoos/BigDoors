package net.minecraft.server;

import java.util.Random;

public abstract class BlockWoodenStep extends BlockStepAbstract {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.of("variant", BlockWood.EnumLogVariant.class);

    public BlockWoodenStep() {
        super(Material.WOOD);
        IBlockData iblockdata = this.blockStateList.getBlockData();

        if (!this.e()) {
            iblockdata = iblockdata.set(BlockWoodenStep.HALF, BlockStepAbstract.EnumSlabHalf.BOTTOM);
        }

        this.w(iblockdata.set(BlockWoodenStep.VARIANT, BlockWood.EnumLogVariant.OAK));
        this.a(CreativeModeTab.b);
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockWoodenStep.VARIANT)).c();
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.WOODEN_SLAB);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.WOODEN_SLAB, 1, ((BlockWood.EnumLogVariant) iblockdata.get(BlockWoodenStep.VARIANT)).a());
    }

    public String b(int i) {
        return super.a() + "." + BlockWood.EnumLogVariant.a(i).d();
    }

    public IBlockState<?> g() {
        return BlockWoodenStep.VARIANT;
    }

    public Comparable<?> a(ItemStack itemstack) {
        return BlockWood.EnumLogVariant.a(itemstack.getData() & 7);
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        BlockWood.EnumLogVariant[] ablockwood_enumlogvariant = BlockWood.EnumLogVariant.values();
        int i = ablockwood_enumlogvariant.length;

        for (int j = 0; j < i; ++j) {
            BlockWood.EnumLogVariant blockwood_enumlogvariant = ablockwood_enumlogvariant[j];

            nonnulllist.add(new ItemStack(this, 1, blockwood_enumlogvariant.a()));
        }

    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData().set(BlockWoodenStep.VARIANT, BlockWood.EnumLogVariant.a(i & 7));

        if (!this.e()) {
            iblockdata = iblockdata.set(BlockWoodenStep.HALF, (i & 8) == 0 ? BlockStepAbstract.EnumSlabHalf.BOTTOM : BlockStepAbstract.EnumSlabHalf.TOP);
        }

        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockWood.EnumLogVariant) iblockdata.get(BlockWoodenStep.VARIANT)).a();

        if (!this.e() && iblockdata.get(BlockWoodenStep.HALF) == BlockStepAbstract.EnumSlabHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return this.e() ? new BlockStateList(this, new IBlockState[] { BlockWoodenStep.VARIANT}) : new BlockStateList(this, new IBlockState[] { BlockWoodenStep.HALF, BlockWoodenStep.VARIANT});
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockWoodenStep.VARIANT)).a();
    }
}
