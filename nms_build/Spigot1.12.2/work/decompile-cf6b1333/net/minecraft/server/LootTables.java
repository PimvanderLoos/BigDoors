package net.minecraft.server;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class LootTables {

    private static final Set<MinecraftKey> aE = Sets.newHashSet();
    private static final Set<MinecraftKey> aF = Collections.unmodifiableSet(LootTables.aE);
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
    public static final MinecraftKey p = a("entities/witch");
    public static final MinecraftKey q = a("entities/blaze");
    public static final MinecraftKey r = a("entities/creeper");
    public static final MinecraftKey s = a("entities/spider");
    public static final MinecraftKey t = a("entities/cave_spider");
    public static final MinecraftKey u = a("entities/giant");
    public static final MinecraftKey v = a("entities/silverfish");
    public static final MinecraftKey w = a("entities/enderman");
    public static final MinecraftKey x = a("entities/guardian");
    public static final MinecraftKey y = a("entities/elder_guardian");
    public static final MinecraftKey z = a("entities/shulker");
    public static final MinecraftKey A = a("entities/iron_golem");
    public static final MinecraftKey B = a("entities/snowman");
    public static final MinecraftKey C = a("entities/rabbit");
    public static final MinecraftKey D = a("entities/chicken");
    public static final MinecraftKey E = a("entities/pig");
    public static final MinecraftKey F = a("entities/polar_bear");
    public static final MinecraftKey G = a("entities/horse");
    public static final MinecraftKey H = a("entities/donkey");
    public static final MinecraftKey I = a("entities/mule");
    public static final MinecraftKey J = a("entities/zombie_horse");
    public static final MinecraftKey K = a("entities/skeleton_horse");
    public static final MinecraftKey L = a("entities/cow");
    public static final MinecraftKey M = a("entities/mushroom_cow");
    public static final MinecraftKey N = a("entities/wolf");
    public static final MinecraftKey O = a("entities/ocelot");
    public static final MinecraftKey P = a("entities/sheep");
    public static final MinecraftKey Q = a("entities/sheep/white");
    public static final MinecraftKey R = a("entities/sheep/orange");
    public static final MinecraftKey S = a("entities/sheep/magenta");
    public static final MinecraftKey T = a("entities/sheep/light_blue");
    public static final MinecraftKey U = a("entities/sheep/yellow");
    public static final MinecraftKey V = a("entities/sheep/lime");
    public static final MinecraftKey W = a("entities/sheep/pink");
    public static final MinecraftKey X = a("entities/sheep/gray");
    public static final MinecraftKey Y = a("entities/sheep/silver");
    public static final MinecraftKey Z = a("entities/sheep/cyan");
    public static final MinecraftKey aa = a("entities/sheep/purple");
    public static final MinecraftKey ab = a("entities/sheep/blue");
    public static final MinecraftKey ac = a("entities/sheep/brown");
    public static final MinecraftKey ad = a("entities/sheep/green");
    public static final MinecraftKey ae = a("entities/sheep/red");
    public static final MinecraftKey af = a("entities/sheep/black");
    public static final MinecraftKey ag = a("entities/bat");
    public static final MinecraftKey ah = a("entities/slime");
    public static final MinecraftKey ai = a("entities/magma_cube");
    public static final MinecraftKey aj = a("entities/ghast");
    public static final MinecraftKey ak = a("entities/squid");
    public static final MinecraftKey al = a("entities/endermite");
    public static final MinecraftKey am = a("entities/zombie");
    public static final MinecraftKey an = a("entities/zombie_pigman");
    public static final MinecraftKey ao = a("entities/skeleton");
    public static final MinecraftKey ap = a("entities/wither_skeleton");
    public static final MinecraftKey aq = a("entities/stray");
    public static final MinecraftKey ar = a("entities/husk");
    public static final MinecraftKey as = a("entities/zombie_villager");
    public static final MinecraftKey at = a("entities/villager");
    public static final MinecraftKey au = a("entities/evocation_illager");
    public static final MinecraftKey av = a("entities/vindication_illager");
    public static final MinecraftKey aw = a("entities/llama");
    public static final MinecraftKey ax = a("entities/parrot");
    public static final MinecraftKey ay = a("entities/vex");
    public static final MinecraftKey az = a("entities/ender_dragon");
    public static final MinecraftKey aA = a("gameplay/fishing");
    public static final MinecraftKey aB = a("gameplay/fishing/junk");
    public static final MinecraftKey aC = a("gameplay/fishing/treasure");
    public static final MinecraftKey aD = a("gameplay/fishing/fish");

    private static MinecraftKey a(String s) {
        return a(new MinecraftKey("minecraft", s));
    }

    public static MinecraftKey a(MinecraftKey minecraftkey) {
        if (LootTables.aE.add(minecraftkey)) {
            return minecraftkey;
        } else {
            throw new IllegalArgumentException(minecraftkey + " is already a registered built-in loot table");
        }
    }

    public static Set<MinecraftKey> a() {
        return LootTables.aF;
    }

    public static boolean b() {
        LootTableRegistry loottableregistry = new LootTableRegistry((File) null);
        Iterator iterator = LootTables.aF.iterator();

        MinecraftKey minecraftkey;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            minecraftkey = (MinecraftKey) iterator.next();
        } while (loottableregistry.a(minecraftkey) != LootTable.a);

        return false;
    }
}
