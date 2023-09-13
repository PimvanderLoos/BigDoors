package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
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

    public static final BlockStateBoolean a = BlockProperties.e;

    public BlockBubbleColumn(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBubbleColumn.a, true));
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        IBlockData iblockdata1 = world.getType(blockposition.up());

        if (iblockdata1.isAir()) {
            entity.k((Boolean) iblockdata.get(BlockBubbleColumn.a));
            if (!world.isClientSide) {
                WorldServer worldserver = (WorldServer) world;

                for (int i = 0; i < 2; ++i) {
                    worldserver.a(Particles.SPLASH, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    worldserver.a(Particles.BUBBLE, (double) blockposition.getX() + world.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else {
            entity.l((Boolean) iblockdata.get(BlockBubbleColumn.a));
        }

    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        a(world, blockposition.up(), a((IBlockAccess) world, blockposition.down()));
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        a(worldserver, blockposition.up(), a((IBlockAccess) worldserver, blockposition));
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    public static void a(GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        if (a(generatoraccess, blockposition)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) Blocks.BUBBLE_COLUMN.getBlockData().set(BlockBubbleColumn.a, flag), 2);
        }

    }

    public static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        Fluid fluid = generatoraccess.getFluid(blockposition);

        return generatoraccess.getType(blockposition).a(Blocks.WATER) && fluid.e() >= 8 && fluid.isSource();
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        return iblockdata.a(Blocks.BUBBLE_COLUMN) ? (Boolean) iblockdata.get(BlockBubbleColumn.a) : !iblockdata.a(Blocks.SOUL_SAND);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.WATER.getBlockData();
        } else {
            if (enumdirection == EnumDirection.DOWN) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) Blocks.BUBBLE_COLUMN.getBlockData().set(BlockBubbleColumn.a, a((IBlockAccess) generatoraccess, blockposition1)), 2);
            } else if (enumdirection == EnumDirection.UP && !iblockdata1.a(Blocks.BUBBLE_COLUMN) && a(generatoraccess, blockposition1)) {
                generatoraccess.getBlockTickList().a(blockposition, this, 5);
            }

            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

        return iblockdata1.a(Blocks.BUBBLE_COLUMN) || iblockdata1.a(Blocks.MAGMA_BLOCK) || iblockdata1.a(Blocks.SOUL_SAND);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBubbleColumn.a);
    }

    @Override
    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
        return FluidTypes.WATER;
    }
}
