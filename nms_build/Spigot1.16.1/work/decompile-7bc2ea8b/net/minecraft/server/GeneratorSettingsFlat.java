package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettingsFlat {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<GeneratorSettingsFlat> a = RecordCodecBuilder.create((instance) -> {
        RecordCodecBuilder recordcodecbuilder = StructureSettings.a.fieldOf("structures").forGetter(GeneratorSettingsFlat::d);
        RecordCodecBuilder recordcodecbuilder1 = WorldGenFlatLayerInfo.a.listOf().fieldOf("layers").forGetter(GeneratorSettingsFlat::f);
        RecordCodecBuilder recordcodecbuilder2 = Codec.BOOL.fieldOf("lakes").withDefault(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.l;
        });
        RecordCodecBuilder recordcodecbuilder3 = Codec.BOOL.fieldOf("features").withDefault(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.k;
        });
        MapCodec mapcodec = IRegistry.BIOME.fieldOf("biome");
        Logger logger = GeneratorSettingsFlat.LOGGER;

        logger.getClass();
        return instance.group(recordcodecbuilder, recordcodecbuilder1, recordcodecbuilder2, recordcodecbuilder3, Codecs.a(mapcodec, SystemUtils.a("Unknown biome, defaulting to plains", logger::error), () -> {
            return Biomes.PLAINS;
        }).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.h;
        })).apply(instance, GeneratorSettingsFlat::new);
    }).stable();
    private static final WorldGenFeatureConfigured<?, ?> c = WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(Blocks.WATER.getBlockData()))).a(WorldGenDecorator.E.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(4))));
    private static final WorldGenFeatureConfigured<?, ?> d = WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(Blocks.LAVA.getBlockData()))).a(WorldGenDecorator.D.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(80))));
    private static final Map<StructureGenerator<?>, StructureFeature<?, ?>> e = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(StructureGenerator.MINESHAFT, BiomeDecoratorGroups.b);
        hashmap.put(StructureGenerator.VILLAGE, BiomeDecoratorGroups.t);
        hashmap.put(StructureGenerator.STRONGHOLD, BiomeDecoratorGroups.k);
        hashmap.put(StructureGenerator.SWAMP_HUT, BiomeDecoratorGroups.j);
        hashmap.put(StructureGenerator.DESERT_PYRAMID, BiomeDecoratorGroups.f);
        hashmap.put(StructureGenerator.JUNGLE_PYRAMID, BiomeDecoratorGroups.e);
        hashmap.put(StructureGenerator.IGLOO, BiomeDecoratorGroups.g);
        hashmap.put(StructureGenerator.OCEAN_RUIN, BiomeDecoratorGroups.m);
        hashmap.put(StructureGenerator.SHIPWRECK, BiomeDecoratorGroups.h);
        hashmap.put(StructureGenerator.MONUMENT, BiomeDecoratorGroups.l);
        hashmap.put(StructureGenerator.ENDCITY, BiomeDecoratorGroups.q);
        hashmap.put(StructureGenerator.MANSION, BiomeDecoratorGroups.d);
        hashmap.put(StructureGenerator.FORTRESS, BiomeDecoratorGroups.o);
        hashmap.put(StructureGenerator.PILLAGER_OUTPOST, BiomeDecoratorGroups.a);
        hashmap.put(StructureGenerator.RUINED_PORTAL, BiomeDecoratorGroups.y);
        hashmap.put(StructureGenerator.BASTION_REMNANT, BiomeDecoratorGroups.s);
    });
    private final StructureSettings f;
    private final List<WorldGenFlatLayerInfo> g;
    private BiomeBase h;
    private final IBlockData[] i;
    private boolean j;
    private boolean k;
    private boolean l;

    public GeneratorSettingsFlat(StructureSettings structuresettings, List<WorldGenFlatLayerInfo> list, boolean flag, boolean flag1, BiomeBase biomebase) {
        this(structuresettings);
        if (flag) {
            this.b();
        }

        if (flag1) {
            this.a();
        }

        this.g.addAll(list);
        this.h();
        this.h = biomebase;
    }

    public GeneratorSettingsFlat(StructureSettings structuresettings) {
        this.g = Lists.newArrayList();
        this.i = new IBlockData[256];
        this.k = false;
        this.l = false;
        this.f = structuresettings;
    }

    public void a() {
        this.k = true;
    }

    public void b() {
        this.l = true;
    }

    public BiomeBase c() {
        BiomeBase biomebase = this.e();
        BiomeBase biomebase1 = new BiomeBase((new BiomeBase.a()).a(biomebase.z()).a(biomebase.d()).a(biomebase.y()).a(biomebase.k()).b(biomebase.o()).c(biomebase.getTemperature()).d(biomebase.getHumidity()).a(biomebase.q()).a(biomebase.C())) {
        };

        if (this.l) {
            biomebase1.a(WorldGenStage.Decoration.LAKES, GeneratorSettingsFlat.c);
            biomebase1.a(WorldGenStage.Decoration.LAKES, GeneratorSettingsFlat.d);
        }

        Iterator iterator = this.f.a().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureGenerator<?>, StructureSettingsFeature> entry = (Entry) iterator.next();

            biomebase1.a(biomebase.b((StructureFeature) GeneratorSettingsFlat.e.get(entry.getKey())));
        }

        boolean flag = (!this.j || biomebase == Biomes.THE_VOID) && this.k;

        if (flag) {
            List<WorldGenStage.Decoration> list = Lists.newArrayList();

            list.add(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
            list.add(WorldGenStage.Decoration.SURFACE_STRUCTURES);
            WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
            int i = aworldgenstage_decoration.length;

            for (int j = 0; j < i; ++j) {
                WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[j];

                if (!list.contains(worldgenstage_decoration)) {
                    Iterator iterator1 = biomebase.a(worldgenstage_decoration).iterator();

                    while (iterator1.hasNext()) {
                        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) iterator1.next();

                        biomebase1.a(worldgenstage_decoration, worldgenfeatureconfigured);
                    }
                }
            }
        }

        IBlockData[] aiblockdata = this.g();

        for (int k = 0; k < aiblockdata.length; ++k) {
            IBlockData iblockdata = aiblockdata[k];

            if (iblockdata != null && !HeightMap.Type.MOTION_BLOCKING.e().test(iblockdata)) {
                this.i[k] = null;
                biomebase1.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.FILL_LAYER.b((WorldGenFeatureConfiguration) (new WorldGenFeatureFillConfiguration(k, iblockdata))));
            }
        }

        return biomebase1;
    }

    public StructureSettings d() {
        return this.f;
    }

    public BiomeBase e() {
        return this.h;
    }

    public void a(BiomeBase biomebase) {
        this.h = biomebase;
    }

    public List<WorldGenFlatLayerInfo> f() {
        return this.g;
    }

    public IBlockData[] g() {
        return this.i;
    }

    public void h() {
        Arrays.fill(this.i, 0, this.i.length, (Object) null);
        int i = 0;

        WorldGenFlatLayerInfo worldgenflatlayerinfo;

        for (Iterator iterator = this.g.iterator(); iterator.hasNext(); i += worldgenflatlayerinfo.a()) {
            worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();
            worldgenflatlayerinfo.a(i);
        }

        this.j = true;
        Iterator iterator1 = this.g.iterator();

        while (iterator1.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo1 = (WorldGenFlatLayerInfo) iterator1.next();

            for (int j = worldgenflatlayerinfo1.c(); j < worldgenflatlayerinfo1.c() + worldgenflatlayerinfo1.a(); ++j) {
                IBlockData iblockdata = worldgenflatlayerinfo1.b();

                if (!iblockdata.a(Blocks.AIR)) {
                    this.j = false;
                    this.i[j] = iblockdata;
                }
            }
        }

    }

    public static GeneratorSettingsFlat i() {
        StructureSettings structuresettings = new StructureSettings(Optional.of(StructureSettings.c), Maps.newHashMap(ImmutableMap.of(StructureGenerator.VILLAGE, StructureSettings.b.get(StructureGenerator.VILLAGE))));
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(structuresettings);

        generatorsettingsflat.a(Biomes.PLAINS);
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.h();
        return generatorsettingsflat;
    }
}
