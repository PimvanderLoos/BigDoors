package net.minecraft.world.item;

import java.util.Iterator;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;

public class ItemTippedArrow extends ItemArrow {

    public ItemTippedArrow(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack createItemStack() {
        return PotionUtil.a(super.createItemStack(), Potions.POISON);
    }

    @Override
    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            Iterator iterator = IRegistry.POTION.iterator();

            while (iterator.hasNext()) {
                PotionRegistry potionregistry = (PotionRegistry) iterator.next();

                if (!potionregistry.a().isEmpty()) {
                    nonnulllist.add(PotionUtil.a(new ItemStack(this), potionregistry));
                }
            }
        }

    }

    @Override
    public String f(ItemStack itemstack) {
        return PotionUtil.d(itemstack).b(this.getName() + ".effect.");
    }
}
