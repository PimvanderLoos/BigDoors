package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class LootItemConditionEntityProperty implements LootItemCondition {

    private final LootEntityProperty[] a;
    private final LootTableInfo.EntityTarget b;

    public LootItemConditionEntityProperty(LootEntityProperty[] alootentityproperty, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        this.a = alootentityproperty;
        this.b = loottableinfo_entitytarget;
    }

    public boolean a(Random random, LootTableInfo loottableinfo) {
        Entity entity = loottableinfo.a(this.b);

        if (entity == null) {
            return false;
        } else {
            LootEntityProperty[] alootentityproperty = this.a;
            int i = alootentityproperty.length;

            for (int j = 0; j < i; ++j) {
                LootEntityProperty lootentityproperty = alootentityproperty[j];

                if (!lootentityproperty.a(random, entity)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static class a extends LootItemCondition.a<LootItemConditionEntityProperty> {

        protected a() {
            super(new MinecraftKey("entity_properties"), LootItemConditionEntityProperty.class);
        }

        public void a(JsonObject jsonobject, LootItemConditionEntityProperty lootitemconditionentityproperty, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject1 = new JsonObject();
            LootEntityProperty[] alootentityproperty = lootitemconditionentityproperty.a;
            int i = alootentityproperty.length;

            for (int j = 0; j < i; ++j) {
                LootEntityProperty lootentityproperty = alootentityproperty[j];
                LootEntityProperty.a lootentityproperty_a = LootEntityProperties.a(lootentityproperty);

                jsonobject1.add(lootentityproperty_a.a().toString(), lootentityproperty_a.a(lootentityproperty, jsonserializationcontext));
            }

            jsonobject.add("properties", jsonobject1);
            jsonobject.add("entity", jsonserializationcontext.serialize(lootitemconditionentityproperty.b));
        }

        public LootItemConditionEntityProperty a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Set set = ChatDeserializer.t(jsonobject, "properties").entrySet();
            LootEntityProperty[] alootentityproperty = new LootEntityProperty[set.size()];
            int i = 0;

            Entry entry;

            for (Iterator iterator = set.iterator(); iterator.hasNext(); alootentityproperty[i++] = LootEntityProperties.a(new MinecraftKey((String) entry.getKey())).a((JsonElement) entry.getValue(), jsondeserializationcontext)) {
                entry = (Entry) iterator.next();
            }

            return new LootItemConditionEntityProperty(alootentityproperty, (LootTableInfo.EntityTarget) ChatDeserializer.a(jsonobject, "entity", jsondeserializationcontext, LootTableInfo.EntityTarget.class));
        }

        public LootItemCondition b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return this.a(jsonobject, jsondeserializationcontext);
        }
    }
}
