package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class Criterion {

    private final CriterionInstance a;

    public Criterion(CriterionInstance criterioninstance) {
        this.a = criterioninstance;
    }

    public Criterion() {
        this.a = null;
    }

    public void a(PacketDataSerializer packetdataserializer) {}

    public static Criterion a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "trigger"));
        CriterionTrigger criteriontrigger = CriterionTriggers.a(minecraftkey);

        if (criteriontrigger == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + minecraftkey);
        } else {
            CriterionInstance criterioninstance = criteriontrigger.a(ChatDeserializer.a(jsonobject, "conditions", new JsonObject()), jsondeserializationcontext);

            return new Criterion(criterioninstance);
        }
    }

    public static Criterion b(PacketDataSerializer packetdataserializer) {
        return new Criterion();
    }

    public static Map<String, Criterion> b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        HashMap hashmap = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            hashmap.put(entry.getKey(), a(ChatDeserializer.m((JsonElement) entry.getValue(), "criterion"), jsondeserializationcontext));
        }

        return hashmap;
    }

    public static Map<String, Criterion> c(PacketDataSerializer packetdataserializer) {
        HashMap hashmap = Maps.newHashMap();
        int i = packetdataserializer.g();

        for (int j = 0; j < i; ++j) {
            hashmap.put(packetdataserializer.e(32767), b(packetdataserializer));
        }

        return hashmap;
    }

    public static void a(Map<String, Criterion> map, PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            packetdataserializer.a((String) entry.getKey());
            ((Criterion) entry.getValue()).a(packetdataserializer);
        }

    }

    @Nullable
    public CriterionInstance a() {
        return this.a;
    }
}
