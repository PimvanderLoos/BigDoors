package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.slf4j.Logger;

public class RegistryDataLoader {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final List<RegistryDataLoader.b<?>> WORLDGEN_REGISTRIES = List.of(new RegistryDataLoader.b<>(Registries.DIMENSION_TYPE, DimensionManager.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.BIOME, BiomeBase.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.CHAT_TYPE, ChatMessageType.CODEC), new RegistryDataLoader.b<>(Registries.CONFIGURED_CARVER, WorldGenCarverWrapper.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.CONFIGURED_FEATURE, WorldGenFeatureConfigured.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.STRUCTURE, Structure.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.PROCESSOR_LIST, DefinedStructureStructureProcessorType.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.TEMPLATE_POOL, WorldGenFeatureDefinedStructurePoolTemplate.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.NOISE_SETTINGS, GeneratorSettingBase.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.NOISE, NoiseGeneratorNormal.a.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC), new RegistryDataLoader.b<>(Registries.DAMAGE_TYPE, DamageType.CODEC), new RegistryDataLoader.b<>(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC));
    public static final List<RegistryDataLoader.b<?>> DIMENSION_REGISTRIES = List.of(new RegistryDataLoader.b<>(Registries.LEVEL_STEM, WorldDimension.CODEC));

    public RegistryDataLoader() {}

    public static IRegistryCustom.Dimension load(IResourceManager iresourcemanager, IRegistryCustom iregistrycustom, List<RegistryDataLoader.b<?>> list) {
        Map<ResourceKey<?>, Exception> map = new HashMap();
        List<Pair<IRegistryWritable<?>, RegistryDataLoader.a>> list1 = list.stream().map((registrydataloader_b) -> {
            return registrydataloader_b.create(Lifecycle.stable(), map);
        }).toList();
        RegistryOps.b registryops_b = createContext(iregistrycustom, list1);

        list1.forEach((pair) -> {
            ((RegistryDataLoader.a) pair.getSecond()).load(iresourcemanager, registryops_b);
        });
        list1.forEach((pair) -> {
            IRegistry iregistry = (IRegistry) pair.getFirst();

            try {
                iregistry.freeze();
            } catch (Exception exception) {
                map.put(iregistry.key(), exception);
            }

        });
        if (!map.isEmpty()) {
            logErrors(map);
            throw new IllegalStateException("Failed to load registries due to above errors");
        } else {
            return (new IRegistryCustom.c(list1.stream().map(Pair::getFirst).toList())).freeze();
        }
    }

    private static RegistryOps.b createContext(IRegistryCustom iregistrycustom, List<Pair<IRegistryWritable<?>, RegistryDataLoader.a>> list) {
        final Map<ResourceKey<? extends IRegistry<?>>, RegistryOps.a<?>> map = new HashMap();

        iregistrycustom.registries().forEach((iregistrycustom_d) -> {
            map.put(iregistrycustom_d.key(), createInfoForContextRegistry(iregistrycustom_d.value()));
        });
        list.forEach((pair) -> {
            map.put(((IRegistryWritable) pair.getFirst()).key(), createInfoForNewRegistry((IRegistryWritable) pair.getFirst()));
        });
        return new RegistryOps.b() {
            @Override
            public <T> Optional<RegistryOps.a<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
                return Optional.ofNullable((RegistryOps.a) map.get(resourcekey));
            }
        };
    }

    private static <T> RegistryOps.a<T> createInfoForNewRegistry(IRegistryWritable<T> iregistrywritable) {
        return new RegistryOps.a<>(iregistrywritable.asLookup(), iregistrywritable.createRegistrationLookup(), iregistrywritable.registryLifecycle());
    }

    private static <T> RegistryOps.a<T> createInfoForContextRegistry(IRegistry<T> iregistry) {
        return new RegistryOps.a<>(iregistry.asLookup(), iregistry.asTagAddingLookup(), iregistry.registryLifecycle());
    }

    private static void logErrors(Map<ResourceKey<?>, Exception> map) {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        Map<MinecraftKey, Map<MinecraftKey, Exception>> map1 = (Map) map.entrySet().stream().collect(Collectors.groupingBy((entry) -> {
            return ((ResourceKey) entry.getKey()).registry();
        }, Collectors.toMap((entry) -> {
            return ((ResourceKey) entry.getKey()).location();
        }, Entry::getValue)));

        map1.entrySet().stream().sorted(Entry.comparingByKey()).forEach((entry) -> {
            printwriter.printf("> Errors in registry %s:%n", entry.getKey());
            ((Map) entry.getValue()).entrySet().stream().sorted(Entry.comparingByKey()).forEach((entry1) -> {
                printwriter.printf(">> Errors in element %s:%n", entry1.getKey());
                ((Exception) entry1.getValue()).printStackTrace(printwriter);
            });
        });
        printwriter.flush();
        RegistryDataLoader.LOGGER.error("Registry loading errors:\n{}", stringwriter);
    }

    private static String registryDirPath(MinecraftKey minecraftkey) {
        return minecraftkey.getPath();
    }

    static <E> void loadRegistryContents(RegistryOps.b registryops_b, IResourceManager iresourcemanager, ResourceKey<? extends IRegistry<E>> resourcekey, IRegistryWritable<E> iregistrywritable, Decoder<E> decoder, Map<ResourceKey<?>, Exception> map) {
        String s = registryDirPath(resourcekey.location());
        FileToIdConverter filetoidconverter = FileToIdConverter.json(s);
        RegistryOps<JsonElement> registryops = RegistryOps.create(JsonOps.INSTANCE, registryops_b);
        Iterator iterator = filetoidconverter.listMatchingResources(iresourcemanager).entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, IResource> entry = (Entry) iterator.next();
            MinecraftKey minecraftkey = (MinecraftKey) entry.getKey();
            ResourceKey<E> resourcekey1 = ResourceKey.create(resourcekey, filetoidconverter.fileToId(minecraftkey));
            IResource iresource = (IResource) entry.getValue();

            try {
                BufferedReader bufferedreader = iresource.openAsReader();

                try {
                    JsonElement jsonelement = JsonParser.parseReader(bufferedreader);
                    DataResult<E> dataresult = decoder.parse(registryops, jsonelement);
                    E e0 = dataresult.getOrThrow(false, (s1) -> {
                    });

                    iregistrywritable.register(resourcekey1, e0, iresource.isBuiltin() ? Lifecycle.stable() : dataresult.lifecycle());
                } catch (Throwable throwable) {
                    if (bufferedreader != null) {
                        try {
                            bufferedreader.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (bufferedreader != null) {
                    bufferedreader.close();
                }
            } catch (Exception exception) {
                map.put(resourcekey1, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", minecraftkey, iresource.sourcePackId()), exception));
            }
        }

    }

    private interface a {

        void load(IResourceManager iresourcemanager, RegistryOps.b registryops_b);
    }

    public static record b<T> (ResourceKey<? extends IRegistry<T>> key, Codec<T> elementCodec) {

        Pair<IRegistryWritable<?>, RegistryDataLoader.a> create(Lifecycle lifecycle, Map<ResourceKey<?>, Exception> map) {
            IRegistryWritable<T> iregistrywritable = new RegistryMaterials<>(this.key, lifecycle);
            RegistryDataLoader.a registrydataloader_a = (iresourcemanager, registryops_b) -> {
                RegistryDataLoader.loadRegistryContents(registryops_b, iresourcemanager, this.key, iregistrywritable, this.elementCodec, map);
            };

            return Pair.of(iregistrywritable, registrydataloader_a);
        }
    }
}
