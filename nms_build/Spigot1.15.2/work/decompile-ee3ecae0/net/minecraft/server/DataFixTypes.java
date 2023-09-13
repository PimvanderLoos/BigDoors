package net.minecraft.server;

import com.mojang.datafixers.DSL.TypeReference;

public enum DataFixTypes {

    LEVEL(DataConverterTypes.LEVEL), PLAYER(DataConverterTypes.PLAYER), CHUNK(DataConverterTypes.CHUNK), HOTBAR(DataConverterTypes.HOTBAR), OPTIONS(DataConverterTypes.OPTIONS), STRUCTURE(DataConverterTypes.STRUCTURE), STATS(DataConverterTypes.STATS), SAVED_DATA(DataConverterTypes.SAVED_DATA), ADVANCEMENTS(DataConverterTypes.ADVANCEMENTS), POI_CHUNK(DataConverterTypes.POI_CHUNK);

    private final TypeReference k;

    private DataFixTypes(TypeReference typereference) {
        this.k = typereference;
    }

    public TypeReference a() {
        return this.k;
    }
}
