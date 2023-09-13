package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.advancements.critereon.LootSerializationContext;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;

public class Criterion {

    private final CriterionInstance trigger;

    public Criterion(CriterionInstance criterioninstance) {
        this.trigger = criterioninstance;
    }

    public Criterion() {
        this.trigger = null;
    }

    public void a(PacketDataSerializer packetdataserializer) {}

    public static Criterion a(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "trigger"));
        CriterionTrigger<?> criteriontrigger = CriterionTriggers.a(minecraftkey);

        if (criteriontrigger == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + minecraftkey);
        } else {
            CriterionInstance criterioninstance = criteriontrigger.a(ChatDeserializer.a(jsonobject, "conditions", new JsonObject()), lootdeserializationcontext);

            return new Criterion(criterioninstance);
        }
    }

    public static Criterion b(PacketDataSerializer packetdataserializer) {
        return new Criterion();
    }

    public static Map<String, Criterion> b(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
        Map<String, Criterion> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry) iterator.next();

            map.put((String) entry.getKey(), a(ChatDeserializer.m((JsonElement) entry.getValue(), "criterion"), lootdeserializationcontext));
        }

        return map;
    }

    public static Map<String, Criterion> c(PacketDataSerializer packetdataserializer) {
        return packetdataserializer.a(PacketDataSerializer::p, Criterion::b);
    }

    public static void a(Map<String, Criterion> map, PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(map, PacketDataSerializer::a, (packetdataserializer1, criterion) -> {
            criterion.a(packetdataserializer1);
        });
    }

    @Nullable
    public CriterionInstance a() {
        return this.trigger;
    }

    public JsonElement b() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("trigger", this.trigger.a().toString());
        JsonObject jsonobject1 = this.trigger.a(LootSerializationContext.INSTANCE);

        if (jsonobject1.size() != 0) {
            jsonobject.add("conditions", jsonobject1);
        }

        return jsonobject;
    }
}
