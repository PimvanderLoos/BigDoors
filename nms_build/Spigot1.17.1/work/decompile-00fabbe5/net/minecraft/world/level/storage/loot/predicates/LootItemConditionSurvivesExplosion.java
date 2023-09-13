package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class LootItemConditionSurvivesExplosion implements LootItemCondition {

    static final LootItemConditionSurvivesExplosion INSTANCE = new LootItemConditionSurvivesExplosion();

    private LootItemConditionSurvivesExplosion() {}

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.SURVIVES_EXPLOSION;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(LootContextParameters.EXPLOSION_RADIUS);
    }

    public boolean test(LootTableInfo loottableinfo) {
        Float ofloat = (Float) loottableinfo.getContextParameter(LootContextParameters.EXPLOSION_RADIUS);

        if (ofloat != null) {
            Random random = loottableinfo.a();
            float f = 1.0F / ofloat;

            return random.nextFloat() <= f;
        } else {
            return true;
        }
    }

    public static LootItemCondition.a c() {
        return () -> {
            return LootItemConditionSurvivesExplosion.INSTANCE;
        };
    }

    public static class a implements LootSerializer<LootItemConditionSurvivesExplosion> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionSurvivesExplosion lootitemconditionsurvivesexplosion, JsonSerializationContext jsonserializationcontext) {}

        @Override
        public LootItemConditionSurvivesExplosion a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return LootItemConditionSurvivesExplosion.INSTANCE;
        }
    }
}
