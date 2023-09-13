package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;

public class BlockSponge extends Block {

    public static final int MAX_DEPTH = 6;
    public static final int MAX_COUNT = 64;

    protected BlockSponge(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            this.tryAbsorbWater(world, blockposition);
        }
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        this.tryAbsorbWater(world, blockposition);
        super.neighborChanged(iblockdata, world, blockposition, block, blockposition1, flag);
    }

    protected void tryAbsorbWater(World world, BlockPosition blockposition) {
        if (this.removeWaterBreadthFirstSearch(world, blockposition)) {
            world.setBlock(blockposition, Blocks.WET_SPONGE.defaultBlockState(), 2);
            world.levelEvent(2001, blockposition, Block.getId(Blocks.WATER.defaultBlockState()));
        }

    }

    private boolean removeWaterBreadthFirstSearch(World world, BlockPosition blockposition) {
        Queue<Tuple<BlockPosition, Integer>> queue = Lists.newLinkedList();

        queue.add(new Tuple<>(blockposition, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Tuple<BlockPosition, Integer> tuple = (Tuple) queue.poll();
            BlockPosition blockposition1 = (BlockPosition) tuple.getA();
            int j = (Integer) tuple.getB();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];
                BlockPosition blockposition2 = blockposition1.relative(enumdirection);
                IBlockData iblockdata = world.getBlockState(blockposition2);
                Fluid fluid = world.getFluidState(blockposition2);
                Material material = iblockdata.getMaterial();

                if (fluid.is((Tag) TagsFluid.WATER)) {
                    if (iblockdata.getBlock() instanceof IFluidSource && !((IFluidSource) iblockdata.getBlock()).pickupBlock(world, blockposition2, iblockdata).isEmpty()) {
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockposition2, j + 1));
                        }
                    } else if (iblockdata.getBlock() instanceof BlockFluids) {
                        world.setBlock(blockposition2, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockposition2, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        TileEntity tileentity = iblockdata.hasBlockEntity() ? world.getBlockEntity(blockposition2) : null;

                        dropResources(iblockdata, world, blockposition2, tileentity);
                        world.setBlock(blockposition2, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockposition2, j + 1));
                        }
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }
}
