package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityLiving;

public abstract class ItemProjectileWeapon extends Item {

    public static final Predicate<ItemStack> a = (itemstack) -> {
        return itemstack.getItem().a((Tag) TagsItem.ARROWS);
    };
    public static final Predicate<ItemStack> b = ItemProjectileWeapon.a.or((itemstack) -> {
        return itemstack.getItem() == Items.FIREWORK_ROCKET;
    });

    public ItemProjectileWeapon(Item.Info item_info) {
        super(item_info);
    }

    public Predicate<ItemStack> e() {
        return this.b();
    }

    public abstract Predicate<ItemStack> b();

    public static ItemStack a(EntityLiving entityliving, Predicate<ItemStack> predicate) {
        return predicate.test(entityliving.b(EnumHand.OFF_HAND)) ? entityliving.b(EnumHand.OFF_HAND) : (predicate.test(entityliving.b(EnumHand.MAIN_HAND)) ? entityliving.b(EnumHand.MAIN_HAND) : ItemStack.b);
    }

    @Override
    public int c() {
        return 1;
    }

    public abstract int d();
}
