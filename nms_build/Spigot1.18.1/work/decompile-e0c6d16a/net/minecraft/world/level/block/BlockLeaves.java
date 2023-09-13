package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockLeaves extends Block {

    public static final int DECAY_DISTANCE = 7;
    public static final BlockStateInteger DISTANCE = BlockProperties.DISTANCE;
    public static final BlockStateBoolean PERSISTENT = BlockProperties.PERSISTENT;
    private static final int TICK_DELAY = 1;

    public BlockLeaves(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockLeaves.DISTANCE, 7)).setValue(BlockLeaves.PERSISTENT, false));
    }

    @Override
    public VoxelShape getBlockSupportShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(BlockLeaves.DISTANCE) == 7 && !(Boolean) iblockdata.getValue(BlockLeaves.PERSISTENT);
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!(Boolean) iblockdata.getValue(BlockLeaves.PERSISTENT) && (Integer) iblockdata.getValue(BlockLeaves.DISTANCE) == 7) {
            dropResources(iblockdata, worldserver, blockposition);
            worldserver.removeBlock(blockposition, false);
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        worldserver.setBlock(blockposition, updateDistance(iblockdata, worldserver, blockposition), 3);
    }

    @Override
    public int getLightBlock(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 1;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        int i = getDistanceAt(iblockdata1) + 1;

        if (i != 1 || (Integer) iblockdata.getValue(BlockLeaves.DISTANCE) != i) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 1);
        }

        return iblockdata;
    }

    private static IBlockData updateDistance(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = 7;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = EnumDirection.values();
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
            i = Math.min(i, getDistanceAt(generatoraccess.getBlockState(blockposition_mutableblockposition)) + 1);
            if (i == 1) {
                break;
            }
        }

        return (IBlockData) iblockdata.setValue(BlockLeaves.DISTANCE, i);
    }

    private static int getDistanceAt(IBlockData iblockdata) {
        return iblockdata.is((Tag) TagsBlock.LOGS) ? 0 : (iblockdata.getBlock() instanceof BlockLeaves ? (Integer) iblockdata.getValue(BlockLeaves.DISTANCE) : 7);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.isRainingAt(blockposition.above())) {
            if (random.nextInt(15) == 1) {
                BlockPosition blockposition1 = blockposition.below();
                IBlockData iblockdata1 = world.getBlockState(blockposition1);

                if (!iblockdata1.canOcclude() || !iblockdata1.isFaceSturdy(world, blockposition1, EnumDirection.UP)) {
                    double d0 = (double) blockposition.getX() + random.nextDouble();
                    double d1 = (double) blockposition.getY() - 0.05D;
                    double d2 = (double) blockposition.getZ() + random.nextDouble();

                    world.addParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockLeaves.DISTANCE, BlockLeaves.PERSISTENT);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return updateDistance((IBlockData) this.defaultBlockState().setValue(BlockLeaves.PERSISTENT, true), blockactioncontext.getLevel(), blockactioncontext.getClickedPos());
    }
}
