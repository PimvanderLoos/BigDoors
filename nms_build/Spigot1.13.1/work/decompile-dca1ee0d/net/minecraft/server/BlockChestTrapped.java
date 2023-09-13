package net.minecraft.server;

public class BlockChestTrapped extends BlockChest {

    public BlockChestTrapped(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityChestTrapped();
    }

    protected Statistic<MinecraftKey> d() {
        return StatisticList.CUSTOM.b(StatisticList.TRIGGER_TRAPPED_CHEST);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return MathHelper.clamp(TileEntityChest.a(iblockaccess, blockposition), 0, 15);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? iblockdata.a(iblockaccess, blockposition, enumdirection) : 0;
    }
}
