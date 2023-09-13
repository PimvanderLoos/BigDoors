package net.minecraft.world.item.enchantment;

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
    public int a(int i) {
        return this.type.a() + (i - 1) * this.type.b();
    }

    @Override
    public int b(int i) {
        return this.a(i) + this.type.b();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int a(int i, DamageSource damagesource) {
        return damagesource.ignoresInvulnerability() ? 0 : (this.type == EnchantmentProtection.DamageType.ALL ? i : (this.type == EnchantmentProtection.DamageType.FIRE && damagesource.isFire() ? i * 2 : (this.type == EnchantmentProtection.DamageType.FALL && damagesource.z() ? i * 3 : (this.type == EnchantmentProtection.DamageType.EXPLOSION && damagesource.isExplosion() ? i * 2 : (this.type == EnchantmentProtection.DamageType.PROJECTILE && damagesource.b() ? i * 2 : 0)))));
    }

    @Override
    public boolean a(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentProtection) {
            EnchantmentProtection enchantmentprotection = (EnchantmentProtection) enchantment;

            return this.type == enchantmentprotection.type ? false : this.type == EnchantmentProtection.DamageType.FALL || enchantmentprotection.type == EnchantmentProtection.DamageType.FALL;
        } else {
            return super.a(enchantment);
        }
    }

    public static int a(EntityLiving entityliving, int i) {
        int j = EnchantmentManager.a(Enchantments.FIRE_PROTECTION, entityliving);

        if (j > 0) {
            i -= MathHelper.d((float) i * (float) j * 0.15F);
        }

        return i;
    }

    public static double a(EntityLiving entityliving, double d0) {
        int i = EnchantmentManager.a(Enchantments.BLAST_PROTECTION, entityliving);

        if (i > 0) {
            d0 -= (double) MathHelper.floor(d0 * (double) ((float) i * 0.15F));
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

        public int a() {
            return this.minCost;
        }

        public int b() {
            return this.levelCost;
        }
    }
}
