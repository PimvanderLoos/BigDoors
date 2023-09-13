package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerSmithing;
import net.minecraft.world.inventory.LegacySmithingMenu;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockSmithingTable extends BlockWorkbench {

    private static final IChatBaseComponent CONTAINER_TITLE = IChatBaseComponent.translatable("container.upgrade");

    protected BlockSmithingTable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return (Container) (world.enabledFeatures().contains(FeatureFlags.UPDATE_1_20) ? new ContainerSmithing(i, playerinventory, ContainerAccess.create(world, blockposition)) : new LegacySmithingMenu(i, playerinventory, ContainerAccess.create(world, blockposition)));
        }, BlockSmithingTable.CONTAINER_TITLE);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openMenu(iblockdata.getMenuProvider(world, blockposition));
            entityhuman.awardStat(StatisticList.INTERACT_WITH_SMITHING_TABLE);
            return EnumInteractionResult.CONSUME;
        }
    }
}
