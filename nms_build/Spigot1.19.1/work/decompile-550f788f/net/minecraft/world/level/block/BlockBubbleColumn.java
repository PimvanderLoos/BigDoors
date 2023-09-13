package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockBubbleColumn extends Block implements IFluidSource {

    public static final BlockStateBoolean DRAG_DOWN = BlockProperties.DRAG;
    private static final int CHECK_PERIOD = 5;

    public BlockBubbleColumn(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockBubbleColumn.DRAG_DOWN, true));
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        IBlockData iblockdata1 = world.getBlockState(blockposition.above());

        if (iblockdata1.isAir()) {
            entity.onAboveBubbleCol((Boolean) iblockdata.getValue(BlockBubbleColumn.DRAG_DOWN));
            if (!world.isClientSide) {
                WorldServer worldserver = (WorldServer) world;

                for (int i = 0; i < 2; ++i) {
                    worldserver.sendParticles(Particles.SPLASH, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    worldserver.sendParticles(Particles.BUBBLE, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else {
            entity.onInsideBubbleColumn((Boolean) iblockdata.getValue(BlockBubbleColumn.DRAG_DOWN));
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        updateColumn(worldserver, blockposition, iblockdata, worldserver.getBlockState(blockposition.below()));
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return FluidTypes.WATER.getSource(false);
    }

    public static void updateColumn(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        updateColumn(generatoraccess, blockposition, generatoraccess.getBlockState(blockposition), iblockdata);
    }

    public static void updateColumn(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        if (canExistIn(iblockdata)) {
            IBlockData iblockdata2 = getColumnState(iblockdata1);

            generatoraccess.setBlock(blockposition, iblockdata2, 2);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable().move(EnumDirection.UP);

            while (canExistIn(generatoraccess.getBlockState(blockposition_mutableblockposition))) {
                if (!generatoraccess.setBlock(blockposition_mutableblockposition, iblockdata2, 2)) {
                    return;
                }

                blockposition_mutableblockposition.move(EnumDirection.UP);
            }

        }
    }

    private static boolean canExistIn(IBlockData iblockdata) {
        return iblockdata.is(Blocks.BUBBLE_COLUMN) || iblockdata.is(Blocks.WATER) && iblockdata.getFluidState().getAmount() >= 8 && iblockdata.getFluidState().isSource();
    }

    private static IBlockData getColumnState(IBlockData iblockdata) {
        return iblockdata.is(Blocks.BUBBLE_COLUMN) ? iblockdata : (iblockdata.is(Blocks.SOUL_SAND) ? (IBlockData) Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BlockBubbleColumn.DRAG_DOWN, false) : (iblockdata.is(Blocks.MAGMA_BLOCK) ? (IBlockData) Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BlockBubbleColumn.DRAG_DOWN, true) : Blocks.WATER.defaultBlockState()));
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        if ((Boolean) iblockdata.getValue(BlockBubbleColumn.DRAG_DOWN)) {
            world.addAlwaysVisibleParticle(Particles.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
            if (randomsource.nextInt(200) == 0) {
                world.playLocalSound(d0, d1, d2, SoundEffects.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + randomsource.nextFloat() * 0.2F, 0.9F + randomsource.nextFloat() * 0.15F, false);
            }
        } else {
            world.addAlwaysVisibleParticle(Particles.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
            world.addAlwaysVisibleParticle(Particles.BUBBLE_COLUMN_UP, d0 + (double) randomsource.nextFloat(), d1 + (double) randomsource.nextFloat(), d2 + (double) randomsource.nextFloat(), 0.0D, 0.04D, 0.0D);
            if (randomsource.nextInt(200) == 0) {
                world.playLocalSound(d0, d1, d2, SoundEffects.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + randomsource.nextFloat() * 0.2F, 0.9F + randomsource.nextFloat() * 0.15F, false);
            }
        }

    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        if (!iblockdata.canSurvive(generatoraccess, blockposition) || enumdirection == EnumDirection.DOWN || enumdirection == EnumDirection.UP && !iblockdata1.is(Blocks.BUBBLE_COLUMN) && canExistIn(iblockdata1)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 5);
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition.below());

        return iblockdata1.is(Blocks.BUBBLE_COLUMN) || iblockdata1.is(Blocks.MAGMA_BLOCK) || iblockdata1.is(Blocks.SOUL_SAND);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.empty();
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockBubbleColumn.DRAG_DOWN);
    }

    @Override
    public ItemStack pickupBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        generatoraccess.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 11);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    public Optional<SoundEffect> getPickupSound() {
        return FluidTypes.WATER.getPickupSound();
    }
}
