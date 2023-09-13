package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.RegistryID;
import net.minecraft.util.datafix.DataBitsPacked;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkConverterPalette extends DataFix {

    private static final int NORTH_WEST_MASK = 128;
    private static final int WEST_MASK = 64;
    private static final int SOUTH_WEST_MASK = 32;
    private static final int SOUTH_MASK = 16;
    private static final int SOUTH_EAST_MASK = 8;
    private static final int EAST_MASK = 4;
    private static final int NORTH_EAST_MASK = 2;
    private static final int NORTH_MASK = 1;
    static final Logger LOGGER = LogManager.getLogger();
    static final BitSet VIRTUAL = new BitSet(256);
    static final BitSet FIX = new BitSet(256);
    static final Dynamic<?> PUMPKIN = DataConverterFlattenData.b("{Name:'minecraft:pumpkin'}");
    static final Dynamic<?> SNOWY_PODZOL = DataConverterFlattenData.b("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
    static final Dynamic<?> SNOWY_GRASS = DataConverterFlattenData.b("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
    static final Dynamic<?> SNOWY_MYCELIUM = DataConverterFlattenData.b("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
    static final Dynamic<?> UPPER_SUNFLOWER = DataConverterFlattenData.b("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_LILAC = DataConverterFlattenData.b("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_TALL_GRASS = DataConverterFlattenData.b("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_LARGE_FERN = DataConverterFlattenData.b("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_ROSE_BUSH = DataConverterFlattenData.b("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_PEONY = DataConverterFlattenData.b("{Name:'minecraft:peony',Properties:{half:'upper'}}");
    static final Map<String, Dynamic<?>> FLOWER_POT_MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put("minecraft:air0", DataConverterFlattenData.b("{Name:'minecraft:flower_pot'}"));
        hashmap.put("minecraft:red_flower0", DataConverterFlattenData.b("{Name:'minecraft:potted_poppy'}"));
        hashmap.put("minecraft:red_flower1", DataConverterFlattenData.b("{Name:'minecraft:potted_blue_orchid'}"));
        hashmap.put("minecraft:red_flower2", DataConverterFlattenData.b("{Name:'minecraft:potted_allium'}"));
        hashmap.put("minecraft:red_flower3", DataConverterFlattenData.b("{Name:'minecraft:potted_azure_bluet'}"));
        hashmap.put("minecraft:red_flower4", DataConverterFlattenData.b("{Name:'minecraft:potted_red_tulip'}"));
        hashmap.put("minecraft:red_flower5", DataConverterFlattenData.b("{Name:'minecraft:potted_orange_tulip'}"));
        hashmap.put("minecraft:red_flower6", DataConverterFlattenData.b("{Name:'minecraft:potted_white_tulip'}"));
        hashmap.put("minecraft:red_flower7", DataConverterFlattenData.b("{Name:'minecraft:potted_pink_tulip'}"));
        hashmap.put("minecraft:red_flower8", DataConverterFlattenData.b("{Name:'minecraft:potted_oxeye_daisy'}"));
        hashmap.put("minecraft:yellow_flower0", DataConverterFlattenData.b("{Name:'minecraft:potted_dandelion'}"));
        hashmap.put("minecraft:sapling0", DataConverterFlattenData.b("{Name:'minecraft:potted_oak_sapling'}"));
        hashmap.put("minecraft:sapling1", DataConverterFlattenData.b("{Name:'minecraft:potted_spruce_sapling'}"));
        hashmap.put("minecraft:sapling2", DataConverterFlattenData.b("{Name:'minecraft:potted_birch_sapling'}"));
        hashmap.put("minecraft:sapling3", DataConverterFlattenData.b("{Name:'minecraft:potted_jungle_sapling'}"));
        hashmap.put("minecraft:sapling4", DataConverterFlattenData.b("{Name:'minecraft:potted_acacia_sapling'}"));
        hashmap.put("minecraft:sapling5", DataConverterFlattenData.b("{Name:'minecraft:potted_dark_oak_sapling'}"));
        hashmap.put("minecraft:red_mushroom0", DataConverterFlattenData.b("{Name:'minecraft:potted_red_mushroom'}"));
        hashmap.put("minecraft:brown_mushroom0", DataConverterFlattenData.b("{Name:'minecraft:potted_brown_mushroom'}"));
        hashmap.put("minecraft:deadbush0", DataConverterFlattenData.b("{Name:'minecraft:potted_dead_bush'}"));
        hashmap.put("minecraft:tallgrass2", DataConverterFlattenData.b("{Name:'minecraft:potted_fern'}"));
        hashmap.put("minecraft:cactus0", DataConverterFlattenData.b(2240));
    });
    static final Map<String, Dynamic<?>> SKULL_MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        a(hashmap, 0, "skeleton", "skull");
        a(hashmap, 1, "wither_skeleton", "skull");
        a(hashmap, 2, "zombie", "head");
        a(hashmap, 3, "player", "head");
        a(hashmap, 4, "creeper", "head");
        a(hashmap, 5, "dragon", "head");
    });
    static final Map<String, Dynamic<?>> DOOR_MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        a(hashmap, "oak_door", 1024);
        a(hashmap, "iron_door", 1136);
        a(hashmap, "spruce_door", 3088);
        a(hashmap, "birch_door", 3104);
        a(hashmap, "jungle_door", 3120);
        a(hashmap, "acacia_door", 3136);
        a(hashmap, "dark_oak_door", 3152);
    });
    static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        for (int i = 0; i < 26; ++i) {
            hashmap.put("true" + i, DataConverterFlattenData.b("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
            hashmap.put("false" + i, DataConverterFlattenData.b("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
        }

    });
    private static final Int2ObjectMap<String> DYE_COLOR_MAP = (Int2ObjectMap) DataFixUtils.make(new Int2ObjectOpenHashMap(), (int2objectopenhashmap) -> {
        int2objectopenhashmap.put(0, "white");
        int2objectopenhashmap.put(1, "orange");
        int2objectopenhashmap.put(2, "magenta");
        int2objectopenhashmap.put(3, "light_blue");
        int2objectopenhashmap.put(4, "yellow");
        int2objectopenhashmap.put(5, "lime");
        int2objectopenhashmap.put(6, "pink");
        int2objectopenhashmap.put(7, "gray");
        int2objectopenhashmap.put(8, "light_gray");
        int2objectopenhashmap.put(9, "cyan");
        int2objectopenhashmap.put(10, "purple");
        int2objectopenhashmap.put(11, "blue");
        int2objectopenhashmap.put(12, "brown");
        int2objectopenhashmap.put(13, "green");
        int2objectopenhashmap.put(14, "red");
        int2objectopenhashmap.put(15, "black");
    });
    static final Map<String, Dynamic<?>> BED_BLOCK_MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        ObjectIterator objectiterator = ChunkConverterPalette.DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

        while (objectiterator.hasNext()) {
            Entry<String> entry = (Entry) objectiterator.next();

            if (!Objects.equals(entry.getValue(), "red")) {
                a(hashmap, entry.getIntKey(), (String) entry.getValue());
            }
        }

    });
    static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        ObjectIterator objectiterator = ChunkConverterPalette.DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

        while (objectiterator.hasNext()) {
            Entry<String> entry = (Entry) objectiterator.next();

            if (!Objects.equals(entry.getValue(), "white")) {
                b(hashmap, 15 - entry.getIntKey(), (String) entry.getValue());
            }
        }

    });
    static final Dynamic<?> AIR;
    private static final int SIZE = 4096;

    public ChunkConverterPalette(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private static void a(Map<String, Dynamic<?>> map, int i, String s, String s1) {
        map.put(i + "north", DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_" + s1 + "',Properties:{facing:'north'}}"));
        map.put(i + "east", DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_" + s1 + "',Properties:{facing:'east'}}"));
        map.put(i + "south", DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_" + s1 + "',Properties:{facing:'south'}}"));
        map.put(i + "west", DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_" + s1 + "',Properties:{facing:'west'}}"));

        for (int j = 0; j < 16; ++j) {
            map.put(i + j, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_" + s1 + "',Properties:{rotation:'" + j + "'}}"));
        }

    }

    private static void a(Map<String, Dynamic<?>> map, String s, int i) {
        map.put("minecraft:" + s + "eastlowerleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "eastlowerleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "eastlowerlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "eastlowerlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "eastlowerrightfalsefalse", DataConverterFlattenData.b(i));
        map.put("minecraft:" + s + "eastlowerrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "eastlowerrighttruefalse", DataConverterFlattenData.b(i + 4));
        map.put("minecraft:" + s + "eastlowerrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "eastupperleftfalsefalse", DataConverterFlattenData.b(i + 8));
        map.put("minecraft:" + s + "eastupperleftfalsetrue", DataConverterFlattenData.b(i + 10));
        map.put("minecraft:" + s + "eastupperlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "eastupperlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "eastupperrightfalsefalse", DataConverterFlattenData.b(i + 9));
        map.put("minecraft:" + s + "eastupperrightfalsetrue", DataConverterFlattenData.b(i + 11));
        map.put("minecraft:" + s + "eastupperrighttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "eastupperrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "northlowerleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "northlowerleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "northlowerlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "northlowerlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "northlowerrightfalsefalse", DataConverterFlattenData.b(i + 3));
        map.put("minecraft:" + s + "northlowerrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "northlowerrighttruefalse", DataConverterFlattenData.b(i + 7));
        map.put("minecraft:" + s + "northlowerrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "northupperleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "northupperleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "northupperlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "northupperlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "northupperrightfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "northupperrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "northupperrighttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "northupperrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "southlowerleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "southlowerleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "southlowerlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "southlowerlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "southlowerrightfalsefalse", DataConverterFlattenData.b(i + 1));
        map.put("minecraft:" + s + "southlowerrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "southlowerrighttruefalse", DataConverterFlattenData.b(i + 5));
        map.put("minecraft:" + s + "southlowerrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "southupperleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "southupperleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "southupperlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "southupperlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "southupperrightfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "southupperrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "southupperrighttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "southupperrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "westlowerleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "westlowerleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "westlowerlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "westlowerlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "westlowerrightfalsefalse", DataConverterFlattenData.b(i + 2));
        map.put("minecraft:" + s + "westlowerrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "westlowerrighttruefalse", DataConverterFlattenData.b(i + 6));
        map.put("minecraft:" + s + "westlowerrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "westupperleftfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "westupperleftfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "westupperlefttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "westupperlefttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + s + "westupperrightfalsefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + s + "westupperrightfalsetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + s + "westupperrighttruefalse", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + s + "westupperrighttruetrue", DataConverterFlattenData.b("{Name:'minecraft:" + s + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
    }

    private static void a(Map<String, Dynamic<?>> map, int i, String s) {
        map.put("southfalsefoot" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
        map.put("westfalsefoot" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
        map.put("northfalsefoot" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
        map.put("eastfalsefoot" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
        map.put("southfalsehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
        map.put("westfalsehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
        map.put("northfalsehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
        map.put("eastfalsehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
        map.put("southtruehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
        map.put("westtruehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
        map.put("northtruehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
        map.put("easttruehead" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
    }

    private static void b(Map<String, Dynamic<?>> map, int i, String s) {
        for (int j = 0; j < 16; ++j) {
            map.put(j + "_" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_banner',Properties:{rotation:'" + j + "'}}"));
        }

        map.put("north_" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_banner',Properties:{facing:'north'}}"));
        map.put("south_" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_banner',Properties:{facing:'south'}}"));
        map.put("west_" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_banner',Properties:{facing:'west'}}"));
        map.put("east_" + i, DataConverterFlattenData.b("{Name:'minecraft:" + s + "_wall_banner',Properties:{facing:'east'}}"));
    }

    public static String a(Dynamic<?> dynamic) {
        return dynamic.get("Name").asString("");
    }

    public static String a(Dynamic<?> dynamic, String s) {
        return dynamic.get("Properties").get(s).asString("");
    }

    public static int a(RegistryID<Dynamic<?>> registryid, Dynamic<?> dynamic) {
        int i = registryid.getId(dynamic);

        if (i == -1) {
            i = registryid.c(dynamic);
        }

        return i;
    }

    private Dynamic<?> b(Dynamic<?> dynamic) {
        Optional<? extends Dynamic<?>> optional = dynamic.get("Level").result();

        return optional.isPresent() && ((Dynamic) optional.get()).get("Sections").asStreamOpt().result().isPresent() ? dynamic.set("Level", (new ChunkConverterPalette.d((Dynamic) optional.get())).a()) : dynamic;
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        Type<?> type1 = this.getOutputSchema().getType(DataConverterTypes.CHUNK);

        return this.writeFixAndRead("ChunkPalettedStorageFix", type, type1, this::b);
    }

    public static int a(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        int i = 0;

        if (flag2) {
            if (flag1) {
                i |= 2;
            } else if (flag) {
                i |= 128;
            } else {
                i |= 1;
            }
        } else if (flag3) {
            if (flag) {
                i |= 32;
            } else if (flag1) {
                i |= 8;
            } else {
                i |= 16;
            }
        } else if (flag1) {
            i |= 4;
        } else if (flag) {
            i |= 64;
        }

        return i;
    }

    static {
        ChunkConverterPalette.FIX.set(2);
        ChunkConverterPalette.FIX.set(3);
        ChunkConverterPalette.FIX.set(110);
        ChunkConverterPalette.FIX.set(140);
        ChunkConverterPalette.FIX.set(144);
        ChunkConverterPalette.FIX.set(25);
        ChunkConverterPalette.FIX.set(86);
        ChunkConverterPalette.FIX.set(26);
        ChunkConverterPalette.FIX.set(176);
        ChunkConverterPalette.FIX.set(177);
        ChunkConverterPalette.FIX.set(175);
        ChunkConverterPalette.FIX.set(64);
        ChunkConverterPalette.FIX.set(71);
        ChunkConverterPalette.FIX.set(193);
        ChunkConverterPalette.FIX.set(194);
        ChunkConverterPalette.FIX.set(195);
        ChunkConverterPalette.FIX.set(196);
        ChunkConverterPalette.FIX.set(197);
        ChunkConverterPalette.VIRTUAL.set(54);
        ChunkConverterPalette.VIRTUAL.set(146);
        ChunkConverterPalette.VIRTUAL.set(25);
        ChunkConverterPalette.VIRTUAL.set(26);
        ChunkConverterPalette.VIRTUAL.set(51);
        ChunkConverterPalette.VIRTUAL.set(53);
        ChunkConverterPalette.VIRTUAL.set(67);
        ChunkConverterPalette.VIRTUAL.set(108);
        ChunkConverterPalette.VIRTUAL.set(109);
        ChunkConverterPalette.VIRTUAL.set(114);
        ChunkConverterPalette.VIRTUAL.set(128);
        ChunkConverterPalette.VIRTUAL.set(134);
        ChunkConverterPalette.VIRTUAL.set(135);
        ChunkConverterPalette.VIRTUAL.set(136);
        ChunkConverterPalette.VIRTUAL.set(156);
        ChunkConverterPalette.VIRTUAL.set(163);
        ChunkConverterPalette.VIRTUAL.set(164);
        ChunkConverterPalette.VIRTUAL.set(180);
        ChunkConverterPalette.VIRTUAL.set(203);
        ChunkConverterPalette.VIRTUAL.set(55);
        ChunkConverterPalette.VIRTUAL.set(85);
        ChunkConverterPalette.VIRTUAL.set(113);
        ChunkConverterPalette.VIRTUAL.set(188);
        ChunkConverterPalette.VIRTUAL.set(189);
        ChunkConverterPalette.VIRTUAL.set(190);
        ChunkConverterPalette.VIRTUAL.set(191);
        ChunkConverterPalette.VIRTUAL.set(192);
        ChunkConverterPalette.VIRTUAL.set(93);
        ChunkConverterPalette.VIRTUAL.set(94);
        ChunkConverterPalette.VIRTUAL.set(101);
        ChunkConverterPalette.VIRTUAL.set(102);
        ChunkConverterPalette.VIRTUAL.set(160);
        ChunkConverterPalette.VIRTUAL.set(106);
        ChunkConverterPalette.VIRTUAL.set(107);
        ChunkConverterPalette.VIRTUAL.set(183);
        ChunkConverterPalette.VIRTUAL.set(184);
        ChunkConverterPalette.VIRTUAL.set(185);
        ChunkConverterPalette.VIRTUAL.set(186);
        ChunkConverterPalette.VIRTUAL.set(187);
        ChunkConverterPalette.VIRTUAL.set(132);
        ChunkConverterPalette.VIRTUAL.set(139);
        ChunkConverterPalette.VIRTUAL.set(199);
        AIR = DataConverterFlattenData.b(0);
    }

    private static final class d {

        private int sides;
        private final ChunkConverterPalette.c[] sections = new ChunkConverterPalette.c[16];
        private final Dynamic<?> level;
        private final int x;
        private final int z;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

        public d(Dynamic<?> dynamic) {
            this.level = dynamic;
            this.x = dynamic.get("xPos").asInt(0) << 4;
            this.z = dynamic.get("zPos").asInt(0) << 4;
            dynamic.get("TileEntities").asStreamOpt().result().ifPresent((stream) -> {
                stream.forEach((dynamic1) -> {
                    int i = dynamic1.get("x").asInt(0) - this.x & 15;
                    int j = dynamic1.get("y").asInt(0);
                    int k = dynamic1.get("z").asInt(0) - this.z & 15;
                    int l = j << 8 | k << 4 | i;

                    if (this.blockEntities.put(l, dynamic1) != null) {
                        ChunkConverterPalette.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, i, j, k);
                    }

                });
            });
            boolean flag = dynamic.get("convertedFromAlphaFormat").asBoolean(false);

            dynamic.get("Sections").asStreamOpt().result().ifPresent((stream) -> {
                stream.forEach((dynamic1) -> {
                    ChunkConverterPalette.c chunkconverterpalette_c = new ChunkConverterPalette.c(dynamic1);

                    this.sides = chunkconverterpalette_c.b(this.sides);
                    this.sections[chunkconverterpalette_c.y] = chunkconverterpalette_c;
                });
            });
            ChunkConverterPalette.c[] achunkconverterpalette_c = this.sections;
            int i = achunkconverterpalette_c.length;

            for (int j = 0; j < i; ++j) {
                ChunkConverterPalette.c chunkconverterpalette_c = achunkconverterpalette_c[j];

                if (chunkconverterpalette_c != null) {
                    ObjectIterator objectiterator = chunkconverterpalette_c.toFix.entrySet().iterator();

                    label229:
                    while (objectiterator.hasNext()) {
                        java.util.Map.Entry<Integer, IntList> java_util_map_entry = (java.util.Map.Entry) objectiterator.next();
                        int k = chunkconverterpalette_c.y << 12;
                        IntListIterator intlistiterator;
                        int l;
                        Dynamic dynamic1;
                        String s;
                        String s1;
                        Dynamic dynamic2;
                        int i1;
                        String s2;
                        String s3;

                        switch ((Integer) java_util_map_entry.getKey()) {
                            case 2:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (intlistiterator.hasNext()) {
                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.a(l);
                                    if ("minecraft:grass_block".equals(ChunkConverterPalette.a(dynamic1))) {
                                        s = ChunkConverterPalette.a(this.a(a(l, ChunkConverterPalette.Direction.UP)));
                                        if ("minecraft:snow".equals(s) || "minecraft:snow_layer".equals(s)) {
                                            this.a(l, ChunkConverterPalette.SNOWY_GRASS);
                                        }
                                    }
                                }
                                break;
                            case 3:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.a(l);
                                    if ("minecraft:podzol".equals(ChunkConverterPalette.a(dynamic1))) {
                                        s = ChunkConverterPalette.a(this.a(a(l, ChunkConverterPalette.Direction.UP)));
                                        if ("minecraft:snow".equals(s) || "minecraft:snow_layer".equals(s)) {
                                            this.a(l, ChunkConverterPalette.SNOWY_PODZOL);
                                        }
                                    }
                                }
                            case 25:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.c(l);
                                    if (dynamic1 != null) {
                                        s1 = Boolean.toString(dynamic1.get("powered").asBoolean(false));
                                        s = s1 + (byte) Math.min(Math.max(dynamic1.get("note").asInt(0), 0), 24);
                                        this.a(l, (Dynamic) ChunkConverterPalette.NOTE_BLOCK_MAP.getOrDefault(s, (Dynamic) ChunkConverterPalette.NOTE_BLOCK_MAP.get("false0")));
                                    }
                                }
                            case 26:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.b(l);
                                    dynamic2 = this.a(l);
                                    if (dynamic1 != null) {
                                        i1 = dynamic1.get("color").asInt(0);
                                        if (i1 != 14 && i1 >= 0 && i1 < 16) {
                                            s1 = ChunkConverterPalette.a(dynamic2, "facing");
                                            s2 = s1 + ChunkConverterPalette.a(dynamic2, "occupied") + ChunkConverterPalette.a(dynamic2, "part") + i1;
                                            if (ChunkConverterPalette.BED_BLOCK_MAP.containsKey(s2)) {
                                                this.a(l, (Dynamic) ChunkConverterPalette.BED_BLOCK_MAP.get(s2));
                                            }
                                        }
                                    }
                                }
                            case 64:
                            case 71:
                            case 193:
                            case 194:
                            case 195:
                            case 196:
                            case 197:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.a(l);
                                    if (ChunkConverterPalette.a(dynamic1).endsWith("_door")) {
                                        dynamic2 = this.a(l);
                                        if ("lower".equals(ChunkConverterPalette.a(dynamic2, "half"))) {
                                            i1 = a(l, ChunkConverterPalette.Direction.UP);
                                            Dynamic<?> dynamic3 = this.a(i1);
                                            String s4 = ChunkConverterPalette.a(dynamic2);

                                            if (s4.equals(ChunkConverterPalette.a(dynamic3))) {
                                                String s5 = ChunkConverterPalette.a(dynamic2, "facing");
                                                String s6 = ChunkConverterPalette.a(dynamic2, "open");
                                                String s7 = flag ? "left" : ChunkConverterPalette.a(dynamic3, "hinge");
                                                String s8 = flag ? "false" : ChunkConverterPalette.a(dynamic3, "powered");

                                                this.a(l, (Dynamic) ChunkConverterPalette.DOOR_MAP.get(s4 + s5 + "lower" + s7 + s6 + s8));
                                                this.a(i1, (Dynamic) ChunkConverterPalette.DOOR_MAP.get(s4 + s5 + "upper" + s7 + s6 + s8));
                                            }
                                        }
                                    }
                                }
                            case 86:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.a(l);
                                    if ("minecraft:carved_pumpkin".equals(ChunkConverterPalette.a(dynamic1))) {
                                        s = ChunkConverterPalette.a(this.a(a(l, ChunkConverterPalette.Direction.DOWN)));
                                        if ("minecraft:grass_block".equals(s) || "minecraft:dirt".equals(s)) {
                                            this.a(l, ChunkConverterPalette.PUMPKIN);
                                        }
                                    }
                                }
                            case 110:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.a(l);
                                    if ("minecraft:mycelium".equals(ChunkConverterPalette.a(dynamic1))) {
                                        s = ChunkConverterPalette.a(this.a(a(l, ChunkConverterPalette.Direction.UP)));
                                        if ("minecraft:snow".equals(s) || "minecraft:snow_layer".equals(s)) {
                                            this.a(l, ChunkConverterPalette.SNOWY_MYCELIUM);
                                        }
                                    }
                                }
                            case 140:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.c(l);
                                    if (dynamic1 != null) {
                                        s1 = dynamic1.get("Item").asString("");
                                        s = s1 + dynamic1.get("Data").asInt(0);
                                        this.a(l, (Dynamic) ChunkConverterPalette.FLOWER_POT_MAP.getOrDefault(s, (Dynamic) ChunkConverterPalette.FLOWER_POT_MAP.get("minecraft:air0")));
                                    }
                                }
                            case 144:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.b(l);
                                    if (dynamic1 != null) {
                                        s = String.valueOf(dynamic1.get("SkullType").asInt(0));
                                        s3 = ChunkConverterPalette.a(this.a(l), "facing");
                                        if (!"up".equals(s3) && !"down".equals(s3)) {
                                            s2 = s + s3;
                                        } else {
                                            s2 = s + String.valueOf(dynamic1.get("Rot").asInt(0));
                                        }

                                        dynamic1.remove("SkullType");
                                        dynamic1.remove("facing");
                                        dynamic1.remove("Rot");
                                        this.a(l, (Dynamic) ChunkConverterPalette.SKULL_MAP.getOrDefault(s2, (Dynamic) ChunkConverterPalette.SKULL_MAP.get("0north")));
                                    }
                                }
                            case 175:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (true) {
                                    if (!intlistiterator.hasNext()) {
                                        continue label229;
                                    }

                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.a(l);
                                    if ("upper".equals(ChunkConverterPalette.a(dynamic1, "half"))) {
                                        dynamic2 = this.a(a(l, ChunkConverterPalette.Direction.DOWN));
                                        s3 = ChunkConverterPalette.a(dynamic2);
                                        if ("minecraft:sunflower".equals(s3)) {
                                            this.a(l, ChunkConverterPalette.UPPER_SUNFLOWER);
                                        } else if ("minecraft:lilac".equals(s3)) {
                                            this.a(l, ChunkConverterPalette.UPPER_LILAC);
                                        } else if ("minecraft:tall_grass".equals(s3)) {
                                            this.a(l, ChunkConverterPalette.UPPER_TALL_GRASS);
                                        } else if ("minecraft:large_fern".equals(s3)) {
                                            this.a(l, ChunkConverterPalette.UPPER_LARGE_FERN);
                                        } else if ("minecraft:rose_bush".equals(s3)) {
                                            this.a(l, ChunkConverterPalette.UPPER_ROSE_BUSH);
                                        } else if ("minecraft:peony".equals(s3)) {
                                            this.a(l, ChunkConverterPalette.UPPER_PEONY);
                                        }
                                    }
                                }
                            case 176:
                            case 177:
                                intlistiterator = ((IntList) java_util_map_entry.getValue()).iterator();

                                while (intlistiterator.hasNext()) {
                                    l = (Integer) intlistiterator.next();
                                    l |= k;
                                    dynamic1 = this.b(l);
                                    dynamic2 = this.a(l);
                                    if (dynamic1 != null) {
                                        i1 = dynamic1.get("Base").asInt(0);
                                        if (i1 != 15 && i1 >= 0 && i1 < 16) {
                                            s1 = ChunkConverterPalette.a(dynamic2, (Integer) java_util_map_entry.getKey() == 176 ? "rotation" : "facing");
                                            s2 = s1 + "_" + i1;
                                            if (ChunkConverterPalette.BANNER_BLOCK_MAP.containsKey(s2)) {
                                                this.a(l, (Dynamic) ChunkConverterPalette.BANNER_BLOCK_MAP.get(s2));
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }
            }

        }

        @Nullable
        private Dynamic<?> b(int i) {
            return (Dynamic) this.blockEntities.get(i);
        }

        @Nullable
        private Dynamic<?> c(int i) {
            return (Dynamic) this.blockEntities.remove(i);
        }

        public static int a(int i, ChunkConverterPalette.Direction chunkconverterpalette_direction) {
            switch (chunkconverterpalette_direction.b()) {
                case X:
                    int j = (i & 15) + chunkconverterpalette_direction.a().a();

                    return j >= 0 && j <= 15 ? i & -16 | j : -1;
                case Y:
                    int k = (i >> 8) + chunkconverterpalette_direction.a().a();

                    return k >= 0 && k <= 255 ? i & 255 | k << 8 : -1;
                case Z:
                    int l = (i >> 4 & 15) + chunkconverterpalette_direction.a().a();

                    return l >= 0 && l <= 15 ? i & -241 | l << 4 : -1;
                default:
                    return -1;
            }
        }

        private void a(int i, Dynamic<?> dynamic) {
            if (i >= 0 && i <= 65535) {
                ChunkConverterPalette.c chunkconverterpalette_c = this.d(i);

                if (chunkconverterpalette_c != null) {
                    chunkconverterpalette_c.a(i & 4095, dynamic);
                }
            }
        }

        @Nullable
        private ChunkConverterPalette.c d(int i) {
            int j = i >> 12;

            return j < this.sections.length ? this.sections[j] : null;
        }

        public Dynamic<?> a(int i) {
            if (i >= 0 && i <= 65535) {
                ChunkConverterPalette.c chunkconverterpalette_c = this.d(i);

                return chunkconverterpalette_c == null ? ChunkConverterPalette.AIR : chunkconverterpalette_c.a(i & 4095);
            } else {
                return ChunkConverterPalette.AIR;
            }
        }

        public Dynamic<?> a() {
            Dynamic<?> dynamic = this.level;

            if (this.blockEntities.isEmpty()) {
                dynamic = dynamic.remove("TileEntities");
            } else {
                dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
            }

            Dynamic<?> dynamic1 = dynamic.emptyMap();
            List<Dynamic<?>> list = Lists.newArrayList();
            ChunkConverterPalette.c[] achunkconverterpalette_c = this.sections;
            int i = achunkconverterpalette_c.length;

            for (int j = 0; j < i; ++j) {
                ChunkConverterPalette.c chunkconverterpalette_c = achunkconverterpalette_c[j];

                if (chunkconverterpalette_c != null) {
                    list.add(chunkconverterpalette_c.a());
                    dynamic1 = dynamic1.set(String.valueOf(chunkconverterpalette_c.y), dynamic1.createIntList(Arrays.stream(chunkconverterpalette_c.update.toIntArray())));
                }
            }

            Dynamic<?> dynamic2 = dynamic.emptyMap();

            dynamic2 = dynamic2.set("Sides", dynamic2.createByte((byte) this.sides));
            dynamic2 = dynamic2.set("Indices", dynamic1);
            return dynamic.set("UpgradeData", dynamic2).set("Sections", dynamic2.createList(list.stream()));
        }
    }

    public static enum Direction {

        DOWN(ChunkConverterPalette.Direction.AxisDirection.NEGATIVE, ChunkConverterPalette.Direction.Axis.Y), UP(ChunkConverterPalette.Direction.AxisDirection.POSITIVE, ChunkConverterPalette.Direction.Axis.Y), NORTH(ChunkConverterPalette.Direction.AxisDirection.NEGATIVE, ChunkConverterPalette.Direction.Axis.Z), SOUTH(ChunkConverterPalette.Direction.AxisDirection.POSITIVE, ChunkConverterPalette.Direction.Axis.Z), WEST(ChunkConverterPalette.Direction.AxisDirection.NEGATIVE, ChunkConverterPalette.Direction.Axis.X), EAST(ChunkConverterPalette.Direction.AxisDirection.POSITIVE, ChunkConverterPalette.Direction.Axis.X);

        private final ChunkConverterPalette.Direction.Axis axis;
        private final ChunkConverterPalette.Direction.AxisDirection axisDirection;

        private Direction(ChunkConverterPalette.Direction.AxisDirection chunkconverterpalette_direction_axisdirection, ChunkConverterPalette.Direction.Axis chunkconverterpalette_direction_axis) {
            this.axis = chunkconverterpalette_direction_axis;
            this.axisDirection = chunkconverterpalette_direction_axisdirection;
        }

        public ChunkConverterPalette.Direction.AxisDirection a() {
            return this.axisDirection;
        }

        public ChunkConverterPalette.Direction.Axis b() {
            return this.axis;
        }

        public static enum Axis {

            X, Y, Z;

            private Axis() {}
        }

        public static enum AxisDirection {

            POSITIVE(1), NEGATIVE(-1);

            private final int step;

            private AxisDirection(int i) {
                this.step = i;
            }

            public int a() {
                return this.step;
            }
        }
    }

    private static class a {

        private static final int SIZE = 2048;
        private static final int NIBBLE_SIZE = 4;
        private final byte[] data;

        public a() {
            this.data = new byte[2048];
        }

        public a(byte[] abyte) {
            this.data = abyte;
            if (abyte.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + abyte.length);
            }
        }

        public int a(int i, int j, int k) {
            int l = this.b(j << 8 | k << 4 | i);

            return this.a(j << 8 | k << 4 | i) ? this.data[l] & 15 : this.data[l] >> 4 & 15;
        }

        private boolean a(int i) {
            return (i & 1) == 0;
        }

        private int b(int i) {
            return i >> 1;
        }
    }

    private static class c {

        private final RegistryID<Dynamic<?>> palette = new RegistryID<>(32);
        private final List<Dynamic<?>> listTag = Lists.newArrayList();
        private final Dynamic<?> section;
        private final boolean hasData;
        final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap();
        final IntList update = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
        private final int[] buffer = new int[4096];

        public c(Dynamic<?> dynamic) {
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasData = dynamic.get("Blocks").result().isPresent();
        }

        public Dynamic<?> a(int i) {
            if (i >= 0 && i <= 4095) {
                Dynamic<?> dynamic = (Dynamic) this.palette.fromId(this.buffer[i]);

                return dynamic == null ? ChunkConverterPalette.AIR : dynamic;
            } else {
                return ChunkConverterPalette.AIR;
            }
        }

        public void a(int i, Dynamic<?> dynamic) {
            if (this.seen.add(dynamic)) {
                this.listTag.add("%%FILTER_ME%%".equals(ChunkConverterPalette.a(dynamic)) ? ChunkConverterPalette.AIR : dynamic);
            }

            this.buffer[i] = ChunkConverterPalette.a(this.palette, dynamic);
        }

        public int b(int i) {
            if (!this.hasData) {
                return i;
            } else {
                ByteBuffer bytebuffer = (ByteBuffer) this.section.get("Blocks").asByteBufferOpt().result().get();
                ChunkConverterPalette.a chunkconverterpalette_a = (ChunkConverterPalette.a) this.section.get("Data").asByteBufferOpt().map((bytebuffer1) -> {
                    return new ChunkConverterPalette.a(DataFixUtils.toArray(bytebuffer1));
                }).result().orElseGet(ChunkConverterPalette.a::new);
                ChunkConverterPalette.a chunkconverterpalette_a1 = (ChunkConverterPalette.a) this.section.get("Add").asByteBufferOpt().map((bytebuffer1) -> {
                    return new ChunkConverterPalette.a(DataFixUtils.toArray(bytebuffer1));
                }).result().orElseGet(ChunkConverterPalette.a::new);

                this.seen.add(ChunkConverterPalette.AIR);
                ChunkConverterPalette.a(this.palette, ChunkConverterPalette.AIR);
                this.listTag.add(ChunkConverterPalette.AIR);

                for (int j = 0; j < 4096; ++j) {
                    int k = j & 15;
                    int l = j >> 8 & 15;
                    int i1 = j >> 4 & 15;
                    int j1 = chunkconverterpalette_a1.a(k, l, i1) << 12 | (bytebuffer.get(j) & 255) << 4 | chunkconverterpalette_a.a(k, l, i1);

                    if (ChunkConverterPalette.FIX.get(j1 >> 4)) {
                        this.a(j1 >> 4, j);
                    }

                    if (ChunkConverterPalette.VIRTUAL.get(j1 >> 4)) {
                        int k1 = ChunkConverterPalette.a(k == 0, k == 15, i1 == 0, i1 == 15);

                        if (k1 == 0) {
                            this.update.add(j);
                        } else {
                            i |= k1;
                        }
                    }

                    this.a(j, DataConverterFlattenData.b(j1));
                }

                return i;
            }
        }

        private void a(int i, int j) {
            Object object = (IntList) this.toFix.get(i);

            if (object == null) {
                object = new IntArrayList();
                this.toFix.put(i, object);
            }

            ((IntList) object).add(j);
        }

        public Dynamic<?> a() {
            Dynamic<?> dynamic = this.section;

            if (!this.hasData) {
                return dynamic;
            } else {
                dynamic = dynamic.set("Palette", dynamic.createList(this.listTag.stream()));
                int i = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
                DataBitsPacked databitspacked = new DataBitsPacked(i, 4096);

                for (int j = 0; j < this.buffer.length; ++j) {
                    databitspacked.a(j, this.buffer[j]);
                }

                dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(databitspacked.a())));
                dynamic = dynamic.remove("Blocks");
                dynamic = dynamic.remove("Data");
                dynamic = dynamic.remove("Add");
                return dynamic;
            }
        }
    }
}
