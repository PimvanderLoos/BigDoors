package net.minecraft.world.level.block.piston;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTileEntity;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockPistonMoving extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockPistonExtension.FACING;
    public static final BlockStateEnum<BlockPropertyPistonType> TYPE = BlockPistonExtension.TYPE;

    public BlockPistonMoving(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPistonMoving.FACING, EnumDirection.NORTH)).setValue(BlockPistonMoving.TYPE, BlockPropertyPistonType.DEFAULT));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public static TileEntity newMovingBlockEntity(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection, boolean flag, boolean flag1) {
        return new TileEntityPiston(blockposition, iblockdata, iblockdata1, enumdirection, flag, flag1);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return createTickerHelper(tileentitytypes, TileEntityTypes.PISTON, TileEntityPiston::tick);
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).finalTick();
            }

        }
    }

    @Override
    public void destroy(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.relative(((EnumDirection) iblockdata.getValue(BlockPistonMoving.FACING)).getOpposite());
        IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);

        if (iblockdata1.getBlock() instanceof BlockPiston && (Boolean) iblockdata1.getValue(BlockPiston.EXTENDED)) {
            generatoraccess.removeBlock(blockposition1, false);
        }

    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!world.isClientSide && world.getBlockEntity(blockposition) == null) {
            world.removeBlock(blockposition, false);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        TileEntityPiston tileentitypiston = this.getBlockEntity(loottableinfo_builder.getLevel(), new BlockPosition((Vec3D) loottableinfo_builder.getParameter(LootContextParameters.ORIGIN)));

        return tileentitypiston == null ? Collections.emptyList() : tileentitypiston.getMovedState().getDrops(loottableinfo_builder);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        TileEntityPiston tileentitypiston = this.getBlockEntity(iblockaccess, blockposition);

        return tileentitypiston != null ? tileentitypiston.getCollisionShape(iblockaccess, blockposition) : VoxelShapes.empty();
    }

    @Nullable
    private TileEntityPiston getBlockEntity(IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

        return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.EMPTY;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockPistonMoving.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockPistonMoving.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockPistonMoving.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPistonMoving.FACING, BlockPistonMoving.TYPE);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
