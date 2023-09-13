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
    private final Boolean a;
    @Nullable
    private final Boolean b;

    private LootItemConditionWeatherCheck(@Nullable Boolean obool, @Nullable Boolean obool1) {
        this.a = obool;
        this.b = obool1;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.n;
    }

    public boolean test(LootTableInfo loottableinfo) {
        WorldServer worldserver = loottableinfo.getWorld();

        return this.a != null && this.a != worldserver.isRaining() ? false : this.b == null || this.b == worldserver.W();
    }

    public static class b implements LootSerializer<LootItemConditionWeatherCheck> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemConditionWeatherCheck lootitemconditionweathercheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("raining", lootitemconditionweathercheck.a);
            jsonobject.addProperty("thundering", lootitemconditionweathercheck.b);
        }

        @Override
        public LootItemConditionWeatherCheck a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Boolean obool = jsonobject.has("raining") ? ChatDeserializer.j(jsonobject, "raining") : null;
            Boolean obool1 = jsonobject.has("thundering") ? ChatDeserializer.j(jsonobject, "thundering") : null;

            return new LootItemConditionWeatherCheck(obool, obool1);
        }
    }
}
