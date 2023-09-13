package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV2509 extends DataConverterSchemaNamed {

    public DataConverterSchemaV2509(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        map.remove("minecraft:zombie_pigman");
        schema.register(map, "minecraft:zombified_piglin", () -> {
            return DataConverterSchemaV100.equipment(schema);
        });
        return map;
    }
}
