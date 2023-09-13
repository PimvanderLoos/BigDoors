package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTables {

    private static final Logger aS = LogManager.getLogger();
    private static final Set<MinecraftKey> aT = Sets.newHashSet();
    private static final Set<MinecraftKey> aU = Collections.unmodifiableSet(LootTables.aT);
    public static final MinecraftKey a = a("empty");
    public static final MinecraftKey b = a("chests/spawn_bonus_chest");
    public static final MinecraftKey c = a("chests/end_city_treasure");
    public static final MinecraftKey d = a("chests/simple_dungeon");
    public static final MinecraftKey e = a("chests/village_blacksmith");
    public static final MinecraftKey f = a("chests/abandoned_mineshaft");
    public static final MinecraftKey g = a("chests/nether_bridge");
    public static final MinecraftKey h = a("chests/stronghold_library");
    public static final MinecraftKey i = a("chests/stronghold_crossing");
    public static final MinecraftKey j = a("chests/stronghold_corridor");
    public static final MinecraftKey k = a("chests/desert_pyramid");
    public static final MinecraftKey l = a("chests/jungle_temple");
    public static final MinecraftKey m = a("chests/jungle_temple_dispenser");
    public static final MinecraftKey n = a("chests/igloo_chest");
    public static final MinecraftKey o = a("chests/woodland_mansion");
    public static final MinecraftKey p = a("chests/underwater_ruin_small");
    public static final MinecraftKey q = a("chests/underwater_ruin_big");
    public static final MinecraftKey r = a("chests/buried_treasure");
    public static final MinecraftKey s = a("chests/shipwreck_map");
    public static final MinecraftKey t = a("chests/shipwreck_supply");
    public static final MinecraftKey u = a("chests/shipwreck_treasure");
    public static final MinecraftKey v = a("entities/witch");
    public static final MinecraftKey w = a("entities/blaze");
    public static final MinecraftKey x = a("entities/creeper");
    public static final MinecraftKey y = a("entities/spider");
    public static final MinecraftKey z = a("entities/cave_spider");
    public static final MinecraftKey A = a("entities/giant");
    public static final MinecraftKey B = a("entities/silverfish");
    public static final MinecraftKey C = a("entities/enderman");
    public static final MinecraftKey D = a("entities/guardian");
    public static final MinecraftKey E = a("entities/elder_guardian");
    public static final MinecraftKey F = a("entities/shulker");
    public static final MinecraftKey G = a("entities/iron_golem");
    public static final MinecraftKey H = a("entities/snow_golem");
    public static final MinecraftKey I = a("entities/rabbit");
    public static final MinecraftKey J = a("entities/chicken");
    public static final MinecraftKey K = a("entities/phantom");
    public static final MinecraftKey L = a("entities/pig");
    public static final MinecraftKey M = a("entities/polar_bear");
    public static final MinecraftKey N = a("entities/horse");
    public static final MinecraftKey O = a("entities/donkey");
    public static final MinecraftKey P = a("entities/mule");
    public static final MinecraftKey Q = a("entities/zombie_horse");
    public static final MinecraftKey R = a("entities/skeleton_horse");
    public static final MinecraftKey S = a("entities/cow");
    public static final MinecraftKey T = a("entities/mushroom_cow");
    public static final MinecraftKey U = a("entities/wolf");
    public static final MinecraftKey V = a("entities/ocelot");
    public static final MinecraftKey W = a("entities/sheep");
    public static final MinecraftKey X = a("entities/sheep/white");
    public static final MinecraftKey Y = a("entities/sheep/orange");
    public static final MinecraftKey Z = a("entities/sheep/magenta");
    public static final MinecraftKey aa = a("entities/sheep/light_blue");
    public static final MinecraftKey ab = a("entities/sheep/yellow");
    public static final MinecraftKey ac = a("entities/sheep/lime");
    public static final MinecraftKey ad = a("entities/sheep/pink");
    public static final MinecraftKey ae = a("entities/sheep/gray");
    public static final MinecraftKey af = a("entities/sheep/light_gray");
    public static final MinecraftKey ag = a("entities/sheep/cyan");
    public static final MinecraftKey ah = a("entities/sheep/purple");
    public static final MinecraftKey ai = a("entities/sheep/blue");
    public static final MinecraftKey aj = a("entities/sheep/brown");
    public static final MinecraftKey ak = a("entities/sheep/green");
    public static final MinecraftKey al = a("entities/sheep/red");
    public static final MinecraftKey am = a("entities/sheep/black");
    public static final MinecraftKey an = a("entities/bat");
    public static final MinecraftKey ao = a("entities/slime");
    public static final MinecraftKey ap = a("entities/magma_cube");
    public static final MinecraftKey aq = a("entities/ghast");
    public static final MinecraftKey ar = a("entities/squid");
    public static final MinecraftKey as = a("entities/endermite");
    public static final MinecraftKey at = a("entities/zombie");
    public static final MinecraftKey au = a("entities/zombie_pigman");
    public static final MinecraftKey av = a("entities/skeleton");
    public static final MinecraftKey aw = a("entities/wither_skeleton");
    public static final MinecraftKey ax = a("entities/stray");
    public static final MinecraftKey ay = a("entities/husk");
    public static final MinecraftKey az = a("entities/zombie_villager");
    public static final MinecraftKey aA = a("entities/villager");
    public static final MinecraftKey aB = a("entities/evoker");
    public static final MinecraftKey aC = a("entities/vindicator");
    public static final MinecraftKey aD = a("entities/llama");
    public static final MinecraftKey aE = a("entities/parrot");
    public static final MinecraftKey aF = a("entities/pufferfish");
    public static final MinecraftKey aG = a("entities/vex");
    public static final MinecraftKey aH = a("entities/ender_dragon");
    public static final MinecraftKey aI = a("entities/turtle");
    public static final MinecraftKey aJ = a("entities/salmon");
    public static final MinecraftKey aK = a("entities/cod");
    public static final MinecraftKey aL = a("entities/tropical_fish");
    public static final MinecraftKey aM = a("entities/drowned");
    public static final MinecraftKey aN = a("entities/dolphin");
    public static final MinecraftKey aO = a("gameplay/fishing");
    public static final MinecraftKey aP = a("gameplay/fishing/junk");
    public static final MinecraftKey aQ = a("gameplay/fishing/treasure");
    public static final MinecraftKey aR = a("gameplay/fishing/fish");

    private static MinecraftKey a(String s) {
        return a(new MinecraftKey(s));
    }

    public static MinecraftKey a(MinecraftKey minecraftkey) {
        if (LootTables.aT.add(minecraftkey)) {
            return minecraftkey;
        } else {
            throw new IllegalArgumentException(minecraftkey + " is already a registered built-in loot table");
        }
    }
}
