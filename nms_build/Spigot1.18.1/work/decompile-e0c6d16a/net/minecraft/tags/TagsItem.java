package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.world.item.Item;

public final class TagsItem {

    protected static final TagUtil<Item> HELPER = TagStatic.create(IRegistry.ITEM_REGISTRY, "tags/items");
    public static final Tag.e<Item> WOOL = bind("wool");
    public static final Tag.e<Item> PLANKS = bind("planks");
    public static final Tag.e<Item> STONE_BRICKS = bind("stone_bricks");
    public static final Tag.e<Item> WOODEN_BUTTONS = bind("wooden_buttons");
    public static final Tag.e<Item> BUTTONS = bind("buttons");
    public static final Tag.e<Item> CARPETS = bind("carpets");
    public static final Tag.e<Item> WOODEN_DOORS = bind("wooden_doors");
    public static final Tag.e<Item> WOODEN_STAIRS = bind("wooden_stairs");
    public static final Tag.e<Item> WOODEN_SLABS = bind("wooden_slabs");
    public static final Tag.e<Item> WOODEN_FENCES = bind("wooden_fences");
    public static final Tag.e<Item> WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
    public static final Tag.e<Item> WOODEN_TRAPDOORS = bind("wooden_trapdoors");
    public static final Tag.e<Item> DOORS = bind("doors");
    public static final Tag.e<Item> SAPLINGS = bind("saplings");
    public static final Tag.e<Item> LOGS_THAT_BURN = bind("logs_that_burn");
    public static final Tag.e<Item> LOGS = bind("logs");
    public static final Tag.e<Item> DARK_OAK_LOGS = bind("dark_oak_logs");
    public static final Tag.e<Item> OAK_LOGS = bind("oak_logs");
    public static final Tag.e<Item> BIRCH_LOGS = bind("birch_logs");
    public static final Tag.e<Item> ACACIA_LOGS = bind("acacia_logs");
    public static final Tag.e<Item> JUNGLE_LOGS = bind("jungle_logs");
    public static final Tag.e<Item> SPRUCE_LOGS = bind("spruce_logs");
    public static final Tag.e<Item> CRIMSON_STEMS = bind("crimson_stems");
    public static final Tag.e<Item> WARPED_STEMS = bind("warped_stems");
    public static final Tag.e<Item> BANNERS = bind("banners");
    public static final Tag.e<Item> SAND = bind("sand");
    public static final Tag.e<Item> STAIRS = bind("stairs");
    public static final Tag.e<Item> SLABS = bind("slabs");
    public static final Tag.e<Item> WALLS = bind("walls");
    public static final Tag.e<Item> ANVIL = bind("anvil");
    public static final Tag.e<Item> RAILS = bind("rails");
    public static final Tag.e<Item> LEAVES = bind("leaves");
    public static final Tag.e<Item> TRAPDOORS = bind("trapdoors");
    public static final Tag.e<Item> SMALL_FLOWERS = bind("small_flowers");
    public static final Tag.e<Item> BEDS = bind("beds");
    public static final Tag.e<Item> FENCES = bind("fences");
    public static final Tag.e<Item> TALL_FLOWERS = bind("tall_flowers");
    public static final Tag.e<Item> FLOWERS = bind("flowers");
    public static final Tag.e<Item> PIGLIN_REPELLENTS = bind("piglin_repellents");
    public static final Tag.e<Item> PIGLIN_LOVED = bind("piglin_loved");
    public static final Tag.e<Item> IGNORED_BY_PIGLIN_BABIES = bind("ignored_by_piglin_babies");
    public static final Tag.e<Item> PIGLIN_FOOD = bind("piglin_food");
    public static final Tag.e<Item> FOX_FOOD = bind("fox_food");
    public static final Tag.e<Item> GOLD_ORES = bind("gold_ores");
    public static final Tag.e<Item> IRON_ORES = bind("iron_ores");
    public static final Tag.e<Item> DIAMOND_ORES = bind("diamond_ores");
    public static final Tag.e<Item> REDSTONE_ORES = bind("redstone_ores");
    public static final Tag.e<Item> LAPIS_ORES = bind("lapis_ores");
    public static final Tag.e<Item> COAL_ORES = bind("coal_ores");
    public static final Tag.e<Item> EMERALD_ORES = bind("emerald_ores");
    public static final Tag.e<Item> COPPER_ORES = bind("copper_ores");
    public static final Tag.e<Item> NON_FLAMMABLE_WOOD = bind("non_flammable_wood");
    public static final Tag.e<Item> SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
    public static final Tag.e<Item> CANDLES = bind("candles");
    public static final Tag.e<Item> DIRT = bind("dirt");
    public static final Tag.e<Item> TERRACOTTA = bind("terracotta");
    public static final Tag.e<Item> BOATS = bind("boats");
    public static final Tag.e<Item> FISHES = bind("fishes");
    public static final Tag.e<Item> SIGNS = bind("signs");
    public static final Tag.e<Item> MUSIC_DISCS = bind("music_discs");
    public static final Tag.e<Item> CREEPER_DROP_MUSIC_DISCS = bind("creeper_drop_music_discs");
    public static final Tag.e<Item> COALS = bind("coals");
    public static final Tag.e<Item> ARROWS = bind("arrows");
    public static final Tag.e<Item> LECTERN_BOOKS = bind("lectern_books");
    public static final Tag.e<Item> BEACON_PAYMENT_ITEMS = bind("beacon_payment_items");
    public static final Tag.e<Item> STONE_TOOL_MATERIALS = bind("stone_tool_materials");
    public static final Tag.e<Item> STONE_CRAFTING_MATERIALS = bind("stone_crafting_materials");
    public static final Tag.e<Item> FREEZE_IMMUNE_WEARABLES = bind("freeze_immune_wearables");
    public static final Tag.e<Item> AXOLOTL_TEMPT_ITEMS = bind("axolotl_tempt_items");
    public static final Tag.e<Item> OCCLUDES_VIBRATION_SIGNALS = bind("occludes_vibration_signals");
    public static final Tag.e<Item> CLUSTER_MAX_HARVESTABLES = bind("cluster_max_harvestables");

    private TagsItem() {}

    private static Tag.e<Item> bind(String s) {
        return TagsItem.HELPER.bind(s);
    }

    public static Tags<Item> getAllTags() {
        return TagsItem.HELPER.getAllTags();
    }
}
