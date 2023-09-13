package net.minecraft.server;

public class BlockSand extends BlockFalling {

    public static final BlockStateEnum<BlockSand.EnumSandVariant> VARIANT = BlockStateEnum.of("variant", BlockSand.EnumSandVariant.class);

    public BlockSand() {
        this.w(this.blockStateList.getBlockData().set(BlockSand.VARIANT, BlockSand.EnumSandVariant.SAND));
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockSand.EnumSandVariant) iblockdata.get(BlockSand.VARIANT)).b();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        BlockSand.EnumSandVariant[] ablocksand_enumsandvariant = BlockSand.EnumSandVariant.values();
        int i = ablocksand_enumsandvariant.length;

        for (int j = 0; j < i; ++j) {
            BlockSand.EnumSandVariant blocksand_enumsandvariant = ablocksand_enumsandvariant[j];

            nonnulllist.add(new ItemStack(this, 1, blocksand_enumsandvariant.b()));
        }

    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((BlockSand.EnumSandVariant) iblockdata.get(BlockSand.VARIANT)).d();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSand.VARIANT, BlockSand.EnumSandVariant.a(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((BlockSand.EnumSandVariant) iblockdata.get(BlockSand.VARIANT)).b();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockSand.VARIANT});
    }

    public static enum EnumSandVariant implements INamable {

        SAND(0, "sand", "default", MaterialMapColor.e, -2370656), RED_SAND(1, "red_sand", "red", MaterialMapColor.r, -5679071);

        private static final BlockSand.EnumSandVariant[] c = new BlockSand.EnumSandVariant[values().length];
        private final int d;
        private final String e;
        private final MaterialMapColor f;
        private final String g;
        private final int h;

        private EnumSandVariant(int i, String s, String s1, MaterialMapColor materialmapcolor, int j) {
            this.d = i;
            this.e = s;
            this.f = materialmapcolor;
            this.g = s1;
            this.h = j;
        }

        public int b() {
            return this.d;
        }

        public String toString() {
            return this.e;
        }

        public MaterialMapColor d() {
            return this.f;
        }

        public static BlockSand.EnumSandVariant a(int i) {
            if (i < 0 || i >= BlockSand.EnumSandVariant.c.length) {
                i = 0;
            }

            return BlockSand.EnumSandVariant.c[i];
        }

        public String getName() {
            return this.e;
        }

        public String e() {
            return this.g;
        }

        static {
            BlockSand.EnumSandVariant[] ablocksand_enumsandvariant = values();
            int i = ablocksand_enumsandvariant.length;

            for (int j = 0; j < i; ++j) {
                BlockSand.EnumSandVariant blocksand_enumsandvariant = ablocksand_enumsandvariant[j];

                BlockSand.EnumSandVariant.c[blocksand_enumsandvariant.b()] = blocksand_enumsandvariant;
            }

        }
    }
}
