package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Tag.TagType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataConverterEntityBlockState extends DataFix {

    private static final Map<String, Integer> a = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put("minecraft:air", Integer.valueOf(0));
        hashmap.put("minecraft:stone", Integer.valueOf(1));
        hashmap.put("minecraft:grass", Integer.valueOf(2));
        hashmap.put("minecraft:dirt", Integer.valueOf(3));
        hashmap.put("minecraft:cobblestone", Integer.valueOf(4));
        hashmap.put("minecraft:planks", Integer.valueOf(5));
        hashmap.put("minecraft:sapling", Integer.valueOf(6));
        hashmap.put("minecraft:bedrock", Integer.valueOf(7));
        hashmap.put("minecraft:flowing_water", Integer.valueOf(8));
        hashmap.put("minecraft:water", Integer.valueOf(9));
        hashmap.put("minecraft:flowing_lava", Integer.valueOf(10));
        hashmap.put("minecraft:lava", Integer.valueOf(11));
        hashmap.put("minecraft:sand", Integer.valueOf(12));
        hashmap.put("minecraft:gravel", Integer.valueOf(13));
        hashmap.put("minecraft:gold_ore", Integer.valueOf(14));
        hashmap.put("minecraft:iron_ore", Integer.valueOf(15));
        hashmap.put("minecraft:coal_ore", Integer.valueOf(16));
        hashmap.put("minecraft:log", Integer.valueOf(17));
        hashmap.put("minecraft:leaves", Integer.valueOf(18));
        hashmap.put("minecraft:sponge", Integer.valueOf(19));
        hashmap.put("minecraft:glass", Integer.valueOf(20));
        hashmap.put("minecraft:lapis_ore", Integer.valueOf(21));
        hashmap.put("minecraft:lapis_block", Integer.valueOf(22));
        hashmap.put("minecraft:dispenser", Integer.valueOf(23));
        hashmap.put("minecraft:sandstone", Integer.valueOf(24));
        hashmap.put("minecraft:noteblock", Integer.valueOf(25));
        hashmap.put("minecraft:bed", Integer.valueOf(26));
        hashmap.put("minecraft:golden_rail", Integer.valueOf(27));
        hashmap.put("minecraft:detector_rail", Integer.valueOf(28));
        hashmap.put("minecraft:sticky_piston", Integer.valueOf(29));
        hashmap.put("minecraft:web", Integer.valueOf(30));
        hashmap.put("minecraft:tallgrass", Integer.valueOf(31));
        hashmap.put("minecraft:deadbush", Integer.valueOf(32));
        hashmap.put("minecraft:piston", Integer.valueOf(33));
        hashmap.put("minecraft:piston_head", Integer.valueOf(34));
        hashmap.put("minecraft:wool", Integer.valueOf(35));
        hashmap.put("minecraft:piston_extension", Integer.valueOf(36));
        hashmap.put("minecraft:yellow_flower", Integer.valueOf(37));
        hashmap.put("minecraft:red_flower", Integer.valueOf(38));
        hashmap.put("minecraft:brown_mushroom", Integer.valueOf(39));
        hashmap.put("minecraft:red_mushroom", Integer.valueOf(40));
        hashmap.put("minecraft:gold_block", Integer.valueOf(41));
        hashmap.put("minecraft:iron_block", Integer.valueOf(42));
        hashmap.put("minecraft:double_stone_slab", Integer.valueOf(43));
        hashmap.put("minecraft:stone_slab", Integer.valueOf(44));
        hashmap.put("minecraft:brick_block", Integer.valueOf(45));
        hashmap.put("minecraft:tnt", Integer.valueOf(46));
        hashmap.put("minecraft:bookshelf", Integer.valueOf(47));
        hashmap.put("minecraft:mossy_cobblestone", Integer.valueOf(48));
        hashmap.put("minecraft:obsidian", Integer.valueOf(49));
        hashmap.put("minecraft:torch", Integer.valueOf(50));
        hashmap.put("minecraft:fire", Integer.valueOf(51));
        hashmap.put("minecraft:mob_spawner", Integer.valueOf(52));
        hashmap.put("minecraft:oak_stairs", Integer.valueOf(53));
        hashmap.put("minecraft:chest", Integer.valueOf(54));
        hashmap.put("minecraft:redstone_wire", Integer.valueOf(55));
        hashmap.put("minecraft:diamond_ore", Integer.valueOf(56));
        hashmap.put("minecraft:diamond_block", Integer.valueOf(57));
        hashmap.put("minecraft:crafting_table", Integer.valueOf(58));
        hashmap.put("minecraft:wheat", Integer.valueOf(59));
        hashmap.put("minecraft:farmland", Integer.valueOf(60));
        hashmap.put("minecraft:furnace", Integer.valueOf(61));
        hashmap.put("minecraft:lit_furnace", Integer.valueOf(62));
        hashmap.put("minecraft:standing_sign", Integer.valueOf(63));
        hashmap.put("minecraft:wooden_door", Integer.valueOf(64));
        hashmap.put("minecraft:ladder", Integer.valueOf(65));
        hashmap.put("minecraft:rail", Integer.valueOf(66));
        hashmap.put("minecraft:stone_stairs", Integer.valueOf(67));
        hashmap.put("minecraft:wall_sign", Integer.valueOf(68));
        hashmap.put("minecraft:lever", Integer.valueOf(69));
        hashmap.put("minecraft:stone_pressure_plate", Integer.valueOf(70));
        hashmap.put("minecraft:iron_door", Integer.valueOf(71));
        hashmap.put("minecraft:wooden_pressure_plate", Integer.valueOf(72));
        hashmap.put("minecraft:redstone_ore", Integer.valueOf(73));
        hashmap.put("minecraft:lit_redstone_ore", Integer.valueOf(74));
        hashmap.put("minecraft:unlit_redstone_torch", Integer.valueOf(75));
        hashmap.put("minecraft:redstone_torch", Integer.valueOf(76));
        hashmap.put("minecraft:stone_button", Integer.valueOf(77));
        hashmap.put("minecraft:snow_layer", Integer.valueOf(78));
        hashmap.put("minecraft:ice", Integer.valueOf(79));
        hashmap.put("minecraft:snow", Integer.valueOf(80));
        hashmap.put("minecraft:cactus", Integer.valueOf(81));
        hashmap.put("minecraft:clay", Integer.valueOf(82));
        hashmap.put("minecraft:reeds", Integer.valueOf(83));
        hashmap.put("minecraft:jukebox", Integer.valueOf(84));
        hashmap.put("minecraft:fence", Integer.valueOf(85));
        hashmap.put("minecraft:pumpkin", Integer.valueOf(86));
        hashmap.put("minecraft:netherrack", Integer.valueOf(87));
        hashmap.put("minecraft:soul_sand", Integer.valueOf(88));
        hashmap.put("minecraft:glowstone", Integer.valueOf(89));
        hashmap.put("minecraft:portal", Integer.valueOf(90));
        hashmap.put("minecraft:lit_pumpkin", Integer.valueOf(91));
        hashmap.put("minecraft:cake", Integer.valueOf(92));
        hashmap.put("minecraft:unpowered_repeater", Integer.valueOf(93));
        hashmap.put("minecraft:powered_repeater", Integer.valueOf(94));
        hashmap.put("minecraft:stained_glass", Integer.valueOf(95));
        hashmap.put("minecraft:trapdoor", Integer.valueOf(96));
        hashmap.put("minecraft:monster_egg", Integer.valueOf(97));
        hashmap.put("minecraft:stonebrick", Integer.valueOf(98));
        hashmap.put("minecraft:brown_mushroom_block", Integer.valueOf(99));
        hashmap.put("minecraft:red_mushroom_block", Integer.valueOf(100));
        hashmap.put("minecraft:iron_bars", Integer.valueOf(101));
        hashmap.put("minecraft:glass_pane", Integer.valueOf(102));
        hashmap.put("minecraft:melon_block", Integer.valueOf(103));
        hashmap.put("minecraft:pumpkin_stem", Integer.valueOf(104));
        hashmap.put("minecraft:melon_stem", Integer.valueOf(105));
        hashmap.put("minecraft:vine", Integer.valueOf(106));
        hashmap.put("minecraft:fence_gate", Integer.valueOf(107));
        hashmap.put("minecraft:brick_stairs", Integer.valueOf(108));
        hashmap.put("minecraft:stone_brick_stairs", Integer.valueOf(109));
        hashmap.put("minecraft:mycelium", Integer.valueOf(110));
        hashmap.put("minecraft:waterlily", Integer.valueOf(111));
        hashmap.put("minecraft:nether_brick", Integer.valueOf(112));
        hashmap.put("minecraft:nether_brick_fence", Integer.valueOf(113));
        hashmap.put("minecraft:nether_brick_stairs", Integer.valueOf(114));
        hashmap.put("minecraft:nether_wart", Integer.valueOf(115));
        hashmap.put("minecraft:enchanting_table", Integer.valueOf(116));
        hashmap.put("minecraft:brewing_stand", Integer.valueOf(117));
        hashmap.put("minecraft:cauldron", Integer.valueOf(118));
        hashmap.put("minecraft:end_portal", Integer.valueOf(119));
        hashmap.put("minecraft:end_portal_frame", Integer.valueOf(120));
        hashmap.put("minecraft:end_stone", Integer.valueOf(121));
        hashmap.put("minecraft:dragon_egg", Integer.valueOf(122));
        hashmap.put("minecraft:redstone_lamp", Integer.valueOf(123));
        hashmap.put("minecraft:lit_redstone_lamp", Integer.valueOf(124));
        hashmap.put("minecraft:double_wooden_slab", Integer.valueOf(125));
        hashmap.put("minecraft:wooden_slab", Integer.valueOf(126));
        hashmap.put("minecraft:cocoa", Integer.valueOf(127));
        hashmap.put("minecraft:sandstone_stairs", Integer.valueOf(128));
        hashmap.put("minecraft:emerald_ore", Integer.valueOf(129));
        hashmap.put("minecraft:ender_chest", Integer.valueOf(130));
        hashmap.put("minecraft:tripwire_hook", Integer.valueOf(131));
        hashmap.put("minecraft:tripwire", Integer.valueOf(132));
        hashmap.put("minecraft:emerald_block", Integer.valueOf(133));
        hashmap.put("minecraft:spruce_stairs", Integer.valueOf(134));
        hashmap.put("minecraft:birch_stairs", Integer.valueOf(135));
        hashmap.put("minecraft:jungle_stairs", Integer.valueOf(136));
        hashmap.put("minecraft:command_block", Integer.valueOf(137));
        hashmap.put("minecraft:beacon", Integer.valueOf(138));
        hashmap.put("minecraft:cobblestone_wall", Integer.valueOf(139));
        hashmap.put("minecraft:flower_pot", Integer.valueOf(140));
        hashmap.put("minecraft:carrots", Integer.valueOf(141));
        hashmap.put("minecraft:potatoes", Integer.valueOf(142));
        hashmap.put("minecraft:wooden_button", Integer.valueOf(143));
        hashmap.put("minecraft:skull", Integer.valueOf(144));
        hashmap.put("minecraft:anvil", Integer.valueOf(145));
        hashmap.put("minecraft:trapped_chest", Integer.valueOf(146));
        hashmap.put("minecraft:light_weighted_pressure_plate", Integer.valueOf(147));
        hashmap.put("minecraft:heavy_weighted_pressure_plate", Integer.valueOf(148));
        hashmap.put("minecraft:unpowered_comparator", Integer.valueOf(149));
        hashmap.put("minecraft:powered_comparator", Integer.valueOf(150));
        hashmap.put("minecraft:daylight_detector", Integer.valueOf(151));
        hashmap.put("minecraft:redstone_block", Integer.valueOf(152));
        hashmap.put("minecraft:quartz_ore", Integer.valueOf(153));
        hashmap.put("minecraft:hopper", Integer.valueOf(154));
        hashmap.put("minecraft:quartz_block", Integer.valueOf(155));
        hashmap.put("minecraft:quartz_stairs", Integer.valueOf(156));
        hashmap.put("minecraft:activator_rail", Integer.valueOf(157));
        hashmap.put("minecraft:dropper", Integer.valueOf(158));
        hashmap.put("minecraft:stained_hardened_clay", Integer.valueOf(159));
        hashmap.put("minecraft:stained_glass_pane", Integer.valueOf(160));
        hashmap.put("minecraft:leaves2", Integer.valueOf(161));
        hashmap.put("minecraft:log2", Integer.valueOf(162));
        hashmap.put("minecraft:acacia_stairs", Integer.valueOf(163));
        hashmap.put("minecraft:dark_oak_stairs", Integer.valueOf(164));
        hashmap.put("minecraft:slime", Integer.valueOf(165));
        hashmap.put("minecraft:barrier", Integer.valueOf(166));
        hashmap.put("minecraft:iron_trapdoor", Integer.valueOf(167));
        hashmap.put("minecraft:prismarine", Integer.valueOf(168));
        hashmap.put("minecraft:sea_lantern", Integer.valueOf(169));
        hashmap.put("minecraft:hay_block", Integer.valueOf(170));
        hashmap.put("minecraft:carpet", Integer.valueOf(171));
        hashmap.put("minecraft:hardened_clay", Integer.valueOf(172));
        hashmap.put("minecraft:coal_block", Integer.valueOf(173));
        hashmap.put("minecraft:packed_ice", Integer.valueOf(174));
        hashmap.put("minecraft:double_plant", Integer.valueOf(175));
        hashmap.put("minecraft:standing_banner", Integer.valueOf(176));
        hashmap.put("minecraft:wall_banner", Integer.valueOf(177));
        hashmap.put("minecraft:daylight_detector_inverted", Integer.valueOf(178));
        hashmap.put("minecraft:red_sandstone", Integer.valueOf(179));
        hashmap.put("minecraft:red_sandstone_stairs", Integer.valueOf(180));
        hashmap.put("minecraft:double_stone_slab2", Integer.valueOf(181));
        hashmap.put("minecraft:stone_slab2", Integer.valueOf(182));
        hashmap.put("minecraft:spruce_fence_gate", Integer.valueOf(183));
        hashmap.put("minecraft:birch_fence_gate", Integer.valueOf(184));
        hashmap.put("minecraft:jungle_fence_gate", Integer.valueOf(185));
        hashmap.put("minecraft:dark_oak_fence_gate", Integer.valueOf(186));
        hashmap.put("minecraft:acacia_fence_gate", Integer.valueOf(187));
        hashmap.put("minecraft:spruce_fence", Integer.valueOf(188));
        hashmap.put("minecraft:birch_fence", Integer.valueOf(189));
        hashmap.put("minecraft:jungle_fence", Integer.valueOf(190));
        hashmap.put("minecraft:dark_oak_fence", Integer.valueOf(191));
        hashmap.put("minecraft:acacia_fence", Integer.valueOf(192));
        hashmap.put("minecraft:spruce_door", Integer.valueOf(193));
        hashmap.put("minecraft:birch_door", Integer.valueOf(194));
        hashmap.put("minecraft:jungle_door", Integer.valueOf(195));
        hashmap.put("minecraft:acacia_door", Integer.valueOf(196));
        hashmap.put("minecraft:dark_oak_door", Integer.valueOf(197));
        hashmap.put("minecraft:end_rod", Integer.valueOf(198));
        hashmap.put("minecraft:chorus_plant", Integer.valueOf(199));
        hashmap.put("minecraft:chorus_flower", Integer.valueOf(200));
        hashmap.put("minecraft:purpur_block", Integer.valueOf(201));
        hashmap.put("minecraft:purpur_pillar", Integer.valueOf(202));
        hashmap.put("minecraft:purpur_stairs", Integer.valueOf(203));
        hashmap.put("minecraft:purpur_double_slab", Integer.valueOf(204));
        hashmap.put("minecraft:purpur_slab", Integer.valueOf(205));
        hashmap.put("minecraft:end_bricks", Integer.valueOf(206));
        hashmap.put("minecraft:beetroots", Integer.valueOf(207));
        hashmap.put("minecraft:grass_path", Integer.valueOf(208));
        hashmap.put("minecraft:end_gateway", Integer.valueOf(209));
        hashmap.put("minecraft:repeating_command_block", Integer.valueOf(210));
        hashmap.put("minecraft:chain_command_block", Integer.valueOf(211));
        hashmap.put("minecraft:frosted_ice", Integer.valueOf(212));
        hashmap.put("minecraft:magma", Integer.valueOf(213));
        hashmap.put("minecraft:nether_wart_block", Integer.valueOf(214));
        hashmap.put("minecraft:red_nether_brick", Integer.valueOf(215));
        hashmap.put("minecraft:bone_block", Integer.valueOf(216));
        hashmap.put("minecraft:structure_void", Integer.valueOf(217));
        hashmap.put("minecraft:observer", Integer.valueOf(218));
        hashmap.put("minecraft:white_shulker_box", Integer.valueOf(219));
        hashmap.put("minecraft:orange_shulker_box", Integer.valueOf(220));
        hashmap.put("minecraft:magenta_shulker_box", Integer.valueOf(221));
        hashmap.put("minecraft:light_blue_shulker_box", Integer.valueOf(222));
        hashmap.put("minecraft:yellow_shulker_box", Integer.valueOf(223));
        hashmap.put("minecraft:lime_shulker_box", Integer.valueOf(224));
        hashmap.put("minecraft:pink_shulker_box", Integer.valueOf(225));
        hashmap.put("minecraft:gray_shulker_box", Integer.valueOf(226));
        hashmap.put("minecraft:silver_shulker_box", Integer.valueOf(227));
        hashmap.put("minecraft:cyan_shulker_box", Integer.valueOf(228));
        hashmap.put("minecraft:purple_shulker_box", Integer.valueOf(229));
        hashmap.put("minecraft:blue_shulker_box", Integer.valueOf(230));
        hashmap.put("minecraft:brown_shulker_box", Integer.valueOf(231));
        hashmap.put("minecraft:green_shulker_box", Integer.valueOf(232));
        hashmap.put("minecraft:red_shulker_box", Integer.valueOf(233));
        hashmap.put("minecraft:black_shulker_box", Integer.valueOf(234));
        hashmap.put("minecraft:white_glazed_terracotta", Integer.valueOf(235));
        hashmap.put("minecraft:orange_glazed_terracotta", Integer.valueOf(236));
        hashmap.put("minecraft:magenta_glazed_terracotta", Integer.valueOf(237));
        hashmap.put("minecraft:light_blue_glazed_terracotta", Integer.valueOf(238));
        hashmap.put("minecraft:yellow_glazed_terracotta", Integer.valueOf(239));
        hashmap.put("minecraft:lime_glazed_terracotta", Integer.valueOf(240));
        hashmap.put("minecraft:pink_glazed_terracotta", Integer.valueOf(241));
        hashmap.put("minecraft:gray_glazed_terracotta", Integer.valueOf(242));
        hashmap.put("minecraft:silver_glazed_terracotta", Integer.valueOf(243));
        hashmap.put("minecraft:cyan_glazed_terracotta", Integer.valueOf(244));
        hashmap.put("minecraft:purple_glazed_terracotta", Integer.valueOf(245));
        hashmap.put("minecraft:blue_glazed_terracotta", Integer.valueOf(246));
        hashmap.put("minecraft:brown_glazed_terracotta", Integer.valueOf(247));
        hashmap.put("minecraft:green_glazed_terracotta", Integer.valueOf(248));
        hashmap.put("minecraft:red_glazed_terracotta", Integer.valueOf(249));
        hashmap.put("minecraft:black_glazed_terracotta", Integer.valueOf(250));
        hashmap.put("minecraft:concrete", Integer.valueOf(251));
        hashmap.put("minecraft:concrete_powder", Integer.valueOf(252));
        hashmap.put("minecraft:structure_block", Integer.valueOf(255));
    });

    public DataConverterEntityBlockState(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public static int a(String s) {
        Integer integer = (Integer) DataConverterEntityBlockState.a.get(s);

        return integer == null ? 0 : integer.intValue();
    }

    public TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        Schema schema1 = this.getOutputSchema();
        Function function = (typed) -> {
            return this.a(typed, "DisplayTile", "DisplayData", "DisplayState");
        };
        Function function1 = (typed) -> {
            return this.a(typed, "inTile", "inData", "inBlockState");
        };
        Type type = DSL.and(DSL.optional(DSL.field("inTile", DSL.named(DataConverterTypes.p.typeName(), DSL.or(DSL.intType(), DSL.namespacedString())))), DSL.remainderType());
        Function function2 = (typed) -> {
            return typed.update(type.finder(), DSL.remainderType(), Pair::getSecond);
        };

        return this.fixTypeEverywhereTyped("EntityBlockStateFix", schema.getType(DataConverterTypes.ENTITY), schema1.getType(DataConverterTypes.ENTITY), (typed) -> {
            typed = this.a(typed, "minecraft:falling_block", this::a);
            typed = this.a(typed, "minecraft:enderman", (typedx) -> {
                return this.a(typedx, "carried", "carriedData", "carriedBlockState");
            });
            typed = this.a(typed, "minecraft:arrow", function);
            typed = this.a(typed, "minecraft:spectral_arrow", function);
            typed = this.a(typed, "minecraft:egg", function1);
            typed = this.a(typed, "minecraft:ender_pearl", function1);
            typed = this.a(typed, "minecraft:fireball", function1);
            typed = this.a(typed, "minecraft:potion", function1);
            typed = this.a(typed, "minecraft:small_fireball", function1);
            typed = this.a(typed, "minecraft:snowball", function1);
            typed = this.a(typed, "minecraft:wither_skull", function1);
            typed = this.a(typed, "minecraft:xp_bottle", function1);
            typed = this.a(typed, "minecraft:commandblock_minecart", function2);
            typed = this.a(typed, "minecraft:minecart", function2);
            typed = this.a(typed, "minecraft:chest_minecart", function2);
            typed = this.a(typed, "minecraft:furnace_minecart", function2);
            typed = this.a(typed, "minecraft:tnt_minecart", function2);
            typed = this.a(typed, "minecraft:hopper_minecart", function2);
            typed = this.a(typed, "minecraft:spawner_minecart", function2);
            return typed;
        });
    }

    private Typed<?> a(Typed<?> typed) {
        Type type = DSL.optional(DSL.field("Block", DSL.named(DataConverterTypes.p.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()))));
        Type type1 = DSL.optional(DSL.field("BlockState", DSL.named(DataConverterTypes.l.typeName(), DSL.remainderType())));
        Dynamic dynamic = (Dynamic) typed.get(DSL.remainderFinder());

        return typed.update(type.finder(), type1, (either) -> {
            int i = ((Integer) either.map((pair) -> {
                return (Integer) ((Either) pair.getSecond()).map((integer) -> {
                    return integer;
                }, DataConverterEntityBlockState::a);
            }, (unit) -> {
                Optional optional = dynamic.get("TileID").flatMap(Dynamic::getNumberValue);

                return (Integer) optional.map(Number::intValue).orElseGet(() -> {
                    return Integer.valueOf(dynamic.getByte("Tile") & 255);
                });
            })).intValue();
            int j = dynamic.getInt("Data") & 15;

            return Either.left(Pair.of(DataConverterTypes.l.typeName(), DataConverterFlattenData.b(i << 4 | j)));
        }).set(DSL.remainderFinder(), dynamic.remove("Data").remove("TileID").remove("Tile"));
    }

    private Typed<?> a(Typed<?> typed, String s, String s1, String s2) {
        TagType tagtype = DSL.field(s, DSL.named(DataConverterTypes.p.typeName(), DSL.or(DSL.intType(), DSL.namespacedString())));
        TagType tagtype1 = DSL.field(s2, DSL.named(DataConverterTypes.l.typeName(), DSL.remainderType()));
        Dynamic dynamic = (Dynamic) typed.getOrCreate(DSL.remainderFinder());

        return typed.update(tagtype.finder(), tagtype1, (pair) -> {
            int i = ((Integer) ((Either) pair.getSecond()).map((integer) -> {
                return integer;
            }, DataConverterEntityBlockState::a)).intValue();
            int j = dynamic.getInt(s) & 15;

            return Pair.of(DataConverterTypes.l.typeName(), DataConverterFlattenData.b(i << 4 | j));
        }).set(DSL.remainderFinder(), dynamic.remove(s1));
    }

    private Typed<?> a(Typed<?> typed, String s, Function<Typed<?>, Typed<?>> function) {
        Type type = this.getInputSchema().getChoiceType(DataConverterTypes.ENTITY, s);
        Type type1 = this.getOutputSchema().getChoiceType(DataConverterTypes.ENTITY, s);

        return typed.updateTyped(DSL.namedChoice(s, type), type1, function);
    }
}
