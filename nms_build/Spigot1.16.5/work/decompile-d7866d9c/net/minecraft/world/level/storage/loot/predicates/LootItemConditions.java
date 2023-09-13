package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Predicate;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class LootItemConditions {

    public static final LootItemConditionType a = a("inverted", (LootSerializer) (new LootItemConditionInverted.a()));
    public static final LootItemConditionType b = a("alternative", (LootSerializer) (new LootItemConditionAlternative.b()));
    public static final LootItemConditionType c = a("random_chance", (LootSerializer) (new LootItemConditionRandomChance.a()));
    public static final LootItemConditionType d = a("random_chance_with_looting", (LootSerializer) (new LootItemConditionRandomChanceWithLooting.a()));
    public static final LootItemConditionType e = a("entity_properties", (LootSerializer) (new LootItemConditionEntityProperty.a()));
    public static final LootItemConditionType f = a("killed_by_player", (LootSerializer) (new LootItemConditionKilledByPlayer.a()));
    public static final LootItemConditionType g = a("entity_scores", (LootSerializer) (new LootItemConditionEntityScore.b()));
    public static final LootItemConditionType h = a("block_state_property", (LootSerializer) (new LootItemConditionBlockStateProperty.b()));
    public static final LootItemConditionType i = a("match_tool", (LootSerializer) (new LootItemConditionMatchTool.a()));
    public static final LootItemConditionType j = a("table_bonus", (LootSerializer) (new LootItemConditionTableBonus.a()));
    public static final LootItemConditionType k = a("survives_explosion", (LootSerializer) (new LootItemConditionSurvivesExplosion.a()));
    public static final LootItemConditionType l = a("damage_source_properties", (LootSerializer) (new LootItemConditionDamageSourceProperties.a()));
    public static final LootItemConditionType m = a("location_check", (LootSerializer) (new LootItemConditionLocationCheck.a()));
    public static final LootItemConditionType n = a("weather_check", (LootSerializer) (new LootItemConditionWeatherCheck.b()));
    public static final LootItemConditionType o = a("reference", (LootSerializer) (new LootItemConditionReference.a()));
    public static final LootItemConditionType p = a("time_check", (LootSerializer) (new LootItemConditionTimeCheck.b()));

    private static LootItemConditionType a(String s, LootSerializer<? extends LootItemCondition> lootserializer) {
        return (LootItemConditionType) IRegistry.a(IRegistry.aq, new MinecraftKey(s), (Object) (new LootItemConditionType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.aq, "condition", "condition", LootItemCondition::b).a();
    }

    public static <T> Predicate<T> a(Predicate<T>[] apredicate) {
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

    public static <T> Predicate<T> b(Predicate<T>[] apredicate) {
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
