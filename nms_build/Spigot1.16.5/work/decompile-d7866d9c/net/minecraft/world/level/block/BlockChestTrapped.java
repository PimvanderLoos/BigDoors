package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.entity.TileEntityChestTrapped;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockChestTrapped extends BlockChest {

    public BlockChestTrapped(BlockBase.Info blockbase_info) {
        super(blockbase_info, () -> {
            return TileEntityTypes.TRAPPED_CHEST;
        });
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityChestTrapped();
    }

    @Override
    protected Statistic<MinecraftKey> c() {
        return StatisticList.CUSTOM.b(StatisticList.TRIGGER_TRAPPED_CHEST);
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return MathHelper.clamp(TileEntityChest.a(iblockaccess, blockposition), 0, 15);
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? iblockdata.b(iblockaccess, blockposition, enumdirection) : 0;
    }
}
