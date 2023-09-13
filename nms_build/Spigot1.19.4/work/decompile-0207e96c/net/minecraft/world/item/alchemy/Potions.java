package net.minecraft.world.item.alchemy;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class Potions {

    public static ResourceKey<PotionRegistry> EMPTY_ID = ResourceKey.create(Registries.POTION, new MinecraftKey("empty"));
    public static final PotionRegistry EMPTY = register(Potions.EMPTY_ID, new PotionRegistry(new MobEffect[0]));
    public static final PotionRegistry WATER = register("water", new PotionRegistry(new MobEffect[0]));
    public static final PotionRegistry MUNDANE = register("mundane", new PotionRegistry(new MobEffect[0]));
    public static final PotionRegistry THICK = register("thick", new PotionRegistry(new MobEffect[0]));
    public static final PotionRegistry AWKWARD = register("awkward", new PotionRegistry(new MobEffect[0]));
    public static final PotionRegistry NIGHT_VISION = register("night_vision", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.NIGHT_VISION, 3600)}));
    public static final PotionRegistry LONG_NIGHT_VISION = register("long_night_vision", new PotionRegistry("night_vision", new MobEffect[]{new MobEffect(MobEffects.NIGHT_VISION, 9600)}));
    public static final PotionRegistry INVISIBILITY = register("invisibility", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.INVISIBILITY, 3600)}));
    public static final PotionRegistry LONG_INVISIBILITY = register("long_invisibility", new PotionRegistry("invisibility", new MobEffect[]{new MobEffect(MobEffects.INVISIBILITY, 9600)}));
    public static final PotionRegistry LEAPING = register("leaping", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.JUMP, 3600)}));
    public static final PotionRegistry LONG_LEAPING = register("long_leaping", new PotionRegistry("leaping", new MobEffect[]{new MobEffect(MobEffects.JUMP, 9600)}));
    public static final PotionRegistry STRONG_LEAPING = register("strong_leaping", new PotionRegistry("leaping", new MobEffect[]{new MobEffect(MobEffects.JUMP, 1800, 1)}));
    public static final PotionRegistry FIRE_RESISTANCE = register("fire_resistance", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.FIRE_RESISTANCE, 3600)}));
    public static final PotionRegistry LONG_FIRE_RESISTANCE = register("long_fire_resistance", new PotionRegistry("fire_resistance", new MobEffect[]{new MobEffect(MobEffects.FIRE_RESISTANCE, 9600)}));
    public static final PotionRegistry SWIFTNESS = register("swiftness", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SPEED, 3600)}));
    public static final PotionRegistry LONG_SWIFTNESS = register("long_swiftness", new PotionRegistry("swiftness", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SPEED, 9600)}));
    public static final PotionRegistry STRONG_SWIFTNESS = register("strong_swiftness", new PotionRegistry("swiftness", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SPEED, 1800, 1)}));
    public static final PotionRegistry SLOWNESS = register("slowness", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 1800)}));
    public static final PotionRegistry LONG_SLOWNESS = register("long_slowness", new PotionRegistry("slowness", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 4800)}));
    public static final PotionRegistry STRONG_SLOWNESS = register("strong_slowness", new PotionRegistry("slowness", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 400, 3)}));
    public static final PotionRegistry TURTLE_MASTER = register("turtle_master", new PotionRegistry("turtle_master", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 400, 3), new MobEffect(MobEffects.DAMAGE_RESISTANCE, 400, 2)}));
    public static final PotionRegistry LONG_TURTLE_MASTER = register("long_turtle_master", new PotionRegistry("turtle_master", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 800, 3), new MobEffect(MobEffects.DAMAGE_RESISTANCE, 800, 2)}));
    public static final PotionRegistry STRONG_TURTLE_MASTER = register("strong_turtle_master", new PotionRegistry("turtle_master", new MobEffect[]{new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 400, 5), new MobEffect(MobEffects.DAMAGE_RESISTANCE, 400, 3)}));
    public static final PotionRegistry WATER_BREATHING = register("water_breathing", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.WATER_BREATHING, 3600)}));
    public static final PotionRegistry LONG_WATER_BREATHING = register("long_water_breathing", new PotionRegistry("water_breathing", new MobEffect[]{new MobEffect(MobEffects.WATER_BREATHING, 9600)}));
    public static final PotionRegistry HEALING = register("healing", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.HEAL, 1)}));
    public static final PotionRegistry STRONG_HEALING = register("strong_healing", new PotionRegistry("healing", new MobEffect[]{new MobEffect(MobEffects.HEAL, 1, 1)}));
    public static final PotionRegistry HARMING = register("harming", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.HARM, 1)}));
    public static final PotionRegistry STRONG_HARMING = register("strong_harming", new PotionRegistry("harming", new MobEffect[]{new MobEffect(MobEffects.HARM, 1, 1)}));
    public static final PotionRegistry POISON = register("poison", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.POISON, 900)}));
    public static final PotionRegistry LONG_POISON = register("long_poison", new PotionRegistry("poison", new MobEffect[]{new MobEffect(MobEffects.POISON, 1800)}));
    public static final PotionRegistry STRONG_POISON = register("strong_poison", new PotionRegistry("poison", new MobEffect[]{new MobEffect(MobEffects.POISON, 432, 1)}));
    public static final PotionRegistry REGENERATION = register("regeneration", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.REGENERATION, 900)}));
    public static final PotionRegistry LONG_REGENERATION = register("long_regeneration", new PotionRegistry("regeneration", new MobEffect[]{new MobEffect(MobEffects.REGENERATION, 1800)}));
    public static final PotionRegistry STRONG_REGENERATION = register("strong_regeneration", new PotionRegistry("regeneration", new MobEffect[]{new MobEffect(MobEffects.REGENERATION, 450, 1)}));
    public static final PotionRegistry STRENGTH = register("strength", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.DAMAGE_BOOST, 3600)}));
    public static final PotionRegistry LONG_STRENGTH = register("long_strength", new PotionRegistry("strength", new MobEffect[]{new MobEffect(MobEffects.DAMAGE_BOOST, 9600)}));
    public static final PotionRegistry STRONG_STRENGTH = register("strong_strength", new PotionRegistry("strength", new MobEffect[]{new MobEffect(MobEffects.DAMAGE_BOOST, 1800, 1)}));
    public static final PotionRegistry WEAKNESS = register("weakness", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.WEAKNESS, 1800)}));
    public static final PotionRegistry LONG_WEAKNESS = register("long_weakness", new PotionRegistry("weakness", new MobEffect[]{new MobEffect(MobEffects.WEAKNESS, 4800)}));
    public static final PotionRegistry LUCK = register("luck", new PotionRegistry("luck", new MobEffect[]{new MobEffect(MobEffects.LUCK, 6000)}));
    public static final PotionRegistry SLOW_FALLING = register("slow_falling", new PotionRegistry(new MobEffect[]{new MobEffect(MobEffects.SLOW_FALLING, 1800)}));
    public static final PotionRegistry LONG_SLOW_FALLING = register("long_slow_falling", new PotionRegistry("slow_falling", new MobEffect[]{new MobEffect(MobEffects.SLOW_FALLING, 4800)}));

    public Potions() {}

    private static PotionRegistry register(String s, PotionRegistry potionregistry) {
        return (PotionRegistry) IRegistry.register(BuiltInRegistries.POTION, s, potionregistry);
    }

    private static PotionRegistry register(ResourceKey<PotionRegistry> resourcekey, PotionRegistry potionregistry) {
        return (PotionRegistry) IRegistry.register(BuiltInRegistries.POTION, resourcekey, potionregistry);
    }
}
