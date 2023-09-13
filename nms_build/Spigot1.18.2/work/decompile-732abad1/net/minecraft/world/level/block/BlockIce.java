package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Material;

public class BlockIce extends BlockHalfTransparent {

    public BlockIce(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void playerDestroy(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.playerDestroy(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        if (EnchantmentManager.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            if (world.dimensionType().ultraWarm()) {
                world.removeBlock(blockposition, false);
                return;
            }

            Material material = world.getBlockState(blockposition.below()).getMaterial();

            if (material.blocksMotion() || material.isLiquid()) {
                world.setBlockAndUpdate(blockposition, Blocks.WATER.defaultBlockState());
            }
        }

    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11 - iblockdata.getLightBlock(worldserver, blockposition)) {
            this.melt(iblockdata, worldserver, blockposition);
        }

    }

    protected void melt(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world.dimensionType().ultraWarm()) {
            world.removeBlock(blockposition, false);
        } else {
            world.setBlockAndUpdate(blockposition, Blocks.WATER.defaultBlockState());
            world.neighborChanged(blockposition, Blocks.WATER, blockposition);
        }
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }
}
