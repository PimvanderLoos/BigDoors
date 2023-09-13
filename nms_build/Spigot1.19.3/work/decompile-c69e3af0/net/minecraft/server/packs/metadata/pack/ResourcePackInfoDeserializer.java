package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ChatDeserializer;

public class ResourcePackInfoDeserializer implements MetadataSectionType<ResourcePackInfo> {

    public ResourcePackInfoDeserializer() {}

    @Override
    public ResourcePackInfo fromJson(JsonObject jsonobject) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(jsonobject.get("description"));

        if (ichatmutablecomponent == null) {
            throw new JsonParseException("Invalid/missing description!");
        } else {
            int i = ChatDeserializer.getAsInt(jsonobject, "pack_format");

            return new ResourcePackInfo(ichatmutablecomponent, i);
        }
    }

    public JsonObject toJson(ResourcePackInfo resourcepackinfo) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.add("description", IChatBaseComponent.ChatSerializer.toJsonTree(resourcepackinfo.getDescription()));
        jsonobject.addProperty("pack_format", resourcepackinfo.getPackFormat());
        return jsonobject;
    }

    @Override
    public String getMetadataSectionName() {
        return "pack";
    }
}
