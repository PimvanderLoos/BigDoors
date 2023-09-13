package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
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
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureFillConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettingsFlat {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<GeneratorSettingsFlat> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryLookupCodec.create(IRegistry.BIOME_REGISTRY).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.biomes;
        }), StructureSettings.CODEC.fieldOf("structures").forGetter(GeneratorSettingsFlat::structureSettings), WorldGenFlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(GeneratorSettingsFlat::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.addLakes;
        }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.decoration;
        }), BiomeBase.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((generatorsettingsflat) -> {
            return Optional.of(generatorsettingsflat.biome);
        })).apply(instance, GeneratorSettingsFlat::new);
    }).comapFlatMap(GeneratorSettingsFlat::validateHeight, Function.identity()).stable();
    private final IRegistry<BiomeBase> biomes;
    private final StructureSettings structureSettings;
    private final List<WorldGenFlatLayerInfo> layersInfo;
    private Supplier<BiomeBase> biome;
    private final List<IBlockData> layers;
    private boolean voidGen;
    private boolean decoration;
    private boolean addLakes;

    private static DataResult<GeneratorSettingsFlat> validateHeight(GeneratorSettingsFlat generatorsettingsflat) {
        int i = generatorsettingsflat.layersInfo.stream().mapToInt(WorldGenFlatLayerInfo::getHeight).sum();

        return i > DimensionManager.Y_SIZE ? DataResult.error("Sum of layer heights is > " + DimensionManager.Y_SIZE, generatorsettingsflat) : DataResult.success(generatorsettingsflat);
    }

    private GeneratorSettingsFlat(IRegistry<BiomeBase> iregistry, StructureSettings structuresettings, List<WorldGenFlatLayerInfo> list, boolean flag, boolean flag1, Optional<Supplier<BiomeBase>> optional) {
        this(structuresettings, iregistry);
        if (flag) {
            this.setAddLakes();
        }

        if (flag1) {
            this.setDecoration();
        }

        this.layersInfo.addAll(list);
        this.updateLayers();
        if (!optional.isPresent()) {
            GeneratorSettingsFlat.LOGGER.error("Unknown biome, defaulting to plains");
            this.biome = () -> {
                return (BiomeBase) iregistry.getOrThrow(Biomes.PLAINS);
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
            return (BiomeBase) iregistry.getOrThrow(Biomes.PLAINS);
        };
        this.layers = Lists.newArrayList();
    }

    public GeneratorSettingsFlat withStructureSettings(StructureSettings structuresettings) {
        return this.withLayers(this.layersInfo, structuresettings);
    }

    public GeneratorSettingsFlat withLayers(List<WorldGenFlatLayerInfo> list, StructureSettings structuresettings) {
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(structuresettings, this.biomes);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            generatorsettingsflat.layersInfo.add(new WorldGenFlatLayerInfo(worldgenflatlayerinfo.getHeight(), worldgenflatlayerinfo.getBlockState().getBlock()));
            generatorsettingsflat.updateLayers();
        }

        generatorsettingsflat.setBiome(this.biome);
        if (this.decoration) {
            generatorsettingsflat.setDecoration();
        }

        if (this.addLakes) {
            generatorsettingsflat.setAddLakes();
        }

        return generatorsettingsflat;
    }

    public void setDecoration() {
        this.decoration = true;
    }

    public void setAddLakes() {
        this.addLakes = true;
    }

    public BiomeBase getBiomeFromSettings() {
        BiomeBase biomebase = this.getBiome();
        BiomeSettingsGeneration biomesettingsgeneration = biomebase.getGenerationSettings();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = new BiomeSettingsGeneration.a();

        if (this.addLakes) {
            biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
            biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
        }

        boolean flag = (!this.voidGen || this.biomes.getResourceKey(biomebase).equals(Optional.of(Biomes.THE_VOID))) && this.decoration;
        List list;
        int i;

        if (flag) {
            list = biomesettingsgeneration.features();

            for (i = 0; i < list.size(); ++i) {
                if (i != WorldGenStage.Decoration.UNDERGROUND_STRUCTURES.ordinal() && i != WorldGenStage.Decoration.SURFACE_STRUCTURES.ordinal()) {
                    List<Supplier<PlacedFeature>> list1 = (List) list.get(i);
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        Supplier<PlacedFeature> supplier = (Supplier) iterator.next();

                        biomesettingsgeneration_a.addFeature(i, supplier);
                    }
                }
            }
        }

        list = this.getLayers();

        for (i = 0; i < list.size(); ++i) {
            IBlockData iblockdata = (IBlockData) list.get(i);

            if (!HeightMap.Type.MOTION_BLOCKING.isOpaque().test(iblockdata)) {
                list.set(i, (Object) null);
                biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.FILL_LAYER.configured(new WorldGenFeatureFillConfiguration(i, iblockdata)).placed());
            }
        }

        return (new BiomeBase.a()).precipitation(biomebase.getPrecipitation()).biomeCategory(biomebase.getBiomeCategory()).temperature(biomebase.getBaseTemperature()).downfall(biomebase.getDownfall()).specialEffects(biomebase.getSpecialEffects()).generationSettings(biomesettingsgeneration_a.build()).mobSpawnSettings(biomebase.getMobSettings()).build();
    }

    public StructureSettings structureSettings() {
        return this.structureSettings;
    }

    public BiomeBase getBiome() {
        return (BiomeBase) this.biome.get();
    }

    public void setBiome(Supplier<BiomeBase> supplier) {
        this.biome = supplier;
    }

    public List<WorldGenFlatLayerInfo> getLayersInfo() {
        return this.layersInfo;
    }

    public List<IBlockData> getLayers() {
        return this.layers;
    }

    public void updateLayers() {
        this.layers.clear();
        Iterator iterator = this.layersInfo.iterator();

        while (iterator.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            for (int i = 0; i < worldgenflatlayerinfo.getHeight(); ++i) {
                this.layers.add(worldgenflatlayerinfo.getBlockState());
            }
        }

        this.voidGen = this.layers.stream().allMatch((iblockdata) -> {
            return iblockdata.is(Blocks.AIR);
        });
    }

    public static GeneratorSettingsFlat getDefault(IRegistry<BiomeBase> iregistry) {
        StructureSettings structuresettings = new StructureSettings(Optional.of(StructureSettings.DEFAULT_STRONGHOLD), Maps.newHashMap(ImmutableMap.of(StructureGenerator.VILLAGE, (StructureSettingsFeature) StructureSettings.DEFAULTS.get(StructureGenerator.VILLAGE))));
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(structuresettings, iregistry);

        generatorsettingsflat.biome = () -> {
            return (BiomeBase) iregistry.getOrThrow(Biomes.PLAINS);
        };
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.updateLayers();
        return generatorsettingsflat;
    }
}
