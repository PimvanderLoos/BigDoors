package net.minecraft.world.item.enchantment;

import java.util.Map.Entry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;

public class EnchantmentThorns extends Enchantment {

    private static final float CHANCE_PER_LEVEL = 0.15F;

    public EnchantmentThorns(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_CHEST, aenumitemslot);
    }

    @Override
    public int getMinCost(int i) {
        return 10 + 20 * (i - 1);
    }

    @Override
    public int getMaxCost(int i) {
        return super.getMinCost(i) + 50;
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
    public void doPostHurt(EntityLiving entityliving, Entity entity, int i) {
        RandomSource randomsource = entityliving.getRandom();
        Entry<EnumItemSlot, ItemStack> entry = EnchantmentManager.getRandomItemWith(Enchantments.THORNS, entityliving);

        if (shouldHit(i, randomsource)) {
            if (entity != null) {
                entity.hurt(entityliving.damageSources().thorns(entityliving), (float) getDamage(i, randomsource));
            }

            if (entry != null) {
                ((ItemStack) entry.getValue()).hurtAndBreak(2, entityliving, (entityliving1) -> {
                    entityliving1.broadcastBreakEvent((EnumItemSlot) entry.getKey());
                });
            }
        }

    }

    public static boolean shouldHit(int i, RandomSource randomsource) {
        return i <= 0 ? false : randomsource.nextFloat() < 0.15F * (float) i;
    }

    public static int getDamage(int i, RandomSource randomsource) {
        return i > 10 ? i - 10 : 1 + randomsource.nextInt(4);
    }
}
