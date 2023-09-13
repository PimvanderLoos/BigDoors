package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV705 extends DataConverterSchemaNamed {

    protected static final HookFunction ADD_NAMES = new HookFunction() {
        public <T> T apply(DynamicOps<T> dynamicops, T t0) {
            return DataConverterSchemaV99.addNames(new Dynamic(dynamicops, t0), DataConverterSchemaV704.ITEM_TO_BLOCKENTITY, "minecraft:armor_stand");
        }
    };

    public DataConverterSchemaV705(int i, Schema schema) {
        super(i, schema);
    }

    protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DataConverterSchemaV100.equipment(schema);
        });
    }

    protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DSL.optionalFields("inTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

        schema.registerSimple(map, "minecraft:area_effect_cloud");
        registerMob(schema, map, "minecraft:armor_stand");
        schema.register(map, "minecraft:arrow", (s) -> {
            return DSL.optionalFields("inTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        registerMob(schema, map, "minecraft:bat");
        registerMob(schema, map, "minecraft:blaze");
        schema.registerSimple(map, "minecraft:boat");
        registerMob(schema, map, "minecraft:cave_spider");
        schema.register(map, "minecraft:chest_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        registerMob(schema, map, "minecraft:chicken");
        schema.register(map, "minecraft:commandblock_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        registerMob(schema, map, "minecraft:cow");
        registerMob(schema, map, "minecraft:creeper");
        schema.register(map, "minecraft:donkey", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        schema.registerSimple(map, "minecraft:dragon_fireball");
        registerThrowableProjectile(schema, map, "minecraft:egg");
        registerMob(schema, map, "minecraft:elder_guardian");
        schema.registerSimple(map, "minecraft:ender_crystal");
        registerMob(schema, map, "minecraft:ender_dragon");
        schema.register(map, "minecraft:enderman", (s) -> {
            return DSL.optionalFields("carried", DataConverterTypes.BLOCK_NAME.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        registerMob(schema, map, "minecraft:endermite");
        registerThrowableProjectile(schema, map, "minecraft:ender_pearl");
        schema.registerSimple(map, "minecraft:eye_of_ender_signal");
        schema.register(map, "minecraft:falling_block", (s) -> {
            return DSL.optionalFields("Block", DataConverterTypes.BLOCK_NAME.in(schema), "TileEntityData", DataConverterTypes.BLOCK_ENTITY.in(schema));
        });
        registerThrowableProjectile(schema, map, "minecraft:fireball");
        schema.register(map, "minecraft:fireworks_rocket", (s) -> {
            return DSL.optionalFields("FireworksItem", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(map, "minecraft:furnace_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        registerMob(schema, map, "minecraft:ghast");
        registerMob(schema, map, "minecraft:giant");
        registerMob(schema, map, "minecraft:guardian");
        schema.register(map, "minecraft:hopper_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.register(map, "minecraft:horse", (s) -> {
            return DSL.optionalFields("ArmorItem", DataConverterTypes.ITEM_STACK.in(schema), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        registerMob(schema, map, "minecraft:husk");
        schema.register(map, "minecraft:item", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(map, "minecraft:item_frame", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.registerSimple(map, "minecraft:leash_knot");
        registerMob(schema, map, "minecraft:magma_cube");
        schema.register(map, "minecraft:minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        registerMob(schema, map, "minecraft:mooshroom");
        schema.register(map, "minecraft:mule", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        registerMob(schema, map, "minecraft:ocelot");
        schema.registerSimple(map, "minecraft:painting");
        schema.registerSimple(map, "minecraft:parrot");
        registerMob(schema, map, "minecraft:pig");
        registerMob(schema, map, "minecraft:polar_bear");
        schema.register(map, "minecraft:potion", (s) -> {
            return DSL.optionalFields("Potion", DataConverterTypes.ITEM_STACK.in(schema), "inTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        registerMob(schema, map, "minecraft:rabbit");
        registerMob(schema, map, "minecraft:sheep");
        registerMob(schema, map, "minecraft:shulker");
        schema.registerSimple(map, "minecraft:shulker_bullet");
        registerMob(schema, map, "minecraft:silverfish");
        registerMob(schema, map, "minecraft:skeleton");
        schema.register(map, "minecraft:skeleton_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        registerMob(schema, map, "minecraft:slime");
        registerThrowableProjectile(schema, map, "minecraft:small_fireball");
        registerThrowableProjectile(schema, map, "minecraft:snowball");
        registerMob(schema, map, "minecraft:snowman");
        schema.register(map, "minecraft:spawner_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema), DataConverterTypes.UNTAGGED_SPAWNER.in(schema));
        });
        schema.register(map, "minecraft:spectral_arrow", (s) -> {
            return DSL.optionalFields("inTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        registerMob(schema, map, "minecraft:spider");
        registerMob(schema, map, "minecraft:squid");
        registerMob(schema, map, "minecraft:stray");
        schema.registerSimple(map, "minecraft:tnt");
        schema.register(map, "minecraft:tnt_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.BLOCK_NAME.in(schema));
        });
        schema.register(map, "minecraft:villager", (s) -> {
            return DSL.optionalFields("Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", DataConverterTypes.ITEM_STACK.in(schema), "buyB", DataConverterTypes.ITEM_STACK.in(schema), "sell", DataConverterTypes.ITEM_STACK.in(schema)))), DataConverterSchemaV100.equipment(schema));
        });
        registerMob(schema, map, "minecraft:villager_golem");
        registerMob(schema, map, "minecraft:witch");
        registerMob(schema, map, "minecraft:wither");
        registerMob(schema, map, "minecraft:wither_skeleton");
        registerThrowableProjectile(schema, map, "minecraft:wither_skull");
        registerMob(schema, map, "minecraft:wolf");
        registerThrowableProjectile(schema, map, "minecraft:xp_bottle");
        schema.registerSimple(map, "minecraft:xp_orb");
        registerMob(schema, map, "minecraft:zombie");
        schema.register(map, "minecraft:zombie_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        registerMob(schema, map, "minecraft:zombie_pigman");
        registerMob(schema, map, "minecraft:zombie_villager");
        schema.registerSimple(map, "minecraft:evocation_fangs");
        registerMob(schema, map, "minecraft:evocation_illager");
        schema.registerSimple(map, "minecraft:illusion_illager");
        schema.register(map, "minecraft:llama", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), "DecorItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.equipment(schema));
        });
        schema.registerSimple(map, "minecraft:llama_spit");
        registerMob(schema, map, "minecraft:vex");
        registerMob(schema, map, "minecraft:vindication_illager");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(true, DataConverterTypes.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", namespacedString(), map);
        });
        schema.registerType(true, DataConverterTypes.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", DataConverterTypes.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", DataConverterTypes.ENTITY_TREE.in(schema), "BlockEntityTag", DataConverterTypes.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(DataConverterTypes.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(DataConverterTypes.BLOCK_NAME.in(schema)), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)))), DataConverterSchemaV705.ADD_NAMES, HookFunction.IDENTITY);
        });
    }
}
