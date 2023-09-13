package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class V3325 extends DataConverterSchemaNamed {

    public V3325(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        schema.register(map, "minecraft:item_display", (s) -> {
            return DSL.optionalFields("item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(map, "minecraft:block_display", (s) -> {
            return DSL.optionalFields("block_state", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        schema.registerSimple(map, "minecraft:text_display");
        return map;
    }
}
