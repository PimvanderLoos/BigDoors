package net.minecraft.server;

import java.util.Random;

public class BlockStone extends Block {

    public static final BlockStateEnum<BlockStone.EnumStoneVariant> VARIANT = BlockStateEnum.of("variant", BlockStone.EnumStoneVariant.class);

    public BlockStone() {
        super(Material.STONE);
        this.w(this.blockStateList.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.STONE));
        this.a(CreativeModeTab.b);
    }

    public String getName() {
        return LocaleI18n.get(this.a() + "." + BlockStone.EnumStoneVariant.STONE.d() + ".name");
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((BlockStone.EnumStoneVariant) iblockdata.get(BlockStone.VARIANT)).c();
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return iblockdata.get(BlockStone.VARIANT) == BlockStone.EnumStoneVariant.STONE ? Item.getItemOf(Blocks.COBBLESTONE) : Item.getItemOf(Blocks.STONE);
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockStone.EnumStoneVariant) iblockdata.get(BlockStone.VARIANT)).a();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        BlockStone.EnumStoneVariant[] ablockstone_enumstonevariant = BlockStone.EnumStoneVariant.values();
        int i = ablockstone_enumstonevariant.length;

        for (int j = 0; j < i; ++j) {
            BlockStone.EnumStoneVariant blockstone_enumstonevariant = ablockstone_enumstonevariant[j];

            nonnulllist.add(new ItemStack(this, 1, blockstone_enumstonevariant.a()));
        }

    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.a(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((BlockStone.EnumStoneVariant) iblockdata.get(BlockStone.VARIANT)).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockStone.VARIANT});
    }

    public static enum EnumStoneVariant implements INamable {

        STONE(0, MaterialMapColor.n, "stone", true), GRANITE(1, MaterialMapColor.m, "granite", true), GRANITE_SMOOTH(2, MaterialMapColor.m, "smooth_granite", "graniteSmooth", false), DIORITE(3, MaterialMapColor.q, "diorite", true), DIORITE_SMOOTH(4, MaterialMapColor.q, "smooth_diorite", "dioriteSmooth", false), ANDESITE(5, MaterialMapColor.n, "andesite", true), ANDESITE_SMOOTH(6, MaterialMapColor.n, "smooth_andesite", "andesiteSmooth", false);

        private static final BlockStone.EnumStoneVariant[] h = new BlockStone.EnumStoneVariant[values().length];
        private final int i;
        private final String j;
        private final String k;
        private final MaterialMapColor l;
        private final boolean m;

        private EnumStoneVariant(int i, MaterialMapColor materialmapcolor, String s, boolean flag) {
            this(i, materialmapcolor, s, s, flag);
        }

        private EnumStoneVariant(int i, MaterialMapColor materialmapcolor, String s, String s1, boolean flag) {
            this.i = i;
            this.j = s;
            this.k = s1;
            this.l = materialmapcolor;
            this.m = flag;
        }

        public int a() {
            return this.i;
        }

        public MaterialMapColor c() {
            return this.l;
        }

        public String toString() {
            return this.j;
        }

        public static BlockStone.EnumStoneVariant a(int i) {
            if (i < 0 || i >= BlockStone.EnumStoneVariant.h.length) {
                i = 0;
            }

            return BlockStone.EnumStoneVariant.h[i];
        }

        public String getName() {
            return this.j;
        }

        public String d() {
            return this.k;
        }

        public boolean e() {
            return this.m;
        }

        static {
            BlockStone.EnumStoneVariant[] ablockstone_enumstonevariant = values();
            int i = ablockstone_enumstonevariant.length;

            for (int j = 0; j < i; ++j) {
                BlockStone.EnumStoneVariant blockstone_enumstonevariant = ablockstone_enumstonevariant[j];

                BlockStone.EnumStoneVariant.h[blockstone_enumstonevariant.a()] = blockstone_enumstonevariant;
            }

        }
    }
}
