package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class LootItemConditionWeatherCheck implements LootItemCondition {

    @Nullable
    final Boolean isRaining;
    @Nullable
    final Boolean isThundering;

    LootItemConditionWeatherCheck(@Nullable Boolean obool, @Nullable Boolean obool1) {
        this.isRaining = obool;
        this.isThundering = obool1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.WEATHER_CHECK;
    }

    public boolean test(LootTableInfo loottableinfo) {
        WorldServer worldserver = loottableinfo.getLevel();

        return this.isRaining != null && this.isRaining != worldserver.isRaining() ? false : this.isThundering == null || this.isThundering == worldserver.isThundering();
    }

    public static LootItemConditionWeatherCheck.a weather() {
        return new LootItemConditionWeatherCheck.a();
    }

    public static class a implements LootItemCondition.a {

        @Nullable
        private Boolean isRaining;
        @Nullable
        private Boolean isThundering;

        public a() {}

        public LootItemConditionWeatherCheck.a setRaining(@Nullable Boolean obool) {
            this.isRaining = obool;
            return this;
        }

        public LootItemConditionWeatherCheck.a setThundering(@Nullable Boolean obool) {
            this.isThundering = obool;
            return this;
        }

        @Override
        public LootItemConditionWeatherCheck build() {
            return new LootItemConditionWeatherCheck(this.isRaining, this.isThundering);
        }
    }

    public static class b implements LootSerializer<LootItemConditionWeatherCheck> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemConditionWeatherCheck lootitemconditionweathercheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("raining", lootitemconditionweathercheck.isRaining);
            jsonobject.addProperty("thundering", lootitemconditionweathercheck.isThundering);
        }

        @Override
        public LootItemConditionWeatherCheck deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Boolean obool = jsonobject.has("raining") ? ChatDeserializer.getAsBoolean(jsonobject, "raining") : null;
            Boolean obool1 = jsonobject.has("thundering") ? ChatDeserializer.getAsBoolean(jsonobject, "thundering") : null;

            return new LootItemConditionWeatherCheck(obool, obool1);
        }
    }
}
