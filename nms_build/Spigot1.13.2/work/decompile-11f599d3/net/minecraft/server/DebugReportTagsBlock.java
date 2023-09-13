package net.minecraft.server;

public class DebugReportTagsBlock extends DebugReportTags<Block> {

    public DebugReportTagsBlock(DebugReportGenerator debugreportgenerator) {
        super(debugreportgenerator, IRegistry.BLOCK);
    }

    protected void b() {
        this.a(TagsBlock.WOOL).a((Object[])(Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL));
        this.a(TagsBlock.PLANKS).a((Object[])(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        this.a(TagsBlock.STONE_BRICKS).a((Object[])(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS));
        this.a(TagsBlock.WOODEN_BUTTONS).a((Object[])(Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.ACACIA_BUTTON, Blocks.DARK_OAK_BUTTON));
        this.a(TagsBlock.BUTTONS).a(TagsBlock.WOODEN_BUTTONS).a((Object)Blocks.STONE_BUTTON);
        this.a(TagsBlock.CARPETS).a((Object[])(Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET));
        this.a(TagsBlock.WOODEN_DOORS).a((Object[])(Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR));
        this.a(TagsBlock.WOODEN_STAIRS).a((Object[])(Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.DARK_OAK_STAIRS));
        this.a(TagsBlock.WOODEN_SLABS).a((Object[])(Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.BIRCH_SLAB, Blocks.JUNGLE_SLAB, Blocks.ACACIA_SLAB, Blocks.DARK_OAK_SLAB));
        this.a(TagsBlock.DOORS).a(TagsBlock.WOODEN_DOORS).a((Object)Blocks.IRON_DOOR);
        this.a(TagsBlock.SAPLINGS).a((Object[])(Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING));
        this.a(TagsBlock.DARK_OAK_LOGS).a((Object[])(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD));
        this.a(TagsBlock.OAK_LOGS).a((Object[])(Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD));
        this.a(TagsBlock.ACACIA_LOGS).a((Object[])(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD));
        this.a(TagsBlock.BIRCH_LOGS).a((Object[])(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD));
        this.a(TagsBlock.JUNGLE_LOGS).a((Object[])(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD));
        this.a(TagsBlock.SPRUCE_LOGS).a((Object[])(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD));
        this.a(TagsBlock.LOGS).a(TagsBlock.DARK_OAK_LOGS).a(TagsBlock.OAK_LOGS).a(TagsBlock.ACACIA_LOGS).a(TagsBlock.BIRCH_LOGS).a(TagsBlock.JUNGLE_LOGS).a(TagsBlock.SPRUCE_LOGS);
        this.a(TagsBlock.ANVIL).a((Object[])(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL));
        this.a(TagsBlock.ENDERMAN_HOLDABLE).a((Object[])(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.TNT, Blocks.CACTUS, Blocks.CLAY, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON, Blocks.MYCELIUM, Blocks.NETHERRACK));
        this.a(TagsBlock.FLOWER_POTS).a((Object[])(Blocks.FLOWER_POT, Blocks.POTTED_POPPY, Blocks.POTTED_BLUE_ORCHID, Blocks.POTTED_ALLIUM, Blocks.POTTED_AZURE_BLUET, Blocks.POTTED_RED_TULIP, Blocks.POTTED_ORANGE_TULIP, Blocks.POTTED_WHITE_TULIP, Blocks.POTTED_PINK_TULIP, Blocks.POTTED_OXEYE_DAISY, Blocks.POTTED_DANDELION, Blocks.POTTED_OAK_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, Blocks.POTTED_BIRCH_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, Blocks.POTTED_ACACIA_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, Blocks.POTTED_RED_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, Blocks.POTTED_DEAD_BUSH, Blocks.POTTED_FERN, Blocks.POTTED_CACTUS));
        this.a(TagsBlock.BANNERS).a((Object[])(Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
        this.a(TagsBlock.WOODEN_PRESSURE_PLATES).a((Object[])(Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE));
        this.a(TagsBlock.STAIRS).a((Object[])(Blocks.OAK_STAIRS, Blocks.COBBLESTONE_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS, Blocks.BRICK_STAIRS, Blocks.PURPUR_STAIRS, Blocks.QUARTZ_STAIRS, Blocks.RED_SANDSTONE_STAIRS, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE_STAIRS));
        this.a(TagsBlock.SLABS).a((Object[])(Blocks.STONE_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.SANDSTONE_SLAB, Blocks.ACACIA_SLAB, Blocks.BIRCH_SLAB, Blocks.DARK_OAK_SLAB, Blocks.JUNGLE_SLAB, Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.PURPUR_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.BRICK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB, Blocks.DARK_PRISMARINE_SLAB));
        this.a(TagsBlock.CORAL_PLANTS).a((Object[])(Blocks.TUBE_CORAL, Blocks.BRAIN_CORAL, Blocks.BUBBLE_CORAL, Blocks.FIRE_CORAL, Blocks.HORN_CORAL));
        this.a(TagsBlock.CORALS).a(TagsBlock.CORAL_PLANTS).a((Object[])(Blocks.TUBE_CORAL_FAN, Blocks.BRAIN_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN, Blocks.FIRE_CORAL_FAN, Blocks.HORN_CORAL_FAN));
        this.a(TagsBlock.WALL_CORALS).a((Object[])(Blocks.TUBE_CORAL_WALL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.HORN_CORAL_WALL_FAN));
        this.a(TagsBlock.SAND).a((Object[])(Blocks.SAND, Blocks.RED_SAND));
        this.a(TagsBlock.RAILS).a((Object[])(Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL));
        this.a(TagsBlock.CORAL_BLOCKS).a((Object[])(Blocks.TUBE_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK));
        this.a(TagsBlock.ICE).a((Object[])(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.FROSTED_ICE));
        this.a(TagsBlock.VALID_SPAWN).a((Object[])(Blocks.GRASS_BLOCK, Blocks.PODZOL));
        this.a(TagsBlock.LEAVES).a((Object[])(Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES));
        this.a(TagsBlock.IMPERMEABLE).a((Object[])(Blocks.GLASS, Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS));
        this.a(TagsBlock.WOODEN_TRAPDOORS).a((Object[])(Blocks.ACACIA_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR));
        this.a(TagsBlock.TRAPDOORS).a(TagsBlock.WOODEN_TRAPDOORS).a((Object)Blocks.IRON_TRAPDOOR);
        this.a(TagsBlock.UNDERWATER_BONEMEALS).a((Object)Blocks.SEAGRASS).a(TagsBlock.CORALS).a(TagsBlock.WALL_CORALS);
    }

    protected java.nio.file.Path a(MinecraftKey minecraftkey) {
        return this.b.b().resolve("data/" + minecraftkey.b() + "/tags/blocks/" + minecraftkey.getKey() + ".json");
    }

    public String a() {
        return "Block Tags";
    }

    protected void a(Tags<Block> tags) {
        TagsBlock.a(tags);
    }
}
