package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettingsFlat extends GeneratorSettingsDefault {

    private static final Logger w = LogManager.getLogger();
    private static final WorldGenFeatureComposite<WorldGenMineshaftConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> x = BiomeBase.a(WorldGenerator.f, new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureVillageConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> y = BiomeBase.a(WorldGenerator.e, new WorldGenFeatureVillageConfiguration(0, WorldGenVillagePieces.Material.OAK), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureStrongholdConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> z = BiomeBase.a(WorldGenerator.m, new WorldGenFeatureStrongholdConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureSwampHutConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> A = BiomeBase.a(WorldGenerator.l, new WorldGenFeatureSwampHutConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureDesertPyramidConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> B = BiomeBase.a(WorldGenerator.i, new WorldGenFeatureDesertPyramidConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureJunglePyramidConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> C = BiomeBase.a(WorldGenerator.h, new WorldGenFeatureJunglePyramidConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureIglooConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> D = BiomeBase.a(WorldGenerator.j, new WorldGenFeatureIglooConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureShipwreckConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> E = BiomeBase.a(WorldGenerator.k, new WorldGenFeatureShipwreckConfiguration(false), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenMonumentConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> F = BiomeBase.a(WorldGenerator.n, new WorldGenMonumentConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureLakeConfiguration, WorldGenDecoratorLakeChanceConfiguration> G = BiomeBase.a(WorldGenerator.am, new WorldGenFeatureLakeConfiguration(Blocks.WATER), BiomeBase.K, new WorldGenDecoratorLakeChanceConfiguration(4));
    private static final WorldGenFeatureComposite<WorldGenFeatureLakeConfiguration, WorldGenDecoratorLakeChanceConfiguration> H = BiomeBase.a(WorldGenerator.am, new WorldGenFeatureLakeConfiguration(Blocks.LAVA), BiomeBase.J, new WorldGenDecoratorLakeChanceConfiguration(80));
    private static final WorldGenFeatureComposite<WorldGenEndCityConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> I = BiomeBase.a(WorldGenerator.q, new WorldGenEndCityConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenMansionConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> J = BiomeBase.a(WorldGenerator.g, new WorldGenMansionConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenNetherConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> K = BiomeBase.a(WorldGenerator.p, new WorldGenNetherConfiguration(), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    private static final WorldGenFeatureComposite<WorldGenFeatureOceanRuinConfiguration, WorldGenFeatureDecoratorEmptyConfiguration> L = BiomeBase.a(WorldGenerator.o, new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.1F), BiomeBase.n, WorldGenFeatureDecoratorConfiguration.e);
    public static final Map<WorldGenFeatureComposite<?, ?>, WorldGenStage.Decoration> t = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(GeneratorSettingsFlat.x, WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.y, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.z, WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.A, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.B, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.C, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.D, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.E, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.L, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.G, WorldGenStage.Decoration.LOCAL_MODIFICATIONS);
        hashmap.put(GeneratorSettingsFlat.H, WorldGenStage.Decoration.LOCAL_MODIFICATIONS);
        hashmap.put(GeneratorSettingsFlat.I, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.J, WorldGenStage.Decoration.SURFACE_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.K, WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
        hashmap.put(GeneratorSettingsFlat.F, WorldGenStage.Decoration.SURFACE_STRUCTURES);
    });
    public static final Map<String, WorldGenFeatureComposite<?, ?>[]> u = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put("mineshaft", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.x});
        hashmap.put("village", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.y});
        hashmap.put("stronghold", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.z});
        hashmap.put("biome_1", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.A, GeneratorSettingsFlat.B, GeneratorSettingsFlat.C, GeneratorSettingsFlat.D, GeneratorSettingsFlat.L, GeneratorSettingsFlat.E});
        hashmap.put("oceanmonument", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.F});
        hashmap.put("lake", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.G});
        hashmap.put("lava_lake", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.H});
        hashmap.put("endcity", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.I});
        hashmap.put("mansion", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.J});
        hashmap.put("fortress", new WorldGenFeatureComposite[] { GeneratorSettingsFlat.K});
    });
    public static final Map<WorldGenFeatureComposite<?, ?>, WorldGenFeatureConfiguration> v = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(GeneratorSettingsFlat.x, new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL));
        hashmap.put(GeneratorSettingsFlat.y, new WorldGenFeatureVillageConfiguration(0, WorldGenVillagePieces.Material.OAK));
        hashmap.put(GeneratorSettingsFlat.z, new WorldGenFeatureStrongholdConfiguration());
        hashmap.put(GeneratorSettingsFlat.A, new WorldGenFeatureSwampHutConfiguration());
        hashmap.put(GeneratorSettingsFlat.B, new WorldGenFeatureDesertPyramidConfiguration());
        hashmap.put(GeneratorSettingsFlat.C, new WorldGenFeatureJunglePyramidConfiguration());
        hashmap.put(GeneratorSettingsFlat.D, new WorldGenFeatureIglooConfiguration());
        hashmap.put(GeneratorSettingsFlat.L, new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.9F));
        hashmap.put(GeneratorSettingsFlat.E, new WorldGenFeatureShipwreckConfiguration(false));
        hashmap.put(GeneratorSettingsFlat.F, new WorldGenMonumentConfiguration());
        hashmap.put(GeneratorSettingsFlat.I, new WorldGenEndCityConfiguration());
        hashmap.put(GeneratorSettingsFlat.J, new WorldGenMansionConfiguration());
        hashmap.put(GeneratorSettingsFlat.K, new WorldGenNetherConfiguration());
    });
    private final List<WorldGenFlatLayerInfo> M = Lists.newArrayList();
    private final Map<String, Map<String, String>> N = Maps.newHashMap();
    private BiomeBase O;
    private final IBlockData[] P = new IBlockData[256];
    private boolean Q;
    private int R;

    public GeneratorSettingsFlat() {}

    @Nullable
    public static Block a(String s) {
        try {
            MinecraftKey minecraftkey = new MinecraftKey(s);

            if (IRegistry.BLOCK.c(minecraftkey)) {
                return (Block) IRegistry.BLOCK.getOrDefault(minecraftkey);
            }
        } catch (IllegalArgumentException illegalargumentexception) {
            GeneratorSettingsFlat.w.warn("Invalid blockstate: {}", s, illegalargumentexception);
        }

        return null;
    }

    public BiomeBase t() {
        return this.O;
    }

    public void a(BiomeBase biomebase) {
        this.O = biomebase;
    }

    public Map<String, Map<String, String>> u() {
        return this.N;
    }

    public List<WorldGenFlatLayerInfo> v() {
        return this.M;
    }

    public void w() {
        int i = 0;

        WorldGenFlatLayerInfo worldgenflatlayerinfo;
        Iterator iterator;

        for (iterator = this.M.iterator(); iterator.hasNext(); i += worldgenflatlayerinfo.a()) {
            worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();
            worldgenflatlayerinfo.a(i);
        }

        this.R = 0;
        this.Q = true;
        i = 0;
        iterator = this.M.iterator();

        while (iterator.hasNext()) {
            worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            for (int j = worldgenflatlayerinfo.c(); j < worldgenflatlayerinfo.c() + worldgenflatlayerinfo.a(); ++j) {
                IBlockData iblockdata = worldgenflatlayerinfo.b();

                if (iblockdata.getBlock() != Blocks.AIR) {
                    this.Q = false;
                    this.P[j] = iblockdata;
                }
            }

            if (worldgenflatlayerinfo.b().getBlock() == Blocks.AIR) {
                i += worldgenflatlayerinfo.a();
            } else {
                this.R += worldgenflatlayerinfo.a() + i;
                i = 0;
            }
        }

    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        int i;

        for (i = 0; i < this.M.size(); ++i) {
            if (i > 0) {
                stringbuilder.append(",");
            }

            stringbuilder.append(this.M.get(i));
        }

        stringbuilder.append(";");
        stringbuilder.append(IRegistry.BIOME.getKey(this.O));
        stringbuilder.append(";");
        if (!this.N.isEmpty()) {
            i = 0;
            Iterator iterator = this.N.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Map<String, String>> entry = (Entry) iterator.next();

                if (i++ > 0) {
                    stringbuilder.append(",");
                }

                stringbuilder.append(((String) entry.getKey()).toLowerCase(Locale.ROOT));
                Map<String, String> map = (Map) entry.getValue();

                if (!map.isEmpty()) {
                    stringbuilder.append("(");
                    int j = 0;
                    Iterator iterator1 = map.entrySet().iterator();

                    while (iterator1.hasNext()) {
                        Entry<String, String> entry1 = (Entry) iterator1.next();

                        if (j++ > 0) {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append((String) entry1.getKey());
                        stringbuilder.append("=");
                        stringbuilder.append((String) entry1.getValue());
                    }

                    stringbuilder.append(")");
                }
            }
        }

        return stringbuilder.toString();
    }

    public static GeneratorSettingsFlat a(Dynamic<?> dynamic) {
        GeneratorSettingsFlat generatorsettingsflat = (GeneratorSettingsFlat) ChunkGeneratorType.e.b();
        List<Pair<Integer, Block>> list = (List) ((Stream) dynamic.get("layers").flatMap(Dynamic::getStream).orElse(Stream.empty())).map((dynamic1) -> {
            return Pair.of(dynamic1.getInt("height", 1), a(dynamic1.getString("block")));
        }).collect(Collectors.toList());

        if (list.stream().anyMatch((pair) -> {
            return pair.getSecond() == null;
        })) {
            return x();
        } else {
            List<WorldGenFlatLayerInfo> list1 = (List) list.stream().map((pair) -> {
                return new WorldGenFlatLayerInfo((Integer) pair.getFirst(), (Block) pair.getSecond());
            }).collect(Collectors.toList());

            if (list1.isEmpty()) {
                return x();
            } else {
                generatorsettingsflat.v().addAll(list1);
                generatorsettingsflat.w();
                generatorsettingsflat.a((BiomeBase) IRegistry.BIOME.get(new MinecraftKey(dynamic.getString("biome"))));
                dynamic.get("structures").flatMap(Dynamic::getMapValues).ifPresent((map) -> {
                    map.keySet().forEach((dynamic1) -> {
                        dynamic1.getStringValue().map((s) -> {
                            return (Map) generatorsettingsflat.u().put(s, Maps.newHashMap());
                        });
                    });
                });
                return generatorsettingsflat;
            }
        }
    }

    public static GeneratorSettingsFlat x() {
        GeneratorSettingsFlat generatorsettingsflat = (GeneratorSettingsFlat) ChunkGeneratorType.e.b();

        generatorsettingsflat.a(Biomes.PLAINS);
        generatorsettingsflat.v().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.v().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.v().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.w();
        generatorsettingsflat.u().put("village", Maps.newHashMap());
        return generatorsettingsflat;
    }

    public boolean y() {
        return this.Q;
    }

    public IBlockData[] A() {
        return this.P;
    }
}
