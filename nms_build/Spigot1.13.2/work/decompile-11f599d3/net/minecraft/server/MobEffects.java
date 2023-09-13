package net.minecraft.server;

import javax.annotation.Nullable;

public class MobEffects {

    public static final MobEffectList FASTER_MOVEMENT;
    public static final MobEffectList SLOWER_MOVEMENT;
    public static final MobEffectList FASTER_DIG;
    public static final MobEffectList SLOWER_DIG;
    public static final MobEffectList INCREASE_DAMAGE;
    public static final MobEffectList HEAL;
    public static final MobEffectList HARM;
    public static final MobEffectList JUMP;
    public static final MobEffectList CONFUSION;
    public static final MobEffectList REGENERATION;
    public static final MobEffectList RESISTANCE;
    public static final MobEffectList FIRE_RESISTANCE;
    public static final MobEffectList WATER_BREATHING;
    public static final MobEffectList INVISIBILITY;
    public static final MobEffectList BLINDNESS;
    public static final MobEffectList NIGHT_VISION;
    public static final MobEffectList HUNGER;
    public static final MobEffectList WEAKNESS;
    public static final MobEffectList POISON;
    public static final MobEffectList WITHER;
    public static final MobEffectList HEALTH_BOOST;
    public static final MobEffectList ABSORBTION;
    public static final MobEffectList SATURATION;
    public static final MobEffectList GLOWING;
    public static final MobEffectList LEVITATION;
    public static final MobEffectList LUCK;
    public static final MobEffectList UNLUCK;
    public static final MobEffectList SLOW_FALLING;
    public static final MobEffectList CONDUIT_POWER;
    public static final MobEffectList DOLPHINS_GRACE;

    @Nullable
    private static MobEffectList a(String s) {
        MobEffectList mobeffectlist = (MobEffectList) IRegistry.MOB_EFFECT.get(new MinecraftKey(s));

        if (mobeffectlist == null) {
            throw new IllegalStateException("Invalid MobEffect requested: " + s);
        } else {
            return mobeffectlist;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed MobEffects before Bootstrap!");
        } else {
            FASTER_MOVEMENT = a("speed");
            SLOWER_MOVEMENT = a("slowness");
            FASTER_DIG = a("haste");
            SLOWER_DIG = a("mining_fatigue");
            INCREASE_DAMAGE = a("strength");
            HEAL = a("instant_health");
            HARM = a("instant_damage");
            JUMP = a("jump_boost");
            CONFUSION = a("nausea");
            REGENERATION = a("regeneration");
            RESISTANCE = a("resistance");
            FIRE_RESISTANCE = a("fire_resistance");
            WATER_BREATHING = a("water_breathing");
            INVISIBILITY = a("invisibility");
            BLINDNESS = a("blindness");
            NIGHT_VISION = a("night_vision");
            HUNGER = a("hunger");
            WEAKNESS = a("weakness");
            POISON = a("poison");
            WITHER = a("wither");
            HEALTH_BOOST = a("health_boost");
            ABSORBTION = a("absorption");
            SATURATION = a("saturation");
            GLOWING = a("glowing");
            LEVITATION = a("levitation");
            LUCK = a("luck");
            UNLUCK = a("unluck");
            SLOW_FALLING = a("slow_falling");
            CONDUIT_POWER = a("conduit_power");
            DOLPHINS_GRACE = a("dolphins_grace");
        }
    }
}
