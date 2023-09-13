package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV2551 extends DataConverterSchemaNamed {

    public DataConverterSchemaV2551(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.WORLD_GEN_SETTINGS, () -> {
            return DSL.fields("dimensions", DSL.compoundList(DSL.constType(namespacedString()), DSL.fields("generator", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL::remainder, "minecraft:flat", () -> {
                return DSL.optionalFields("settings", DSL.optionalFields("biome", DataConverterTypes.BIOME.in(schema), "layers", DSL.list(DSL.optionalFields("block", DataConverterTypes.BLOCK_NAME.in(schema)))));
            }, "minecraft:noise", () -> {
                return DSL.optionalFields("biome_source", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:fixed", () -> {
                    return DSL.fields("biome", DataConverterTypes.BIOME.in(schema));
                }, "minecraft:multi_noise", () -> {
                    return DSL.list(DSL.fields("biome", DataConverterTypes.BIOME.in(schema)));
                }, "minecraft:checkerboard", () -> {
                    return DSL.fields("biomes", DSL.list(DataConverterTypes.BIOME.in(schema)));
                }, "minecraft:vanilla_layered", DSL::remainder, "minecraft:the_end", DSL::remainder)), "settings", DSL.or(DSL.constType(DSL.string()), DSL.optionalFields("default_block", DataConverterTypes.BLOCK_NAME.in(schema), "default_fluid", DataConverterTypes.BLOCK_NAME.in(schema))));
            })))));
        });
    }
}
