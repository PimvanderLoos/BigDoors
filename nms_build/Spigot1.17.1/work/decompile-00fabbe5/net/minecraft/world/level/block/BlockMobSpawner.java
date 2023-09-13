package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockMobSpawner extends BlockTileEntity {

    protected BlockMobSpawner(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityMobSpawner(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return a(tileentitytypes, TileEntityTypes.MOB_SPAWNER, world.isClientSide ? TileEntityMobSpawner::a : TileEntityMobSpawner::b);
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, worldserver, blockposition, itemstack);
        int i = 15 + worldserver.random.nextInt(15) + worldserver.random.nextInt(15);

        this.dropExperience(worldserver, blockposition, i);
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.EMPTY;
    }
}
