package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
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
import net.minecraft.world.level.dimension.DimensionManager;
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
    public static final Codec<GeneratorSettingsFlat> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryLookupCodec.a(IRegistry.BIOME_REGISTRY).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.biomes;
        }), StructureSettings.CODEC.fieldOf("structures").forGetter(GeneratorSettingsFlat::d), WorldGenFlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(GeneratorSettingsFlat::f), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.addLakes;
        }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.decoration;
        }), BiomeBase.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((generatorsettingsflat) -> {
            return Optional.of(generatorsettingsflat.biome);
        })).apply(instance, GeneratorSettingsFlat::new);
    }).comapFlatMap(GeneratorSettingsFlat::a, Function.identity()).stable();
    private static final Map<StructureGenerator<?>, StructureFeature<?, ?>> STRUCTURE_FEATURES = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(StructureGenerator.MINESHAFT, StructureFeatures.MINESHAFT);
        hashmap.put(StructureGenerator.VILLAGE, StructureFeatures.VILLAGE_PLAINS);
        hashmap.put(StructureGenerator.STRONGHOLD, StructureFeatures.STRONGHOLD);
        hashmap.put(StructureGenerator.SWAMP_HUT, StructureFeatures.SWAMP_HUT);
        hashmap.put(StructureGenerator.DESERT_PYRAMID, StructureFeatures.DESERT_PYRAMID);
        hashmap.put(StructureGenerator.JUNGLE_TEMPLE, StructureFeatures.JUNGLE_TEMPLE);
        hashmap.put(StructureGenerator.IGLOO, StructureFeatures.IGLOO);
        hashmap.put(StructureGenerator.OCEAN_RUIN, StructureFeatures.OCEAN_RUIN_COLD);
        hashmap.put(StructureGenerator.SHIPWRECK, StructureFeatures.SHIPWRECK);
        hashmap.put(StructureGenerator.OCEAN_MONUMENT, StructureFeatures.OCEAN_MONUMENT);
        hashmap.put(StructureGenerator.END_CITY, StructureFeatures.END_CITY);
        hashmap.put(StructureGenerator.WOODLAND_MANSION, StructureFeatures.WOODLAND_MANSION);
        hashmap.put(StructureGenerator.NETHER_BRIDGE, StructureFeatures.NETHER_BRIDGE);
        hashmap.put(StructureGenerator.PILLAGER_OUTPOST, StructureFeatures.PILLAGER_OUTPOST);
        hashmap.put(StructureGenerator.RUINED_PORTAL, StructureFeatures.RUINED_PORTAL_STANDARD);
        hashmap.put(StructureGenerator.BASTION_REMNANT, StructureFeatures.BASTION_REMNANT);
    });
    private final IRegistry<BiomeBase> biomes;
    private final StructureSettings structureSettings;
    private final List<WorldGenFlatLayerInfo> layersInfo;
    private Supplier<BiomeBase> biome;
    private final List<IBlockData> layers;
    private boolean voidGen;
    private boolean decoration;
    private boolean addLakes;

    private static DataResult<GeneratorSettingsFlat> a(GeneratorSettingsFlat generatorsettingsflat) {
        int i = generatorsettingsflat.layersInfo.stream().mapToInt(WorldGenFlatLayerInfo::a).sum();

        return i > DimensionManager.Y_SIZE ? DataResult.error("Sum of layer heights is > " + DimensionManager.Y_SIZE, generatorsettingsflat) : DataResult.success(generatorsettingsflat);
    }

    private GeneratorSettingsFlat(IRegistry<BiomeBase> iregistry, StructureSettings structuresettings, List<WorldGenFlatLayerInfo> list, boolean flag, boolean flag1, Optional<Supplier<BiomeBase>> optional) {
        this(structuresettings, iregistry);
        if (flag) {
            this.b();
        }

        if (flag1) {
            this.a();
        }

        this.layersInfo.addAll(list);
        this.h();
        if (!optional.isPresent()) {
            GeneratorSettingsFlat.LOGGER.error("Unknown biome, defaulting to plains");
            this.biome = () -> {
                return (BiomeBase) iregistry.d(Biomes.PLAINS);
            };
        } else {
            this.biome = (Supplier) optional.get();
        }

    }

    public GeneratorSettingsFlat(StructureSettings structuresettings, IRegistry<BiomeBase> iregistry) {
        this.layersInfo = Lists.newArrayList();
        this.biomes = iregistry;
        this.structureSettings = structuresettings;
        this.biome = () -> {
            return (BiomeBase) iregistry.d(Biomes.PLAINS);
        };
        this.layers = Lists.newArrayList();
    }

    public GeneratorSettingsFlat a(StructureSettings structuresettings) {
        return this.a(this.layersInfo, structuresettings);
    }

    public GeneratorSettingsFlat a(List<WorldGenFlatLayerInfo> list, StructureSettings structuresettings) {
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(structuresettings, this.biomes);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            generatorsettingsflat.layersInfo.add(new WorldGenFlatLayerInfo(worldgenflatlayerinfo.a(), worldgenflatlayerinfo.b().getBlock()));
            generatorsettingsflat.h();
        }

        generatorsettingsflat.a(this.biome);
        if (this.decoration) {
            generatorsettingsflat.a();
        }

        if (this.addLakes) {
            generatorsettingsflat.b();
        }

        return generatorsettingsflat;
    }

    public void a() {
        this.decoration = true;
    }

    public void b() {
        this.addLakes = true;
    }

    public BiomeBase c() {
        BiomeBase biomebase = this.e();
        BiomeSettingsGeneration biomesettingsgeneration = biomebase.e();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(biomesettingsgeneration.d());

        if (this.addLakes) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_WATER);
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_LAVA);
        }

        Iterator iterator = this.structureSettings.a().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureGenerator<?>, StructureSettingsFeature> entry = (Entry) iterator.next();

            biomesettingsgeneration_a.a(biomesettingsgeneration.a((StructureFeature) GeneratorSettingsFlat.STRUCTURE_FEATURES.get(entry.getKey())));
        }

        boolean flag = (!this.voidGen || this.biomes.c((Object) biomebase).equals(Optional.of(Biomes.THE_VOID))) && this.decoration;
        List list;
        int i;

        if (flag) {
            list = biomesettingsgeneration.c();

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

        list = this.g();

        for (i = 0; i < list.size(); ++i) {
            IBlockData iblockdata = (IBlockData) list.get(i);

            if (!HeightMap.Type.MOTION_BLOCKING.e().test(iblockdata)) {
                list.set(i, (Object) null);
                biomesettingsgeneration_a.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.FILL_LAYER.b((WorldGenFeatureConfiguration) (new WorldGenFeatureFillConfiguration(i, iblockdata))));
            }
        }

        return (new BiomeBase.a()).a(biomebase.c()).a(biomebase.t()).a(biomebase.h()).b(biomebase.j()).c(biomebase.k()).d(biomebase.getHumidity()).a(biomebase.l()).a(biomesettingsgeneration_a.a()).a(biomebase.b()).a();
    }

    public StructureSettings d() {
        return this.structureSettings;
    }

    public BiomeBase e() {
        return (BiomeBase) this.biome.get();
    }

    public void a(Supplier<BiomeBase> supplier) {
        this.biome = supplier;
    }

    public List<WorldGenFlatLayerInfo> f() {
        return this.layersInfo;
    }

    public List<IBlockData> g() {
        return this.layers;
    }

    public void h() {
        this.layers.clear();
        Iterator iterator = this.layersInfo.iterator();

        while (iterator.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            for (int i = 0; i < worldgenflatlayerinfo.a(); ++i) {
                this.layers.add(worldgenflatlayerinfo.b());
            }
        }

        this.voidGen = this.layers.stream().allMatch((iblockdata) -> {
            return iblockdata.a(Blocks.AIR);
        });
    }

    public static GeneratorSettingsFlat a(IRegistry<BiomeBase> iregistry) {
        StructureSettings structuresettings = new StructureSettings(Optional.of(StructureSettings.DEFAULT_STRONGHOLD), Maps.newHashMap(ImmutableMap.of(StructureGenerator.VILLAGE, (StructureSettingsFeature) StructureSettings.DEFAULTS.get(StructureGenerator.VILLAGE))));
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(structuresettings, iregistry);

        generatorsettingsflat.biome = () -> {
            return (BiomeBase) iregistry.d(Biomes.PLAINS);
        };
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.f().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.h();
        return generatorsettingsflat;
    }
}
