package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockRedstoneTorch extends BlockTorch {

    public static final BlockStateBoolean LIT = BlockProperties.o;
    private static final Map<IBlockAccess, List<BlockRedstoneTorch.RedstoneUpdateInfo>> b = Maps.newHashMap();

    protected BlockRedstoneTorch(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRedstoneTorch.LIT, Boolean.valueOf(true)));
    }

    public int a(IWorldReader iworldreader) {
        return 2;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            world.applyPhysics(blockposition.shift(enumdirection), this);
        }

    }

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

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)).booleanValue() && EnumDirection.UP != enumdirection ? 15 : 0;
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return world.isBlockFacePowered(blockposition.down(), EnumDirection.DOWN);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        a(iblockdata, world, blockposition, random, this.a(world, blockposition, iblockdata));
    }

    public static void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random, boolean flag) {
        List list = (List) BlockRedstoneTorch.b.get(world);

        while (list != null && !list.isEmpty() && world.getTime() - ((BlockRedstoneTorch.RedstoneUpdateInfo) list.get(0)).b > 60L) {
            list.remove(0);
        }

        if (((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)).booleanValue()) {
            if (flag) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneTorch.LIT, Boolean.valueOf(false)), 3);
                if (a(world, blockposition, true)) {
                    world.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                    for (int i = 0; i < 5; ++i) {
                        double d0 = (double) blockposition.getX() + random.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double) blockposition.getY() + random.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double) blockposition.getZ() + random.nextDouble() * 0.6D + 0.2D;

                        world.addParticle(Particles.M, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }

                    world.I().a(blockposition, world.getType(blockposition).getBlock(), 160);
                }
            }
        } else if (!flag && !a(world, blockposition, false)) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneTorch.LIT, Boolean.valueOf(true)), 3);
        }

    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)).booleanValue() == this.a(world, blockposition, iblockdata) && !world.I().b(blockposition, this)) {
            world.I().a(blockposition, this, this.a((IWorldReader) world));
        }

    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? iblockdata.a(iblockaccess, blockposition, enumdirection) : 0;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public int m(IBlockData iblockdata) {
        return ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)).booleanValue() ? super.m(iblockdata) : 0;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(new IBlockState[] { BlockRedstoneTorch.LIT});
    }

    private static boolean a(World world, BlockPosition blockposition, boolean flag) {
        Object object = (List) BlockRedstoneTorch.b.get(world);

        if (object == null) {
            object = Lists.newArrayList();
            BlockRedstoneTorch.b.put(world, object);
        }

        if (flag) {
            ((List) object).add(new BlockRedstoneTorch.RedstoneUpdateInfo(blockposition.h(), world.getTime()));
        }

        int i = 0;

        for (int j = 0; j < ((List) object).size(); ++j) {
            BlockRedstoneTorch.RedstoneUpdateInfo blockredstonetorch_redstoneupdateinfo = (BlockRedstoneTorch.RedstoneUpdateInfo) ((List) object).get(j);

            if (blockredstonetorch_redstoneupdateinfo.a.equals(blockposition)) {
                ++i;
                if (i >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class RedstoneUpdateInfo {

        private final BlockPosition a;
        private final long b;

        public RedstoneUpdateInfo(BlockPosition blockposition, long i) {
            this.a = blockposition;
            this.b = i;
        }
    }
}
