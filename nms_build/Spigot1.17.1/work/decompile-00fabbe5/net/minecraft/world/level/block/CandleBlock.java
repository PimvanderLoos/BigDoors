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
import net.minecraft.tags.Tag;
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
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
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
        return (Boolean) iblockdata.get(CandleBlock.LIT) ? 3 * (Integer) iblockdata.get(CandleBlock.CANDLES) : 0;
    };
    private static final Int2ObjectMap<List<Vec3D>> PARTICLE_OFFSETS = (Int2ObjectMap) SystemUtils.a(() -> {
        Int2ObjectMap<List<Vec3D>> int2objectmap = new Int2ObjectOpenHashMap();

        int2objectmap.defaultReturnValue(ImmutableList.of());
        int2objectmap.put(1, ImmutableList.of(new Vec3D(0.5D, 0.5D, 0.5D)));
        int2objectmap.put(2, ImmutableList.of(new Vec3D(0.375D, 0.44D, 0.5D), new Vec3D(0.625D, 0.5D, 0.44D)));
        int2objectmap.put(3, ImmutableList.of(new Vec3D(0.5D, 0.313D, 0.625D), new Vec3D(0.375D, 0.44D, 0.5D), new Vec3D(0.56D, 0.5D, 0.44D)));
        int2objectmap.put(4, ImmutableList.of(new Vec3D(0.44D, 0.313D, 0.56D), new Vec3D(0.625D, 0.44D, 0.56D), new Vec3D(0.375D, 0.44D, 0.375D), new Vec3D(0.56D, 0.5D, 0.375D)));
        return Int2ObjectMaps.unmodifiable(int2objectmap);
    });
    private static final VoxelShape ONE_AABB = Block.a(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D);
    private static final VoxelShape TWO_AABB = Block.a(5.0D, 0.0D, 6.0D, 11.0D, 6.0D, 9.0D);
    private static final VoxelShape THREE_AABB = Block.a(5.0D, 0.0D, 6.0D, 10.0D, 6.0D, 11.0D);
    private static final VoxelShape FOUR_AABB = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 10.0D);

    public CandleBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(CandleBlock.CANDLES, 1)).set(CandleBlock.LIT, false)).set(CandleBlock.WATERLOGGED, false));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (entityhuman.getAbilities().mayBuild && entityhuman.b(enumhand).isEmpty() && (Boolean) iblockdata.get(CandleBlock.LIT)) {
            a(entityhuman, iblockdata, (GeneratorAccess) world, blockposition);
            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.isSneaking() && blockactioncontext.getItemStack().getItem() == this.getItem() && (Integer) iblockdata.get(CandleBlock.CANDLES) < 4 ? true : super.a(iblockdata, blockactioncontext);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition());

        if (iblockdata.a((Block) this)) {
            return (IBlockData) iblockdata.a((IBlockState) CandleBlock.CANDLES);
        } else {
            Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
            boolean flag = fluid.getType() == FluidTypes.WATER;

            return (IBlockData) super.getPlacedState(blockactioncontext).set(CandleBlock.WATERLOGGED, flag);
        }
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(CandleBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(CandleBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((Integer) iblockdata.get(CandleBlock.CANDLES)) {
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
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(CandleBlock.CANDLES, CandleBlock.LIT, CandleBlock.WATERLOGGED);
    }

    @Override
    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(CandleBlock.WATERLOGGED) && fluid.getType() == FluidTypes.WATER) {
            IBlockData iblockdata1 = (IBlockData) iblockdata.set(CandleBlock.WATERLOGGED, true);

            if ((Boolean) iblockdata.get(CandleBlock.LIT)) {
                a((EntityHuman) null, iblockdata1, generatoraccess, blockposition);
            } else {
                generatoraccess.setTypeAndData(blockposition, iblockdata1, 3);
            }

            generatoraccess.getFluidTickList().a(blockposition, fluid.getType(), fluid.getType().a((IWorldReader) generatoraccess));
            return true;
        } else {
            return false;
        }
    }

    public static boolean g(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.CANDLES, (blockbase_blockdata) -> {
            return blockbase_blockdata.b(CandleBlock.LIT) && blockbase_blockdata.b(CandleBlock.WATERLOGGED);
        }) && !(Boolean) iblockdata.get(CandleBlock.LIT) && !(Boolean) iblockdata.get(CandleBlock.WATERLOGGED);
    }

    @Override
    protected Iterable<Vec3D> a(IBlockData iblockdata) {
        return (Iterable) CandleBlock.PARTICLE_OFFSETS.get((Integer) iblockdata.get(CandleBlock.CANDLES));
    }

    @Override
    protected boolean c(IBlockData iblockdata) {
        return !(Boolean) iblockdata.get(CandleBlock.WATERLOGGED) && super.c(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return Block.a(iworldreader, blockposition.down(), EnumDirection.UP);
    }
}
