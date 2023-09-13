package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV1466 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1466(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.c, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(DataConverterTypes.n.in(schema)), "TileEntities", DSL.list(DataConverterTypes.j.in(schema)), "TileTicks", DSL.list(DSL.fields("i", DataConverterTypes.p.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(DataConverterTypes.l.in(schema)))), "Structures", DSL.optionalFields("Starts", DSL.compoundList(DataConverterTypes.s.in(schema)))));
        });
        schema.registerType(false, DataConverterTypes.s, () -> {
            return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", DataConverterTypes.l.in(schema), "CB", DataConverterTypes.l.in(schema), "CC", DataConverterTypes.l.in(schema), "CD", DataConverterTypes.l.in(schema))), "biome", DataConverterTypes.w.in(schema));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        map.put("DUMMY", DSL::remainder);
        return map;
    }
}
