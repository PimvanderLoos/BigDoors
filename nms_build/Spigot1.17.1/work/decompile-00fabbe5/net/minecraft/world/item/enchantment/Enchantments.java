package net.minecraft.world.item.enchantment;

import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.EnumItemSlot;

public class Enchantments {

    private static final EnumItemSlot[] ARMOR_SLOTS = new EnumItemSlot[]{EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};
    public static final Enchantment ALL_DAMAGE_PROTECTION = a("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.DamageType.ALL, Enchantments.ARMOR_SLOTS));
    public static final Enchantment FIRE_PROTECTION = a("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FIRE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment FALL_PROTECTION = a("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FALL, Enchantments.ARMOR_SLOTS));
    public static final Enchantment BLAST_PROTECTION = a("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.DamageType.EXPLOSION, Enchantments.ARMOR_SLOTS));
    public static final Enchantment PROJECTILE_PROTECTION = a("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.PROJECTILE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment RESPIRATION = a("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment AQUA_AFFINITY = a("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment THORNS = a("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment DEPTH_STRIDER = a("depth_strider", new EnchantmentDepthStrider(Enchantment.Rarity.RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment FROST_WALKER = a("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.FEET}));
    public static final Enchantment BINDING_CURSE = a("binding_curse", new EnchantmentBinding(Enchantment.Rarity.VERY_RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment SOUL_SPEED = a("soul_speed", new EnchantmentSoulSpeed(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.FEET}));
    public static final Enchantment SHARPNESS = a("sharpness", new EnchantmentWeaponDamage(Enchantment.Rarity.COMMON, 0, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment SMITE = a("smite", new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 1, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment BANE_OF_ARTHROPODS = a("bane_of_arthropods", new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 2, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment KNOCKBACK = a("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FIRE_ASPECT = a("fire_aspect", new EnchantmentFire(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment MOB_LOOTING = a("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.WEAPON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment SWEEPING_EDGE = a("sweeping", new EnchantmentSweeping(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment BLOCK_EFFICIENCY = a("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment SILK_TOUCH = a("silk_touch", new EnchantmentSilkTouch(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment UNBREAKING = a("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment BLOCK_FORTUNE = a("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.DIGGER, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment POWER_ARROWS = a("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment PUNCH_ARROWS = a("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FLAMING_ARROWS = a("flame", new EnchantmentFlameArrows(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment INFINITY_ARROWS = a("infinity", new EnchantmentInfiniteArrows(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FISHING_LUCK = a("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FISHING_SPEED = a("lure", new EnchantmentLure(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment LOYALTY = a("loyalty", new EnchantmentTridentLoyalty(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment IMPALING = a("impaling", new EnchantmentTridentImpaling(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment RIPTIDE = a("riptide", new EnchantmentTridentRiptide(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment CHANNELING = a("channeling", new EnchantmentTridentChanneling(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment MULTISHOT = a("multishot", new EnchantmentMultishot(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment QUICK_CHARGE = a("quick_charge", new EnchantmentQuickCharge(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment PIERCING = a("piercing", new EnchantmentPiercing(Enchantment.Rarity.COMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment MENDING = a("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EnumItemSlot.values()));
    public static final Enchantment VANISHING_CURSE = a("vanishing_curse", new EnchantmentVanishing(Enchantment.Rarity.VERY_RARE, EnumItemSlot.values()));

    public Enchantments() {}

    private static Enchantment a(String s, Enchantment enchantment) {
        return (Enchantment) IRegistry.a(IRegistry.ENCHANTMENT, s, (Object) enchantment);
    }
}
