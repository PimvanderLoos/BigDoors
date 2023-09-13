package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEnchantTable;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockEnchantmentTable extends BlockTileEntity {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    protected BlockEnchantmentTable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean c_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockEnchantmentTable.a;
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityEnchantTable();
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            return EnumInteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityEnchantTable) {
            IChatBaseComponent ichatbasecomponent = ((INamableTileEntity) tileentity).getScoreboardDisplayName();

            return new TileInventory((i, playerinventory, entityhuman) -> {
                return new ContainerEnchantTable(i, playerinventory, ContainerAccess.at(world, blockposition));
            }, ichatbasecomponent);
        } else {
            return null;
        }
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityEnchantTable) {
                ((TileEntityEnchantTable) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
