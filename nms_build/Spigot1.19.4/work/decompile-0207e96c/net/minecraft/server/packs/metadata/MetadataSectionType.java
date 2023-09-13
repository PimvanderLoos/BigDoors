package net.minecraft.server.packs.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

public interface MetadataSectionType<T> extends ResourcePackMetaParser<T> {

    JsonObject toJson(T t0);

    static <T> MetadataSectionType<T> fromCodec(final String s, final Codec<T> codec) {
        return new MetadataSectionType<T>() {
            @Override
            public String getMetadataSectionName() {
                return s;
            }

            @Override
            public T fromJson(JsonObject jsonobject) {
                return codec.parse(JsonOps.INSTANCE, jsonobject).getOrThrow(false, (s1) -> {
                });
            }

            @Override
            public JsonObject toJson(T t0) {
                return ((JsonElement) codec.encodeStart(JsonOps.INSTANCE, t0).getOrThrow(false, (s1) -> {
                })).getAsJsonObject();
            }
        };
    }
}
