package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public class LootItemConditionTimeCheck implements LootItemCondition {

    @Nullable
    final Long period;
    final IntRange value;

    LootItemConditionTimeCheck(@Nullable Long olong, IntRange intrange) {
        this.period = olong;
        this.value = intrange;
    }

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.TIME_CHECK;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.value.a();
    }

    public boolean test(LootTableInfo loottableinfo) {
        WorldServer worldserver = loottableinfo.getWorld();
        long i = worldserver.getDayTime();

        if (this.period != null) {
            i %= this.period;
        }

        return this.value.b(loottableinfo, (int) i);
    }

    public static LootItemConditionTimeCheck.a a(IntRange intrange) {
        return new LootItemConditionTimeCheck.a(intrange);
    }

    public static class a implements LootItemCondition.a {

        @Nullable
        private Long period;
        private final IntRange value;

        public a(IntRange intrange) {
            this.value = intrange;
        }

        public LootItemConditionTimeCheck.a a(long i) {
            this.period = i;
            return this;
        }

        @Override
        public LootItemConditionTimeCheck build() {
            return new LootItemConditionTimeCheck(this.period, this.value);
        }
    }

    public static class b implements LootSerializer<LootItemConditionTimeCheck> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemConditionTimeCheck lootitemconditiontimecheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("period", lootitemconditiontimecheck.period);
            jsonobject.add("value", jsonserializationcontext.serialize(lootitemconditiontimecheck.value));
        }

        @Override
        public LootItemConditionTimeCheck a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Long olong = jsonobject.has("period") ? ChatDeserializer.m(jsonobject, "period") : null;
            IntRange intrange = (IntRange) ChatDeserializer.a(jsonobject, "value", jsondeserializationcontext, IntRange.class);

            return new LootItemConditionTimeCheck(olong, intrange);
        }
    }
}
