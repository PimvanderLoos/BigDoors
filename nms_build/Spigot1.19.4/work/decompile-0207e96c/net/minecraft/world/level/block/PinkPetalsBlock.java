package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class PinkPetalsBlock extends BlockPlant implements IBlockFragilePlantElement {

    public static final int MIN_FLOWERS = 1;
    public static final int MAX_FLOWERS = 4;
    public static final BlockStateDirection FACING = BlockProperties.HORIZONTAL_FACING;
    public static final BlockStateInteger AMOUNT = BlockProperties.FLOWER_AMOUNT;

    protected PinkPetalsBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(PinkPetalsBlock.FACING, EnumDirection.NORTH)).setValue(PinkPetalsBlock.AMOUNT, 1));
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(PinkPetalsBlock.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(PinkPetalsBlock.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(PinkPetalsBlock.FACING)));
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.isSecondaryUseActive() && blockactioncontext.getItemInHand().is(this.asItem()) && (Integer) iblockdata.getValue(PinkPetalsBlock.AMOUNT) < 4 ? true : super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos());

        return iblockdata.is((Block) this) ? (IBlockData) iblockdata.setValue(PinkPetalsBlock.AMOUNT, Math.min(4, (Integer) iblockdata.getValue(PinkPetalsBlock.AMOUNT) + 1)) : (IBlockData) this.defaultBlockState().setValue(PinkPetalsBlock.FACING, blockactioncontext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(PinkPetalsBlock.FACING, PinkPetalsBlock.AMOUNT);
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        int i = (Integer) iblockdata.getValue(PinkPetalsBlock.AMOUNT);

        if (i < 4) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(PinkPetalsBlock.AMOUNT, i + 1), 2);
        } else {
            popResource(worldserver, blockposition, new ItemStack(this));
        }

    }
}
