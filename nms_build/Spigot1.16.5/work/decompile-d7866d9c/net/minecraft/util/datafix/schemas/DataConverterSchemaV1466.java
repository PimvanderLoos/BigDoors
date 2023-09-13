package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV1466 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1466(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.CHUNK, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(DataConverterTypes.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(DataConverterTypes.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", DataConverterTypes.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(DataConverterTypes.BLOCK_STATE.in(schema)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(DataConverterTypes.STRUCTURE_FEATURE.in(schema)))));
        });
        schema.registerType(false, DataConverterTypes.STRUCTURE_FEATURE, () -> {
            return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", DataConverterTypes.BLOCK_STATE.in(schema), "CB", DataConverterTypes.BLOCK_STATE.in(schema), "CC", DataConverterTypes.BLOCK_STATE.in(schema), "CD", DataConverterTypes.BLOCK_STATE.in(schema))), "biome", DataConverterTypes.BIOME.in(schema));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        map.put("DUMMY", DSL::remainder);
        return map;
    }
}
