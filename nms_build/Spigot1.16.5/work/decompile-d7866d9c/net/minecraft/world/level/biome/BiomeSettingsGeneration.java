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
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.SystemUtils;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
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
    public static final BiomeSettingsGeneration b = new BiomeSettingsGeneration(() -> {
        return WorldGenSurfaceComposites.p;
    }, ImmutableMap.of(), ImmutableList.of(), ImmutableList.of());
    public static final MapCodec<BiomeSettingsGeneration> c = RecordCodecBuilder.mapCodec((instance) -> {
        RecordCodecBuilder recordcodecbuilder = WorldGenSurfaceComposite.b.fieldOf("surface_builder").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.d;
        });
        Codec codec = WorldGenStage.Features.c;
        Codec codec1 = WorldGenCarverWrapper.c;
        Logger logger = BiomeSettingsGeneration.LOGGER;

        logger.getClass();
        RecordCodecBuilder recordcodecbuilder1 = Codec.simpleMap(codec, codec1.promotePartial(SystemUtils.a("Carver: ", logger::error)), INamable.a(WorldGenStage.Features.values())).fieldOf("carvers").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.e;
        });

        codec1 = WorldGenFeatureConfigured.c;
        logger = BiomeSettingsGeneration.LOGGER;
        logger.getClass();
        RecordCodecBuilder recordcodecbuilder2 = codec1.promotePartial(SystemUtils.a("Feature: ", logger::error)).listOf().fieldOf("features").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.f;
        });
        Codec codec2 = StructureFeature.c;
        Logger logger1 = BiomeSettingsGeneration.LOGGER;

        logger1.getClass();
        return instance.group(recordcodecbuilder, recordcodecbuilder1, recordcodecbuilder2, codec2.promotePartial(SystemUtils.a("Structure start: ", logger1::error)).fieldOf("starts").forGetter((biomesettingsgeneration) -> {
            return biomesettingsgeneration.g;
        })).apply(instance, BiomeSettingsGeneration::new);
    });
    private final Supplier<WorldGenSurfaceComposite<?>> d;
    private final Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> e;
    private final List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> f;
    private final List<Supplier<StructureFeature<?, ?>>> g;
    private final List<WorldGenFeatureConfigured<?, ?>> h;

    private BiomeSettingsGeneration(Supplier<WorldGenSurfaceComposite<?>> supplier, Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> map, List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> list, List<Supplier<StructureFeature<?, ?>>> list1) {
        this.d = supplier;
        this.e = map;
        this.f = list;
        this.g = list1;
        this.h = (List) list.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(WorldGenFeatureConfigured::d).filter((worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.e == WorldGenerator.FLOWER;
        }).collect(ImmutableList.toImmutableList());
    }

    public List<Supplier<WorldGenCarverWrapper<?>>> a(WorldGenStage.Features worldgenstage_features) {
        return (List) this.e.getOrDefault(worldgenstage_features, ImmutableList.of());
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return this.g.stream().anyMatch((supplier) -> {
            return ((StructureFeature) supplier.get()).d == structuregenerator;
        });
    }

    public Collection<Supplier<StructureFeature<?, ?>>> a() {
        return this.g;
    }

    public StructureFeature<?, ?> a(StructureFeature<?, ?> structurefeature) {
        return (StructureFeature) DataFixUtils.orElse(this.g.stream().map(Supplier::get).filter((structurefeature1) -> {
            return structurefeature1.d == structurefeature.d;
        }).findAny(), structurefeature);
    }

    public List<WorldGenFeatureConfigured<?, ?>> b() {
        return this.h;
    }

    public List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> c() {
        return this.f;
    }

    public Supplier<WorldGenSurfaceComposite<?>> d() {
        return this.d;
    }

    public WorldGenSurfaceConfiguration e() {
        return ((WorldGenSurfaceComposite) this.d.get()).a();
    }

    public static class a {

        private Optional<Supplier<WorldGenSurfaceComposite<?>>> a = Optional.empty();
        private final Map<WorldGenStage.Features, List<Supplier<WorldGenCarverWrapper<?>>>> b = Maps.newLinkedHashMap();
        private final List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> c = Lists.newArrayList();
        private final List<Supplier<StructureFeature<?, ?>>> d = Lists.newArrayList();

        public a() {}

        public BiomeSettingsGeneration.a a(WorldGenSurfaceComposite<?> worldgensurfacecomposite) {
            return this.a(() -> {
                return worldgensurfacecomposite;
            });
        }

        public BiomeSettingsGeneration.a a(Supplier<WorldGenSurfaceComposite<?>> supplier) {
            this.a = Optional.of(supplier);
            return this;
        }

        public BiomeSettingsGeneration.a a(WorldGenStage.Decoration worldgenstage_decoration, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
            return this.a(worldgenstage_decoration.ordinal(), () -> {
                return worldgenfeatureconfigured;
            });
        }

        public BiomeSettingsGeneration.a a(int i, Supplier<WorldGenFeatureConfigured<?, ?>> supplier) {
            this.a(i);
            ((List) this.c.get(i)).add(supplier);
            return this;
        }

        public <C extends WorldGenCarverConfiguration> BiomeSettingsGeneration.a a(WorldGenStage.Features worldgenstage_features, WorldGenCarverWrapper<C> worldgencarverwrapper) {
            ((List) this.b.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
                return Lists.newArrayList();
            })).add(() -> {
                return worldgencarverwrapper;
            });
            return this;
        }

        public BiomeSettingsGeneration.a a(StructureFeature<?, ?> structurefeature) {
            this.d.add(() -> {
                return structurefeature;
            });
            return this;
        }

        private void a(int i) {
            while (this.c.size() <= i) {
                this.c.add(Lists.newArrayList());
            }

        }

        public BiomeSettingsGeneration a() {
            return new BiomeSettingsGeneration((Supplier) this.a.orElseThrow(() -> {
                return new IllegalStateException("Missing surface builder");
            }), (Map) this.b.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
                return ImmutableList.copyOf((Collection) entry.getValue());
            })), (List) this.c.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()), ImmutableList.copyOf(this.d));
        }
    }
}
