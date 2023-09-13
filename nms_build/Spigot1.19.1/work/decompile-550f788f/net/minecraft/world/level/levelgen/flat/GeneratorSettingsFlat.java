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
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryCodecs;
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
        return instance.group(RegistryOps.retrieveRegistry(IRegistry.BIOME_REGISTRY).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.biomes;
        }), RegistryCodecs.homogeneousList(IRegistry.STRUCTURE_SET_REGISTRY).optionalFieldOf("structure_overrides").forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.structureOverrides;
        }), WorldGenFlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(GeneratorSettingsFlat::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.addLakes;
        }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((generatorsettingsflat) -> {
            return generatorsettingsflat.decoration;
        }), BiomeBase.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((generatorsettingsflat) -> {
            return Optional.of(generatorsettingsflat.biome);
        })).apply(instance, GeneratorSettingsFlat::new);
    }).comapFlatMap(GeneratorSettingsFlat::validateHeight, Function.identity()).stable();
    private final IRegistry<BiomeBase> biomes;
    private final Optional<HolderSet<StructureSet>> structureOverrides;
    private final List<WorldGenFlatLayerInfo> layersInfo;
    private Holder<BiomeBase> biome;
    private final List<IBlockData> layers;
    private boolean voidGen;
    private boolean decoration;
    private boolean addLakes;

    private static DataResult<GeneratorSettingsFlat> validateHeight(GeneratorSettingsFlat generatorsettingsflat) {
        int i = generatorsettingsflat.layersInfo.stream().mapToInt(WorldGenFlatLayerInfo::getHeight).sum();

        return i > DimensionManager.Y_SIZE ? DataResult.error("Sum of layer heights is > " + DimensionManager.Y_SIZE, generatorsettingsflat) : DataResult.success(generatorsettingsflat);
    }

    private GeneratorSettingsFlat(IRegistry<BiomeBase> iregistry, Optional<HolderSet<StructureSet>> optional, List<WorldGenFlatLayerInfo> list, boolean flag, boolean flag1, Optional<Holder<BiomeBase>> optional1) {
        this(optional, iregistry);
        if (flag) {
            this.setAddLakes();
        }

        if (flag1) {
            this.setDecoration();
        }

        this.layersInfo.addAll(list);
        this.updateLayers();
        if (optional1.isEmpty()) {
            GeneratorSettingsFlat.LOGGER.error("Unknown biome, defaulting to plains");
            this.biome = iregistry.getOrCreateHolderOrThrow(Biomes.PLAINS);
        } else {
            this.biome = (Holder) optional1.get();
        }

    }

    public GeneratorSettingsFlat(Optional<HolderSet<StructureSet>> optional, IRegistry<BiomeBase> iregistry) {
        this.layersInfo = Lists.newArrayList();
        this.biomes = iregistry;
        this.structureOverrides = optional;
        this.biome = iregistry.getOrCreateHolderOrThrow(Biomes.PLAINS);
        this.layers = Lists.newArrayList();
    }

    public GeneratorSettingsFlat withLayers(List<WorldGenFlatLayerInfo> list, Optional<HolderSet<StructureSet>> optional) {
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(optional, this.biomes);
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

    public BiomeSettingsGeneration adjustGenerationSettings(Holder<BiomeBase> holder) {
        if (!holder.equals(this.biome)) {
            return ((BiomeBase) holder.value()).getGenerationSettings();
        } else {
            BiomeSettingsGeneration biomesettingsgeneration = ((BiomeBase) this.getBiome().value()).getGenerationSettings();
            BiomeSettingsGeneration.a biomesettingsgeneration_a = new BiomeSettingsGeneration.a();

            if (this.addLakes) {
                biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
                biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
            }

            boolean flag = (!this.voidGen || holder.is(Biomes.THE_VOID)) && this.decoration;
            List list;
            int i;

            if (flag) {
                list = biomesettingsgeneration.features();

                for (i = 0; i < list.size(); ++i) {
                    if (i != WorldGenStage.Decoration.UNDERGROUND_STRUCTURES.ordinal() && i != WorldGenStage.Decoration.SURFACE_STRUCTURES.ordinal()) {
                        HolderSet<PlacedFeature> holderset = (HolderSet) list.get(i);
                        Iterator iterator = holderset.iterator();

                        while (iterator.hasNext()) {
                            Holder<PlacedFeature> holder1 = (Holder) iterator.next();

                            biomesettingsgeneration_a.addFeature(i, holder1);
                        }
                    }
                }
            }

            list = this.getLayers();

            for (i = 0; i < list.size(); ++i) {
                IBlockData iblockdata = (IBlockData) list.get(i);

                if (!HeightMap.Type.MOTION_BLOCKING.isOpaque().test(iblockdata)) {
                    list.set(i, (Object) null);
                    biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, PlacementUtils.inlinePlaced(WorldGenerator.FILL_LAYER, new WorldGenFeatureFillConfiguration(i, iblockdata)));
                }
            }

            return biomesettingsgeneration_a.build();
        }
    }

    public Optional<HolderSet<StructureSet>> structureOverrides() {
        return this.structureOverrides;
    }

    public Holder<BiomeBase> getBiome() {
        return this.biome;
    }

    public void setBiome(Holder<BiomeBase> holder) {
        this.biome = holder;
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

    public static GeneratorSettingsFlat getDefault(IRegistry<BiomeBase> iregistry, IRegistry<StructureSet> iregistry1) {
        HolderSet<StructureSet> holderset = HolderSet.direct(iregistry1.getHolderOrThrow(BuiltinStructureSets.STRONGHOLDS), iregistry1.getHolderOrThrow(BuiltinStructureSets.VILLAGES));
        GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(Optional.of(holderset), iregistry);

        generatorsettingsflat.biome = iregistry.getOrCreateHolderOrThrow(Biomes.PLAINS);
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(2, Blocks.DIRT));
        generatorsettingsflat.getLayersInfo().add(new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK));
        generatorsettingsflat.updateLayers();
        return generatorsettingsflat;
    }
}
