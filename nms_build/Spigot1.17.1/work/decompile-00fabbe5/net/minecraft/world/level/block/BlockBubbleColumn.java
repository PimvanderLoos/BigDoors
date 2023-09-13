package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
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
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockBubbleColumn extends Block implements IFluidSource {

    public static final BlockStateBoolean DRAG_DOWN = BlockProperties.DRAG;
    private static final int CHECK_PERIOD = 5;

    public BlockBubbleColumn(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockBubbleColumn.DRAG_DOWN, true));
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        IBlockData iblockdata1 = world.getType(blockposition.up());

        if (iblockdata1.isAir()) {
            entity.k((Boolean) iblockdata.get(BlockBubbleColumn.DRAG_DOWN));
            if (!world.isClientSide) {
                WorldServer worldserver = (WorldServer) world;

                for (int i = 0; i < 2; ++i) {
                    worldserver.a(Particles.SPLASH, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    worldserver.a(Particles.BUBBLE, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else {
            entity.l((Boolean) iblockdata.get(BlockBubbleColumn.DRAG_DOWN));
        }

    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        a((GeneratorAccess) worldserver, blockposition, iblockdata, worldserver.getType(blockposition.down()));
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    public static void b(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        a(generatoraccess, blockposition, generatoraccess.getType(blockposition), iblockdata);
    }

    public static void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        if (h(iblockdata)) {
            IBlockData iblockdata2 = n(iblockdata1);

            generatoraccess.setTypeAndData(blockposition, iblockdata2, 2);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i().c(EnumDirection.UP);

            while (h(generatoraccess.getType(blockposition_mutableblockposition))) {
                if (!generatoraccess.setTypeAndData(blockposition_mutableblockposition, iblockdata2, 2)) {
                    return;
                }

                blockposition_mutableblockposition.c(EnumDirection.UP);
            }

        }
    }

    private static boolean h(IBlockData iblockdata) {
        return iblockdata.a(Blocks.BUBBLE_COLUMN) || iblockdata.a(Blocks.WATER) && iblockdata.getFluid().e() >= 8 && iblockdata.getFluid().isSource();
    }

    private static IBlockData n(IBlockData iblockdata) {
        return iblockdata.a(Blocks.BUBBLE_COLUMN) ? iblockdata : (iblockdata.a(Blocks.SOUL_SAND) ? (IBlockData) Blocks.BUBBLE_COLUMN.getBlockData().set(BlockBubbleColumn.DRAG_DOWN, false) : (iblockdata.a(Blocks.MAGMA_BLOCK) ? (IBlockData) Blocks.BUBBLE_COLUMN.getBlockData().set(BlockBubbleColumn.DRAG_DOWN, true) : Blocks.WATER.getBlockData()));
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        if ((Boolean) iblockdata.get(BlockBubbleColumn.DRAG_DOWN)) {
            world.b(Particles.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
            if (random.nextInt(200) == 0) {
                world.a(d0, d1, d2, SoundEffects.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        } else {
            world.b(Particles.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
            world.b(Particles.BUBBLE_COLUMN_UP, d0 + (double) random.nextFloat(), d1 + (double) random.nextFloat(), d2 + (double) random.nextFloat(), 0.0D, 0.04D, 0.0D);
            if (random.nextInt(200) == 0) {
                world.a(d0, d1, d2, SoundEffects.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        if (!iblockdata.canPlace(generatoraccess, blockposition) || enumdirection == EnumDirection.DOWN || enumdirection == EnumDirection.UP && !iblockdata1.a(Blocks.BUBBLE_COLUMN) && h(iblockdata1)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 5);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

        return iblockdata1.a(Blocks.BUBBLE_COLUMN) || iblockdata1.a(Blocks.MAGMA_BLOCK) || iblockdata1.a(Blocks.SOUL_SAND);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBubbleColumn.DRAG_DOWN);
    }

    @Override
    public ItemStack removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    public Optional<SoundEffect> V_() {
        return FluidTypes.WATER.k();
    }
}
