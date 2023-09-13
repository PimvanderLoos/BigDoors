package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV2501 extends DataConverterSchemaNamed {

    public DataConverterSchemaV2501(int i, Schema schema) {
        super(i, schema);
    }

    private static void a(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "RecipesUsed", DSL.compoundList(DataConverterTypes.RECIPE.in(schema), DSL.constType(DSL.intType())));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        a(schema, map, "minecraft:furnace");
        a(schema, map, "minecraft:smoker");
        a(schema, map, "minecraft:blast_furnace");
        return map;
    }
}
