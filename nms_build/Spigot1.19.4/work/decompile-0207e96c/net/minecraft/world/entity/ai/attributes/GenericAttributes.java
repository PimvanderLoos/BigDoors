package net.minecraft.world.entity.ai.attributes;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class GenericAttributes {

    public static final AttributeBase MAX_HEALTH = register("generic.max_health", (new AttributeRanged("attribute.name.generic.max_health", 20.0D, 1.0D, 1024.0D)).setSyncable(true));
    public static final AttributeBase FOLLOW_RANGE = register("generic.follow_range", new AttributeRanged("attribute.name.generic.follow_range", 32.0D, 0.0D, 2048.0D));
    public static final AttributeBase KNOCKBACK_RESISTANCE = register("generic.knockback_resistance", new AttributeRanged("attribute.name.generic.knockback_resistance", 0.0D, 0.0D, 1.0D));
    public static final AttributeBase MOVEMENT_SPEED = register("generic.movement_speed", (new AttributeRanged("attribute.name.generic.movement_speed", 0.699999988079071D, 0.0D, 1024.0D)).setSyncable(true));
    public static final AttributeBase FLYING_SPEED = register("generic.flying_speed", (new AttributeRanged("attribute.name.generic.flying_speed", 0.4000000059604645D, 0.0D, 1024.0D)).setSyncable(true));
    public static final AttributeBase ATTACK_DAMAGE = register("generic.attack_damage", new AttributeRanged("attribute.name.generic.attack_damage", 2.0D, 0.0D, 2048.0D));
    public static final AttributeBase ATTACK_KNOCKBACK = register("generic.attack_knockback", new AttributeRanged("attribute.name.generic.attack_knockback", 0.0D, 0.0D, 5.0D));
    public static final AttributeBase ATTACK_SPEED = register("generic.attack_speed", (new AttributeRanged("attribute.name.generic.attack_speed", 4.0D, 0.0D, 1024.0D)).setSyncable(true));
    public static final AttributeBase ARMOR = register("generic.armor", (new AttributeRanged("attribute.name.generic.armor", 0.0D, 0.0D, 30.0D)).setSyncable(true));
    public static final AttributeBase ARMOR_TOUGHNESS = register("generic.armor_toughness", (new AttributeRanged("attribute.name.generic.armor_toughness", 0.0D, 0.0D, 20.0D)).setSyncable(true));
    public static final AttributeBase LUCK = register("generic.luck", (new AttributeRanged("attribute.name.generic.luck", 0.0D, -1024.0D, 1024.0D)).setSyncable(true));
    public static final AttributeBase SPAWN_REINFORCEMENTS_CHANCE = register("zombie.spawn_reinforcements", new AttributeRanged("attribute.name.zombie.spawn_reinforcements", 0.0D, 0.0D, 1.0D));
    public static final AttributeBase JUMP_STRENGTH = register("horse.jump_strength", (new AttributeRanged("attribute.name.horse.jump_strength", 0.7D, 0.0D, 2.0D)).setSyncable(true));

    public GenericAttributes() {}

    private static AttributeBase register(String s, AttributeBase attributebase) {
        return (AttributeBase) IRegistry.register(BuiltInRegistries.ATTRIBUTE, s, attributebase);
    }
}
