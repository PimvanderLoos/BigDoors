package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.FluidTypes;

public class BuddingAmethystBlock extends AmethystBlock {

    public static final int GROWTH_CHANCE = 5;
    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();

    public BuddingAmethystBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (random.nextInt(5) == 0) {
            EnumDirection enumdirection = BuddingAmethystBlock.DIRECTIONS[random.nextInt(BuddingAmethystBlock.DIRECTIONS.length)];
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData iblockdata1 = worldserver.getType(blockposition1);
            Block block = null;

            if (g(iblockdata1)) {
                block = Blocks.SMALL_AMETHYST_BUD;
            } else if (iblockdata1.a(Blocks.SMALL_AMETHYST_BUD) && iblockdata1.get(AmethystClusterBlock.FACING) == enumdirection) {
                block = Blocks.MEDIUM_AMETHYST_BUD;
            } else if (iblockdata1.a(Blocks.MEDIUM_AMETHYST_BUD) && iblockdata1.get(AmethystClusterBlock.FACING) == enumdirection) {
                block = Blocks.LARGE_AMETHYST_BUD;
            } else if (iblockdata1.a(Blocks.LARGE_AMETHYST_BUD) && iblockdata1.get(AmethystClusterBlock.FACING) == enumdirection) {
                block = Blocks.AMETHYST_CLUSTER;
            }

            if (block != null) {
                IBlockData iblockdata2 = (IBlockData) ((IBlockData) block.getBlockData().set(AmethystClusterBlock.FACING, enumdirection)).set(AmethystClusterBlock.WATERLOGGED, iblockdata1.getFluid().getType() == FluidTypes.WATER);

                worldserver.setTypeUpdate(blockposition1, iblockdata2);
            }

        }
    }

    public static boolean g(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a(Blocks.WATER) && iblockdata.getFluid().e() == 8;
    }
}
