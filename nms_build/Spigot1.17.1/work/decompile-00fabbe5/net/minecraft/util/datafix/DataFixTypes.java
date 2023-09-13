package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public enum DataFixTypes {

    LEVEL(DataConverterTypes.LEVEL), PLAYER(DataConverterTypes.PLAYER), CHUNK(DataConverterTypes.CHUNK), HOTBAR(DataConverterTypes.HOTBAR), OPTIONS(DataConverterTypes.OPTIONS), STRUCTURE(DataConverterTypes.STRUCTURE), STATS(DataConverterTypes.STATS), SAVED_DATA(DataConverterTypes.SAVED_DATA), ADVANCEMENTS(DataConverterTypes.ADVANCEMENTS), POI_CHUNK(DataConverterTypes.POI_CHUNK), WORLD_GEN_SETTINGS(DataConverterTypes.WORLD_GEN_SETTINGS), ENTITY_CHUNK(DataConverterTypes.ENTITY_CHUNK);

    private final TypeReference type;

    private DataFixTypes(TypeReference typereference) {
        this.type = typereference;
    }

    public TypeReference a() {
        return this.type;
    }
}
