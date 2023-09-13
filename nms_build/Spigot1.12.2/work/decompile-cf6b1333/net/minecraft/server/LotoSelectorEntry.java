package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;

public abstract class LotoSelectorEntry {

    protected final int c;
    protected final int d;
    protected final LootItemCondition[] e;

    protected LotoSelectorEntry(int i, int j, LootItemCondition[] alootitemcondition) {
        this.c = i;
        this.d = j;
        this.e = alootitemcondition;
    }

    public int a(float f) {
        return Math.max(MathHelper.d((float) this.c + (float) this.d * f), 0);
    }

    public abstract void a(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo);

    protected abstract void a(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext);

    public static class a implements JsonDeserializer<LotoSelectorEntry>, JsonSerializer<LotoSelectorEntry> {

        public a() {}

        public LotoSelectorEntry a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot item");
            String s = ChatDeserializer.h(jsonobject, "type");
            int i = ChatDeserializer.a(jsonobject, "weight", 1);
            int j = ChatDeserializer.a(jsonobject, "quality", 0);
            LootItemCondition[] alootitemcondition;

            if (jsonobject.has("conditions")) {
                alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", jsondeserializationcontext, LootItemCondition[].class);
            } else {
                alootitemcondition = new LootItemCondition[0];
            }

            if ("item".equals(s)) {
                return LootItem.a(jsonobject, jsondeserializationcontext, i, j, alootitemcondition);
            } else if ("loot_table".equals(s)) {
                return LootSelectorLootTable.a(jsonobject, jsondeserializationcontext, i, j, alootitemcondition);
            } else if ("empty".equals(s)) {
                return LootSelectorEmpty.a(jsonobject, jsondeserializationcontext, i, j, alootitemcondition);
            } else {
                throw new JsonSyntaxException("Unknown loot entry type \'" + s + "\'");
            }
        }

        public JsonElement a(LotoSelectorEntry lotoselectorentry, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("weight", Integer.valueOf(lotoselectorentry.c));
            jsonobject.addProperty("quality", Integer.valueOf(lotoselectorentry.d));
            if (lotoselectorentry.e.length > 0) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(lotoselectorentry.e));
            }

            if (lotoselectorentry instanceof LootItem) {
                jsonobject.addProperty("type", "item");
            } else if (lotoselectorentry instanceof LootSelectorLootTable) {
                jsonobject.addProperty("type", "loot_table");
            } else {
                if (!(lotoselectorentry instanceof LootSelectorEmpty)) {
                    throw new IllegalArgumentException("Don\'t know how to serialize " + lotoselectorentry);
                }

                jsonobject.addProperty("type", "empty");
            }

            lotoselectorentry.a(jsonobject, jsonserializationcontext);
            return jsonobject;
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((LotoSelectorEntry) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
