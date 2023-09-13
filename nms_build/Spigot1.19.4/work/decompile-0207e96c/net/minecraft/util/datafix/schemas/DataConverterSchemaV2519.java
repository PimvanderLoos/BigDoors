package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV2519 extends DataConverterSchemaNamed {

    public DataConverterSchemaV2519(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        schema.register(map, "minecraft:strider", () -> {
            return DataConverterSchemaV100.equipment(schema);
        });
        return map;
    }
}
