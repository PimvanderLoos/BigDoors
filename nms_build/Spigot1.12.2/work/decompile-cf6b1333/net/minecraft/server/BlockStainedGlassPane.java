package net.minecraft.server;

public class BlockStainedGlassPane extends BlockThin {

    public static final BlockStateEnum<EnumColor> COLOR = BlockStateEnum.of("color", EnumColor.class);

    public BlockStainedGlassPane() {
        super(Material.SHATTERABLE, false);
        this.w(this.blockStateList.getBlockData().set(BlockStainedGlassPane.NORTH, Boolean.valueOf(false)).set(BlockStainedGlassPane.EAST, Boolean.valueOf(false)).set(BlockStainedGlassPane.SOUTH, Boolean.valueOf(false)).set(BlockStainedGlassPane.WEST, Boolean.valueOf(false)).set(BlockStainedGlassPane.COLOR, EnumColor.WHITE));
        this.a(CreativeModeTab.c);
    }

    public int getDropData(IBlockData iblockdata) {
        return ((EnumColor) iblockdata.get(BlockStainedGlassPane.COLOR)).getColorIndex();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        for (int i = 0; i < EnumColor.values().length; ++i) {
            nonnulllist.add(new ItemStack(this, 1, i));
        }

    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return MaterialMapColor.a((EnumColor) iblockdata.get(BlockStainedGlassPane.COLOR));
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockStainedGlassPane.COLOR, EnumColor.fromColorIndex(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumColor) iblockdata.get(BlockStainedGlassPane.COLOR)).getColorIndex();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return iblockdata.set(BlockStainedGlassPane.NORTH, iblockdata.get(BlockStainedGlassPane.SOUTH)).set(BlockStainedGlassPane.EAST, iblockdata.get(BlockStainedGlassPane.WEST)).set(BlockStainedGlassPane.SOUTH, iblockdata.get(BlockStainedGlassPane.NORTH)).set(BlockStainedGlassPane.WEST, iblockdata.get(BlockStainedGlassPane.EAST));

        case COUNTERCLOCKWISE_90:
            return iblockdata.set(BlockStainedGlassPane.NORTH, iblockdata.get(BlockStainedGlassPane.EAST)).set(BlockStainedGlassPane.EAST, iblockdata.get(BlockStainedGlassPane.SOUTH)).set(BlockStainedGlassPane.SOUTH, iblockdata.get(BlockStainedGlassPane.WEST)).set(BlockStainedGlassPane.WEST, iblockdata.get(BlockStainedGlassPane.NORTH));

        case CLOCKWISE_90:
            return iblockdata.set(BlockStainedGlassPane.NORTH, iblockdata.get(BlockStainedGlassPane.WEST)).set(BlockStainedGlassPane.EAST, iblockdata.get(BlockStainedGlassPane.NORTH)).set(BlockStainedGlassPane.SOUTH, iblockdata.get(BlockStainedGlassPane.EAST)).set(BlockStainedGlassPane.WEST, iblockdata.get(BlockStainedGlassPane.SOUTH));

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return iblockdata.set(BlockStainedGlassPane.NORTH, iblockdata.get(BlockStainedGlassPane.SOUTH)).set(BlockStainedGlassPane.SOUTH, iblockdata.get(BlockStainedGlassPane.NORTH));

        case FRONT_BACK:
            return iblockdata.set(BlockStainedGlassPane.EAST, iblockdata.get(BlockStainedGlassPane.WEST)).set(BlockStainedGlassPane.WEST, iblockdata.get(BlockStainedGlassPane.EAST));

        default:
            return super.a(iblockdata, enumblockmirror);
        }
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockStainedGlassPane.NORTH, BlockStainedGlassPane.EAST, BlockStainedGlassPane.WEST, BlockStainedGlassPane.SOUTH, BlockStainedGlassPane.COLOR});
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            BlockBeacon.c(world, blockposition);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            BlockBeacon.c(world, blockposition);
        }

    }
}
