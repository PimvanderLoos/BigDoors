package net.minecraft.server.packs.metadata;

import com.google.gson.JsonObject;

public interface ResourcePackMetaParser<T> {

    String a();

    T a(JsonObject jsonobject);
}
