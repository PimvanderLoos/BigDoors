package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.util.ChatDeserializer;

public class ResourcePackInfoDeserializer implements ResourcePackMetaParser<ResourcePackInfo> {

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

    @Override
    public String getMetadataSectionName() {
        return "pack";
    }
}
