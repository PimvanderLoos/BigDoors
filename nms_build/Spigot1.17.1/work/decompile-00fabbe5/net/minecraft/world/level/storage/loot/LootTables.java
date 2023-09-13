package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.MinecraftKey;

public class LootTables {

    private static final Set<MinecraftKey> LOCATIONS = Sets.newHashSet();
    private static final Set<MinecraftKey> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LootTables.LOCATIONS);
    public static final MinecraftKey EMPTY = new MinecraftKey("empty");
    public static final MinecraftKey SPAWN_BONUS_CHEST = a("chests/spawn_bonus_chest");
    public static final MinecraftKey END_CITY_TREASURE = a("chests/end_city_treasure");
    public static final MinecraftKey SIMPLE_DUNGEON = a("chests/simple_dungeon");
    public static final MinecraftKey VILLAGE_WEAPONSMITH = a("chests/village/village_weaponsmith");
    public static final MinecraftKey VILLAGE_TOOLSMITH = a("chests/village/village_toolsmith");
    public static final MinecraftKey VILLAGE_ARMORER = a("chests/village/village_armorer");
    public static final MinecraftKey VILLAGE_CARTOGRAPHER = a("chests/village/village_cartographer");
    public static final MinecraftKey VILLAGE_MASON = a("chests/village/village_mason");
    public static final MinecraftKey VILLAGE_SHEPHERD = a("chests/village/village_shepherd");
    public static final MinecraftKey VILLAGE_BUTCHER = a("chests/village/village_butcher");
    public static final MinecraftKey VILLAGE_FLETCHER = a("chests/village/village_fletcher");
    public static final MinecraftKey VILLAGE_FISHER = a("chests/village/village_fisher");
    public static final MinecraftKey VILLAGE_TANNERY = a("chests/village/village_tannery");
    public static final MinecraftKey VILLAGE_TEMPLE = a("chests/village/village_temple");
    public static final MinecraftKey VILLAGE_DESERT_HOUSE = a("chests/village/village_desert_house");
    public static final MinecraftKey VILLAGE_PLAINS_HOUSE = a("chests/village/village_plains_house");
    public static final MinecraftKey VILLAGE_TAIGA_HOUSE = a("chests/village/village_taiga_house");
    public static final MinecraftKey VILLAGE_SNOWY_HOUSE = a("chests/village/village_snowy_house");
    public static final MinecraftKey VILLAGE_SAVANNA_HOUSE = a("chests/village/village_savanna_house");
    public static final MinecraftKey ABANDONED_MINESHAFT = a("chests/abandoned_mineshaft");
    public static final MinecraftKey NETHER_BRIDGE = a("chests/nether_bridge");
    public static final MinecraftKey STRONGHOLD_LIBRARY = a("chests/stronghold_library");
    public static final MinecraftKey STRONGHOLD_CROSSING = a("chests/stronghold_crossing");
    public static final MinecraftKey STRONGHOLD_CORRIDOR = a("chests/stronghold_corridor");
    public static final MinecraftKey DESERT_PYRAMID = a("chests/desert_pyramid");
    public static final MinecraftKey JUNGLE_TEMPLE = a("chests/jungle_temple");
    public static final MinecraftKey JUNGLE_TEMPLE_DISPENSER = a("chests/jungle_temple_dispenser");
    public static final MinecraftKey IGLOO_CHEST = a("chests/igloo_chest");
    public static final MinecraftKey WOODLAND_MANSION = a("chests/woodland_mansion");
    public static final MinecraftKey UNDERWATER_RUIN_SMALL = a("chests/underwater_ruin_small");
    public static final MinecraftKey UNDERWATER_RUIN_BIG = a("chests/underwater_ruin_big");
    public static final MinecraftKey BURIED_TREASURE = a("chests/buried_treasure");
    public static final MinecraftKey SHIPWRECK_MAP = a("chests/shipwreck_map");
    public static final MinecraftKey SHIPWRECK_SUPPLY = a("chests/shipwreck_supply");
    public static final MinecraftKey SHIPWRECK_TREASURE = a("chests/shipwreck_treasure");
    public static final MinecraftKey PILLAGER_OUTPOST = a("chests/pillager_outpost");
    public static final MinecraftKey BASTION_TREASURE = a("chests/bastion_treasure");
    public static final MinecraftKey BASTION_OTHER = a("chests/bastion_other");
    public static final MinecraftKey BASTION_BRIDGE = a("chests/bastion_bridge");
    public static final MinecraftKey BASTION_HOGLIN_STABLE = a("chests/bastion_hoglin_stable");
    public static final MinecraftKey RUINED_PORTAL = a("chests/ruined_portal");
    public static final MinecraftKey SHEEP_WHITE = a("entities/sheep/white");
    public static final MinecraftKey SHEEP_ORANGE = a("entities/sheep/orange");
    public static final MinecraftKey SHEEP_MAGENTA = a("entities/sheep/magenta");
    public static final MinecraftKey SHEEP_LIGHT_BLUE = a("entities/sheep/light_blue");
    public static final MinecraftKey SHEEP_YELLOW = a("entities/sheep/yellow");
    public static final MinecraftKey SHEEP_LIME = a("entities/sheep/lime");
    public static final MinecraftKey SHEEP_PINK = a("entities/sheep/pink");
    public static final MinecraftKey SHEEP_GRAY = a("entities/sheep/gray");
    public static final MinecraftKey SHEEP_LIGHT_GRAY = a("entities/sheep/light_gray");
    public static final MinecraftKey SHEEP_CYAN = a("entities/sheep/cyan");
    public static final MinecraftKey SHEEP_PURPLE = a("entities/sheep/purple");
    public static final MinecraftKey SHEEP_BLUE = a("entities/sheep/blue");
    public static final MinecraftKey SHEEP_BROWN = a("entities/sheep/brown");
    public static final MinecraftKey SHEEP_GREEN = a("entities/sheep/green");
    public static final MinecraftKey SHEEP_RED = a("entities/sheep/red");
    public static final MinecraftKey SHEEP_BLACK = a("entities/sheep/black");
    public static final MinecraftKey FISHING = a("gameplay/fishing");
    public static final MinecraftKey FISHING_JUNK = a("gameplay/fishing/junk");
    public static final MinecraftKey FISHING_TREASURE = a("gameplay/fishing/treasure");
    public static final MinecraftKey FISHING_FISH = a("gameplay/fishing/fish");
    public static final MinecraftKey CAT_MORNING_GIFT = a("gameplay/cat_morning_gift");
    public static final MinecraftKey ARMORER_GIFT = a("gameplay/hero_of_the_village/armorer_gift");
    public static final MinecraftKey BUTCHER_GIFT = a("gameplay/hero_of_the_village/butcher_gift");
    public static final MinecraftKey CARTOGRAPHER_GIFT = a("gameplay/hero_of_the_village/cartographer_gift");
    public static final MinecraftKey CLERIC_GIFT = a("gameplay/hero_of_the_village/cleric_gift");
    public static final MinecraftKey FARMER_GIFT = a("gameplay/hero_of_the_village/farmer_gift");
    public static final MinecraftKey FISHERMAN_GIFT = a("gameplay/hero_of_the_village/fisherman_gift");
    public static final MinecraftKey FLETCHER_GIFT = a("gameplay/hero_of_the_village/fletcher_gift");
    public static final MinecraftKey LEATHERWORKER_GIFT = a("gameplay/hero_of_the_village/leatherworker_gift");
    public static final MinecraftKey LIBRARIAN_GIFT = a("gameplay/hero_of_the_village/librarian_gift");
    public static final MinecraftKey MASON_GIFT = a("gameplay/hero_of_the_village/mason_gift");
    public static final MinecraftKey SHEPHERD_GIFT = a("gameplay/hero_of_the_village/shepherd_gift");
    public static final MinecraftKey TOOLSMITH_GIFT = a("gameplay/hero_of_the_village/toolsmith_gift");
    public static final MinecraftKey WEAPONSMITH_GIFT = a("gameplay/hero_of_the_village/weaponsmith_gift");
    public static final MinecraftKey PIGLIN_BARTERING = a("gameplay/piglin_bartering");

    public LootTables() {}

    private static MinecraftKey a(String s) {
        return a(new MinecraftKey(s));
    }

    private static MinecraftKey a(MinecraftKey minecraftkey) {
        if (LootTables.LOCATIONS.add(minecraftkey)) {
            return minecraftkey;
        } else {
            throw new IllegalArgumentException(minecraftkey + " is already a registered built-in loot table");
        }
    }

    public static Set<MinecraftKey> a() {
        return LootTables.IMMUTABLE_LOCATIONS;
    }
}
