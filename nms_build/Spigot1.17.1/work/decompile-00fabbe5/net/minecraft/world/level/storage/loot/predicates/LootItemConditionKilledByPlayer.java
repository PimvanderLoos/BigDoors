package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class LootItemConditionKilledByPlayer implements LootItemCondition {

    static final LootItemConditionKilledByPlayer INSTANCE = new LootItemConditionKilledByPlayer();

    private LootItemConditionKilledByPlayer() {}

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.KILLED_BY_PLAYER;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public boolean test(LootTableInfo loottableinfo) {
        return loottableinfo.hasContextParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public static LootItemCondition.a c() {
        return () -> {
            return LootItemConditionKilledByPlayer.INSTANCE;
        };
    }

    public static class a implements LootSerializer<LootItemConditionKilledByPlayer> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionKilledByPlayer lootitemconditionkilledbyplayer, JsonSerializationContext jsonserializationcontext) {}

        @Override
        public LootItemConditionKilledByPlayer a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return LootItemConditionKilledByPlayer.INSTANCE;
        }
    }
}
