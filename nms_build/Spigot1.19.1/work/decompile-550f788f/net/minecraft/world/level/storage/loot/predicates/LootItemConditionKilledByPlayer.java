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
    public LootItemConditionType getType() {
        return LootItemConditions.KILLED_BY_PLAYER;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public boolean test(LootTableInfo loottableinfo) {
        return loottableinfo.hasParam(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public static LootItemCondition.a killedByPlayer() {
        return () -> {
            return LootItemConditionKilledByPlayer.INSTANCE;
        };
    }

    public static class a implements LootSerializer<LootItemConditionKilledByPlayer> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemConditionKilledByPlayer lootitemconditionkilledbyplayer, JsonSerializationContext jsonserializationcontext) {}

        @Override
        public LootItemConditionKilledByPlayer deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return LootItemConditionKilledByPlayer.INSTANCE;
        }
    }
}
