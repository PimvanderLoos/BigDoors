package net.minecraft.world.item.enchantment;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;

public class EnchantmentProtection extends Enchantment {

    public final EnchantmentProtection.DamageType type;

    public EnchantmentProtection(Enchantment.Rarity enchantment_rarity, EnchantmentProtection.DamageType enchantmentprotection_damagetype, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, enchantmentprotection_damagetype == EnchantmentProtection.DamageType.FALL ? EnchantmentSlotType.ARMOR_FEET : EnchantmentSlotType.ARMOR, aenumitemslot);
        this.type = enchantmentprotection_damagetype;
    }

    @Override
    public int getMinCost(int i) {
        return this.type.getMinCost() + (i - 1) * this.type.getLevelCost();
    }

    @Override
    public int getMaxCost(int i) {
        return this.getMinCost(i) + this.type.getLevelCost();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getDamageProtection(int i, DamageSource damagesource) {
        return damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? 0 : (this.type == EnchantmentProtection.DamageType.ALL ? i : (this.type == EnchantmentProtection.DamageType.FIRE && damagesource.is(DamageTypeTags.IS_FIRE) ? i * 2 : (this.type == EnchantmentProtection.DamageType.FALL && damagesource.is(DamageTypeTags.IS_FALL) ? i * 3 : (this.type == EnchantmentProtection.DamageType.EXPLOSION && damagesource.is(DamageTypeTags.IS_EXPLOSION) ? i * 2 : (this.type == EnchantmentProtection.DamageType.PROJECTILE && damagesource.is(DamageTypeTags.IS_PROJECTILE) ? i * 2 : 0)))));
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentProtection) {
            EnchantmentProtection enchantmentprotection = (EnchantmentProtection) enchantment;

            return this.type == enchantmentprotection.type ? false : this.type == EnchantmentProtection.DamageType.FALL || enchantmentprotection.type == EnchantmentProtection.DamageType.FALL;
        } else {
            return super.checkCompatibility(enchantment);
        }
    }

    public static int getFireAfterDampener(EntityLiving entityliving, int i) {
        int j = EnchantmentManager.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, entityliving);

        if (j > 0) {
            i -= MathHelper.floor((float) i * (float) j * 0.15F);
        }

        return i;
    }

    public static double getExplosionKnockbackAfterDampener(EntityLiving entityliving, double d0) {
        int i = EnchantmentManager.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, entityliving);

        if (i > 0) {
            d0 *= MathHelper.clamp(1.0D - (double) i * 0.15D, 0.0D, 1.0D);
        }

        return d0;
    }

    public static enum DamageType {

        ALL(1, 11), FIRE(10, 8), FALL(5, 6), EXPLOSION(5, 8), PROJECTILE(3, 6);

        private final int minCost;
        private final int levelCost;

        private DamageType(int i, int j) {
            this.minCost = i;
            this.levelCost = j;
        }

        public int getMinCost() {
            return this.minCost;
        }

        public int getLevelCost() {
            return this.levelCost;
        }
    }
}
