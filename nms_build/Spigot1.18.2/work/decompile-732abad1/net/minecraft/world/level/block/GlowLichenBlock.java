package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class GlowLichenBlock extends MultifaceBlock implements IBlockFragilePlantElement, IBlockWaterlogged {

    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;

    public GlowLichenBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) this.defaultBlockState().setValue(GlowLichenBlock.WATERLOGGED, false));
    }

    public static ToIntFunction<IBlockData> emission(int i) {
        return (iblockdata) -> {
            return MultifaceBlock.hasAnyFace(iblockdata) ? i : 0;
        };
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        super.createBlockStateDefinition(blockstatelist_a);
        blockstatelist_a.add(GlowLichenBlock.WATERLOGGED);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(GlowLichenBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.getItemInHand().is(Items.GLOW_LICHEN) || super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return Stream.of(GlowLichenBlock.DIRECTIONS).anyMatch((enumdirection) -> {
            return this.canSpread(iblockdata, iblockaccess, blockposition, enumdirection.getOpposite());
        });
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.spreadFromRandomFaceTowardRandomDirection(iblockdata, worldserver, blockposition, random);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(GlowLichenBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getFluidState().isEmpty();
    }
}
