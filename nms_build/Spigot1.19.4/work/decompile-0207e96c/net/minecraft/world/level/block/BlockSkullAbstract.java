package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;

public abstract class BlockSkullAbstract extends BlockTileEntity implements Equipable {

    private final BlockSkull.a type;

    public BlockSkullAbstract(BlockSkull.a blockskull_a, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.type = blockskull_a;
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntitySkull(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        if (world.isClientSide) {
            boolean flag = iblockdata.is(Blocks.DRAGON_HEAD) || iblockdata.is(Blocks.DRAGON_WALL_HEAD) || iblockdata.is(Blocks.PIGLIN_HEAD) || iblockdata.is(Blocks.PIGLIN_WALL_HEAD);

            if (flag) {
                return createTickerHelper(tileentitytypes, TileEntityTypes.SKULL, TileEntitySkull::animation);
            }
        }

        return null;
    }

    public BlockSkull.a getType() {
        return this.type;
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public EnumItemSlot getEquipmentSlot() {
        return EnumItemSlot.HEAD;
    }
}
