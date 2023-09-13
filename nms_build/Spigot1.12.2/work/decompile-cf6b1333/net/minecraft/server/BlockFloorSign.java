package net.minecraft.server;

public class BlockFloorSign extends BlockSign {

    public static final BlockStateInteger ROTATION = BlockStateInteger.of("rotation", 0, 15);

    public BlockFloorSign() {
        this.w(this.blockStateList.getBlockData().set(BlockFloorSign.ROTATION, Integer.valueOf(0)));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.getType(blockposition.down()).getMaterial().isBuildable()) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

        super.a(iblockdata, world, blockposition, block, blockposition1);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFloorSign.ROTATION, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockFloorSign.ROTATION)).intValue();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockFloorSign.ROTATION, Integer.valueOf(enumblockrotation.a(((Integer) iblockdata.get(BlockFloorSign.ROTATION)).intValue(), 16)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.set(BlockFloorSign.ROTATION, Integer.valueOf(enumblockmirror.a(((Integer) iblockdata.get(BlockFloorSign.ROTATION)).intValue(), 16)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFloorSign.ROTATION});
    }
}
