package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV1125 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1125(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        schema.registerSimple(map, "minecraft:bed");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.ADVANCEMENTS, () -> {
            return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))));
        });
        schema.registerType(false, DataConverterTypes.BIOME, () -> {
            return DSL.constType(a());
        });
        schema.registerType(false, DataConverterTypes.ENTITY_NAME, () -> {
            return DSL.constType(a());
        });
    }
}
