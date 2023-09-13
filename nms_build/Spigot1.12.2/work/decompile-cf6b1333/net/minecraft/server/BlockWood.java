package net.minecraft.server;

public class BlockWood extends Block {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.of("variant", BlockWood.EnumLogVariant.class);

    public BlockWood() {
        super(Material.WOOD);
        this.w(this.blockStateList.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.OAK));
        this.a(CreativeModeTab.b);
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockWood.VARIANT)).a();
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
        return this.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.a(i));
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockWood.VARIANT)).c();
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockWood.VARIANT)).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockWood.VARIANT});
    }

    public static enum EnumLogVariant implements INamable {

        OAK(0, "oak", MaterialMapColor.p), SPRUCE(1, "spruce", MaterialMapColor.K), BIRCH(2, "birch", MaterialMapColor.e), JUNGLE(3, "jungle", MaterialMapColor.m), ACACIA(4, "acacia", MaterialMapColor.r), DARK_OAK(5, "dark_oak", "big_oak", MaterialMapColor.C);

        private static final BlockWood.EnumLogVariant[] g = new BlockWood.EnumLogVariant[values().length];
        private final int h;
        private final String i;
        private final String j;
        private final MaterialMapColor k;

        private EnumLogVariant(int i, String s, MaterialMapColor materialmapcolor) {
            this(i, s, s, materialmapcolor);
        }

        private EnumLogVariant(int i, String s, String s1, MaterialMapColor materialmapcolor) {
            this.h = i;
            this.i = s;
            this.j = s1;
            this.k = materialmapcolor;
        }

        public int a() {
            return this.h;
        }

        public MaterialMapColor c() {
            return this.k;
        }

        public String toString() {
            return this.i;
        }

        public static BlockWood.EnumLogVariant a(int i) {
            if (i < 0 || i >= BlockWood.EnumLogVariant.g.length) {
                i = 0;
            }

            return BlockWood.EnumLogVariant.g[i];
        }

        public String getName() {
            return this.i;
        }

        public String d() {
            return this.j;
        }

        static {
            BlockWood.EnumLogVariant[] ablockwood_enumlogvariant = values();
            int i = ablockwood_enumlogvariant.length;

            for (int j = 0; j < i; ++j) {
                BlockWood.EnumLogVariant blockwood_enumlogvariant = ablockwood_enumlogvariant[j];

                BlockWood.EnumLogVariant.g[blockwood_enumlogvariant.a()] = blockwood_enumlogvariant;
            }

        }
    }
}
