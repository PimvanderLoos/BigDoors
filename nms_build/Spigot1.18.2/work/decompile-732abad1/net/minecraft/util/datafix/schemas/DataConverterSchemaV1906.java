package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV1906 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1906(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        registerInventory(schema, map, "minecraft:barrel");
        registerInventory(schema, map, "minecraft:smoker");
        registerInventory(schema, map, "minecraft:blast_furnace");
        schema.register(map, "minecraft:lectern", (s) -> {
            return DSL.optionalFields("Book", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.registerSimple(map, "minecraft:bell");
        return map;
    }

    protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
    }
}
