package net.minecraft.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ResourcePackInfoDeserializer implements ResourcePackMetaParser<ResourcePackInfo> {

    public ResourcePackInfoDeserializer() {}

    public ResourcePackInfo b(JsonObject jsonobject) {
        IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(jsonobject.get("description"));

        if (ichatbasecomponent == null) {
            throw new JsonParseException("Invalid/missing description!");
        } else {
            int i = ChatDeserializer.n(jsonobject, "pack_format");

            return new ResourcePackInfo(ichatbasecomponent, i);
        }
    }

    public String a() {
        return "pack";
    }

    public Object a(JsonObject jsonobject) {
        return this.b(jsonobject);
    }
}
