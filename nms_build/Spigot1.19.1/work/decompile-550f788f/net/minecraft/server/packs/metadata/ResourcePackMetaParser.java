package net.minecraft.server.packs.metadata;

import com.google.gson.JsonObject;

public interface ResourcePackMetaParser<T> {

    String getMetadataSectionName();

    T fromJson(JsonObject jsonobject);
}
