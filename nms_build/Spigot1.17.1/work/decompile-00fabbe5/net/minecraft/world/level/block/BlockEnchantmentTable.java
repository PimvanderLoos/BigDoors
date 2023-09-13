package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEnchantTable;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockEnchantmentTable extends BlockTileEntity {

    protected static final VoxelShape SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    protected BlockEnchantmentTable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean g_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockEnchantmentTable.SHAPE;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.a(iblockdata, world, blockposition, random);

        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }

                if (random.nextInt(16) == 0) {
                    for (int k = 0; k <= 1; ++k) {
                        BlockPosition blockposition1 = blockposition.c(i, k, j);

                        if (world.getType(blockposition1).a(Blocks.BOOKSHELF)) {
                            if (!world.isEmpty(blockposition.c(i / 2, 0, j / 2))) {
                                break;
                            }

                            world.addParticle(Particles.ENCHANT, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 2.0D, (double) blockposition.getZ() + 0.5D, (double) ((float) i + random.nextFloat()) - 0.5D, (double) ((float) k - random.nextFloat() - 1.0F), (double) ((float) j + random.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }

    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityEnchantTable(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? a(tileentitytypes, TileEntityTypes.ENCHANTING_TABLE, TileEntityEnchantTable::a) : null;
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
