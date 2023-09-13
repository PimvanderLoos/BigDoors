package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.SystemUtils;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.AreaLazy;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;
import net.minecraft.world.level.newbiome.context.WorldGenContextArea;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;

public class GenLayers {

    private static final Int2IntMap a = (Int2IntMap) SystemUtils.a((Object) (new Int2IntOpenHashMap()), (int2intopenhashmap) -> {
        a(int2intopenhashmap, GenLayers.Type.BEACH, 16);
        a(int2intopenhashmap, GenLayers.Type.BEACH, 26);
        a(int2intopenhashmap, GenLayers.Type.DESERT, 2);
        a(int2intopenhashmap, GenLayers.Type.DESERT, 17);
        a(int2intopenhashmap, GenLayers.Type.DESERT, 130);
        a(int2intopenhashmap, GenLayers.Type.EXTREME_HILLS, 131);
        a(int2intopenhashmap, GenLayers.Type.EXTREME_HILLS, 162);
        a(int2intopenhashmap, GenLayers.Type.EXTREME_HILLS, 20);
        a(int2intopenhashmap, GenLayers.Type.EXTREME_HILLS, 3);
        a(int2intopenhashmap, GenLayers.Type.EXTREME_HILLS, 34);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 27);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 28);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 29);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 157);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 132);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 4);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 155);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 156);
        a(int2intopenhashmap, GenLayers.Type.FOREST, 18);
        a(int2intopenhashmap, GenLayers.Type.ICY, 140);
        a(int2intopenhashmap, GenLayers.Type.ICY, 13);
        a(int2intopenhashmap, GenLayers.Type.ICY, 12);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 168);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 169);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 21);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 23);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 22);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 149);
        a(int2intopenhashmap, GenLayers.Type.JUNGLE, 151);
        a(int2intopenhashmap, GenLayers.Type.MESA, 37);
        a(int2intopenhashmap, GenLayers.Type.MESA, 165);
        a(int2intopenhashmap, GenLayers.Type.MESA, 167);
        a(int2intopenhashmap, GenLayers.Type.MESA, 166);
        a(int2intopenhashmap, GenLayers.Type.BADLANDS_PLATEAU, 39);
        a(int2intopenhashmap, GenLayers.Type.BADLANDS_PLATEAU, 38);
        a(int2intopenhashmap, GenLayers.Type.MUSHROOM, 14);
        a(int2intopenhashmap, GenLayers.Type.MUSHROOM, 15);
        a(int2intopenhashmap, GenLayers.Type.NONE, 25);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 46);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 49);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 50);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 48);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 24);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 47);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 10);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 45);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 0);
        a(int2intopenhashmap, GenLayers.Type.OCEAN, 44);
        a(int2intopenhashmap, GenLayers.Type.PLAINS, 1);
        a(int2intopenhashmap, GenLayers.Type.PLAINS, 129);
        a(int2intopenhashmap, GenLayers.Type.RIVER, 11);
        a(int2intopenhashmap, GenLayers.Type.RIVER, 7);
        a(int2intopenhashmap, GenLayers.Type.SAVANNA, 35);
        a(int2intopenhashmap, GenLayers.Type.SAVANNA, 36);
        a(int2intopenhashmap, GenLayers.Type.SAVANNA, 163);
        a(int2intopenhashmap, GenLayers.Type.SAVANNA, 164);
        a(int2intopenhashmap, GenLayers.Type.SWAMP, 6);
        a(int2intopenhashmap, GenLayers.Type.SWAMP, 134);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 160);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 161);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 32);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 33);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 30);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 31);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 158);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 5);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 19);
        a(int2intopenhashmap, GenLayers.Type.TAIGA, 133);
    });

    private static <T extends Area, C extends AreaContextTransformed<T>> AreaFactory<T> a(long i, AreaTransformer2 areatransformer2, AreaFactory<T> areafactory, int j, LongFunction<C> longfunction) {
        AreaFactory<T> areafactory1 = areafactory;

        for (int k = 0; k < j; ++k) {
            areafactory1 = areatransformer2.a((AreaContextTransformed) longfunction.apply(i + (long) k), areafactory1);
        }

        return areafactory1;
    }

    private static <T extends Area, C extends AreaContextTransformed<T>> AreaFactory<T> a(boolean flag, int i, int j, LongFunction<C> longfunction) {
        AreaFactory<T> areafactory = LayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(1L));

        areafactory = GenLayerZoom.FUZZY.a((AreaContextTransformed) longfunction.apply(2000L), areafactory);
        areafactory = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(1L), areafactory);
        areafactory = GenLayerZoom.NORMAL.a((AreaContextTransformed) longfunction.apply(2001L), areafactory);
        areafactory = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(2L), areafactory);
        areafactory = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(50L), areafactory);
        areafactory = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(70L), areafactory);
        areafactory = GenLayerIcePlains.INSTANCE.a((AreaContextTransformed) longfunction.apply(2L), areafactory);
        AreaFactory<T> areafactory1 = GenLayerOceanEdge.INSTANCE.a((AreaContextTransformed) longfunction.apply(2L));

        areafactory1 = a(2001L, GenLayerZoom.NORMAL, areafactory1, 6, longfunction);
        areafactory = GenLayerTopSoil.INSTANCE.a((AreaContextTransformed) longfunction.apply(2L), areafactory);
        areafactory = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(3L), areafactory);
        areafactory = GenLayerSpecial.Special1.INSTANCE.a((AreaContextTransformed) longfunction.apply(2L), areafactory);
        areafactory = GenLayerSpecial.Special2.INSTANCE.a((AreaContextTransformed) longfunction.apply(2L), areafactory);
        areafactory = GenLayerSpecial.Special3.INSTANCE.a((AreaContextTransformed) longfunction.apply(3L), areafactory);
        areafactory = GenLayerZoom.NORMAL.a((AreaContextTransformed) longfunction.apply(2002L), areafactory);
        areafactory = GenLayerZoom.NORMAL.a((AreaContextTransformed) longfunction.apply(2003L), areafactory);
        areafactory = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(4L), areafactory);
        areafactory = GenLayerMushroomIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(5L), areafactory);
        areafactory = GenLayerDeepOcean.INSTANCE.a((AreaContextTransformed) longfunction.apply(4L), areafactory);
        areafactory = a(1000L, GenLayerZoom.NORMAL, areafactory, 0, longfunction);
        AreaFactory<T> areafactory2 = a(1000L, GenLayerZoom.NORMAL, areafactory, 0, longfunction);

        areafactory2 = GenLayerCleaner.INSTANCE.a((AreaContextTransformed) longfunction.apply(100L), areafactory2);
        AreaFactory<T> areafactory3 = (new GenLayerBiome(flag)).a((AreaContextTransformed) longfunction.apply(200L), areafactory);

        areafactory3 = GenLayerJungle.INSTANCE.a((AreaContextTransformed) longfunction.apply(1001L), areafactory3);
        areafactory3 = a(1000L, GenLayerZoom.NORMAL, areafactory3, 2, longfunction);
        areafactory3 = GenLayerDesert.INSTANCE.a((AreaContextTransformed) longfunction.apply(1000L), areafactory3);
        AreaFactory<T> areafactory4 = a(1000L, GenLayerZoom.NORMAL, areafactory2, 2, longfunction);

        areafactory3 = GenLayerRegionHills.INSTANCE.a((AreaContextTransformed) longfunction.apply(1000L), areafactory3, areafactory4);
        areafactory2 = a(1000L, GenLayerZoom.NORMAL, areafactory2, 2, longfunction);
        areafactory2 = a(1000L, GenLayerZoom.NORMAL, areafactory2, j, longfunction);
        areafactory2 = GenLayerRiver.INSTANCE.a((AreaContextTransformed) longfunction.apply(1L), areafactory2);
        areafactory2 = GenLayerSmooth.INSTANCE.a((AreaContextTransformed) longfunction.apply(1000L), areafactory2);
        areafactory3 = GenLayerPlains.INSTANCE.a((AreaContextTransformed) longfunction.apply(1001L), areafactory3);

        for (int k = 0; k < i; ++k) {
            areafactory3 = GenLayerZoom.NORMAL.a((AreaContextTransformed) longfunction.apply((long) (1000 + k)), areafactory3);
            if (k == 0) {
                areafactory3 = GenLayerIsland.INSTANCE.a((AreaContextTransformed) longfunction.apply(3L), areafactory3);
            }

            if (k == 1 || i == 1) {
                areafactory3 = GenLayerMushroomShore.INSTANCE.a((AreaContextTransformed) longfunction.apply(1000L), areafactory3);
            }
        }

        areafactory3 = GenLayerSmooth.INSTANCE.a((AreaContextTransformed) longfunction.apply(1000L), areafactory3);
        areafactory3 = GenLayerRiverMix.INSTANCE.a((AreaContextTransformed) longfunction.apply(100L), areafactory3, areafactory2);
        areafactory3 = GenLayerOcean.INSTANCE.a((AreaContextTransformed) longfunction.apply(100L), areafactory3, areafactory1);
        return areafactory3;
    }

    public static GenLayer a(long i, boolean flag, int j, int k) {
        boolean flag1 = true;
        AreaFactory<AreaLazy> areafactory = a(flag, j, k, (l) -> {
            return new WorldGenContextArea(25, i, l);
        });

        return new GenLayer(areafactory);
    }

    public static boolean a(int i, int j) {
        return i == j ? true : GenLayers.a.get(i) == GenLayers.a.get(j);
    }

    private static void a(Int2IntOpenHashMap int2intopenhashmap, GenLayers.Type genlayers_type, int i) {
        int2intopenhashmap.put(i, genlayers_type.ordinal());
    }

    protected static boolean a(int i) {
        return i == 44 || i == 45 || i == 0 || i == 46 || i == 10 || i == 47 || i == 48 || i == 24 || i == 49 || i == 50;
    }

    protected static boolean b(int i) {
        return i == 44 || i == 45 || i == 0 || i == 46 || i == 10;
    }

    static enum Type {

        NONE, TAIGA, EXTREME_HILLS, JUNGLE, MESA, BADLANDS_PLATEAU, PLAINS, SAVANNA, ICY, BEACH, FOREST, OCEAN, DESERT, RIVER, SWAMP, MUSHROOM;

        private Type() {}
    }
}
