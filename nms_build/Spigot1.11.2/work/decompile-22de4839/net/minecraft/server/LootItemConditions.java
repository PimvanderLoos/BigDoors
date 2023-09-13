package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public class LootItemConditions {

    private static final Map<MinecraftKey, LootItemCondition.a<?>> a = Maps.newHashMap();
    private static final Map<Class<? extends LootItemCondition>, LootItemCondition.a<?>> b = Maps.newHashMap();

    public static <T extends LootItemCondition> void a(LootItemCondition.a<? extends T> lootitemcondition_a) {
        MinecraftKey minecraftkey = lootitemcondition_a.a();
        Class oclass = lootitemcondition_a.b();

        if (LootItemConditions.a.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("Can\'t re-register item condition name " + minecraftkey);
        } else if (LootItemConditions.b.containsKey(oclass)) {
            throw new IllegalArgumentException("Can\'t re-register item condition class " + oclass.getName());
        } else {
            LootItemConditions.a.put(minecraftkey, lootitemcondition_a);
            LootItemConditions.b.put(oclass, lootitemcondition_a);
        }
    }

    public static boolean a(@Nullable LootItemCondition[] alootitemcondition, Random random, LootTableInfo loottableinfo) {
        if (alootitemcondition == null) {
            return true;
        } else {
            LootItemCondition[] alootitemcondition1 = alootitemcondition;
            int i = alootitemcondition.length;

            for (int j = 0; j < i; ++j) {
                LootItemCondition lootitemcondition = alootitemcondition1[j];

                if (!lootitemcondition.a(random, loottableinfo)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static LootItemCondition.a<?> a(MinecraftKey minecraftkey) {
        LootItemCondition.a lootitemcondition_a = (LootItemCondition.a) LootItemConditions.a.get(minecraftkey);

        if (lootitemcondition_a == null) {
            throw new IllegalArgumentException("Unknown loot item condition \'" + minecraftkey + "\'");
        } else {
            return lootitemcondition_a;
        }
    }

    public static <T extends LootItemCondition> LootItemCondition.a<T> a(T t0) {
        LootItemCondition.a lootitemcondition_a = (LootItemCondition.a) LootItemConditions.b.get(t0.getClass());

        if (lootitemcondition_a == null) {
            throw new IllegalArgumentException("Unknown loot item condition " + t0);
        } else {
            return lootitemcondition_a;
        }
    }

    static {
        a((LootItemCondition.a) (new LootItemConditionRandomChance.a()));
        a((LootItemCondition.a) (new LootItemConditionRandomChanceWithLooting.a()));
        a((LootItemCondition.a) (new LootItemConditionEntityProperty.a()));
        a((LootItemCondition.a) (new LootItemConditionKilledByPlayer.a()));
        a((LootItemCondition.a) (new LootItemConditionEntityScore.a()));
    }

    public static class a implements JsonDeserializer<LootItemCondition>, JsonSerializer<LootItemCondition> {

        public a() {}

        public LootItemCondition a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "condition");
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "condition"));

            LootItemCondition.a lootitemcondition_a;

            try {
                lootitemcondition_a = LootItemConditions.a(minecraftkey);
            } catch (IllegalArgumentException illegalargumentexception) {
                throw new JsonSyntaxException("Unknown condition \'" + minecraftkey + "\'");
            }

            return lootitemcondition_a.b(jsonobject, jsondeserializationcontext);
        }

        public JsonElement a(LootItemCondition lootitemcondition, Type type, JsonSerializationContext jsonserializationcontext) {
            LootItemCondition.a lootitemcondition_a = LootItemConditions.a(lootitemcondition);
            JsonObject jsonobject = new JsonObject();

            lootitemcondition_a.a(jsonobject, lootitemcondition, jsonserializationcontext);
            jsonobject.addProperty("condition", lootitemcondition_a.a().toString());
            return jsonobject;
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((LootItemCondition) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
