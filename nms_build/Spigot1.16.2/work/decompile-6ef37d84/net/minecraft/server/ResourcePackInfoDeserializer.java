package net.minecraft.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ResourcePackInfoDeserializer implements ResourcePackMetaParser<ResourcePackInfo> {

    public ResourcePackInfoDeserializer() {}

    @Override
    public ResourcePackInfo a(JsonObject jsonobject) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(jsonobject.get("description"));

        if (ichatmutablecomponent == null) {
            throw new JsonParseException("Invalid/missing description!");
        } else {
            int i = ChatDeserializer.n(jsonobject, "pack_format");

            return new ResourcePackInfo(ichatmutablecomponent, i);
        }
    }

    @Override
    public String a() {
        return "pack";
    }
}
