package net.minecraft.server;

public class BlockCloth extends Block {

    public static final BlockStateEnum<EnumColor> COLOR = BlockStateEnum.of("color", EnumColor.class);

    public BlockCloth(Material material) {
        super(material);
        this.w(this.blockStateList.getBlockData().set(BlockCloth.COLOR, EnumColor.WHITE));
        this.a(CreativeModeTab.b);
    }

    public int getDropData(IBlockData iblockdata) {
        return ((EnumColor) iblockdata.get(BlockCloth.COLOR)).getColorIndex();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            nonnulllist.add(new ItemStack(this, 1, enumcolor.getColorIndex()));
        }

    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return MaterialMapColor.a((EnumColor) iblockdata.get(BlockCloth.COLOR));
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCloth.COLOR, EnumColor.fromColorIndex(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumColor) iblockdata.get(BlockCloth.COLOR)).getColorIndex();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockCloth.COLOR});
    }
}
