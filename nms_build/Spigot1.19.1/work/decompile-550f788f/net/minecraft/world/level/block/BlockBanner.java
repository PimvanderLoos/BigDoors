package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockBanner extends BlockBannerAbstract {

    public static final BlockStateInteger ROTATION = BlockProperties.ROTATION_16;
    private static final Map<EnumColor, Block> BY_COLOR = Maps.newHashMap();
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public BlockBanner(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(enumcolor, blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockBanner.ROTATION, 0));
        BlockBanner.BY_COLOR.put(enumcolor, this);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getBlockState(blockposition.below()).getMaterial().isSolid();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockBanner.SHAPE;
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockBanner.ROTATION, MathHelper.floor((double) ((180.0F + blockactioncontext.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockBanner.ROTATION, enumblockrotation.rotate((Integer) iblockdata.getValue(BlockBanner.ROTATION), 16));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.setValue(BlockBanner.ROTATION, enumblockmirror.mirror((Integer) iblockdata.getValue(BlockBanner.ROTATION), 16));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockBanner.ROTATION);
    }

    public static Block byColor(EnumColor enumcolor) {
        return (Block) BlockBanner.BY_COLOR.getOrDefault(enumcolor, Blocks.WHITE_BANNER);
    }
}
