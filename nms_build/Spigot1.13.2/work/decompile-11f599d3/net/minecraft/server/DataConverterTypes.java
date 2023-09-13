package net.minecraft.server;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DSL.TypeReference;

public class DataConverterTypes {

    public static final TypeReference a = DataFixTypes.LEVEL;
    public static final TypeReference PLAYER = DataFixTypes.PLAYER;
    public static final TypeReference c = DataFixTypes.CHUNK;
    public static final TypeReference d = DataFixTypes.HOTBAR;
    public static final TypeReference e = DataFixTypes.OPTIONS;
    public static final TypeReference f = DataFixTypes.STRUCTURE;
    public static final TypeReference g = DataFixTypes.STATS;
    public static final TypeReference h = DataFixTypes.SAVED_DATA;
    public static final TypeReference i = DataFixTypes.ADVANCEMENTS;
    public static final TypeReference j = () -> {
        return "block_entity";
    };
    public static final TypeReference ITEM_STACK = () -> {
        return "item_stack";
    };
    public static final TypeReference l = () -> {
        return "block_state";
    };
    public static final TypeReference m = () -> {
        return "entity_name";
    };
    public static final TypeReference n = () -> {
        return "entity_tree";
    };
    public static final TypeReference ENTITY = () -> {
        return "entity";
    };
    public static final TypeReference p = () -> {
        return "block_name";
    };
    public static final TypeReference q = () -> {
        return "item_name";
    };
    public static final TypeReference r = () -> {
        return "untagged_spawner";
    };
    public static final TypeReference s = () -> {
        return "structure_feature";
    };
    public static final TypeReference t = () -> {
        return "objective";
    };
    public static final TypeReference u = () -> {
        return "team";
    };
    public static final TypeReference v = () -> {
        return "recipe";
    };
    public static final TypeReference w = () -> {
        return "biome";
    };
}
