package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.INamable;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeSettingsGeneration {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final BiomeSettingsGeneration EMPTY = new BiomeSettingsGeneration(ImmutableMap.of(), ImmutableList.of());
    public static final MapCodec<BiomeSettingsGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        Codec codec = WorldGenStage.Features.CODEC;
        Codec codec1 = WorldGenCarverWrapper.LIST_CODEC;
        Logger logger = BiomeSettingsGeneration.LOGGER;

        Objects.requireNonNull(logger);
        RecordCodecBuilder recordcodecbuilder = Codec.simpleMap(codec, codec1.promotePartial(SystemUtils.prefix("Carver: ", logger::error)).flatXmap(ExtraCodecs.nonNullSupplierListCheck(), ExtraCodecs.nonNullSupplierListCheck()), INamable.keys(WorldGenStage.Features.values())).fieldOf("carvers").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.carvers;
        });

        codec1 = PlacedFeature.LIST_CODEC;
        logger = BiomeSettingsGeneration.LOGGER;
        Objects.requireNonNull(logger);
        return instance.group(recordcodecbuilder, codec1.promotePartial(SystemUtils.prefix("Feature: ", logger::error)).flatXmap(ExtraCodecs.nonNullSupplierListCheck(), ExtraCodecs.nonNullSupplierListCheck()).listOf().fieldOf("features").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.features;
        })).apply(instance, BiomeSettingsGeneration::new);
    });
    private final Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> carvers;
    private final List<List<Supplier<PlacedFeature>>> features;
    private final List<WorldGenFeatureConfigured<?, ?>> flowerFeatures;
    private final Set<PlacedFeature> featureSet;

    BiomeSettingsGeneration(Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> map, List<List<Supplier<PlacedFeature>>> list) {
        this.carvers = map;
        this.features = list;
        this.flowerFeatures = (List) list.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(PlacedFeature::getFeatures).filter((worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.feature == WorldGenerator.FLOWER;
        }).collect(ImmutableList.toImmutableList());
        this.featureSet = (Set) list.stream().flatMap(Collection::stream).map(Supplier::get).collect(Collectors.toSet());
    }

    public List<Supplier<WorldGenCarverWrapper<?>>> getCarvers(WorldGenStage.Features worldgenstage_features) {
        return (List) this.carvers.getOrDefault(worldgenstage_features, ImmutableList.of());
    }

    public List<WorldGenFeatureConfigured<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    public List<List<Supplier<PlacedFeature>>> features() {
        return this.features;
    }

    public boolean hasFeature(PlacedFeature placedfeature) {
        return this.featureSet.contains(placedfeature);
    }

    public static class a {

        private final Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> carvers = Maps.newLinkedHashMap();
        private final List<List<Supplier<PlacedFeature>>> features = Lists.newArrayList();

        public a() {}

        public BiomeSettingsGeneration.a addFeature(WorldGenStage.Decoration worldgenstage_decoration, PlacedFeature placedfeature) {
            return this.addFeature(worldgenstage_decoration.ordinal(), () -> {
                return placedfeature;
            });
        }

        public BiomeSettingsGeneration.a addFeature(int i, Supplier<PlacedFeature> supplier) {
            this.addFeatureStepsUpTo(i);
            ((List) this.features.get(i)).add(supplier);
            return this;
        }

        public <C extends WorldGenCarverConfiguration> BiomeSettingsGeneration.a addCarver(WorldGenStage.Features worldgenstage_features, WorldGenCarverWrapper<C> worldgencarverwrapper) {
            ((List) this.carvers.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
                return Lists.newArrayList();
            })).add(() -> {
                return worldgencarverwrapper;
            });
            return this;
        }

        private void addFeatureStepsUpTo(int i) {
            while (this.features.size() <= i) {
                this.features.add(Lists.newArrayList());
            }

        }

        public BiomeSettingsGeneration build() {
            return new BiomeSettingsGeneration((Map) this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
                return ImmutableList.copyOf((Collection) entry.getValue());
            })), (List) this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()));
        }
    }
}
