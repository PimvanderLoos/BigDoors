package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV2100 extends DataConverterSchemaNamed {

    public DataConverterSchemaV2100(int i, Schema schema) {
        super(i, schema);
    }

    protected static void a(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DataConverterSchemaV100.a(schema);
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        a(schema, map, "minecraft:bee");
        a(schema, map, "minecraft:bee_stinger");
        return map;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        schema.register(map, "minecraft:beehive", () -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "Bees", DSL.list(DSL.optionalFields("EntityData", DataConverterTypes.ENTITY_TREE.in(schema))));
        });
        return map;
    }
}
