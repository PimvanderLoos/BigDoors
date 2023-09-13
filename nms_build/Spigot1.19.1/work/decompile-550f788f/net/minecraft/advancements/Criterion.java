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

    @Nullable
    private final CriterionInstance trigger;

    public Criterion(CriterionInstance criterioninstance) {
        this.trigger = criterioninstance;
    }

    public Criterion() {
        this.trigger = null;
    }

    public void serializeToNetwork(PacketDataSerializer packetdataserializer) {}

    public static Criterion criterionFromJson(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "trigger"));
        CriterionTrigger<?> criteriontrigger = CriterionTriggers.getCriterion(minecraftkey);

        if (criteriontrigger == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + minecraftkey);
        } else {
            CriterionInstance criterioninstance = criteriontrigger.createInstance(ChatDeserializer.getAsJsonObject(jsonobject, "conditions", new JsonObject()), lootdeserializationcontext);

            return new Criterion(criterioninstance);
        }
    }

    public static Criterion criterionFromNetwork(PacketDataSerializer packetdataserializer) {
        return new Criterion();
    }

    public static Map<String, Criterion> criteriaFromJson(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
        Map<String, Criterion> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry) iterator.next();

            map.put((String) entry.getKey(), criterionFromJson(ChatDeserializer.convertToJsonObject((JsonElement) entry.getValue(), "criterion"), lootdeserializationcontext));
        }

        return map;
    }

    public static Map<String, Criterion> criteriaFromNetwork(PacketDataSerializer packetdataserializer) {
        return packetdataserializer.readMap(PacketDataSerializer::readUtf, Criterion::criterionFromNetwork);
    }

    public static void serializeToNetwork(Map<String, Criterion> map, PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeMap(map, PacketDataSerializer::writeUtf, (packetdataserializer1, criterion) -> {
            criterion.serializeToNetwork(packetdataserializer1);
        });
    }

    @Nullable
    public CriterionInstance getTrigger() {
        return this.trigger;
    }

    public JsonElement serializeToJson() {
        if (this.trigger == null) {
            throw new JsonSyntaxException("Missing trigger");
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("trigger", this.trigger.getCriterion().toString());
            JsonObject jsonobject1 = this.trigger.serializeToJson(LootSerializationContext.INSTANCE);

            if (jsonobject1.size() != 0) {
                jsonobject.add("conditions", jsonobject1);
            }

            return jsonobject;
        }
    }
}
