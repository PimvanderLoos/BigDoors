package net.minecraft.world.item.enchantment;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.item.ItemAxe;
import net.minecraft.world.item.ItemStack;

public class EnchantmentWeaponDamage extends Enchantment {

    public static final int ALL = 0;
    public static final int UNDEAD = 1;
    public static final int ARTHROPODS = 2;
    private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
    private static final int[] MIN_COST = new int[]{1, 5, 5};
    private static final int[] LEVEL_COST = new int[]{11, 8, 8};
    private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
    public final int type;

    public EnchantmentWeaponDamage(Enchantment.Rarity enchantment_rarity, int i, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.WEAPON, aenumitemslot);
        this.type = i;
    }

    @Override
    public int getMinCost(int i) {
        return EnchantmentWeaponDamage.MIN_COST[this.type] + (i - 1) * EnchantmentWeaponDamage.LEVEL_COST[this.type];
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + EnchantmentWeaponDamage.LEVEL_COST_SPAN[this.type];
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(int i, EnumMonsterType enummonstertype) {
        return this.type == 0 ? 1.0F + (float) Math.max(0, i - 1) * 0.5F : (this.type == 1 && enummonstertype == EnumMonsterType.UNDEAD ? (float) i * 2.5F : (this.type == 2 && enummonstertype == EnumMonsterType.ARTHROPOD ? (float) i * 2.5F : 0.0F));
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentWeaponDamage);
    }

    @Override
    public boolean canEnchant(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemAxe ? true : super.canEnchant(itemstack);
    }

    @Override
    public void doPostAttack(EntityLiving entityliving, Entity entity, int i) {
        if (entity instanceof EntityLiving) {
            EntityLiving entityliving1 = (EntityLiving) entity;

            if (this.type == 2 && i > 0 && entityliving1.getMobType() == EnumMonsterType.ARTHROPOD) {
                int j = 20 + entityliving.getRandom().nextInt(10 * i);

                entityliving1.addEffect(new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, j, 3));
            }
        }

    }
}
