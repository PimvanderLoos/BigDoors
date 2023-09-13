package net.minecraft.world.item.enchantment;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EnumItemSlot;

public class Enchantments {

    private static final EnumItemSlot[] ARMOR_SLOTS = new EnumItemSlot[]{EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};
    public static final Enchantment ALL_DAMAGE_PROTECTION = register("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.DamageType.ALL, Enchantments.ARMOR_SLOTS));
    public static final Enchantment FIRE_PROTECTION = register("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FIRE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment FALL_PROTECTION = register("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FALL, Enchantments.ARMOR_SLOTS));
    public static final Enchantment BLAST_PROTECTION = register("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.DamageType.EXPLOSION, Enchantments.ARMOR_SLOTS));
    public static final Enchantment PROJECTILE_PROTECTION = register("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.PROJECTILE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment RESPIRATION = register("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment AQUA_AFFINITY = register("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment THORNS = register("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment DEPTH_STRIDER = register("depth_strider", new EnchantmentDepthStrider(Enchantment.Rarity.RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment FROST_WALKER = register("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.FEET}));
    public static final Enchantment BINDING_CURSE = register("binding_curse", new EnchantmentBinding(Enchantment.Rarity.VERY_RARE, Enchantments.ARMOR_SLOTS));
    public static final Enchantment SOUL_SPEED = register("soul_speed", new EnchantmentSoulSpeed(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.FEET}));
    public static final Enchantment SWIFT_SNEAK = register("swift_sneak", new SwiftSneakEnchantment(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.LEGS}));
    public static final Enchantment SHARPNESS = register("sharpness", new EnchantmentWeaponDamage(Enchantment.Rarity.COMMON, 0, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment SMITE = register("smite", new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 1, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment BANE_OF_ARTHROPODS = register("bane_of_arthropods", new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 2, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment KNOCKBACK = register("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FIRE_ASPECT = register("fire_aspect", new EnchantmentFire(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment MOB_LOOTING = register("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.WEAPON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment SWEEPING_EDGE = register("sweeping", new EnchantmentSweeping(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment BLOCK_EFFICIENCY = register("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment SILK_TOUCH = register("silk_touch", new EnchantmentSilkTouch(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment UNBREAKING = register("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment BLOCK_FORTUNE = register("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.DIGGER, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment POWER_ARROWS = register("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment PUNCH_ARROWS = register("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FLAMING_ARROWS = register("flame", new EnchantmentFlameArrows(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment INFINITY_ARROWS = register("infinity", new EnchantmentInfiniteArrows(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FISHING_LUCK = register("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment FISHING_SPEED = register("lure", new EnchantmentLure(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment LOYALTY = register("loyalty", new EnchantmentTridentLoyalty(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment IMPALING = register("impaling", new EnchantmentTridentImpaling(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment RIPTIDE = register("riptide", new EnchantmentTridentRiptide(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment CHANNELING = register("channeling", new EnchantmentTridentChanneling(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment MULTISHOT = register("multishot", new EnchantmentMultishot(Enchantment.Rarity.RARE, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment QUICK_CHARGE = register("quick_charge", new EnchantmentQuickCharge(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment PIERCING = register("piercing", new EnchantmentPiercing(Enchantment.Rarity.COMMON, new EnumItemSlot[]{EnumItemSlot.MAINHAND}));
    public static final Enchantment MENDING = register("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EnumItemSlot.values()));
    public static final Enchantment VANISHING_CURSE = register("vanishing_curse", new EnchantmentVanishing(Enchantment.Rarity.VERY_RARE, EnumItemSlot.values()));

    public Enchantments() {}

    private static Enchantment register(String s, Enchantment enchantment) {
        return (Enchantment) IRegistry.register(BuiltInRegistries.ENCHANTMENT, s, enchantment);
    }
}
