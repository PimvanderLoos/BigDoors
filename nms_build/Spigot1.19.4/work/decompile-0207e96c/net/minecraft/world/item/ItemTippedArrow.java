package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;

public class ItemTippedArrow extends ItemArrow {

    public ItemTippedArrow(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtil.setPotion(super.getDefaultInstance(), Potions.POISON);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        PotionUtil.addPotionTooltip(itemstack, list, 0.125F);
    }

    @Override
    public String getDescriptionId(ItemStack itemstack) {
        return PotionUtil.getPotion(itemstack).getName(this.getDescriptionId() + ".effect.");
    }
}
