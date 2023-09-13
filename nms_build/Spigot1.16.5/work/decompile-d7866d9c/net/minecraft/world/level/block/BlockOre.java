package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockOre extends Block {

    public BlockOre(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    protected int a(Random random) {
        return this == Blocks.COAL_ORE ? MathHelper.nextInt(random, 0, 2) : (this == Blocks.DIAMOND_ORE ? MathHelper.nextInt(random, 3, 7) : (this == Blocks.EMERALD_ORE ? MathHelper.nextInt(random, 3, 7) : (this == Blocks.LAPIS_ORE ? MathHelper.nextInt(random, 2, 5) : (this == Blocks.NETHER_QUARTZ_ORE ? MathHelper.nextInt(random, 2, 5) : (this == Blocks.NETHER_GOLD_ORE ? MathHelper.nextInt(random, 0, 1) : 0)))));
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, worldserver, blockposition, itemstack);
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            int i = this.a(worldserver.random);

            if (i > 0) {
                this.dropExperience(worldserver, blockposition, i);
            }
        }

    }
}
