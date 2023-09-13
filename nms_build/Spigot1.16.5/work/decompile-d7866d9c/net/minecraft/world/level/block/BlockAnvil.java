package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockAnvil extends BlockFalling {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    private static final VoxelShape b = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape c = Block.a(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
    private static final VoxelShape d = Block.a(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    private static final VoxelShape e = Block.a(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
    private static final VoxelShape f = Block.a(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
    private static final VoxelShape g = Block.a(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
    private static final VoxelShape h = Block.a(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
    private static final VoxelShape i = VoxelShapes.a(BlockAnvil.b, BlockAnvil.c, BlockAnvil.d, BlockAnvil.e);
    private static final VoxelShape j = VoxelShapes.a(BlockAnvil.b, BlockAnvil.f, BlockAnvil.g, BlockAnvil.h);
    private static final IChatBaseComponent k = new ChatMessage("container.repair");

    public BlockAnvil(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockAnvil.FACING, EnumDirection.NORTH));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockAnvil.FACING, blockactioncontext.f().g());
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_ANVIL);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerAnvil(i, playerinventory, ContainerAccess.at(world, blockposition));
        }, BlockAnvil.k);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockAnvil.FACING);

        return enumdirection.n() == EnumDirection.EnumAxis.X ? BlockAnvil.i : BlockAnvil.j;
    }

    @Override
    protected void a(EntityFallingBlock entityfallingblock) {
        entityfallingblock.a(true);
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EntityFallingBlock entityfallingblock) {
        if (!entityfallingblock.isSilent()) {
            world.triggerEffect(1031, blockposition, 0);
        }

    }

    @Override
    public void a(World world, BlockPosition blockposition, EntityFallingBlock entityfallingblock) {
        if (!entityfallingblock.isSilent()) {
            world.triggerEffect(1029, blockposition, 0);
        }

    }

    @Nullable
    public static IBlockData c(IBlockData iblockdata) {
        return iblockdata.a(Blocks.ANVIL) ? (IBlockData) Blocks.CHIPPED_ANVIL.getBlockData().set(BlockAnvil.FACING, iblockdata.get(BlockAnvil.FACING)) : (iblockdata.a(Blocks.CHIPPED_ANVIL) ? (IBlockData) Blocks.DAMAGED_ANVIL.getBlockData().set(BlockAnvil.FACING, iblockdata.get(BlockAnvil.FACING)) : null);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockAnvil.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockAnvil.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockAnvil.FACING);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
