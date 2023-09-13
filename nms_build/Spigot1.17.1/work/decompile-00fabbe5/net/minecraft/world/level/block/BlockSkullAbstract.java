package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.item.ItemWearable;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;

public abstract class BlockSkullAbstract extends BlockTileEntity implements ItemWearable {

    private final BlockSkull.a type;

    public BlockSkullAbstract(BlockSkull.a blockskull_a, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.type = blockskull_a;
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntitySkull(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide && (iblockdata.a(Blocks.DRAGON_HEAD) || iblockdata.a(Blocks.DRAGON_WALL_HEAD)) ? a(tileentitytypes, TileEntityTypes.SKULL, TileEntitySkull::a) : null;
    }

    public BlockSkull.a b() {
        return this.type;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
