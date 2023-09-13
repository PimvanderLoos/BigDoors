package net.minecraft.server;

import javax.annotation.Nullable;

public class Enchantments {

    public static final Enchantment PROTECTION_ENVIRONMENTAL = a("protection");
    public static final Enchantment PROTECTION_FIRE = a("fire_protection");
    public static final Enchantment PROTECTION_FALL = a("feather_falling");
    public static final Enchantment PROTECTION_EXPLOSIONS = a("blast_protection");
    public static final Enchantment PROTECTION_PROJECTILE = a("projectile_protection");
    public static final Enchantment OXYGEN = a("respiration");
    public static final Enchantment WATER_WORKER = a("aqua_affinity");
    public static final Enchantment THORNS = a("thorns");
    public static final Enchantment DEPTH_STRIDER = a("depth_strider");
    public static final Enchantment j = a("frost_walker");
    public static final Enchantment k = a("binding_curse");
    public static final Enchantment DAMAGE_ALL = a("sharpness");
    public static final Enchantment DAMAGE_UNDEAD = a("smite");
    public static final Enchantment DAMAGE_ARTHROPODS = a("bane_of_arthropods");
    public static final Enchantment KNOCKBACK = a("knockback");
    public static final Enchantment FIRE_ASPECT = a("fire_aspect");
    public static final Enchantment LOOT_BONUS_MOBS = a("looting");
    public static final Enchantment r = a("sweeping");
    public static final Enchantment DIG_SPEED = a("efficiency");
    public static final Enchantment SILK_TOUCH = a("silk_touch");
    public static final Enchantment DURABILITY = a("unbreaking");
    public static final Enchantment LOOT_BONUS_BLOCKS = a("fortune");
    public static final Enchantment ARROW_DAMAGE = a("power");
    public static final Enchantment ARROW_KNOCKBACK = a("punch");
    public static final Enchantment ARROW_FIRE = a("flame");
    public static final Enchantment ARROW_INFINITE = a("infinity");
    public static final Enchantment LUCK = a("luck_of_the_sea");
    public static final Enchantment LURE = a("lure");
    public static final Enchantment C = a("mending");
    public static final Enchantment D = a("vanishing_curse");

    @Nullable
    private static Enchantment a(String s) {
        Enchantment enchantment = (Enchantment) Enchantment.enchantments.get(new MinecraftKey(s));

        if (enchantment == null) {
            throw new IllegalStateException("Invalid Enchantment requested: " + s);
        } else {
            return enchantment;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed Enchantments before Bootstrap!");
        }
    }
}
