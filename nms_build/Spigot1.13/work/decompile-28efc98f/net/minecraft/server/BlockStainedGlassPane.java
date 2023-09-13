package net.minecraft.server;

public class BlockStainedGlassPane extends BlockGlassPane {

    private final EnumColor color;

    public BlockStainedGlassPane(EnumColor enumcolor, Block.Info block_info) {
        super(block_info);
        this.color = enumcolor;
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockStainedGlassPane.NORTH, Boolean.valueOf(false))).set(BlockStainedGlassPane.EAST, Boolean.valueOf(false))).set(BlockStainedGlassPane.SOUTH, Boolean.valueOf(false))).set(BlockStainedGlassPane.WEST, Boolean.valueOf(false))).set(BlockStainedGlassPane.q, Boolean.valueOf(false)));
    }

    public EnumColor d() {
        return this.color;
    }

    public TextureType c() {
        return TextureType.TRANSLUCENT;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            if (!world.isClientSide) {
                BlockBeacon.a(world, blockposition);
            }

        }
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            if (!world.isClientSide) {
                BlockBeacon.a(world, blockposition);
            }

        }
    }
}
