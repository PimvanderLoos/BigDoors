package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class V3081 extends DataConverterSchemaNamed {

    public V3081(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        schema.register(map, "minecraft:warden", () -> {
            return DSL.optionalFields("listener", DSL.optionalFields("event", DSL.optionalFields("game_event", DataConverterTypes.GAME_EVENT_NAME.in(schema))), DataConverterSchemaV100.equipment(schema));
        });
        return map;
    }
}
