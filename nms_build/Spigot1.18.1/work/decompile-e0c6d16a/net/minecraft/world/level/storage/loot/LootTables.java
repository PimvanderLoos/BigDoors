package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.MinecraftKey;

public class LootTables {

    private static final Set<MinecraftKey> LOCATIONS = Sets.newHashSet();
    private static final Set<MinecraftKey> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LootTables.LOCATIONS);
    public static final MinecraftKey EMPTY = new MinecraftKey("empty");
    public static final MinecraftKey SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
    public static final MinecraftKey END_CITY_TREASURE = register("chests/end_city_treasure");
    public static final MinecraftKey SIMPLE_DUNGEON = register("chests/simple_dungeon");
    public static final MinecraftKey VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
    public static final MinecraftKey VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
    public static final MinecraftKey VILLAGE_ARMORER = register("chests/village/village_armorer");
    public static final MinecraftKey VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
    public static final MinecraftKey VILLAGE_MASON = register("chests/village/village_mason");
    public static final MinecraftKey VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
    public static final MinecraftKey VILLAGE_BUTCHER = register("chests/village/village_butcher");
    public static final MinecraftKey VILLAGE_FLETCHER = register("chests/village/village_fletcher");
    public static final MinecraftKey VILLAGE_FISHER = register("chests/village/village_fisher");
    public static final MinecraftKey VILLAGE_TANNERY = register("chests/village/village_tannery");
    public static final MinecraftKey VILLAGE_TEMPLE = register("chests/village/village_temple");
    public static final MinecraftKey VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
    public static final MinecraftKey VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
    public static final MinecraftKey VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
    public static final MinecraftKey VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
    public static final MinecraftKey VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
    public static final MinecraftKey ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
    public static final MinecraftKey NETHER_BRIDGE = register("chests/nether_bridge");
    public static final MinecraftKey STRONGHOLD_LIBRARY = register("chests/stronghold_library");
    public static final MinecraftKey STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
    public static final MinecraftKey STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
    public static final MinecraftKey DESERT_PYRAMID = register("chests/desert_pyramid");
    public static final MinecraftKey JUNGLE_TEMPLE = register("chests/jungle_temple");
    public static final MinecraftKey JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
    public static final MinecraftKey IGLOO_CHEST = register("chests/igloo_chest");
    public static final MinecraftKey WOODLAND_MANSION = register("chests/woodland_mansion");
    public static final MinecraftKey UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
    public static final MinecraftKey UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
    public static final MinecraftKey BURIED_TREASURE = register("chests/buried_treasure");
    public static final MinecraftKey SHIPWRECK_MAP = register("chests/shipwreck_map");
    public static final MinecraftKey SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
    public static final MinecraftKey SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
    public static final MinecraftKey PILLAGER_OUTPOST = register("chests/pillager_outpost");
    public static final MinecraftKey BASTION_TREASURE = register("chests/bastion_treasure");
    public static final MinecraftKey BASTION_OTHER = register("chests/bastion_other");
    public static final MinecraftKey BASTION_BRIDGE = register("chests/bastion_bridge");
    public static final MinecraftKey BASTION_HOGLIN_STABLE = register("chests/bastion_hoglin_stable");
    public static final MinecraftKey RUINED_PORTAL = register("chests/ruined_portal");
    public static final MinecraftKey SHEEP_WHITE = register("entities/sheep/white");
    public static final MinecraftKey SHEEP_ORANGE = register("entities/sheep/orange");
    public static final MinecraftKey SHEEP_MAGENTA = register("entities/sheep/magenta");
    public static final MinecraftKey SHEEP_LIGHT_BLUE = register("entities/sheep/light_blue");
    public static final MinecraftKey SHEEP_YELLOW = register("entities/sheep/yellow");
    public static final MinecraftKey SHEEP_LIME = register("entities/sheep/lime");
    public static final MinecraftKey SHEEP_PINK = register("entities/sheep/pink");
    public static final MinecraftKey SHEEP_GRAY = register("entities/sheep/gray");
    public static final MinecraftKey SHEEP_LIGHT_GRAY = register("entities/sheep/light_gray");
    public static final MinecraftKey SHEEP_CYAN = register("entities/sheep/cyan");
    public static final MinecraftKey SHEEP_PURPLE = register("entities/sheep/purple");
    public static final MinecraftKey SHEEP_BLUE = register("entities/sheep/blue");
    public static final MinecraftKey SHEEP_BROWN = register("entities/sheep/brown");
    public static final MinecraftKey SHEEP_GREEN = register("entities/sheep/green");
    public static final MinecraftKey SHEEP_RED = register("entities/sheep/red");
    public static final MinecraftKey SHEEP_BLACK = register("entities/sheep/black");
    public static final MinecraftKey FISHING = register("gameplay/fishing");
    public static final MinecraftKey FISHING_JUNK = register("gameplay/fishing/junk");
    public static final MinecraftKey FISHING_TREASURE = register("gameplay/fishing/treasure");
    public static final MinecraftKey FISHING_FISH = register("gameplay/fishing/fish");
    public static final MinecraftKey CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
    public static final MinecraftKey ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
    public static final MinecraftKey BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
    public static final MinecraftKey CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
    public static final MinecraftKey CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
    public static final MinecraftKey FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
    public static final MinecraftKey FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
    public static final MinecraftKey FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
    public static final MinecraftKey LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
    public static final MinecraftKey LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
    public static final MinecraftKey MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
    public static final MinecraftKey SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
    public static final MinecraftKey TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
    public static final MinecraftKey WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");
    public static final MinecraftKey PIGLIN_BARTERING = register("gameplay/piglin_bartering");

    public LootTables() {}

    private static MinecraftKey register(String s) {
        return register(new MinecraftKey(s));
    }

    private static MinecraftKey register(MinecraftKey minecraftkey) {
        if (LootTables.LOCATIONS.add(minecraftkey)) {
            return minecraftkey;
        } else {
            throw new IllegalArgumentException(minecraftkey + " is already a registered built-in loot table");
        }
    }

    public static Set<MinecraftKey> all() {
        return LootTables.IMMUTABLE_LOCATIONS;
    }
}
