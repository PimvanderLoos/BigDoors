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
import net.minecraft.world.level.IBlockAccess;
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
    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            if (world.getDimensionManager().isNether()) {
                world.a(blockposition, false);
                return;
            }

            Material material = world.getType(blockposition.down()).getMaterial();

            if (material.isSolid() || material.isLiquid()) {
                world.setTypeUpdate(blockposition, Blocks.WATER.getBlockData());
            }
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11 - iblockdata.b((IBlockAccess) worldserver, blockposition)) {
            this.melt(iblockdata, worldserver, blockposition);
        }

    }

    protected void melt(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world.getDimensionManager().isNether()) {
            world.a(blockposition, false);
        } else {
            world.setTypeUpdate(blockposition, Blocks.WATER.getBlockData());
            world.a(blockposition, Blocks.WATER, blockposition);
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }
}
