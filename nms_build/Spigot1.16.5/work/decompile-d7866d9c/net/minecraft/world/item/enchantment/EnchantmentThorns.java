package net.minecraft.world.item.enchantment;

import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;

public class EnchantmentThorns extends Enchantment {

    public EnchantmentThorns(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_CHEST, aenumitemslot);
    }

    @Override
    public int a(int i) {
        return 10 + 20 * (i - 1);
    }

    @Override
    public int b(int i) {
        return super.a(i) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemArmor ? true : super.canEnchant(itemstack);
    }

    @Override
    public void b(EntityLiving entityliving, Entity entity, int i) {
        Random random = entityliving.getRandom();
        Entry<EnumItemSlot, ItemStack> entry = EnchantmentManager.b(Enchantments.THORNS, entityliving);

        if (a(i, random)) {
            if (entity != null) {
                entity.damageEntity(DamageSource.a(entityliving), (float) b(i, random));
            }

            if (entry != null) {
                ((ItemStack) entry.getValue()).damage(2, entityliving, (entityliving1) -> {
                    entityliving1.broadcastItemBreak((EnumItemSlot) entry.getKey());
                });
            }
        }

    }

    public static boolean a(int i, Random random) {
        return i <= 0 ? false : random.nextFloat() < 0.15F * (float) i;
    }

    public static int b(int i, Random random) {
        return i > 10 ? i - 10 : 1 + random.nextInt(4);
    }
}
