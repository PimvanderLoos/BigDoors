package net.minecraft.server;

public class BlockConcretePowder extends BlockFalling {

    public static final BlockStateEnum<EnumColor> a = BlockStateEnum.of("color", EnumColor.class);

    public BlockConcretePowder() {
        super(Material.SAND);
        this.w(this.blockStateList.getBlockData().set(BlockConcretePowder.a, EnumColor.WHITE));
        this.a(CreativeModeTab.b);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        if (iblockdata1.getMaterial().isLiquid()) {
            world.setTypeAndData(blockposition, Blocks.dR.getBlockData().set(BlockCloth.COLOR, iblockdata.get(BlockConcretePowder.a)), 3);
        }

    }

    protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = false;
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection != EnumDirection.DOWN) {
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (world.getType(blockposition1).getMaterial() == Material.WATER) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag) {
            world.setTypeAndData(blockposition, Blocks.dR.getBlockData().set(BlockCloth.COLOR, iblockdata.get(BlockConcretePowder.a)), 3);
        }

        return flag;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!this.e(world, blockposition, iblockdata)) {
            super.a(iblockdata, world, blockposition, block, blockposition1);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.e(world, blockposition, iblockdata)) {
            super.onPlace(world, blockposition, iblockdata);
        }

    }

    public int getDropData(IBlockData iblockdata) {
        return ((EnumColor) iblockdata.get(BlockConcretePowder.a)).getColorIndex();
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
        return MaterialMapColor.a((EnumColor) iblockdata.get(BlockConcretePowder.a));
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockConcretePowder.a, EnumColor.fromColorIndex(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumColor) iblockdata.get(BlockConcretePowder.a)).getColorIndex();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockConcretePowder.a});
    }
}
