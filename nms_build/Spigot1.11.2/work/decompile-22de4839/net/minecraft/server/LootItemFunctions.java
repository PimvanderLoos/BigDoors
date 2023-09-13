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

public class LootItemFunctions {

    private static final Map<MinecraftKey, LootItemFunction.a<?>> a = Maps.newHashMap();
    private static final Map<Class<? extends LootItemFunction>, LootItemFunction.a<?>> b = Maps.newHashMap();

    public static <T extends LootItemFunction> void a(LootItemFunction.a<? extends T> lootitemfunction_a) {
        MinecraftKey minecraftkey = lootitemfunction_a.a();
        Class oclass = lootitemfunction_a.b();

        if (LootItemFunctions.a.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("Can\'t re-register item function name " + minecraftkey);
        } else if (LootItemFunctions.b.containsKey(oclass)) {
            throw new IllegalArgumentException("Can\'t re-register item function class " + oclass.getName());
        } else {
            LootItemFunctions.a.put(minecraftkey, lootitemfunction_a);
            LootItemFunctions.b.put(oclass, lootitemfunction_a);
        }
    }

    public static LootItemFunction.a<?> a(MinecraftKey minecraftkey) {
        LootItemFunction.a lootitemfunction_a = (LootItemFunction.a) LootItemFunctions.a.get(minecraftkey);

        if (lootitemfunction_a == null) {
            throw new IllegalArgumentException("Unknown loot item function \'" + minecraftkey + "\'");
        } else {
            return lootitemfunction_a;
        }
    }

    public static <T extends LootItemFunction> LootItemFunction.a<T> a(T t0) {
        LootItemFunction.a lootitemfunction_a = (LootItemFunction.a) LootItemFunctions.b.get(t0.getClass());

        if (lootitemfunction_a == null) {
            throw new IllegalArgumentException("Unknown loot item function " + t0);
        } else {
            return lootitemfunction_a;
        }
    }

    static {
        a((LootItemFunction.a) (new LootItemFunctionSetCount.a()));
        a((LootItemFunction.a) (new LootItemFunctionSetData.a()));
        a((LootItemFunction.a) (new LootEnchantLevel.a()));
        a((LootItemFunction.a) (new LootItemFunctionEnchant.a()));
        a((LootItemFunction.a) (new LootItemFunctionSetTag.a()));
        a((LootItemFunction.a) (new LootItemFunctionSmelt.a()));
        a((LootItemFunction.a) (new LootEnchantFunction.a()));
        a((LootItemFunction.a) (new LootItemFunctionSetDamage.a()));
        a((LootItemFunction.a) (new LootItemFunctionSetAttribute.b()));
    }

    public static class a implements JsonDeserializer<LootItemFunction>, JsonSerializer<LootItemFunction> {

        public a() {}

        public LootItemFunction a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "function");
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "function"));

            LootItemFunction.a lootitemfunction_a;

            try {
                lootitemfunction_a = LootItemFunctions.a(minecraftkey);
            } catch (IllegalArgumentException illegalargumentexception) {
                throw new JsonSyntaxException("Unknown function \'" + minecraftkey + "\'");
            }

            return lootitemfunction_a.b(jsonobject, jsondeserializationcontext, (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class));
        }

        public JsonElement a(LootItemFunction lootitemfunction, Type type, JsonSerializationContext jsonserializationcontext) {
            LootItemFunction.a lootitemfunction_a = LootItemFunctions.a(lootitemfunction);
            JsonObject jsonobject = new JsonObject();

            lootitemfunction_a.a(jsonobject, lootitemfunction, jsonserializationcontext);
            jsonobject.addProperty("function", lootitemfunction_a.a().toString());
            if (lootitemfunction.a() != null && lootitemfunction.a().length > 0) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(lootitemfunction.a()));
            }

            return jsonobject;
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((LootItemFunction) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
