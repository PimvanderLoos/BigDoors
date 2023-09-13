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
    public LootItemConditionType a() {
        return LootItemConditions.WEATHER_CHECK;
    }

    public boolean test(LootTableInfo loottableinfo) {
        WorldServer worldserver = loottableinfo.getWorld();

        return this.isRaining != null && this.isRaining != worldserver.isRaining() ? false : this.isThundering == null || this.isThundering == worldserver.Y();
    }

    public static LootItemConditionWeatherCheck.a c() {
        return new LootItemConditionWeatherCheck.a();
    }

    public static class a implements LootItemCondition.a {

        @Nullable
        private Boolean isRaining;
        @Nullable
        private Boolean isThundering;

        public a() {}

        public LootItemConditionWeatherCheck.a a(@Nullable Boolean obool) {
            this.isRaining = obool;
            return this;
        }

        public LootItemConditionWeatherCheck.a b(@Nullable Boolean obool) {
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

        public void a(JsonObject jsonobject, LootItemConditionWeatherCheck lootitemconditionweathercheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("raining", lootitemconditionweathercheck.isRaining);
            jsonobject.addProperty("thundering", lootitemconditionweathercheck.isThundering);
        }

        @Override
        public LootItemConditionWeatherCheck a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Boolean obool = jsonobject.has("raining") ? ChatDeserializer.j(jsonobject, "raining") : null;
            Boolean obool1 = jsonobject.has("thundering") ? ChatDeserializer.j(jsonobject, "thundering") : null;

            return new LootItemConditionWeatherCheck(obool, obool1);
        }
    }
}
