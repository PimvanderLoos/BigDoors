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
import net.minecraft.world.damagesource.DamageSource;
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
    private static final VoxelShape BASE = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape X_LEG1 = Block.a(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
    private static final VoxelShape X_LEG2 = Block.a(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    private static final VoxelShape X_TOP = Block.a(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
    private static final VoxelShape Z_LEG1 = Block.a(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
    private static final VoxelShape Z_LEG2 = Block.a(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
    private static final VoxelShape Z_TOP = Block.a(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
    private static final VoxelShape X_AXIS_AABB = VoxelShapes.a(BlockAnvil.BASE, BlockAnvil.X_LEG1, BlockAnvil.X_LEG2, BlockAnvil.X_TOP);
    private static final VoxelShape Z_AXIS_AABB = VoxelShapes.a(BlockAnvil.BASE, BlockAnvil.Z_LEG1, BlockAnvil.Z_LEG2, BlockAnvil.Z_TOP);
    private static final IChatBaseComponent CONTAINER_TITLE = new ChatMessage("container.repair");
    private static final float FALL_DAMAGE_PER_DISTANCE = 2.0F;
    private static final int FALL_DAMAGE_MAX = 40;

    public BlockAnvil(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockAnvil.FACING, EnumDirection.NORTH));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockAnvil.FACING, blockactioncontext.g().g());
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
        }, BlockAnvil.CONTAINER_TITLE);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockAnvil.FACING);

        return enumdirection.n() == EnumDirection.EnumAxis.X ? BlockAnvil.X_AXIS_AABB : BlockAnvil.Z_AXIS_AABB;
    }

    @Override
    protected void a(EntityFallingBlock entityfallingblock) {
        entityfallingblock.b(2.0F, 40);
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

    @Override
    public DamageSource b() {
        return DamageSource.ANVIL;
    }

    @Nullable
    public static IBlockData e(IBlockData iblockdata) {
        return iblockdata.a(Blocks.ANVIL) ? (IBlockData) Blocks.CHIPPED_ANVIL.getBlockData().set(BlockAnvil.FACING, (EnumDirection) iblockdata.get(BlockAnvil.FACING)) : (iblockdata.a(Blocks.CHIPPED_ANVIL) ? (IBlockData) Blocks.DAMAGED_ANVIL.getBlockData().set(BlockAnvil.FACING, (EnumDirection) iblockdata.get(BlockAnvil.FACING)) : null);
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

    @Override
    public int d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.d(iblockaccess, blockposition).col;
    }
}
