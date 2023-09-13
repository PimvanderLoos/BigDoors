package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataConverterSchemaV705 extends DataConverterSchemaNamed {

    protected static final HookFunction a = new HookFunction() {
        public <T> T apply(DynamicOps<T> dynamicops, T t0) {
            return DataConverterSchemaV99.a(new Dynamic(dynamicops, t0), DataConverterSchemaV704.a, "minecraft:armor_stand");
        }
    };

    public DataConverterSchemaV705(int i, Schema schema) {
        super(i, schema);
    }

    protected static void a(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DataConverterSchemaV100.a(schema);
        });
    }

    protected static void b(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DSL.optionalFields("inTile", DataConverterTypes.p.in(schema));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap hashmap = Maps.newHashMap();

        schema.registerSimple(hashmap, "minecraft:area_effect_cloud");
        a(schema, hashmap, "minecraft:armor_stand");
        schema.register(hashmap, "minecraft:arrow", (s) -> {
            return DSL.optionalFields("inTile", DataConverterTypes.p.in(schema));
        });
        a(schema, hashmap, "minecraft:bat");
        a(schema, hashmap, "minecraft:blaze");
        schema.registerSimple(hashmap, "minecraft:boat");
        a(schema, hashmap, "minecraft:cave_spider");
        schema.register(hashmap, "minecraft:chest_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        a(schema, hashmap, "minecraft:chicken");
        schema.register(hashmap, "minecraft:commandblock_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema));
        });
        a(schema, hashmap, "minecraft:cow");
        a(schema, hashmap, "minecraft:creeper");
        schema.register(hashmap, "minecraft:donkey", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.registerSimple(hashmap, "minecraft:dragon_fireball");
        b(schema, hashmap, "minecraft:egg");
        a(schema, hashmap, "minecraft:elder_guardian");
        schema.registerSimple(hashmap, "minecraft:ender_crystal");
        a(schema, hashmap, "minecraft:ender_dragon");
        schema.register(hashmap, "minecraft:enderman", (s) -> {
            return DSL.optionalFields("carried", DataConverterTypes.p.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:endermite");
        b(schema, hashmap, "minecraft:ender_pearl");
        schema.registerSimple(hashmap, "minecraft:eye_of_ender_signal");
        schema.register(hashmap, "minecraft:falling_block", (s) -> {
            return DSL.optionalFields("Block", DataConverterTypes.p.in(schema), "TileEntityData", DataConverterTypes.j.in(schema));
        });
        b(schema, hashmap, "minecraft:fireball");
        schema.register(hashmap, "minecraft:fireworks_rocket", (s) -> {
            return DSL.optionalFields("FireworksItem", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(hashmap, "minecraft:furnace_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema));
        });
        a(schema, hashmap, "minecraft:ghast");
        a(schema, hashmap, "minecraft:giant");
        a(schema, hashmap, "minecraft:guardian");
        schema.register(hashmap, "minecraft:hopper_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.register(hashmap, "minecraft:horse", (s) -> {
            return DSL.optionalFields("ArmorItem", DataConverterTypes.ITEM_STACK.in(schema), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:husk");
        schema.register(hashmap, "minecraft:item", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(hashmap, "minecraft:item_frame", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.registerSimple(hashmap, "minecraft:leash_knot");
        a(schema, hashmap, "minecraft:magma_cube");
        schema.register(hashmap, "minecraft:minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema));
        });
        a(schema, hashmap, "minecraft:mooshroom");
        schema.register(hashmap, "minecraft:mule", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:ocelot");
        schema.registerSimple(hashmap, "minecraft:painting");
        schema.registerSimple(hashmap, "minecraft:parrot");
        a(schema, hashmap, "minecraft:pig");
        a(schema, hashmap, "minecraft:polar_bear");
        schema.register(hashmap, "minecraft:potion", (s) -> {
            return DSL.optionalFields("Potion", DataConverterTypes.ITEM_STACK.in(schema), "inTile", DataConverterTypes.p.in(schema));
        });
        a(schema, hashmap, "minecraft:rabbit");
        a(schema, hashmap, "minecraft:sheep");
        a(schema, hashmap, "minecraft:shulker");
        schema.registerSimple(hashmap, "minecraft:shulker_bullet");
        a(schema, hashmap, "minecraft:silverfish");
        a(schema, hashmap, "minecraft:skeleton");
        schema.register(hashmap, "minecraft:skeleton_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:slime");
        b(schema, hashmap, "minecraft:small_fireball");
        b(schema, hashmap, "minecraft:snowball");
        a(schema, hashmap, "minecraft:snowman");
        schema.register(hashmap, "minecraft:spawner_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema), DataConverterTypes.r.in(schema));
        });
        schema.register(hashmap, "minecraft:spectral_arrow", (s) -> {
            return DSL.optionalFields("inTile", DataConverterTypes.p.in(schema));
        });
        a(schema, hashmap, "minecraft:spider");
        a(schema, hashmap, "minecraft:squid");
        a(schema, hashmap, "minecraft:stray");
        schema.registerSimple(hashmap, "minecraft:tnt");
        schema.register(hashmap, "minecraft:tnt_minecart", (s) -> {
            return DSL.optionalFields("DisplayTile", DataConverterTypes.p.in(schema));
        });
        schema.register(hashmap, "minecraft:villager", (s) -> {
            return DSL.optionalFields("Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", DataConverterTypes.ITEM_STACK.in(schema), "buyB", DataConverterTypes.ITEM_STACK.in(schema), "sell", DataConverterTypes.ITEM_STACK.in(schema)))), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:villager_golem");
        a(schema, hashmap, "minecraft:witch");
        a(schema, hashmap, "minecraft:wither");
        a(schema, hashmap, "minecraft:wither_skeleton");
        b(schema, hashmap, "minecraft:wither_skull");
        a(schema, hashmap, "minecraft:wolf");
        b(schema, hashmap, "minecraft:xp_bottle");
        schema.registerSimple(hashmap, "minecraft:xp_orb");
        a(schema, hashmap, "minecraft:zombie");
        schema.register(hashmap, "minecraft:zombie_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:zombie_pigman");
        a(schema, hashmap, "minecraft:zombie_villager");
        schema.registerSimple(hashmap, "minecraft:evocation_fangs");
        a(schema, hashmap, "minecraft:evocation_illager");
        schema.registerSimple(hashmap, "minecraft:illusion_illager");
        schema.register(hashmap, "minecraft:llama", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), "DecorItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.registerSimple(hashmap, "minecraft:llama_spit");
        a(schema, hashmap, "minecraft:vex");
        a(schema, hashmap, "minecraft:vindication_illager");
        return hashmap;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(true, DataConverterTypes.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), map);
        });
        schema.registerType(true, DataConverterTypes.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", DataConverterTypes.q.in(schema), "tag", DSL.optionalFields("EntityTag", DataConverterTypes.n.in(schema), "BlockEntityTag", DataConverterTypes.j.in(schema), "CanDestroy", DSL.list(DataConverterTypes.p.in(schema)), "CanPlaceOn", DSL.list(DataConverterTypes.p.in(schema)))), DataConverterSchemaV705.a, HookFunction.IDENTITY);
        });
    }
}
