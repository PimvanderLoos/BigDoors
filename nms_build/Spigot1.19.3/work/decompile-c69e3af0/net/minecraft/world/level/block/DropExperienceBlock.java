package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class DropExperienceBlock extends Block {

    private final IntProvider xpRange;

    public DropExperienceBlock(BlockBase.Info blockbase_info) {
        this(blockbase_info, ConstantInt.of(0));
    }

    public DropExperienceBlock(BlockBase.Info blockbase_info, IntProvider intprovider) {
        super(blockbase_info);
        this.xpRange = intprovider;
    }

    @Override
    public void spawnAfterBreak(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack, boolean flag) {
        super.spawnAfterBreak(iblockdata, worldserver, blockposition, itemstack, flag);
        if (flag) {
            this.tryDropExperience(worldserver, blockposition, itemstack, this.xpRange);
        }

    }
}
