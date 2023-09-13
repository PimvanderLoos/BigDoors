package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.alchemy.PotionRegistry;
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
    public void fillItemCategory(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.allowdedIn(creativemodetab)) {
            Iterator iterator = IRegistry.POTION.iterator();

            while (iterator.hasNext()) {
                PotionRegistry potionregistry = (PotionRegistry) iterator.next();

                if (!potionregistry.getEffects().isEmpty()) {
                    nonnulllist.add(PotionUtil.setPotion(new ItemStack(this), potionregistry));
                }
            }
        }

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
