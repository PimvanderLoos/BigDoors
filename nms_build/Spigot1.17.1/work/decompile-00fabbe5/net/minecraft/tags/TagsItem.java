package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.world.item.Item;

public final class TagsItem {

    protected static final TagUtil<Item> HELPER = TagStatic.a(IRegistry.ITEM_REGISTRY, "tags/items");
    public static final Tag.e<Item> WOOL = a("wool");
    public static final Tag.e<Item> PLANKS = a("planks");
    public static final Tag.e<Item> STONE_BRICKS = a("stone_bricks");
    public static final Tag.e<Item> WOODEN_BUTTONS = a("wooden_buttons");
    public static final Tag.e<Item> BUTTONS = a("buttons");
    public static final Tag.e<Item> CARPETS = a("carpets");
    public static final Tag.e<Item> WOODEN_DOORS = a("wooden_doors");
    public static final Tag.e<Item> WOODEN_STAIRS = a("wooden_stairs");
    public static final Tag.e<Item> WOODEN_SLABS = a("wooden_slabs");
    public static final Tag.e<Item> WOODEN_FENCES = a("wooden_fences");
    public static final Tag.e<Item> WOODEN_PRESSURE_PLATES = a("wooden_pressure_plates");
    public static final Tag.e<Item> WOODEN_TRAPDOORS = a("wooden_trapdoors");
    public static final Tag.e<Item> DOORS = a("doors");
    public static final Tag.e<Item> SAPLINGS = a("saplings");
    public static final Tag.e<Item> LOGS_THAT_BURN = a("logs_that_burn");
    public static final Tag.e<Item> LOGS = a("logs");
    public static final Tag.e<Item> DARK_OAK_LOGS = a("dark_oak_logs");
    public static final Tag.e<Item> OAK_LOGS = a("oak_logs");
    public static final Tag.e<Item> BIRCH_LOGS = a("birch_logs");
    public static final Tag.e<Item> ACACIA_LOGS = a("acacia_logs");
    public static final Tag.e<Item> JUNGLE_LOGS = a("jungle_logs");
    public static final Tag.e<Item> SPRUCE_LOGS = a("spruce_logs");
    public static final Tag.e<Item> CRIMSON_STEMS = a("crimson_stems");
    public static final Tag.e<Item> WARPED_STEMS = a("warped_stems");
    public static final Tag.e<Item> BANNERS = a("banners");
    public static final Tag.e<Item> SAND = a("sand");
    public static final Tag.e<Item> STAIRS = a("stairs");
    public static final Tag.e<Item> SLABS = a("slabs");
    public static final Tag.e<Item> WALLS = a("walls");
    public static final Tag.e<Item> ANVIL = a("anvil");
    public static final Tag.e<Item> RAILS = a("rails");
    public static final Tag.e<Item> LEAVES = a("leaves");
    public static final Tag.e<Item> TRAPDOORS = a("trapdoors");
    public static final Tag.e<Item> SMALL_FLOWERS = a("small_flowers");
    public static final Tag.e<Item> BEDS = a("beds");
    public static final Tag.e<Item> FENCES = a("fences");
    public static final Tag.e<Item> TALL_FLOWERS = a("tall_flowers");
    public static final Tag.e<Item> FLOWERS = a("flowers");
    public static final Tag.e<Item> PIGLIN_REPELLENTS = a("piglin_repellents");
    public static final Tag.e<Item> PIGLIN_LOVED = a("piglin_loved");
    public static final Tag.e<Item> IGNORED_BY_PIGLIN_BABIES = a("ignored_by_piglin_babies");
    public static final Tag.e<Item> PIGLIN_FOOD = a("piglin_food");
    public static final Tag.e<Item> FOX_FOOD = a("fox_food");
    public static final Tag.e<Item> GOLD_ORES = a("gold_ores");
    public static final Tag.e<Item> IRON_ORES = a("iron_ores");
    public static final Tag.e<Item> DIAMOND_ORES = a("diamond_ores");
    public static final Tag.e<Item> REDSTONE_ORES = a("redstone_ores");
    public static final Tag.e<Item> LAPIS_ORES = a("lapis_ores");
    public static final Tag.e<Item> COAL_ORES = a("coal_ores");
    public static final Tag.e<Item> EMERALD_ORES = a("emerald_ores");
    public static final Tag.e<Item> COPPER_ORES = a("copper_ores");
    public static final Tag.e<Item> NON_FLAMMABLE_WOOD = a("non_flammable_wood");
    public static final Tag.e<Item> SOUL_FIRE_BASE_BLOCKS = a("soul_fire_base_blocks");
    public static final Tag.e<Item> CANDLES = a("candles");
    public static final Tag.e<Item> BOATS = a("boats");
    public static final Tag.e<Item> FISHES = a("fishes");
    public static final Tag.e<Item> SIGNS = a("signs");
    public static final Tag.e<Item> MUSIC_DISCS = a("music_discs");
    public static final Tag.e<Item> CREEPER_DROP_MUSIC_DISCS = a("creeper_drop_music_discs");
    public static final Tag.e<Item> COALS = a("coals");
    public static final Tag.e<Item> ARROWS = a("arrows");
    public static final Tag.e<Item> LECTERN_BOOKS = a("lectern_books");
    public static final Tag.e<Item> BEACON_PAYMENT_ITEMS = a("beacon_payment_items");
    public static final Tag.e<Item> STONE_TOOL_MATERIALS = a("stone_tool_materials");
    public static final Tag.e<Item> STONE_CRAFTING_MATERIALS = a("stone_crafting_materials");
    public static final Tag.e<Item> FREEZE_IMMUNE_WEARABLES = a("freeze_immune_wearables");
    public static final Tag.e<Item> AXOLOTL_TEMPT_ITEMS = a("axolotl_tempt_items");
    public static final Tag.e<Item> OCCLUDES_VIBRATION_SIGNALS = a("occludes_vibration_signals");
    public static final Tag.e<Item> CLUSTER_MAX_HARVESTABLES = a("cluster_max_harvestables");

    private TagsItem() {}

    private static Tag.e<Item> a(String s) {
        return TagsItem.HELPER.a(s);
    }

    public static Tags<Item> a() {
        return TagsItem.HELPER.b();
    }
}
