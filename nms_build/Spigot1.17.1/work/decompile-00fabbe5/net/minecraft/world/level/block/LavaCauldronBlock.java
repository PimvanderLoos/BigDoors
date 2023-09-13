package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class LavaCauldronBlock extends AbstractCauldronBlock {

    public LavaCauldronBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info, CauldronInteraction.LAVA);
    }

    @Override
    protected double a(IBlockData iblockdata) {
        return 0.9375D;
    }

    @Override
    public boolean c(IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (this.a(iblockdata, blockposition, entity)) {
            entity.burnFromLava();
        }

    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return 3;
    }
}
