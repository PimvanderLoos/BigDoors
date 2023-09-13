package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV106 extends Schema {

    public DataConverterSchemaV106(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(true, DataConverterTypes.UNTAGGED_SPAWNER, () -> {
            return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", DataConverterTypes.ENTITY_TREE.in(schema))), "SpawnData", DataConverterTypes.ENTITY_TREE.in(schema));
        });
    }
}
