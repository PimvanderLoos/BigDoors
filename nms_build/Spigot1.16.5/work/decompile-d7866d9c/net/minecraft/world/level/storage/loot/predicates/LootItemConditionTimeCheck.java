package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootValueBounds;

public class LootItemConditionTimeCheck implements LootItemCondition {

    @Nullable
    private final Long a;
    private final LootValueBounds b;

    private LootItemConditionTimeCheck(@Nullable Long olong, LootValueBounds lootvaluebounds) {
        this.a = olong;
        this.b = lootvaluebounds;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.p;
    }

    public boolean test(LootTableInfo loottableinfo) {
        WorldServer worldserver = loottableinfo.getWorld();
        long i = worldserver.getDayTime();

        if (this.a != null) {
            i %= this.a;
        }

        return this.b.a((int) i);
    }

    public static class b implements LootSerializer<LootItemConditionTimeCheck> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemConditionTimeCheck lootitemconditiontimecheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("period", lootitemconditiontimecheck.a);
            jsonobject.add("value", jsonserializationcontext.serialize(lootitemconditiontimecheck.b));
        }

        @Override
        public LootItemConditionTimeCheck a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Long olong = jsonobject.has("period") ? ChatDeserializer.m(jsonobject, "period") : null;
            LootValueBounds lootvaluebounds = (LootValueBounds) ChatDeserializer.a(jsonobject, "value", jsondeserializationcontext, LootValueBounds.class);

            return new LootItemConditionTimeCheck(olong, lootvaluebounds);
        }
    }
}
