package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV1451_6 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1451_6(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        Supplier<TypeTemplate> supplier = () -> {
            return DSL.compoundList(DataConverterTypes.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
        };

        schema.registerType(false, DataConverterTypes.STATS, () -> {
            return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(DataConverterTypes.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate) supplier.get(), "minecraft:used", (TypeTemplate) supplier.get(), "minecraft:broken", (TypeTemplate) supplier.get(), "minecraft:picked_up", (TypeTemplate) supplier.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier.get(), "minecraft:killed", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(a()), DSL.constType(DSL.intType())))));
        });
    }
}
