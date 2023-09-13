package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureFillConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class GeneratorSettingsFlat {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<GeneratorSettingsFlat> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET).optionalFieldOf("structure_overrides").forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.structureOverrides;
        }), WorldGenFlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(GeneratorSettingsFlat::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.addLakes;
        }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.decoration;
        }), BiomeBase.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((generatorsettingsflat) -> {
            return Optional.of(generatorsettingsflat.biome);
        }), RegistryOps.retrieveElement(Biomes.PLAINS), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_SURFACE)).apply(instance, GeneratorSettingsFlat::new);
    }).comapFlatMap(GeneratorSettingsFlat::validateHeight, Function.identity()).stable();
    private final Optional<HolderSet<StructureSet>> structureOverrides;
    private final List<WorldGenFlatLayerInfo> layersInfo;
    private final Holder<BiomeBase> biome;
    private final List<IBlockData> layers;
    private boolean voidGen;
    private boolean decoration;
    private boolean addLakes;
    private final List<Holder<PlacedFeature>> lakes;

    private static DataResult<GeneratorSettingsFlat> validateHeight(GeneratorSettingsFlat generatorsettingsflat) {
        int i = generatorsettingsflat.layersInfo.stream().mapToInt(WorldGenFlatLayerInfo::getHeight).sum();

        return i > DimensionManager.Y_SIZE ? DataResult.error(() -> {
            return "Sum of layer heights is > " + DimensionManager.Y_SIZE;
        }, generatorsettingsflat) : DataResult.success(generatorsettingsflat);
    }

    private GeneratorSettingsFlat(Optional<HolderSet<StructureSet>> optional, List<WorldGenFlatLayerInfo> list, boolean flag, boolean flag1, Optional<Holder<BiomeBase>> optional1, Holder.c<BiomeBase> holder_c, Holder<PlacedFeature> holder, Holder<PlacedFeature> holder1) {
        this(optional, getBiome(optional1, holder_c), List.of(holder, holder1));
        if (flag) {
            this.setAddLakes();
        }

        if (flag1) {
            this.setDecoration();
        }

        this.layersInfo.addAll(list);
        this.updateLayers();
    }

    private static Holder<BiomeBase> getBiome(Optional<? extends Holder<BiomeBase>> optional, Holder<BiomeBase> holder) {
        if (optional.isEmpty()) {
            GeneratorSettingsFlat.LOGGER.error("Unknown biome, defaulting to plains");
            return holder;
        } else {
            return (Holder) optional.get();
        }
    }

    public GeneratorSettingsFlat(Optional<HolderSet<StructureSet>> optional, Holder<BiomeBase> holder, List<Holder<PlacedFeature>> list) {
        this.layersInfo = Lists.newArrayList();
        this.structureOverrides = optional;
        this.biome = holder;
        this.layers = Lists.newArrayList();
        this.lakes = list;
    }

    public GeneratorSettingsFlat withBiomeAndLayers(List<WorldGenFlatLayerInfo> list, Optional<HolderSet<StructureSet>> optional, Holder<BiomeBase> holder) {
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(optional, holder, this.lakes);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            generatorsettingsflat.layersInfo.add(new WorldGenFlatLayerInfo(worldgenflatlayerinfo.getHeight(), worldgenflatlayerinfo.getBlockState().getBlock()));
            generatorsettingsflat.updateLayers();
        }

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

    public BiomeSettingsGeneration adjustGenerationSettings(Holder<BiomeBase> holder) {
        if (!holder.equals(this.biome)) {
            return ((BiomeBase) holder.value()).getGenerationSettings();
        } else {
            BiomeSettingsGeneration biomesettingsgeneration = ((BiomeBase) this.getBiome().value()).getGenerationSettings();
            BiomeSettingsGeneration.b biomesettingsgeneration_b = new BiomeSettingsGeneration.b();

            if (this.addLakes) {
                Iterator iterator = this.lakes.iterator();

                while (iterator.hasNext()) {
                    Holder<PlacedFeature> holder1 = (Holder) iterator.next();

                    biomesettingsgeneration_b.addFeature(WorldGenStage.Decoration.LAKES, holder1);
                }
            }

            boolean flag = (!this.voidGen || holder.is(Biomes.THE_VOID)) && this.decoration;
            List list;
            int i;

            if (flag) {
                list = biomesettingsgeneration.features();

                for (i = 0; i < list.size(); ++i) {
                    if (i != WorldGenStage.Decoration.UNDERGROUND_STRUCTURES.ordinal() && i != WorldGenStage.Decoration.SURFACE_STRUCTURES.ordinal() && (!this.addLakes || i != WorldGenStage.Decoration.LAKES.ordinal())) {
                        HolderSet<PlacedFeature> holderset = (HolderSet) list.get(i);
                        Iterator iterator1 = holderset.iterator();

                        while (iterator1.hasNext()) {
                            Holder<PlacedFeature> holder2 = (Holder) iterator1.next();

                            biomesettingsgeneration_b.addFeature(i, holder2);
                        }
                    }
                }
            }

            list = this.getLayers();

            for (i = 0; i < list.size(); ++i) {
                IBlockData iblockdata = (IBlockData) list.get(i);

                if (!HeightMap.Type.MOTION_BLOCKING.isOpaque().test(iblockdata)) {
                    list.set(i, (Object) null);
                    biomesettingsgeneration_b.addFeature(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, PlacementUtils.inlinePlaced(WorldGenerator.FILL_LAYER, new WorldGenFeatureFillConfiguration(i, iblockdata)));
                }
            }

            return biomesettingsgeneration_b.build();
        }
    }

    public Optional<HolderSet<StructureSet>> structureOverrides() {
        return this.structureOverrides;
    }

    public Holder<BiomeBase> getBiome() {
        return this.biome;
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

    public static GeneratorSettingsFlat getDefault(HolderGetter<BiomeBase> holdergetter, HolderGetter<StructureSet> holdergetter1, HolderGetter<PlacedFeature> holdergetter2) {
        HolderSet<StructureSet> holderset = HolderSet.direct(holdergetter1.getOrThrow(BuiltinStructureSets.STRONGHOLDS), holdergetter1.getOrThrow(BuiltinStructureSets.VILLAGES));
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(Optional.of(holderset), getDefaultBiome(holdergetter), createLakesList(holdergetter2));

        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.updateLayers();
        return generatorsettingsflat;
    }

    public static Holder<BiomeBase> getDefaultBiome(HolderGetter<BiomeBase> holdergetter) {
        return holdergetter.getOrThrow(Biomes.PLAINS);
    }

    public static List<Holder<PlacedFeature>> createLakesList(HolderGetter<PlacedFeature> holdergetter) {
        return List.of(holdergetter.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), holdergetter.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_SURFACE));
    }
}
