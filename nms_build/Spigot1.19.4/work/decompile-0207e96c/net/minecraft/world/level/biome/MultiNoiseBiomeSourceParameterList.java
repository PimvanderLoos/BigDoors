package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class MultiNoiseBiomeSourceParameterList {

    public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(MultiNoiseBiomeSourceParameterList.a.CODEC.fieldOf("preset").forGetter((multinoisebiomesourceparameterlist) -> {
            return multinoisebiomesourceparameterlist.preset;
        }), RegistryOps.retrieveGetter(Registries.BIOME)).apply(instance, MultiNoiseBiomeSourceParameterList::new);
    });
    public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC = RegistryFileCodec.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC);
    private final MultiNoiseBiomeSourceParameterList.a preset;
    private final Climate.c<Holder<BiomeBase>> parameters;

    public MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.a multinoisebiomesourceparameterlist_a, HolderGetter<BiomeBase> holdergetter) {
        this.preset = multinoisebiomesourceparameterlist_a;
        MultiNoiseBiomeSourceParameterList.a.a multinoisebiomesourceparameterlist_a_a = multinoisebiomesourceparameterlist_a.provider;

        Objects.requireNonNull(holdergetter);
        this.parameters = multinoisebiomesourceparameterlist_a_a.apply(holdergetter::getOrThrow);
    }

    public Climate.c<Holder<BiomeBase>> parameters() {
        return this.parameters;
    }

    public static Map<MultiNoiseBiomeSourceParameterList.a, Climate.c<ResourceKey<BiomeBase>>> knownPresets() {
        return (Map) MultiNoiseBiomeSourceParameterList.a.BY_NAME.values().stream().collect(Collectors.toMap((multinoisebiomesourceparameterlist_a) -> {
            return multinoisebiomesourceparameterlist_a;
        }, (multinoisebiomesourceparameterlist_a) -> {
            return multinoisebiomesourceparameterlist_a.provider().apply((resourcekey) -> {
                return resourcekey;
            });
        }));
    }

    public static record a(MinecraftKey id, MultiNoiseBiomeSourceParameterList.a.a provider) {

        public static final MultiNoiseBiomeSourceParameterList.a NETHER = new MultiNoiseBiomeSourceParameterList.a(new MinecraftKey("nether"), new MultiNoiseBiomeSourceParameterList.a.a() {
            @Override
            public <T> Climate.c<T> apply(Function<ResourceKey<BiomeBase>, T> function) {
                return new Climate.c<>(List.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), function.apply(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), function.apply(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), function.apply(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), function.apply(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), function.apply(Biomes.BASALT_DELTAS))));
            }
        });
        public static final MultiNoiseBiomeSourceParameterList.a OVERWORLD = new MultiNoiseBiomeSourceParameterList.a(new MinecraftKey("overworld"), new MultiNoiseBiomeSourceParameterList.a.a() {
            @Override
            public <T> Climate.c<T> apply(Function<ResourceKey<BiomeBase>, T> function) {
                return MultiNoiseBiomeSourceParameterList.a.generateOverworldBiomes(function, OverworldBiomeBuilder.a.NONE);
            }
        });
        public static final MultiNoiseBiomeSourceParameterList.a OVERWORLD_UPDATE_1_20 = new MultiNoiseBiomeSourceParameterList.a(new MinecraftKey("overworld_update_1_20"), new MultiNoiseBiomeSourceParameterList.a.a() {
            @Override
            public <T> Climate.c<T> apply(Function<ResourceKey<BiomeBase>, T> function) {
                return MultiNoiseBiomeSourceParameterList.a.generateOverworldBiomes(function, OverworldBiomeBuilder.a.UPDATE_1_20);
            }
        });
        static final Map<MinecraftKey, MultiNoiseBiomeSourceParameterList.a> BY_NAME = (Map) Stream.of(MultiNoiseBiomeSourceParameterList.a.NETHER, MultiNoiseBiomeSourceParameterList.a.OVERWORLD, MultiNoiseBiomeSourceParameterList.a.OVERWORLD_UPDATE_1_20).collect(Collectors.toMap(MultiNoiseBiomeSourceParameterList.a::id, (multinoisebiomesourceparameterlist_a) -> {
            return multinoisebiomesourceparameterlist_a;
        }));
        public static final Codec<MultiNoiseBiomeSourceParameterList.a> CODEC = MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
            return (DataResult) Optional.ofNullable((MultiNoiseBiomeSourceParameterList.a) MultiNoiseBiomeSourceParameterList.a.BY_NAME.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    return "Unknown preset: " + minecraftkey;
                });
            });
        }, (multinoisebiomesourceparameterlist_a) -> {
            return DataResult.success(multinoisebiomesourceparameterlist_a.id);
        });

        static <T> Climate.c<T> generateOverworldBiomes(Function<ResourceKey<BiomeBase>, T> function, OverworldBiomeBuilder.a overworldbiomebuilder_a) {
            Builder<Pair<Climate.d, T>> builder = ImmutableList.builder();

            (new OverworldBiomeBuilder(overworldbiomebuilder_a)).addBiomes((pair) -> {
                builder.add(pair.mapSecond(function));
            });
            return new Climate.c<>(builder.build());
        }

        public Stream<ResourceKey<BiomeBase>> usedBiomes() {
            return this.provider.apply((resourcekey) -> {
                return resourcekey;
            }).values().stream().map(Pair::getSecond).distinct();
        }

        @FunctionalInterface
        private interface a {

            <T> Climate.c<T> apply(Function<ResourceKey<BiomeBase>, T> function);
        }
    }
}
