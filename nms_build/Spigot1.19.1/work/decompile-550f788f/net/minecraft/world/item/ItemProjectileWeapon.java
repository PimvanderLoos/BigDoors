package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityLiving;

public abstract class ItemProjectileWeapon extends Item {

    public static final Predicate<ItemStack> ARROW_ONLY = (itemstack) -> {
        return itemstack.is(TagsItem.ARROWS);
    };
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ItemProjectileWeapon.ARROW_ONLY.or((itemstack) -> {
        return itemstack.is(Items.FIREWORK_ROCKET);
    });

    public ItemProjectileWeapon(Item.Info item_info) {
        super(item_info);
    }

    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }

    public abstract Predicate<ItemStack> getAllSupportedProjectiles();

    public static ItemStack getHeldProjectile(EntityLiving entityliving, Predicate<ItemStack> predicate) {
        return predicate.test(entityliving.getItemInHand(EnumHand.OFF_HAND)) ? entityliving.getItemInHand(EnumHand.OFF_HAND) : (predicate.test(entityliving.getItemInHand(EnumHand.MAIN_HAND)) ? entityliving.getItemInHand(EnumHand.MAIN_HAND) : ItemStack.EMPTY);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    public abstract int getDefaultProjectileRange();
}
