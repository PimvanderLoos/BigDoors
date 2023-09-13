package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataConverterSchemaV1460 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1460(int i, Schema schema) {
        super(i, schema);
    }

    protected static void a(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DataConverterSchemaV100.a(schema);
        });
    }

    protected static void b(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap hashmap = Maps.newHashMap();

        schema.registerSimple(hashmap, "minecraft:area_effect_cloud");
        a(schema, hashmap, "minecraft:armor_stand");
        schema.register(hashmap, "minecraft:arrow", (s) -> {
            return DSL.optionalFields("inBlockState", DataConverterTypes.l.in(schema));
        });
        a(schema, hashmap, "minecraft:bat");
        a(schema, hashmap, "minecraft:blaze");
        schema.registerSimple(hashmap, "minecraft:boat");
        a(schema, hashmap, "minecraft:cave_spider");
        schema.register(hashmap, "minecraft:chest_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        a(schema, hashmap, "minecraft:chicken");
        schema.register(hashmap, "minecraft:commandblock_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema));
        });
        a(schema, hashmap, "minecraft:cow");
        a(schema, hashmap, "minecraft:creeper");
        schema.register(hashmap, "minecraft:donkey", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.registerSimple(hashmap, "minecraft:dragon_fireball");
        schema.registerSimple(hashmap, "minecraft:egg");
        a(schema, hashmap, "minecraft:elder_guardian");
        schema.registerSimple(hashmap, "minecraft:ender_crystal");
        a(schema, hashmap, "minecraft:ender_dragon");
        schema.register(hashmap, "minecraft:enderman", (s) -> {
            return DSL.optionalFields("carriedBlockState", DataConverterTypes.l.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:endermite");
        schema.registerSimple(hashmap, "minecraft:ender_pearl");
        schema.registerSimple(hashmap, "minecraft:evocation_fangs");
        a(schema, hashmap, "minecraft:evocation_illager");
        schema.registerSimple(hashmap, "minecraft:eye_of_ender_signal");
        schema.register(hashmap, "minecraft:falling_block", (s) -> {
            return DSL.optionalFields("BlockState", DataConverterTypes.l.in(schema), "TileEntityData", DataConverterTypes.j.in(schema));
        });
        schema.registerSimple(hashmap, "minecraft:fireball");
        schema.register(hashmap, "minecraft:fireworks_rocket", (s) -> {
            return DSL.optionalFields("FireworksItem", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(hashmap, "minecraft:furnace_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema));
        });
        a(schema, hashmap, "minecraft:ghast");
        a(schema, hashmap, "minecraft:giant");
        a(schema, hashmap, "minecraft:guardian");
        schema.register(hashmap, "minecraft:hopper_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.register(hashmap, "minecraft:horse", (s) -> {
            return DSL.optionalFields("ArmorItem", DataConverterTypes.ITEM_STACK.in(schema), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:husk");
        schema.registerSimple(hashmap, "minecraft:illusion_illager");
        schema.register(hashmap, "minecraft:item", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(hashmap, "minecraft:item_frame", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.registerSimple(hashmap, "minecraft:leash_knot");
        schema.register(hashmap, "minecraft:llama", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), "DecorItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.registerSimple(hashmap, "minecraft:llama_spit");
        a(schema, hashmap, "minecraft:magma_cube");
        schema.register(hashmap, "minecraft:minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema));
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
            return DSL.optionalFields("Potion", DataConverterTypes.ITEM_STACK.in(schema));
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
        schema.registerSimple(hashmap, "minecraft:small_fireball");
        schema.registerSimple(hashmap, "minecraft:snowball");
        a(schema, hashmap, "minecraft:snowman");
        schema.register(hashmap, "minecraft:spawner_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema), DataConverterTypes.r.in(schema));
        });
        schema.register(hashmap, "minecraft:spectral_arrow", (s) -> {
            return DSL.optionalFields("inBlockState", DataConverterTypes.l.in(schema));
        });
        a(schema, hashmap, "minecraft:spider");
        a(schema, hashmap, "minecraft:squid");
        a(schema, hashmap, "minecraft:stray");
        schema.registerSimple(hashmap, "minecraft:tnt");
        schema.register(hashmap, "minecraft:tnt_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.l.in(schema));
        });
        a(schema, hashmap, "minecraft:vex");
        schema.register(hashmap, "minecraft:villager", (s) -> {
            return DSL.optionalFields("Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", DataConverterTypes.ITEM_STACK.in(schema), "buyB", DataConverterTypes.ITEM_STACK.in(schema), "sell", DataConverterTypes.ITEM_STACK.in(schema)))), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:villager_golem");
        a(schema, hashmap, "minecraft:vindication_illager");
        a(schema, hashmap, "minecraft:witch");
        a(schema, hashmap, "minecraft:wither");
        a(schema, hashmap, "minecraft:wither_skeleton");
        schema.registerSimple(hashmap, "minecraft:wither_skull");
        a(schema, hashmap, "minecraft:wolf");
        schema.registerSimple(hashmap, "minecraft:xp_bottle");
        schema.registerSimple(hashmap, "minecraft:xp_orb");
        a(schema, hashmap, "minecraft:zombie");
        schema.register(hashmap, "minecraft:zombie_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, hashmap, "minecraft:zombie_pigman");
        a(schema, hashmap, "minecraft:zombie_villager");
        return hashmap;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap hashmap = Maps.newHashMap();

        b(schema, hashmap, "minecraft:furnace");
        b(schema, hashmap, "minecraft:chest");
        b(schema, hashmap, "minecraft:trapped_chest");
        schema.registerSimple(hashmap, "minecraft:ender_chest");
        schema.register(hashmap, "minecraft:jukebox", (s) -> {
            return DSL.optionalFields("RecordItem", DataConverterTypes.ITEM_STACK.in(schema));
        });
        b(schema, hashmap, "minecraft:dispenser");
        b(schema, hashmap, "minecraft:dropper");
        schema.registerSimple(hashmap, "minecraft:sign");
        schema.register(hashmap, "minecraft:mob_spawner", (s) -> {
            return DataConverterTypes.r.in(schema);
        });
        schema.register(hashmap, "minecraft:piston", (s) -> {
            return DSL.optionalFields("blockState", DataConverterTypes.l.in(schema));
        });
        b(schema, hashmap, "minecraft:brewing_stand");
        schema.registerSimple(hashmap, "minecraft:enchanting_table");
        schema.registerSimple(hashmap, "minecraft:end_portal");
        schema.registerSimple(hashmap, "minecraft:beacon");
        schema.registerSimple(hashmap, "minecraft:skull");
        schema.registerSimple(hashmap, "minecraft:daylight_detector");
        b(schema, hashmap, "minecraft:hopper");
        schema.registerSimple(hashmap, "minecraft:comparator");
        schema.registerSimple(hashmap, "minecraft:banner");
        schema.registerSimple(hashmap, "minecraft:structure_block");
        schema.registerSimple(hashmap, "minecraft:end_gateway");
        schema.registerSimple(hashmap, "minecraft:command_block");
        b(schema, hashmap, "minecraft:shulker_box");
        schema.registerSimple(hashmap, "minecraft:bed");
        return hashmap;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        schema.registerType(false, DataConverterTypes.a, DSL::remainder);
        schema.registerType(false, DataConverterTypes.v, () -> {
            return DSL.constType(DSL.namespacedString());
        });
        schema.registerType(false, DataConverterTypes.b, () -> {
            return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", DataConverterTypes.n.in(schema)), "Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "EnderItems", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", DataConverterTypes.n.in(schema), "ShoulderEntityRight", DataConverterTypes.n.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(DataConverterTypes.v.in(schema)), "toBeDisplayed", DSL.list(DataConverterTypes.v.in(schema)))));
        });
        schema.registerType(false, DataConverterTypes.c, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(DataConverterTypes.n.in(schema)), "TileEntities", DSL.list(DataConverterTypes.j.in(schema)), "TileTicks", DSL.list(DSL.fields("i", DataConverterTypes.p.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(DataConverterTypes.l.in(schema))))));
        });
        schema.registerType(true, DataConverterTypes.j, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), map);
        });
        schema.registerType(true, DataConverterTypes.n, () -> {
            return DSL.optionalFields("Passengers", DSL.list(DataConverterTypes.n.in(schema)), DataConverterTypes.ENTITY.in(schema));
        });
        schema.registerType(true, DataConverterTypes.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", DSL.namespacedString(), map);
        });
        schema.registerType(true, DataConverterTypes.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", DataConverterTypes.q.in(schema), "tag", DSL.optionalFields("EntityTag", DataConverterTypes.n.in(schema), "BlockEntityTag", DataConverterTypes.j.in(schema), "CanDestroy", DSL.list(DataConverterTypes.p.in(schema)), "CanPlaceOn", DSL.list(DataConverterTypes.p.in(schema)))), DataConverterSchemaV705.a, HookFunction.IDENTITY);
        });
        schema.registerType(false, DataConverterTypes.d, () -> {
            return DSL.compoundList(DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.registerType(false, DataConverterTypes.e, DSL::remainder);
        schema.registerType(false, DataConverterTypes.f, () -> {
            return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", DataConverterTypes.n.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", DataConverterTypes.j.in(schema))), "palette", DSL.list(DataConverterTypes.l.in(schema)));
        });
        schema.registerType(false, DataConverterTypes.p, () -> {
            return DSL.constType(DSL.namespacedString());
        });
        schema.registerType(false, DataConverterTypes.q, () -> {
            return DSL.constType(DSL.namespacedString());
        });
        schema.registerType(false, DataConverterTypes.l, DSL::remainder);
        Supplier supplier = () -> {
            return DSL.compoundList(DataConverterTypes.q.in(schema), DSL.constType(DSL.intType()));
        };

        schema.registerType(false, DataConverterTypes.g, () -> {
            return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(DataConverterTypes.p.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate) supplier.get(), "minecraft:used", (TypeTemplate) supplier.get(), "minecraft:broken", (TypeTemplate) supplier.get(), "minecraft:picked_up", (TypeTemplate) supplier.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier.get(), "minecraft:killed", DSL.compoundList(DataConverterTypes.m.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(DataConverterTypes.m.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
        });
        schema.registerType(false, DataConverterTypes.h, () -> {
            return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(DataConverterTypes.s.in(schema)), "Objectives", DSL.list(DataConverterTypes.t.in(schema)), "Teams", DSL.list(DataConverterTypes.u.in(schema))));
        });
        schema.registerType(false, DataConverterTypes.s, () -> {
            return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", DataConverterTypes.l.in(schema), "CB", DataConverterTypes.l.in(schema), "CC", DataConverterTypes.l.in(schema), "CD", DataConverterTypes.l.in(schema))));
        });
        schema.registerType(false, DataConverterTypes.t, DSL::remainder);
        schema.registerType(false, DataConverterTypes.u, DSL::remainder);
        schema.registerType(true, DataConverterTypes.r, () -> {
            return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", DataConverterTypes.n.in(schema))), "SpawnData", DataConverterTypes.n.in(schema));
        });
        schema.registerType(false, DataConverterTypes.i, () -> {
            return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.w.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.m.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.m.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.m.in(schema), DSL.constType(DSL.string()))));
        });
        schema.registerType(false, DataConverterTypes.w, () -> {
            return DSL.constType(DSL.namespacedString());
        });
        schema.registerType(false, DataConverterTypes.m, () -> {
            return DSL.constType(DSL.namespacedString());
        });
    }
}
