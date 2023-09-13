package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Predicate;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class LootItemConditions {

    public static final LootItemConditionType INVERTED = register("inverted", new LootItemConditionInverted.a());
    public static final LootItemConditionType ALTERNATIVE = register("alternative", new LootItemConditionAlternative.b());
    public static final LootItemConditionType RANDOM_CHANCE = register("random_chance", new LootItemConditionRandomChance.a());
    public static final LootItemConditionType RANDOM_CHANCE_WITH_LOOTING = register("random_chance_with_looting", new LootItemConditionRandomChanceWithLooting.a());
    public static final LootItemConditionType ENTITY_PROPERTIES = register("entity_properties", new LootItemConditionEntityProperty.a());
    public static final LootItemConditionType KILLED_BY_PLAYER = register("killed_by_player", new LootItemConditionKilledByPlayer.a());
    public static final LootItemConditionType ENTITY_SCORES = register("entity_scores", new LootItemConditionEntityScore.b());
    public static final LootItemConditionType BLOCK_STATE_PROPERTY = register("block_state_property", new LootItemConditionBlockStateProperty.b());
    public static final LootItemConditionType MATCH_TOOL = register("match_tool", new LootItemConditionMatchTool.a());
    public static final LootItemConditionType TABLE_BONUS = register("table_bonus", new LootItemConditionTableBonus.a());
    public static final LootItemConditionType SURVIVES_EXPLOSION = register("survives_explosion", new LootItemConditionSurvivesExplosion.a());
    public static final LootItemConditionType DAMAGE_SOURCE_PROPERTIES = register("damage_source_properties", new LootItemConditionDamageSourceProperties.a());
    public static final LootItemConditionType LOCATION_CHECK = register("location_check", new LootItemConditionLocationCheck.a());
    public static final LootItemConditionType WEATHER_CHECK = register("weather_check", new LootItemConditionWeatherCheck.b());
    public static final LootItemConditionType REFERENCE = register("reference", new LootItemConditionReference.a());
    public static final LootItemConditionType TIME_CHECK = register("time_check", new LootItemConditionTimeCheck.b());
    public static final LootItemConditionType VALUE_CHECK = register("value_check", new ValueCheckCondition.a());

    public LootItemConditions() {}

    private static LootItemConditionType register(String s, LootSerializer<? extends LootItemCondition> lootserializer) {
        return (LootItemConditionType) IRegistry.register(IRegistry.LOOT_CONDITION_TYPE, new MinecraftKey(s), new LootItemConditionType(lootserializer));
    }

    public static Object createGsonAdapter() {
        return JsonRegistry.builder(IRegistry.LOOT_CONDITION_TYPE, "condition", "condition", LootItemCondition::getType).build();
    }

    public static <T> Predicate<T> andConditions(Predicate<T>[] apredicate) {
        switch (apredicate.length) {
            case 0:
                return (object) -> {
                    return true;
                };
            case 1:
                return apredicate[0];
            case 2:
                return apredicate[0].and(apredicate[1]);
            default:
                return (object) -> {
                    Predicate[] apredicate1 = apredicate;
                    int i = apredicate.length;

                    for (int j = 0; j < i; ++j) {
                        Predicate<T> predicate = apredicate1[j];

                        if (!predicate.test(object)) {
                            return false;
                        }
                    }

                    return true;
                };
        }
    }

    public static <T> Predicate<T> orConditions(Predicate<T>[] apredicate) {
        switch (apredicate.length) {
            case 0:
                return (object) -> {
                    return false;
                };
            case 1:
                return apredicate[0];
            case 2:
                return apredicate[0].or(apredicate[1]);
            default:
                return (object) -> {
                    Predicate[] apredicate1 = apredicate;
                    int i = apredicate.length;

                    for (int j = 0; j < i; ++j) {
                        Predicate<T> predicate = apredicate1[j];

                        if (predicate.test(object)) {
                            return true;
                        }
                    }

                    return false;
                };
        }
    }
}
