package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.RandomSource;
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

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final List<BlockPosition> BOOKSHELF_OFFSETS = BlockPosition.betweenClosedStream(-2, 0, -2, 2, 1, 2).filter((blockposition) -> {
        return Math.abs(blockposition.getX()) == 2 || Math.abs(blockposition.getZ()) == 2;
    }).map(BlockPosition::immutable).toList();

    protected BlockEnchantmentTable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    public static boolean isValidBookShelf(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        return world.getBlockState(blockposition.offset(blockposition1)).is(Blocks.BOOKSHELF) && world.isEmptyBlock(blockposition.offset(blockposition1.getX() / 2, blockposition1.getY(), blockposition1.getZ() / 2));
    }

    @Override
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockEnchantmentTable.SHAPE;
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        super.animateTick(iblockdata, world, blockposition, randomsource);
        Iterator iterator = BlockEnchantmentTable.BOOKSHELF_OFFSETS.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (randomsource.nextInt(16) == 0 && isValidBookShelf(world, blockposition, blockposition1)) {
                world.addParticle(Particles.ENCHANT, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 2.0D, (double) blockposition.getZ() + 0.5D, (double) ((float) blockposition1.getX() + randomsource.nextFloat()) - 0.5D, (double) ((float) blockposition1.getY() - randomsource.nextFloat() - 1.0F), (double) ((float) blockposition1.getZ() + randomsource.nextFloat()) - 0.5D);
            }
        }

    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityEnchantTable(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? createTickerHelper(tileentitytypes, TileEntityTypes.ENCHANTING_TABLE, TileEntityEnchantTable::bookAnimationTick) : null;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openMenu(iblockdata.getMenuProvider(world, blockposition));
            return EnumInteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityEnchantTable) {
            IChatBaseComponent ichatbasecomponent = ((INamableTileEntity) tileentity).getDisplayName();

            return new TileInventory((i, playerinventory, entityhuman) -> {
                return new ContainerEnchantTable(i, playerinventory, ContainerAccess.create(world, blockposition));
            }, ichatbasecomponent);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasCustomHoverName()) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityEnchantTable) {
                ((TileEntityEnchantTable) tileentity).setCustomName(itemstack.getHoverName());
            }
        }

    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
