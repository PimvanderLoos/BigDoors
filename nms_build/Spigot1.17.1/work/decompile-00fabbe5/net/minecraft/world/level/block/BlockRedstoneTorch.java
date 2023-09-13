package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public class BlockRedstoneTorch extends BlockTorch {

    public static final BlockStateBoolean LIT = BlockProperties.LIT;
    private static final Map<IBlockAccess, List<BlockRedstoneTorch.RedstoneUpdateInfo>> RECENT_TOGGLES = new WeakHashMap();
    public static final int RECENT_TOGGLE_TIMER = 60;
    public static final int MAX_RECENT_TOGGLES = 8;
    public static final int RESTART_DELAY = 160;
    private static final int TOGGLE_DELAY = 2;

    protected BlockRedstoneTorch(BlockBase.Info blockbase_info) {
        super(blockbase_info, ParticleParamRedstone.REDSTONE);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockRedstoneTorch.LIT, true));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            world.applyPhysics(blockposition.shift(enumdirection), this);
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

        }
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockRedstoneTorch.LIT) && EnumDirection.UP != enumdirection ? 15 : 0;
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return world.isBlockFacePowered(blockposition.down(), EnumDirection.DOWN);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        boolean flag = this.a((World) worldserver, blockposition, iblockdata);
        List list = (List) BlockRedstoneTorch.RECENT_TOGGLES.get(worldserver);

        while (list != null && !list.isEmpty() && worldserver.getTime() - ((BlockRedstoneTorch.RedstoneUpdateInfo) list.get(0)).when > 60L) {
            list.remove(0);
        }

        if ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)) {
            if (flag) {
                worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneTorch.LIT, false), 3);
                if (a(worldserver, blockposition, true)) {
                    worldserver.triggerEffect(1502, blockposition, 0);
                    worldserver.getBlockTickList().a(blockposition, worldserver.getType(blockposition).getBlock(), 160);
                }
            }
        } else if (!flag && !a(worldserver, blockposition, false)) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneTorch.LIT, true), 3);
        }

    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT) == this.a(world, blockposition, iblockdata) && !world.getBlockTickList().b(blockposition, this)) {
            world.getBlockTickList().a(blockposition, this, 2);
        }

    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? iblockdata.b(iblockaccess, blockposition, enumdirection) : 0;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)) {
            double d0 = (double) blockposition.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double) blockposition.getY() + 0.7D + (random.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double) blockposition.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;

            world.addParticle(this.flameParticle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneTorch.LIT);
    }

    private static boolean a(World world, BlockPosition blockposition, boolean flag) {
        List<BlockRedstoneTorch.RedstoneUpdateInfo> list = (List) BlockRedstoneTorch.RECENT_TOGGLES.computeIfAbsent(world, (iblockaccess) -> {
            return Lists.newArrayList();
        });

        if (flag) {
            list.add(new BlockRedstoneTorch.RedstoneUpdateInfo(blockposition.immutableCopy(), world.getTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j) {
            BlockRedstoneTorch.RedstoneUpdateInfo blockredstonetorch_redstoneupdateinfo = (BlockRedstoneTorch.RedstoneUpdateInfo) list.get(j);

            if (blockredstonetorch_redstoneupdateinfo.pos.equals(blockposition)) {
                ++i;
                if (i >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class RedstoneUpdateInfo {

        final BlockPosition pos;
        final long when;

        public RedstoneUpdateInfo(BlockPosition blockposition, long i) {
            this.pos = blockposition;
            this.when = i;
        }
    }
}
