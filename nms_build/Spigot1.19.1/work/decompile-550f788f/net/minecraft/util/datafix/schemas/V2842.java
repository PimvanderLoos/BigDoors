package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class V2842 extends DataConverterSchemaNamed {

    public V2842(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.CHUNK, () -> {
            return DSL.optionalFields("entities", DSL.list(DataConverterTypes.ENTITY_TREE.in(schema)), "block_entities", DSL.list(DSL.or(DataConverterTypes.BLOCK_ENTITY.in(schema), DSL.remainder())), "block_ticks", DSL.list(DSL.fields("i", DataConverterTypes.BLOCK_NAME.in(schema))), "sections", DSL.list(DSL.optionalFields("biomes", DSL.optionalFields("palette", DSL.list(DataConverterTypes.BIOME.in(schema))), "block_states", DSL.optionalFields("palette", DSL.list(DataConverterTypes.BLOCK_STATE.in(schema))))), "structures", DSL.optionalFields("starts", DSL.compoundList(DataConverterTypes.STRUCTURE_FEATURE.in(schema))));
        });
    }
}
