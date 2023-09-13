package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV1022 extends Schema {

    public DataConverterSchemaV1022(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.v, () -> {
            return DSL.constType(DSL.namespacedString());
        });
        schema.registerType(false, DataConverterTypes.PLAYER, () -> {
            return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", DataConverterTypes.n.in(schema)), "Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "EnderItems", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", DataConverterTypes.n.in(schema), "ShoulderEntityRight", DataConverterTypes.n.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(DataConverterTypes.v.in(schema)), "toBeDisplayed", DSL.list(DataConverterTypes.v.in(schema)))));
        });
        schema.registerType(false, DataConverterTypes.d, () -> {
            return DSL.compoundList(DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
    }
}
