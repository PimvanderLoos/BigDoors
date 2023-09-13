package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
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
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockLeaves.DISTANCE, 7)).set(BlockLeaves.PERSISTENT, false));
    }

    @Override
    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7 && !(Boolean) iblockdata.get(BlockLeaves.PERSISTENT);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!(Boolean) iblockdata.get(BlockLeaves.PERSISTENT) && (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7) {
            c(iblockdata, (World) worldserver, blockposition);
            worldserver.a(blockposition, false);
        }

    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        worldserver.setTypeAndData(blockposition, a(iblockdata, (GeneratorAccess) worldserver, blockposition), 3);
    }

    @Override
    public int g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 1;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        int i = h(iblockdata1) + 1;

        if (i != 1 || (Integer) iblockdata.get(BlockLeaves.DISTANCE) != i) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        return iblockdata;
    }

    private static IBlockData a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = 7;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = EnumDirection.values();
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
            i = Math.min(i, h(generatoraccess.getType(blockposition_mutableblockposition)) + 1);
            if (i == 1) {
                break;
            }
        }

        return (IBlockData) iblockdata.set(BlockLeaves.DISTANCE, i);
    }

    private static int h(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.LOGS) ? 0 : (iblockdata.getBlock() instanceof BlockLeaves ? (Integer) iblockdata.get(BlockLeaves.DISTANCE) : 7);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.isRainingAt(blockposition.up())) {
            if (random.nextInt(15) == 1) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata1 = world.getType(blockposition1);

                if (!iblockdata1.l() || !iblockdata1.d(world, blockposition1, EnumDirection.UP)) {
                    double d0 = (double) blockposition.getX() + random.nextDouble();
                    double d1 = (double) blockposition.getY() - 0.05D;
                    double d2 = (double) blockposition.getZ() + random.nextDouble();

                    world.addParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLeaves.DISTANCE, BlockLeaves.PERSISTENT);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return a((IBlockData) this.getBlockData().set(BlockLeaves.PERSISTENT, true), (GeneratorAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }
}
