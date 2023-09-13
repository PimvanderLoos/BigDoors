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
    private static final Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE = (Object2IntMap) SystemUtils.a((Object) (new Object2IntArrayMap()), (object2intarraymap) -> {
        object2intarraymap.defaultReturnValue(-1);
        object2intarraymap.put(Tilt.UNSTABLE, 10);
        object2intarraymap.put(Tilt.PARTIAL, 10);
        object2intarraymap.put(Tilt.FULL, 100);
    });
    private static final int MAX_GEN_HEIGHT = 5;
    private static final int STEM_WIDTH = 6;
    private static final int ENTITY_DETECTION_MIN_Y = 11;
    private static final int LOWEST_LEAF_TOP = 13;
    private static final Map<Tilt, VoxelShape> LEAF_SHAPES = ImmutableMap.of(Tilt.NONE, Block.a(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.UNSTABLE, Block.a(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D), Tilt.PARTIAL, Block.a(0.0D, 11.0D, 0.0D, 16.0D, 13.0D, 16.0D), Tilt.FULL, VoxelShapes.a());
    private static final VoxelShape STEM_SLICER = Block.a(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final Map<EnumDirection, VoxelShape> STEM_SHAPES = ImmutableMap.of(EnumDirection.NORTH, VoxelShapes.b(BigDripleafStemBlock.NORTH_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST), EnumDirection.SOUTH, VoxelShapes.b(BigDripleafStemBlock.SOUTH_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST), EnumDirection.EAST, VoxelShapes.b(BigDripleafStemBlock.EAST_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST), EnumDirection.WEST, VoxelShapes.b(BigDripleafStemBlock.WEST_SHAPE, BigDripleafBlock.STEM_SLICER, OperatorBoolean.ONLY_FIRST));
    private final Map<IBlockData, VoxelShape> shapesCache;

    protected BigDripleafBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BigDripleafBlock.WATERLOGGED, false)).set(BigDripleafBlock.FACING, EnumDirection.NORTH)).set(BigDripleafBlock.TILT, Tilt.NONE));
        this.shapesCache = this.a(BigDripleafBlock::h);
    }

    private static VoxelShape h(IBlockData iblockdata) {
        return VoxelShapes.a((VoxelShape) BigDripleafBlock.LEAF_SHAPES.get(iblockdata.get(BigDripleafBlock.TILT)), (VoxelShape) BigDripleafBlock.STEM_SHAPES.get(iblockdata.get(BigDripleafBlock.FACING)));
    }

    public static void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, EnumDirection enumdirection) {
        int i = MathHelper.nextInt(random, 2, 5);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        int j = 0;

        while (j < i && a((LevelHeightAccessor) generatoraccess, (BlockPosition) blockposition_mutableblockposition, generatoraccess.getType(blockposition_mutableblockposition))) {
            ++j;
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

        int k = blockposition.getY() + j - 1;

        blockposition_mutableblockposition.t(blockposition.getY());

        while (blockposition_mutableblockposition.getY() < k) {
            BigDripleafStemBlock.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, generatoraccess.getFluid(blockposition_mutableblockposition), enumdirection);
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

        a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, generatoraccess.getFluid(blockposition_mutableblockposition), enumdirection);
    }

    private static boolean n(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a(Blocks.WATER) || iblockdata.a(Blocks.SMALL_DRIPLEAF);
    }

    protected static boolean a(LevelHeightAccessor levelheightaccessor, BlockPosition blockposition, IBlockData iblockdata) {
        return !levelheightaccessor.isOutsideWorld(blockposition) && n(iblockdata);
    }

    protected static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid, EnumDirection enumdirection) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.BIG_DRIPLEAF.getBlockData().set(BigDripleafBlock.WATERLOGGED, fluid.a((FluidType) FluidTypes.WATER))).set(BigDripleafBlock.FACING, enumdirection);

        return generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        this.a(iblockdata, world, movingobjectpositionblock.getBlockPosition(), Tilt.FULL, SoundEffects.BIG_DRIPLEAF_TILT_DOWN);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BigDripleafBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        return iblockdata1.a(Blocks.BIG_DRIPLEAF_STEM) || iblockdata1.a((Block) this) || iblockdata1.d(iworldreader, blockposition1, EnumDirection.UP);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            if ((Boolean) iblockdata.get(BigDripleafBlock.WATERLOGGED)) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return enumdirection == EnumDirection.UP && iblockdata1.a((Block) this) ? Blocks.BIG_DRIPLEAF_STEM.l(iblockdata) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.up());

        return n(iblockdata1);
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = worldserver.getType(blockposition1);

        if (a((LevelHeightAccessor) worldserver, blockposition1, iblockdata1)) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BigDripleafBlock.FACING);

            BigDripleafStemBlock.a((GeneratorAccess) worldserver, blockposition, iblockdata.getFluid(), enumdirection);
            a((GeneratorAccess) worldserver, blockposition1, iblockdata1.getFluid(), enumdirection);
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide) {
            if (iblockdata.get(BigDripleafBlock.TILT) == Tilt.NONE && a(blockposition, entity) && !world.isBlockIndirectlyPowered(blockposition)) {
                this.a(iblockdata, world, blockposition, Tilt.UNSTABLE, (SoundEffect) null);
            }

        }
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.isBlockIndirectlyPowered(blockposition)) {
            d(iblockdata, worldserver, blockposition);
        } else {
            Tilt tilt = (Tilt) iblockdata.get(BigDripleafBlock.TILT);

            if (tilt == Tilt.UNSTABLE) {
                this.a(iblockdata, (World) worldserver, blockposition, Tilt.PARTIAL, SoundEffects.BIG_DRIPLEAF_TILT_DOWN);
            } else if (tilt == Tilt.PARTIAL) {
                this.a(iblockdata, (World) worldserver, blockposition, Tilt.FULL, SoundEffects.BIG_DRIPLEAF_TILT_DOWN);
            } else if (tilt == Tilt.FULL) {
                d(iblockdata, worldserver, blockposition);
            }

        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world.isBlockIndirectlyPowered(blockposition)) {
            d(iblockdata, world, blockposition);
        }

    }

    private static void a(World world, BlockPosition blockposition, SoundEffect soundeffect) {
        float f = MathHelper.b(world.random, 0.8F, 1.2F);

        world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, f);
    }

    private static boolean a(BlockPosition blockposition, Entity entity) {
        return entity.isOnGround() && entity.getPositionVector().y > (double) ((float) blockposition.getY() + 0.6875F);
    }

    private void a(IBlockData iblockdata, World world, BlockPosition blockposition, Tilt tilt, @Nullable SoundEffect soundeffect) {
        a(iblockdata, world, blockposition, tilt);
        if (soundeffect != null) {
            a(world, blockposition, soundeffect);
        }

        int i = BigDripleafBlock.DELAY_UNTIL_NEXT_TILT_STATE.getInt(tilt);

        if (i != -1) {
            world.getBlockTickList().a(blockposition, this, i);
        }

    }

    private static void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        a(iblockdata, world, blockposition, Tilt.NONE);
        if (iblockdata.get(BigDripleafBlock.TILT) != Tilt.NONE) {
            a(world, blockposition, SoundEffects.BIG_DRIPLEAF_TILT_UP);
        }

    }

    private static void a(IBlockData iblockdata, World world, BlockPosition blockposition, Tilt tilt) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BigDripleafBlock.TILT, tilt), 2);
        if (tilt.a()) {
            world.a(GameEvent.BLOCK_CHANGE, blockposition);
        }

    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BigDripleafBlock.LEAF_SHAPES.get(iblockdata.get(BigDripleafBlock.TILT));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapesCache.get(iblockdata);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().down());
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        boolean flag = iblockdata.a(Blocks.BIG_DRIPLEAF) || iblockdata.a(Blocks.BIG_DRIPLEAF_STEM);

        return (IBlockData) ((IBlockData) this.getBlockData().set(BigDripleafBlock.WATERLOGGED, fluid.a((FluidType) FluidTypes.WATER))).set(BigDripleafBlock.FACING, flag ? (EnumDirection) iblockdata.get(BigDripleafBlock.FACING) : blockactioncontext.g().opposite());
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BigDripleafBlock.WATERLOGGED, BigDripleafBlock.FACING, BigDripleafBlock.TILT);
    }
}
