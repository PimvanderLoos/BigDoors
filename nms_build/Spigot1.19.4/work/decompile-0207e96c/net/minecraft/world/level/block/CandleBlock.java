package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class CandleBlock extends AbstractCandleBlock implements IBlockWaterlogged {

    public static final int MIN_CANDLES = 1;
    public static final int MAX_CANDLES = 4;
    public static final BlockStateInteger CANDLES = BlockProperties.CANDLES;
    public static final BlockStateBoolean LIT = AbstractCandleBlock.LIT;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final ToIntFunction<IBlockData> LIGHT_EMISSION = (iblockdata) -> {
        return (Boolean) iblockdata.getValue(CandleBlock.LIT) ? 3 * (Integer) iblockdata.getValue(CandleBlock.CANDLES) : 0;
    };
    private static final Int2ObjectMap<List<Vec3D>> PARTICLE_OFFSETS = (Int2ObjectMap) SystemUtils.make(() -> {
        Int2ObjectMap<List<Vec3D>> int2objectmap = new Int2ObjectOpenHashMap();

        int2objectmap.defaultReturnValue(ImmutableList.of());
        int2objectmap.put(1, ImmutableList.of(new Vec3D(0.5D, 0.5D, 0.5D)));
        int2objectmap.put(2, ImmutableList.of(new Vec3D(0.375D, 0.44D, 0.5D), new Vec3D(0.625D, 0.5D, 0.44D)));
        int2objectmap.put(3, ImmutableList.of(new Vec3D(0.5D, 0.313D, 0.625D), new Vec3D(0.375D, 0.44D, 0.5D), new Vec3D(0.56D, 0.5D, 0.44D)));
        int2objectmap.put(4, ImmutableList.of(new Vec3D(0.44D, 0.313D, 0.56D), new Vec3D(0.625D, 0.44D, 0.56D), new Vec3D(0.375D, 0.44D, 0.375D), new Vec3D(0.56D, 0.5D, 0.375D)));
        return Int2ObjectMaps.unmodifiable(int2objectmap);
    });
    private static final VoxelShape ONE_AABB = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D);
    private static final VoxelShape TWO_AABB = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 6.0D, 9.0D);
    private static final VoxelShape THREE_AABB = Block.box(5.0D, 0.0D, 6.0D, 10.0D, 6.0D, 11.0D);
    private static final VoxelShape FOUR_AABB = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 10.0D);

    public CandleBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(CandleBlock.CANDLES, 1)).setValue(CandleBlock.LIT, false)).setValue(CandleBlock.WATERLOGGED, false));
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (entityhuman.getAbilities().mayBuild && entityhuman.getItemInHand(enumhand).isEmpty() && (Boolean) iblockdata.getValue(CandleBlock.LIT)) {
            extinguish(entityhuman, iblockdata, world, blockposition);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.isSecondaryUseActive() && blockactioncontext.getItemInHand().getItem() == this.asItem() && (Integer) iblockdata.getValue(CandleBlock.CANDLES) < 4 ? true : super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos());

        if (iblockdata.is((Block) this)) {
            return (IBlockData) iblockdata.cycle(CandleBlock.CANDLES);
        } else {
            Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
            boolean flag = fluid.getType() == FluidTypes.WATER;

            return (IBlockData) super.getStateForPlacement(blockactioncontext).setValue(CandleBlock.WATERLOGGED, flag);
        }
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(CandleBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(CandleBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((Integer) iblockdata.getValue(CandleBlock.CANDLES)) {
            case 1:
            default:
                return CandleBlock.ONE_AABB;
            case 2:
                return CandleBlock.TWO_AABB;
            case 3:
                return CandleBlock.THREE_AABB;
            case 4:
                return CandleBlock.FOUR_AABB;
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(CandleBlock.CANDLES, CandleBlock.LIT, CandleBlock.WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.getValue(CandleBlock.WATERLOGGED) && fluid.getType() == FluidTypes.WATER) {
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(CandleBlock.WATERLOGGED, true);

            if ((Boolean) iblockdata.getValue(CandleBlock.LIT)) {
                extinguish((EntityHuman) null, iblockdata1, generatoraccess, blockposition);
            } else {
                generatoraccess.setBlock(blockposition, iblockdata1, 3);
            }

            generatoraccess.scheduleTick(blockposition, fluid.getType(), fluid.getType().getTickDelay(generatoraccess));
            return true;
        } else {
            return false;
        }
    }

    public static boolean canLight(IBlockData iblockdata) {
        return iblockdata.is(TagsBlock.CANDLES, (blockbase_blockdata) -> {
            return blockbase_blockdata.hasProperty(CandleBlock.LIT) && blockbase_blockdata.hasProperty(CandleBlock.WATERLOGGED);
        }) && !(Boolean) iblockdata.getValue(CandleBlock.LIT) && !(Boolean) iblockdata.getValue(CandleBlock.WATERLOGGED);
    }

    @Override
    protected Iterable<Vec3D> getParticleOffsets(IBlockData iblockdata) {
        return (Iterable) CandleBlock.PARTICLE_OFFSETS.get((Integer) iblockdata.getValue(CandleBlock.CANDLES));
    }

    @Override
    protected boolean canBeLit(IBlockData iblockdata) {
        return !(Boolean) iblockdata.getValue(CandleBlock.WATERLOGGED) && super.canBeLit(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return Block.canSupportCenter(iworldreader, blockposition.below(), EnumDirection.UP);
    }
}
