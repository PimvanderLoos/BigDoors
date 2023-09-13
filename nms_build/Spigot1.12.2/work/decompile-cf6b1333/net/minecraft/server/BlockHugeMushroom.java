package net.minecraft.server;

import java.util.Random;

public class BlockHugeMushroom extends Block {

    public static final BlockStateEnum<BlockHugeMushroom.EnumHugeMushroomVariant> VARIANT = BlockStateEnum.of("variant", BlockHugeMushroom.EnumHugeMushroomVariant.class);
    private final Block b;

    public BlockHugeMushroom(Material material, MaterialMapColor materialmapcolor, Block block) {
        super(material, materialmapcolor);
        this.w(this.blockStateList.getBlockData().set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.ALL_OUTSIDE));
        this.b = block;
    }

    public int a(Random random) {
        return Math.max(0, random.nextInt(10) - 7);
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((BlockHugeMushroom.EnumHugeMushroomVariant) iblockdata.get(BlockHugeMushroom.VARIANT)) {
        case ALL_STEM:
            return MaterialMapColor.f;

        case ALL_INSIDE:
            return MaterialMapColor.e;

        case STEM:
            return MaterialMapColor.e;

        default:
            return super.c(iblockdata, iblockaccess, blockposition);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(this.b);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this.b);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.a(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((BlockHugeMushroom.EnumHugeMushroomVariant) iblockdata.get(BlockHugeMushroom.VARIANT)).a();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch ((BlockHugeMushroom.EnumHugeMushroomVariant) iblockdata.get(BlockHugeMushroom.VARIANT)) {
            case STEM:
                break;

            case NORTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_EAST);

            case NORTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH);

            case NORTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_WEST);

            case WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.EAST);

            case EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_EAST);

            case SOUTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_WEST);

            default:
                return iblockdata;
            }

        case COUNTERCLOCKWISE_90:
            switch ((BlockHugeMushroom.EnumHugeMushroomVariant) iblockdata.get(BlockHugeMushroom.VARIANT)) {
            case STEM:
                break;

            case NORTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_WEST);

            case NORTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_WEST);

            case WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH);

            case EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH);

            case SOUTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_EAST);

            case SOUTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.EAST);

            case SOUTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_EAST);

            default:
                return iblockdata;
            }

        case CLOCKWISE_90:
            switch ((BlockHugeMushroom.EnumHugeMushroomVariant) iblockdata.get(BlockHugeMushroom.VARIANT)) {
            case STEM:
                break;

            case NORTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_EAST);

            case NORTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_EAST);

            case WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH);

            case EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH);

            case SOUTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_WEST);

            case SOUTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.WEST);

            case SOUTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_WEST);

            default:
                return iblockdata;
            }

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockHugeMushroom.EnumHugeMushroomVariant blockhugemushroom_enumhugemushroomvariant = (BlockHugeMushroom.EnumHugeMushroomVariant) iblockdata.get(BlockHugeMushroom.VARIANT);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            switch (blockhugemushroom_enumhugemushroomvariant) {
            case NORTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_WEST);

            case NORTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH);

            case NORTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_EAST);

            case WEST:
            case EAST:
            default:
                return super.a(iblockdata, enumblockmirror);

            case SOUTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_WEST);

            case SOUTH:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_EAST);
            }

        case FRONT_BACK:
            switch (blockhugemushroom_enumhugemushroomvariant) {
            case NORTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_EAST);

            case NORTH:
            case SOUTH:
            default:
                break;

            case NORTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.NORTH_WEST);

            case WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.EAST);

            case EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_EAST);

            case SOUTH_EAST:
                return iblockdata.set(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumHugeMushroomVariant.SOUTH_WEST);
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockHugeMushroom.VARIANT});
    }

    public static enum EnumHugeMushroomVariant implements INamable {

        NORTH_WEST(1, "north_west"), NORTH(2, "north"), NORTH_EAST(3, "north_east"), WEST(4, "west"), CENTER(5, "center"), EAST(6, "east"), SOUTH_WEST(7, "south_west"), SOUTH(8, "south"), SOUTH_EAST(9, "south_east"), STEM(10, "stem"), ALL_INSIDE(0, "all_inside"), ALL_OUTSIDE(14, "all_outside"), ALL_STEM(15, "all_stem");

        private static final BlockHugeMushroom.EnumHugeMushroomVariant[] n = new BlockHugeMushroom.EnumHugeMushroomVariant[16];
        private final int o;
        private final String p;

        private EnumHugeMushroomVariant(int i, String s) {
            this.o = i;
            this.p = s;
        }

        public int a() {
            return this.o;
        }

        public String toString() {
            return this.p;
        }

        public static BlockHugeMushroom.EnumHugeMushroomVariant a(int i) {
            if (i < 0 || i >= BlockHugeMushroom.EnumHugeMushroomVariant.n.length) {
                i = 0;
            }

            BlockHugeMushroom.EnumHugeMushroomVariant blockhugemushroom_enumhugemushroomvariant = BlockHugeMushroom.EnumHugeMushroomVariant.n[i];

            return blockhugemushroom_enumhugemushroomvariant == null ? BlockHugeMushroom.EnumHugeMushroomVariant.n[0] : blockhugemushroom_enumhugemushroomvariant;
        }

        public String getName() {
            return this.p;
        }

        static {
            BlockHugeMushroom.EnumHugeMushroomVariant[] ablockhugemushroom_enumhugemushroomvariant = values();
            int i = ablockhugemushroom_enumhugemushroomvariant.length;

            for (int j = 0; j < i; ++j) {
                BlockHugeMushroom.EnumHugeMushroomVariant blockhugemushroom_enumhugemushroomvariant = ablockhugemushroom_enumhugemushroomvariant[j];

                BlockHugeMushroom.EnumHugeMushroomVariant.n[blockhugemushroom_enumhugemushroomvariant.a()] = blockhugemushroom_enumhugemushroomvariant;
            }

        }
    }
}
