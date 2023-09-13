package net.minecraft.world.level.levelgen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public final class RandomState {

    final PositionalRandomFactory random;
    private final HolderGetter<NoiseGeneratorNormal.a> noises;
    private final NoiseRouter router;
    private final Climate.Sampler sampler;
    private final SurfaceSystem surfaceSystem;
    private final PositionalRandomFactory aquiferRandom;
    private final PositionalRandomFactory oreRandom;
    private final Map<ResourceKey<NoiseGeneratorNormal.a>, NoiseGeneratorNormal> noiseIntances;
    private final Map<MinecraftKey, PositionalRandomFactory> positionalRandoms;

    public static RandomState create(HolderGetter.a holdergetter_a, ResourceKey<GeneratorSettingBase> resourcekey, long i) {
        return create((GeneratorSettingBase) holdergetter_a.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(resourcekey).value(), holdergetter_a.lookupOrThrow(Registries.NOISE), i);
    }

    public static RandomState create(GeneratorSettingBase generatorsettingbase, HolderGetter<NoiseGeneratorNormal.a> holdergetter, long i) {
        return new RandomState(generatorsettingbase, holdergetter, i);
    }

    private RandomState(GeneratorSettingBase generatorsettingbase, HolderGetter<NoiseGeneratorNormal.a> holdergetter, final long i) {
        this.random = generatorsettingbase.getRandomSource().newInstance(i).forkPositional();
        this.noises = holdergetter;
        this.aquiferRandom = this.random.fromHashOf(new MinecraftKey("aquifer")).forkPositional();
        this.oreRandom = this.random.fromHashOf(new MinecraftKey("ore")).forkPositional();
        this.noiseIntances = new ConcurrentHashMap();
        this.positionalRandoms = new ConcurrentHashMap();
        this.surfaceSystem = new SurfaceSystem(this, generatorsettingbase.defaultBlock(), generatorsettingbase.seaLevel(), this.random);
        final boolean flag = generatorsettingbase.useLegacyRandomSource();

        class a implements DensityFunction.f {

            private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();

            a() {}

            private RandomSource newLegacyInstance(long j) {
                return new LegacyRandomSource(i + j);
            }

            @Override
            public DensityFunction.c visitNoise(DensityFunction.c densityfunction_c) {
                Holder<NoiseGeneratorNormal.a> holder = densityfunction_c.noiseData();
                NoiseGeneratorNormal noisegeneratornormal;

                if (flag) {
                    if (holder.is(Noises.TEMPERATURE)) {
                        noisegeneratornormal = NoiseGeneratorNormal.createLegacyNetherBiome(this.newLegacyInstance(0L), new NoiseGeneratorNormal.a(-7, 1.0D, new double[]{1.0D}));
                        return new DensityFunction.c(holder, noisegeneratornormal);
                    }

                    if (holder.is(Noises.VEGETATION)) {
                        noisegeneratornormal = NoiseGeneratorNormal.createLegacyNetherBiome(this.newLegacyInstance(1L), new NoiseGeneratorNormal.a(-7, 1.0D, new double[]{1.0D}));
                        return new DensityFunction.c(holder, noisegeneratornormal);
                    }

                    if (holder.is(Noises.SHIFT)) {
                        noisegeneratornormal = NoiseGeneratorNormal.create(RandomState.this.random.fromHashOf(Noises.SHIFT.location()), new NoiseGeneratorNormal.a(0, 0.0D, new double[0]));
                        return new DensityFunction.c(holder, noisegeneratornormal);
                    }
                }

                noisegeneratornormal = RandomState.this.getOrCreateNoise((ResourceKey) holder.unwrapKey().orElseThrow());
                return new DensityFunction.c(holder, noisegeneratornormal);
            }

            private DensityFunction wrapNew(DensityFunction densityfunction) {
                if (densityfunction instanceof BlendedNoise) {
                    BlendedNoise blendednoise = (BlendedNoise) densityfunction;
                    RandomSource randomsource = flag ? this.newLegacyInstance(0L) : RandomState.this.random.fromHashOf(new MinecraftKey("terrain"));

                    return blendednoise.withNewRandom(randomsource);
                } else {
                    return (DensityFunction) (densityfunction instanceof DensityFunctions.i ? new DensityFunctions.i(i) : densityfunction);
                }
            }

            @Override
            public DensityFunction apply(DensityFunction densityfunction) {
                return (DensityFunction) this.wrapped.computeIfAbsent(densityfunction, this::wrapNew);
            }
        }

        this.router = generatorsettingbase.noiseRouter().mapAll(new a());
        DensityFunction.f densityfunction_f = new DensityFunction.f() {
            private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();

            private DensityFunction wrapNew(DensityFunction densityfunction) {
                if (densityfunction instanceof DensityFunctions.j) {
                    DensityFunctions.j densityfunctions_j = (DensityFunctions.j) densityfunction;

                    return (DensityFunction) densityfunctions_j.function().value();
                } else if (densityfunction instanceof DensityFunctions.l) {
                    DensityFunctions.l densityfunctions_l = (DensityFunctions.l) densityfunction;

                    return densityfunctions_l.wrapped();
                } else {
                    return densityfunction;
                }
            }

            @Override
            public DensityFunction apply(DensityFunction densityfunction) {
                return (DensityFunction) this.wrapped.computeIfAbsent(densityfunction, this::wrapNew);
            }
        };

        this.sampler = new Climate.Sampler(this.router.temperature().mapAll(densityfunction_f), this.router.vegetation().mapAll(densityfunction_f), this.router.continents().mapAll(densityfunction_f), this.router.erosion().mapAll(densityfunction_f), this.router.depth().mapAll(densityfunction_f), this.router.ridges().mapAll(densityfunction_f), generatorsettingbase.spawnTarget());
    }

    public NoiseGeneratorNormal getOrCreateNoise(ResourceKey<NoiseGeneratorNormal.a> resourcekey) {
        return (NoiseGeneratorNormal) this.noiseIntances.computeIfAbsent(resourcekey, (resourcekey1) -> {
            return Noises.instantiate(this.noises, this.random, resourcekey);
        });
    }

    public PositionalRandomFactory getOrCreateRandomFactory(MinecraftKey minecraftkey) {
        return (PositionalRandomFactory) this.positionalRandoms.computeIfAbsent(minecraftkey, (minecraftkey1) -> {
            return this.random.fromHashOf(minecraftkey).forkPositional();
        });
    }

    public NoiseRouter router() {
        return this.router;
    }

    public Climate.Sampler sampler() {
        return this.sampler;
    }

    public SurfaceSystem surfaceSystem() {
        return this.surfaceSystem;
    }

    public PositionalRandomFactory aquiferRandom() {
        return this.aquiferRandom;
    }

    public PositionalRandomFactory oreRandom() {
        return this.oreRandom;
    }
}
