package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV1451_3 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1451_3(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        schema.registerSimple(map, "minecraft:egg");
        schema.registerSimple(map, "minecraft:ender_pearl");
        schema.registerSimple(map, "minecraft:fireball");
        schema.register(map, "minecraft:potion", (s) -> {
            return DSL.optionalFields("Potion", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.registerSimple(map, "minecraft:small_fireball");
        schema.registerSimple(map, "minecraft:snowball");
        schema.registerSimple(map, "minecraft:wither_skull");
        schema.registerSimple(map, "minecraft:xp_bottle");
        schema.register(map, "minecraft:arrow", () -> {
            return DSL.optionalFields("inBlockState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        schema.register(map, "minecraft:enderman", () -> {
            return DSL.optionalFields("carriedBlockState", DataConverterTypes.BLOCK_STATE.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.register(map, "minecraft:falling_block", () -> {
            return DSL.optionalFields("BlockState", DataConverterTypes.BLOCK_STATE.in(schema), "TileEntityData", DataConverterTypes.BLOCK_ENTITY.in(schema));
        });
        schema.register(map, "minecraft:spectral_arrow", () -> {
            return DSL.optionalFields("inBlockState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        schema.register(map, "minecraft:chest_minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.register(map, "minecraft:commandblock_minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        schema.register(map, "minecraft:furnace_minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        schema.register(map, "minecraft:hopper_minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.register(map, "minecraft:minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        schema.register(map, "minecraft:spawner_minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema), DataConverterTypes.UNTAGGED_SPAWNER.in(schema));
        });
        schema.register(map, "minecraft:tnt_minecart", () -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        return map;
    }
}
