package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;

public class ItemAir extends Item {

    private final Block block;

    public ItemAir(Block block, Item.Info item_info) {
        super(item_info);
        this.block = block;
    }

    @Override
    public String getName() {
        return this.block.h();
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        super.a(itemstack, world, list, tooltipflag);
        this.block.a(itemstack, (IBlockAccess) world, list, tooltipflag);
    }
}
