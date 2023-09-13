package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.SystemUtils;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.INamable;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeSettingsGeneration {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final BiomeSettingsGeneration EMPTY = new BiomeSettingsGeneration(() -> {
        return WorldGenSurfaceComposites.NOPE;
    }, ImmutableMap.of(), ImmutableList.of(), ImmutableList.of());
    public static final MapCodec<BiomeSettingsGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        RecordCodecBuilder recordcodecbuilder = WorldGenSurfaceComposite.CODEC.fieldOf("surface_builder").flatXmap(ExtraCodecs.c(), ExtraCodecs.c()).forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.surfaceBuilder;
        });
        Codec codec = WorldGenStage.Features.CODEC;
        Codec codec1 = WorldGenCarverWrapper.LIST_CODEC;
        Logger logger = BiomeSettingsGeneration.LOGGER;

        Objects.requireNonNull(logger);
        RecordCodecBuilder recordcodecbuilder1 = Codec.simpleMap(codec, codec1.promotePartial(SystemUtils.a("Carver: ", logger::error)).flatXmap(ExtraCodecs.b(), ExtraCodecs.b()), INamable.a((INamable[]) WorldGenStage.Features.values())).fieldOf("carvers").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.carvers;
        });

        codec1 = WorldGenFeatureConfigured.LIST_CODEC;
        logger = BiomeSettingsGeneration.LOGGER;
        Objects.requireNonNull(logger);
        RecordCodecBuilder recordcodecbuilder2 = codec1.promotePartial(SystemUtils.a("Feature: ", logger::error)).flatXmap(ExtraCodecs.b(), ExtraCodecs.b()).listOf().fieldOf("features").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.features;
        });
        Codec codec2 = StructureFeature.LIST_CODEC;
        Logger logger1 = BiomeSettingsGeneration.LOGGER;

        Objects.requireNonNull(logger1);
        return instance.group(recordcodecbuilder, recordcodecbuilder1, recordcodecbuilder2, codec2.promotePartial(SystemUtils.a("Structure start: ", logger1::error)).fieldOf("starts").flatXmap(ExtraCodecs.b(), ExtraCodecs.b()).forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.structureStarts;
        })).apply(instance, BiomeSettingsGeneration::new);
    });
    private final Supplier<WorldGenSurfaceComposite<?>> surfaceBuilder;
    private final Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> carvers;
    private final List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> features;
    private final List<Supplier<StructureFeature<?, ?>>> structureStarts;
    private final List<WorldGenFeatureConfigured<?, ?>> flowerFeatures;

    BiomeSettingsGeneration(Supplier<WorldGenSurfaceComposite<?>> supplier, Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> map, List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> list, List<Supplier<StructureFeature<?, ?>>> list1) {
        this.surfaceBuilder = supplier;
        this.carvers = map;
        this.features = list;
        this.structureStarts = list1;
        this.flowerFeatures = (List) list.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(WorldGenFeatureConfigured::d).filter((worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.feature == WorldGenerator.FLOWER;
        }).collect(ImmutableList.toImmutableList());
    }

    public List<Supplier<WorldGenCarverWrapper<?>>> a(WorldGenStage.Features worldgenstage_features) {
        return (List) this.carvers.getOrDefault(worldgenstage_features, ImmutableList.of());
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return this.structureStarts.stream().anyMatch((supplier) -> {
            return ((StructureFeature) supplier.get()).feature == structuregenerator;
        });
    }

    public Collection<Supplier<StructureFeature<?, ?>>> a() {
        return this.structureStarts;
    }

    public StructureFeature<?, ?> a(StructureFeature<?, ?> structurefeature) {
        return (StructureFeature) DataFixUtils.orElse(this.structureStarts.stream().map(Supplier::get).filter((structurefeature1) -> {
            return structurefeature1.feature == structurefeature.feature;
        }).findAny(), structurefeature);
    }

    public List<WorldGenFeatureConfigured<?, ?>> b() {
        return this.flowerFeatures;
    }

    public List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> c() {
        return this.features;
    }

    public Supplier<WorldGenSurfaceComposite<?>> d() {
        return this.surfaceBuilder;
    }

    public WorldGenSurfaceConfiguration e() {
        return ((WorldGenSurfaceComposite) this.surfaceBuilder.get()).a();
    }

    public static class a {

        private Optional<Supplier<WorldGenSurfaceComposite<?>>> surfaceBuilder = Optional.empty();
        private final Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> carvers = Maps.newLinkedHashMap();
        private final List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> features = Lists.newArrayList();
        private final List<Supplier<StructureFeature<?, ?>>> structureStarts = Lists.newArrayList();

        public a() {}

        public BiomeSettingsGeneration.a a(WorldGenSurfaceComposite<?> worldgensurfacecomposite) {
            return this.a(() -> {
                return worldgensurfacecomposite;
            });
        }

        public BiomeSettingsGeneration.a a(Supplier<WorldGenSurfaceComposite<?>> supplier) {
            this.surfaceBuilder = Optional.of(supplier);
            return this;
        }

        public BiomeSettingsGeneration.a a(WorldGenStage.Decoration worldgenstage_decoration, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
            return this.a(worldgenstage_decoration.ordinal(), () -> {
                return worldgenfeatureconfigured;
            });
        }

        public BiomeSettingsGeneration.a a(int i, Supplier<WorldGenFeatureConfigured<?, ?>> supplier) {
            this.a(i);
            ((List) this.features.get(i)).add(supplier);
            return this;
        }

        public <C extends WorldGenCarverConfiguration> BiomeSettingsGeneration.a a(WorldGenStage.Features worldgenstage_features, WorldGenCarverWrapper<C> worldgencarverwrapper) {
            ((List) this.carvers.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
                return Lists.newArrayList();
            })).add(() -> {
                return worldgencarverwrapper;
            });
            return this;
        }

        public BiomeSettingsGeneration.a a(StructureFeature<?, ?> structurefeature) {
            this.structureStarts.add(() -> {
                return structurefeature;
            });
            return this;
        }

        private void a(int i) {
            while (this.features.size() <= i) {
                this.features.add(Lists.newArrayList());
            }

        }

        public BiomeSettingsGeneration a() {
            return new BiomeSettingsGeneration((Supplier) this.surfaceBuilder.orElseThrow(() -> {
                return new IllegalStateException("Missing surface builder");
            }), (Map) this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
                return ImmutableList.copyOf((Collection) entry.getValue());
            })), (List) this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()), ImmutableList.copyOf(this.structureStarts));
        }
    }
}
