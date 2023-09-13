package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureFillConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettingsFlat {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<GeneratorSettingsFlat> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryLookupCodec.a(IRegistry.ay).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.d;
        }), StructureSettings.a.fieldOf("structures").forGetter(GeneratorSettingsFlat::d), WorldGenFlatLayerInfo.a.listOf().fieldOf("layers").forGetter(GeneratorSettingsFlat::f), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.k;
        }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.j;
        }), BiomeBase.d.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((generatorsettingsflat) -> {
            return Optional.of(generatorsettingsflat.g);
        })).apply(instance, GeneratorSettingsFlat::new);
    }).stable();
    private static final Map<StructureGenerator<?>, StructureFeature<?, ?>> c = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(StructureGenerator.MINESHAFT, StructureFeatures.b);
        hashmap.put(StructureGenerator.VILLAGE, StructureFeatures.t);
        hashmap.put(StructureGenerator.STRONGHOLD, StructureFeatures.k);
        hashmap.put(StructureGenerator.SWAMP_HUT, StructureFeatures.j);
        hashmap.put(StructureGenerator.DESERT_PYRAMID, StructureFeatures.f);
        hashmap.put(StructureGenerator.JUNGLE_PYRAMID, StructureFeatures.e);
        hashmap.put(StructureGenerator.IGLOO, StructureFeatures.g);
        hashmap.put(StructureGenerator.OCEAN_RUIN, StructureFeatures.m);
        hashmap.put(StructureGenerator.SHIPWRECK, StructureFeatures.h);
        hashmap.put(StructureGenerator.MONUMENT, StructureFeatures.l);
        hashmap.put(StructureGenerator.ENDCITY, StructureFeatures.q);
        hashmap.put(StructureGenerator.MANSION, StructureFeatures.d);
        hashmap.put(StructureGenerator.FORTRESS, StructureFeatures.o);
        hashmap.put(StructureGenerator.PILLAGER_OUTPOST, StructureFeatures.a);
        hashmap.put(StructureGenerator.RUINED_PORTAL, StructureFeatures.y);
        hashmap.put(StructureGenerator.BASTION_REMNANT, StructureFeatures.s);
    });
    private final IRegistry<BiomeBase> d;
    private final StructureSettings e;
    private final List<WorldGenFlatLayerInfo> f;
    private Supplier<BiomeBase> g;
    private final IBlockData[] h;
    private boolean i;
    private boolean j;
    private boolean k;

    public GeneratorSettingsFlat(IRegistry<BiomeBase> iregistry, StructureSettings structuresettings, List<WorldGenFlatLayerInfo> list, boolean flag, boolean flag1, Optional<Supplier<BiomeBase>> optional) {
        this(structuresettings, iregistry);
        if (flag) {
            this.b();
        }

        if (flag1) {
            this.a();
        }

        this.f.addAll(list);
        this.h();
        if (!optional.isPresent()) {
            GeneratorSettingsFlat.LOGGER.error("Unknown biome, defaulting to plains");
            this.g = () -> {
                return (BiomeBase) iregistry.d(Biomes.PLAINS);
            };
        } else {
            this.g = (Supplier) optional.get();
        }

    }

    public GeneratorSettingsFlat(StructureSettings structuresettings, IRegistry<BiomeBase> iregistry) {
        this.f = Lists.newArrayList();
        this.h = new IBlockData[256];
        this.j = false;
        this.k = false;
        this.d = iregistry;
        this.e = structuresettings;
        this.g = () -> {
            return (BiomeBase) iregistry.d(Biomes.PLAINS);
        };
    }

    public void a() {
        this.j = true;
    }

    public void b() {
        this.k = true;
    }

    public BiomeBase c() {
        BiomeBase biomebase = this.e();
        BiomeSettingsGeneration biomesettingsgeneration = biomebase.e();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(biomesettingsgeneration.d());

        if (this.k) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_WATER);
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_LAVA);
        }

        Iterator iterator = this.e.a().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureGenerator<?>, StructureSettingsFeature> entry = (Entry) iterator.next();

            biomesettingsgeneration_a.a(biomesettingsgeneration.a((StructureFeature) GeneratorSettingsFlat.c.get(entry.getKey())));
        }

        boolean flag = (!this.i || this.d.c(biomebase).equals(Optional.of(Biomes.THE_VOID))) && this.j;
        int i;

        if (flag) {
            List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> list = biomesettingsgeneration.c();

            for (i = 0; i < list.size(); ++i) {
                if (i != WorldGenStage.Decoration.UNDERGROUND_STRUCTURES.ordinal() && i != WorldGenStage.Decoration.SURFACE_STRUCTURES.ordinal()) {
                    List<Supplier<WorldGenFeatureConfigured<?, ?>>> list1 = (List) list.get(i);
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        Supplier<WorldGenFeatureConfigured<?, ?>> supplier = (Supplier) iterator1.next();

                        biomesettingsgeneration_a.a(i, supplier);
                    }
                }
            }
        }

        IBlockData[] aiblockdata = this.g();

        for (i = 0; i < aiblockdata.length; ++i) {
            IBlockData iblockdata = aiblockdata[i];

            if (iblockdata != null && !HeightMap.Type.MOTION_BLOCKING.e().test(iblockdata)) {
                this.h[i] = null;
                biomesettingsgeneration_a.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.FILL_LAYER.b((WorldGenFeatureConfiguration) (new WorldGenFeatureFillConfiguration(i, iblockdata))));
            }
        }

        return (new BiomeBase.a()).a(biomebase.c()).a(biomebase.t()).a(biomebase.h()).b(biomebase.j()).c(biomebase.k()).d(biomebase.getHumidity()).a(biomebase.l()).a(biomesettingsgeneration_a.a()).a(biomebase.b()).a();
    }

    public StructureSettings d() {
        return this.e;
    }

    public BiomeBase e() {
        return (BiomeBase) this.g.get();
    }

    public List<WorldGenFlatLayerInfo> f() {
        return this.f;
    }

    public IBlockData[] g() {
        return this.h;
    }

    public void h() {
        Arrays.fill(this.h, 0, this.h.length, (Object) null);
        int i = 0;

        WorldGenFlatLayerInfo worldgenflatlayerinfo;

        for (Iterator iterator = this.f.iterator(); iterator.hasNext(); i += worldgenflatlayerinfo.a()) {
            worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();
            worldgenflatlayerinfo.a(i);
        }

        this.i = true;
        Iterator iterator1 = this.f.iterator();

        while (iterator1.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo1 = (WorldGenFlatLayerInfo) iterator1.next();

            for (int j = worldgenflatlayerinfo1.c(); j < worldgenflatlayerinfo1.c() + worldgenflatlayerinfo1.a(); ++j) {
                IBlockData iblockdata = worldgenflatlayerinfo1.b();

                if (!iblockdata.a(Blocks.AIR)) {
                    this.i = false;
                    this.h[j] = iblockdata;
                }
            }
        }

    }

    public static GeneratorSettingsFlat a(IRegistry<BiomeBase> iregistry) {
        StructureSettings structuresettings = new StructureSettings(Optional.of(StructureSettings.c), Maps.newHashMap(ImmutableMap.of(StructureGenerator.VILLAGE, StructureSettings.b.get(StructureGenerator.VILLAGE))));
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(structuresettings, iregistry);

        generatorsettingsflat.g = () -> {
            return (BiomeBase) iregistry.d(Biomes.PLAINS);
        };
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.h();
        return generatorsettingsflat;
    }
}
