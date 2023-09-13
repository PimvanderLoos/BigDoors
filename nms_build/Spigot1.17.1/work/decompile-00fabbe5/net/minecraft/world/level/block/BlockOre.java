package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockOre extends Block {

    private final UniformInt xpRange;

    public BlockOre(BlockBase.Info blockbase_info) {
        this(blockbase_info, UniformInt.a(0, 0));
    }

    public BlockOre(BlockBase.Info blockbase_info, UniformInt uniformint) {
        super(blockbase_info);
        this.xpRange = uniformint;
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, worldserver, blockposition, itemstack);
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            int i = this.xpRange.a(worldserver.random);

            if (i > 0) {
                this.dropExperience(worldserver, blockposition, i);
            }
        }

    }
}
