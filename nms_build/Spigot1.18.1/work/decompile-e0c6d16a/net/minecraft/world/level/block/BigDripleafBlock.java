package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BigDripleafBlock extends BlockFacingHorizontal implements IBlockFragilePlantElement, IBlockWaterlogged {

    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private static final BlockStateEnum<Tilt> TILT = BlockProperties.TILT;
    private static final int NO_TICK = -1;
    private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE = (Object2IntMap) SystemUtils.make(new Object2IntArrayMap(), (object2intarraymap) -> {
        object2intarraymap.defaultReturnValue(-1);
        object2intarraymap.put(Tilt.UNSTABLE, 10);
        object2intarraymap.put(Tilt.PARTIAL, 10);
        object2intarraymap.put(Tilt.FULL, 100);
    });
    private static final int MAX_GEN_HEIGHT = 5;
    private static final int STEM_WIDTH = 6;
    private static final int ENTITY_DETECTION_MIN_Y = 11;
    private static final int LOWEST_LEAF_TOP = 13;
    private static final Map<Tilt, VoxelShape> LEAF_SHAPES = ImmutableMap.of(Tilt.NONE, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.UNSTABLE, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.PARTIAL, Block.box(0.0D, 11.0D, 0.0D, 16.0D, 13.0D, 16.0D), Tilt.FULL, VoxelShapes.empty());
    private static final VoxelShape STEM_SLICER = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final Map<EnumDirection, VoxelShape> STEM_SHAPES = ImmutableMap.of(EnumDirection.NORTH, VoxelShapes.joinUnoptimized(BigDripleafStemBlock.NORTH_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST), EnumDirection.SOUTH, VoxelShapes.joinUnoptimized(BigDripleafStemBlock.SOUTH_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST), EnumDirection.EAST, VoxelShapes.joinUnoptimized(BigDripleafStemBlock.EAST_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST), EnumDirection.WEST, VoxelShapes.joinUnoptimized(BigDripleafStemBlock.WEST_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST));
    private final Map<IBlockData, VoxelShape> shapesCache;

    protected BigDripleafBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BigDripleafBlock.WATERLOGGED, false)).setValue(BigDripleafBlock.FACING, EnumDirection.NORTH)).setValue(BigDripleafBlock.TILT, Tilt.NONE));
        this.shapesCache = this.getShapeForEachState(BigDripleafBlock::calculateShape);
    }

    private static VoxelShape calculateShape(IBlockData iblockdata) {
        return VoxelShapes.or((VoxelShape) BigDripleafBlock.LEAF_SHAPES.get(iblockdata.getValue(BigDripleafBlock.TILT)), (VoxelShape) BigDripleafBlock.STEM_SHAPES.get(iblockdata.getValue(BigDripleafBlock.FACING)));
    }

    public static void placeWithRandomHeight(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, EnumDirection enumdirection) {
        int i = MathHelper.nextInt(random, 2, 5);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        int j = 0;

        while (j < i && canPlaceAt(generatoraccess, blockposition_mutableblockposition, generatoraccess.getBlockState(blockposition_mutableblockposition))) {
            ++j;
            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        int k = blockposition.getY() + j - 1;

        blockposition_mutableblockposition.setY(blockposition.getY());

        while (blockposition_mutableblockposition.getY() < k) {
            BigDripleafStemBlock.place(generatoraccess, blockposition_mutableblockposition, generatoraccess.getFluidState(blockposition_mutableblockposition), enumdirection);
            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        place(generatoraccess, blockposition_mutableblockposition, generatoraccess.getFluidState(blockposition_mutableblockposition), enumdirection);
    }

    private static boolean canReplace(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER) || iblockdata.is(Blocks.SMALL_DRIPLEAF);
    }

    protected static boolean canPlaceAt(LevelHeightAccessor levelheightaccessor, BlockPosition blockposition, IBlockData iblockdata) {
        return !levelheightaccessor.isOutsideBuildHeight(blockposition) && canReplace(iblockdata);
    }

    protected static boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid, EnumDirection enumdirection) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(BigDripleafBlock.WATERLOGGED, fluid.isSourceOfType(FluidTypes.WATER))).setValue(BigDripleafBlock.FACING, enumdirection);

        return generatoraccess.setBlock(blockposition, iblockdata, 3);
    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        this.setTiltAndScheduleTick(iblockdata, world, movingobjectpositionblock.getBlockPos(), Tilt.FULL, SoundEffects.BIG_DRIPLEAF_TILT_DOWN);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BigDripleafBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        return iblockdata1.is((Block) this) || iblockdata1.is(Blocks.BIG_DRIPLEAF_STEM) || iblockdata1.is((Tag) TagsBlock.BIG_DRIPLEAF_PLACEABLE);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if ((Boolean) iblockdata.getValue(BigDripleafBlock.WATERLOGGED)) {
                generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
            }

            return enumdirection == EnumDirection.UP && iblockdata1.is((Block) this) ? Blocks.BIG_DRIPLEAF_STEM.withPropertiesOf(iblockdata) : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        IBlockData iblockdata1 = iblockaccess.getBlockState(blockposition.above());

        return canReplace(iblockdata1);
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);

        if (canPlaceAt(worldserver, blockposition1, iblockdata1)) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BigDripleafBlock.FACING);

            BigDripleafStemBlock.place(worldserver, blockposition, iblockdata.getFluidState(), enumdirection);
            place(worldserver, blockposition1, iblockdata1.getFluidState(), enumdirection);
        }

    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide) {
            if (iblockdata.getValue(BigDripleafBlock.TILT) == Tilt.NONE && canEntityTilt(blockposition, entity) && !world.hasNeighborSignal(blockposition)) {
                this.setTiltAndScheduleTick(iblockdata, world, blockposition, Tilt.UNSTABLE, (SoundEffect) null);
            }

        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.hasNeighborSignal(blockposition)) {
            resetTilt(iblockdata, worldserver, blockposition);
        } else {
            Tilt tilt = (Tilt) iblockdata.getValue(BigDripleafBlock.TILT);

            if (tilt == Tilt.UNSTABLE) {
                this.setTiltAndScheduleTick(iblockdata, worldserver, blockposition, Tilt.PARTIAL, SoundEffects.BIG_DRIPLEAF_TILT_DOWN);
            } else if (tilt == Tilt.PARTIAL) {
                this.setTiltAndScheduleTick(iblockdata, worldserver, blockposition, Tilt.FULL, SoundEffects.BIG_DRIPLEAF_TILT_DOWN);
            } else if (tilt == Tilt.FULL) {
                resetTilt(iblockdata, worldserver, blockposition);
            }

        }
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world.hasNeighborSignal(blockposition)) {
            resetTilt(iblockdata, world, blockposition);
        }

    }

    private static void playTiltSound(World world, BlockPosition blockposition, SoundEffect soundeffect) {
        float f = MathHelper.randomBetween(world.random, 0.8F, 1.2F);

        world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, f);
    }

    private static boolean canEntityTilt(BlockPosition blockposition, Entity entity) {
        return entity.isOnGround() && entity.position().y > (double) ((float) blockposition.getY() + 0.6875F);
    }

    private void setTiltAndScheduleTick(IBlockData iblockdata, World world, BlockPosition blockposition, Tilt tilt, @Nullable SoundEffect soundeffect) {
        setTilt(iblockdata, world, blockposition, tilt);
        if (soundeffect != null) {
            playTiltSound(world, blockposition, soundeffect);
        }

        int i = BigDripleafBlock.DELAY_UNTIL_NEXT_TILT_STATE.getInt(tilt);

        if (i != -1) {
            world.scheduleTick(blockposition, (Block) this, i);
        }

    }

    private static void resetTilt(IBlockData iblockdata, World world, BlockPosition blockposition) {
        setTilt(iblockdata, world, blockposition, Tilt.NONE);
        if (iblockdata.getValue(BigDripleafBlock.TILT) != Tilt.NONE) {
            playTiltSound(world, blockposition, SoundEffects.BIG_DRIPLEAF_TILT_UP);
        }

    }

    private static void setTilt(IBlockData iblockdata, World world, BlockPosition blockposition, Tilt tilt) {
        world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BigDripleafBlock.TILT, tilt), 2);
        if (tilt.causesVibration()) {
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition);
        }

    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BigDripleafBlock.LEAF_SHAPES.get(iblockdata.getValue(BigDripleafBlock.TILT));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapesCache.get(iblockdata);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().below());
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        boolean flag = iblockdata.is(Blocks.BIG_DRIPLEAF) || iblockdata.is(Blocks.BIG_DRIPLEAF_STEM);

        return (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BigDripleafBlock.WATERLOGGED, fluid.isSourceOfType(FluidTypes.WATER))).setValue(BigDripleafBlock.FACING, flag ? (EnumDirection) iblockdata.getValue(BigDripleafBlock.FACING) : blockactioncontext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BigDripleafBlock.WATERLOGGED, BigDripleafBlock.FACING, BigDripleafBlock.TILT);
    }
}
