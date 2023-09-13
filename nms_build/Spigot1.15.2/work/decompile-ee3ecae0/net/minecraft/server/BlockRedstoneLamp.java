package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockRedstoneLamp extends Block {

    public static final BlockStateBoolean a = BlockRedstoneTorch.LIT;

    public BlockRedstoneLamp(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) this.getBlockData().set(BlockRedstoneLamp.a, false));
    }

    @Override
    public int a(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockRedstoneLamp.a) ? super.a(iblockdata) : 0;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        super.onPlace(iblockdata, world, blockposition, iblockdata1, flag);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockRedstoneLamp.a, blockactioncontext.getWorld().isBlockIndirectlyPowered(blockactioncontext.getClickPosition()));
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            boolean flag1 = (Boolean) iblockdata.get(BlockRedstoneLamp.a);

            if (flag1 != world.isBlockIndirectlyPowered(blockposition)) {
                if (flag1) {
                    world.getBlockTickList().a(blockposition, this, 4);
                } else {
                    world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRedstoneLamp.a), 2);
                }
            }

        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockRedstoneLamp.a) && !worldserver.isBlockIndirectlyPowered(blockposition)) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRedstoneLamp.a), 2);
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneLamp.a);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return true;
    }
}
