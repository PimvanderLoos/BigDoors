package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.MathHelper;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3Handler;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;

public class NoiseSampler implements Climate.Sampler {

    private static final float ORE_VEIN_RARITY = 1.0F;
    private static final float ORE_THICKNESS = 0.08F;
    private static final float VEININESS_THRESHOLD = 0.4F;
    private static final double VEININESS_FREQUENCY = 1.5D;
    private static final int EDGE_ROUNDOFF_BEGIN = 20;
    private static final double MAX_EDGE_ROUNDOFF = 0.2D;
    private static final float VEIN_SOLIDNESS = 0.7F;
    private static final float MIN_RICHNESS = 0.1F;
    private static final float MAX_RICHNESS = 0.3F;
    private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
    private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
    private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;
    private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5D;
    private final NoiseSettings noiseSettings;
    private final boolean isNoiseCavesEnabled;
    private final NoiseChunk.b baseNoise;
    private final BlendedNoise blendedNoise;
    @Nullable
    private final NoiseGenerator3Handler islandNoise;
    private final NoiseGeneratorNormal jaggedNoise;
    private final NoiseGeneratorNormal barrierNoise;
    private final NoiseGeneratorNormal fluidLevelFloodednessNoise;
    private final NoiseGeneratorNormal fluidLevelSpreadNoise;
    private final NoiseGeneratorNormal lavaNoise;
    private final NoiseGeneratorNormal layerNoiseSource;
    private final NoiseGeneratorNormal pillarNoiseSource;
    private final NoiseGeneratorNormal pillarRarenessModulator;
    private final NoiseGeneratorNormal pillarThicknessModulator;
    private final NoiseGeneratorNormal spaghetti2DNoiseSource;
    private final NoiseGeneratorNormal spaghetti2DElevationModulator;
    private final NoiseGeneratorNormal spaghetti2DRarityModulator;
    private final NoiseGeneratorNormal spaghetti2DThicknessModulator;
    private final NoiseGeneratorNormal spaghetti3DNoiseSource1;
    private final NoiseGeneratorNormal spaghetti3DNoiseSource2;
    private final NoiseGeneratorNormal spaghetti3DRarityModulator;
    private final NoiseGeneratorNormal spaghetti3DThicknessModulator;
    private final NoiseGeneratorNormal spaghettiRoughnessNoise;
    private final NoiseGeneratorNormal spaghettiRoughnessModulator;
    private final NoiseGeneratorNormal bigEntranceNoiseSource;
    private final NoiseGeneratorNormal cheeseNoiseSource;
    private final NoiseGeneratorNormal temperatureNoise;
    private final NoiseGeneratorNormal humidityNoise;
    private final NoiseGeneratorNormal continentalnessNoise;
    private final NoiseGeneratorNormal erosionNoise;
    private final NoiseGeneratorNormal weirdnessNoise;
    private final NoiseGeneratorNormal offsetNoise;
    private final NoiseGeneratorNormal gapNoise;
    private final NoiseChunk.b veininess;
    private final NoiseChunk.b veinA;
    private final NoiseChunk.b veinB;
    private final NoiseChunk.b noodleToggle;
    private final NoiseChunk.b noodleThickness;
    private final NoiseChunk.b noodleRidgeA;
    private final NoiseChunk.b noodleRidgeB;
    private final PositionalRandomFactory aquiferPositionalRandomFactory;
    private final PositionalRandomFactory oreVeinsPositionalRandomFactory;
    private final PositionalRandomFactory depthBasedLayerPositionalRandomFactory;
    private final List<Climate.d> spawnTarget = (new OverworldBiomeBuilder()).spawnTarget();
    private final boolean amplified;

    public NoiseSampler(NoiseSettings noisesettings, boolean flag, long i, IRegistry<NoiseGeneratorNormal.a> iregistry, SeededRandom.a seededrandom_a) {
        this.noiseSettings = noisesettings;
        this.isNoiseCavesEnabled = flag;
        this.baseNoise = (noisechunk) -> {
            return noisechunk.createNoiseInterpolator((j, k, l) -> {
                return this.calculateBaseNoise(j, k, l, noisechunk.noiseData(QuartPos.fromBlock(j), QuartPos.fromBlock(l)).terrainInfo(), noisechunk.getBlender());
            });
        };
        if (noisesettings.islandNoiseOverride()) {
            RandomSource randomsource = seededrandom_a.newInstance(i);

            randomsource.consumeCount(17292);
            this.islandNoise = new NoiseGenerator3Handler(randomsource);
        } else {
            this.islandNoise = null;
        }

        this.amplified = noisesettings.isAmplified();
        int j = noisesettings.minY();
        int k = Stream.of(NoiseSampler.c.values()).mapToInt((noisesampler_c) -> {
            return noisesampler_c.minY;
        }).min().orElse(j);
        int l = Stream.of(NoiseSampler.c.values()).mapToInt((noisesampler_c) -> {
            return noisesampler_c.maxY;
        }).max().orElse(j);
        float f = 4.0F;
        double d0 = 2.6666666666666665D;
        int i1 = j + 4;
        int j1 = j + noisesettings.height();
        boolean flag1 = noisesettings.largeBiomes();
        PositionalRandomFactory positionalrandomfactory = seededrandom_a.newInstance(i).forkPositional();

        if (seededrandom_a != SeededRandom.a.LEGACY) {
            this.blendedNoise = new BlendedNoise(positionalrandomfactory.fromHashOf(new MinecraftKey("terrain")), noisesettings.noiseSamplingSettings(), noisesettings.getCellWidth(), noisesettings.getCellHeight());
            this.temperatureNoise = Noises.instantiate(iregistry, positionalrandomfactory, flag1 ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE);
            this.humidityNoise = Noises.instantiate(iregistry, positionalrandomfactory, flag1 ? Noises.VEGETATION_LARGE : Noises.VEGETATION);
            this.offsetNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SHIFT);
        } else {
            this.blendedNoise = new BlendedNoise(seededrandom_a.newInstance(i), noisesettings.noiseSamplingSettings(), noisesettings.getCellWidth(), noisesettings.getCellHeight());
            this.temperatureNoise = NoiseGeneratorNormal.createLegacyNetherBiome(seededrandom_a.newInstance(i), new NoiseGeneratorNormal.a(-7, 1.0D, new double[]{1.0D}));
            this.humidityNoise = NoiseGeneratorNormal.createLegacyNetherBiome(seededrandom_a.newInstance(i + 1L), new NoiseGeneratorNormal.a(-7, 1.0D, new double[]{1.0D}));
            this.offsetNoise = NoiseGeneratorNormal.create(positionalrandomfactory.fromHashOf(Noises.SHIFT.location()), new NoiseGeneratorNormal.a(0, 0.0D, new double[0]));
        }

        this.aquiferPositionalRandomFactory = positionalrandomfactory.fromHashOf(new MinecraftKey("aquifer")).forkPositional();
        this.oreVeinsPositionalRandomFactory = positionalrandomfactory.fromHashOf(new MinecraftKey("ore")).forkPositional();
        this.depthBasedLayerPositionalRandomFactory = positionalrandomfactory.fromHashOf(new MinecraftKey("depth_based_layer")).forkPositional();
        this.barrierNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.AQUIFER_BARRIER);
        this.fluidLevelFloodednessNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS);
        this.lavaNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.AQUIFER_LAVA);
        this.fluidLevelSpreadNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.AQUIFER_FLUID_LEVEL_SPREAD);
        this.pillarNoiseSource = Noises.instantiate(iregistry, positionalrandomfactory, Noises.PILLAR);
        this.pillarRarenessModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.PILLAR_RARENESS);
        this.pillarThicknessModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.PILLAR_THICKNESS);
        this.spaghetti2DNoiseSource = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_2D);
        this.spaghetti2DElevationModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_2D_ELEVATION);
        this.spaghetti2DRarityModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_2D_MODULATOR);
        this.spaghetti2DThicknessModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_2D_THICKNESS);
        this.spaghetti3DNoiseSource1 = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_3D_1);
        this.spaghetti3DNoiseSource2 = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_3D_2);
        this.spaghetti3DRarityModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_3D_RARITY);
        this.spaghetti3DThicknessModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_3D_THICKNESS);
        this.spaghettiRoughnessNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_ROUGHNESS);
        this.spaghettiRoughnessModulator = Noises.instantiate(iregistry, positionalrandomfactory, Noises.SPAGHETTI_ROUGHNESS_MODULATOR);
        this.bigEntranceNoiseSource = Noises.instantiate(iregistry, positionalrandomfactory, Noises.CAVE_ENTRANCE);
        this.layerNoiseSource = Noises.instantiate(iregistry, positionalrandomfactory, Noises.CAVE_LAYER);
        this.cheeseNoiseSource = Noises.instantiate(iregistry, positionalrandomfactory, Noises.CAVE_CHEESE);
        this.continentalnessNoise = Noises.instantiate(iregistry, positionalrandomfactory, flag1 ? Noises.CONTINENTALNESS_LARGE : Noises.CONTINENTALNESS);
        this.erosionNoise = Noises.instantiate(iregistry, positionalrandomfactory, flag1 ? Noises.EROSION_LARGE : Noises.EROSION);
        this.weirdnessNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.RIDGE);
        this.veininess = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.ORE_VEININESS), k, l, 0, 1.5D);
        this.veinA = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.ORE_VEIN_A), k, l, 0, 4.0D);
        this.veinB = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.ORE_VEIN_B), k, l, 0, 4.0D);
        this.gapNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.ORE_GAP);
        this.noodleToggle = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.NOODLE), i1, j1, -1, 1.0D);
        this.noodleThickness = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.NOODLE_THICKNESS), i1, j1, 0, 1.0D);
        this.noodleRidgeA = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.NOODLE_RIDGE_A), i1, j1, 0, 2.6666666666666665D);
        this.noodleRidgeB = yLimitedInterpolatableNoise(Noises.instantiate(iregistry, positionalrandomfactory, Noises.NOODLE_RIDGE_B), i1, j1, 0, 2.6666666666666665D);
        this.jaggedNoise = Noises.instantiate(iregistry, positionalrandomfactory, Noises.JAGGED);
    }

    private static NoiseChunk.b yLimitedInterpolatableNoise(NoiseGeneratorNormal noisegeneratornormal, int i, int j, int k, double d0) {
        NoiseChunk.c noisechunk_c = (l, i1, j1) -> {
            return i1 <= j && i1 >= i ? noisegeneratornormal.getValue((double) l * d0, (double) i1 * d0, (double) j1 * d0) : (double) k;
        };

        return (noisechunk) -> {
            return noisechunk.createNoiseInterpolator(noisechunk_c);
        };
    }

    private double calculateBaseNoise(int i, int j, int k, TerrainInfo terraininfo, Blender blender) {
        double d0 = this.blendedNoise.calculateNoise(i, j, k);
        boolean flag = !this.isNoiseCavesEnabled;

        return this.calculateBaseNoise(i, j, k, terraininfo, d0, flag, true, blender);
    }

    private double calculateBaseNoise(int i, int j, int k, TerrainInfo terraininfo, double d0, boolean flag, boolean flag1, Blender blender) {
        double d1;
        double d2;
        double d3;

        if (this.islandNoise != null) {
            d1 = ((double) WorldChunkManagerTheEnd.getHeightValue(this.islandNoise, i / 8, k / 8) - 8.0D) / 128.0D;
        } else {
            d2 = flag1 ? this.sampleJaggedNoise(terraininfo.jaggedness(), (double) i, (double) k) : 0.0D;
            d3 = (this.computeBaseDensity(j, terraininfo) + d2) * terraininfo.factor();
            d1 = d3 * (double) (d3 > 0.0D ? 4 : 1);
        }

        d2 = d1 + d0;
        d3 = 1.5625D;
        double d4;
        double d5;
        double d6;
        double d7;

        if (!flag && d2 >= -64.0D) {
            d4 = d2 - 1.5625D;
            boolean flag2 = d4 < 0.0D;
            double d8 = this.getBigEntrances(i, j, k);
            double d9 = this.spaghettiRoughness(i, j, k);
            double d10 = this.getSpaghetti3D(i, j, k);
            double d11 = Math.min(d8, d10 + d9);

            if (flag2) {
                d5 = d2;
                d6 = d11 * 5.0D;
                d7 = -64.0D;
            } else {
                double d12 = this.getLayerizedCaverns(i, j, k);
                double d13;

                if (d12 > 64.0D) {
                    d5 = 64.0D;
                } else {
                    d13 = this.cheeseNoiseSource.getValue((double) i, (double) j / 1.5D, (double) k);
                    double d14 = MathHelper.clamp(d13 + 0.27D, -1.0D, 1.0D);
                    double d15 = d4 * 1.28D;
                    double d16 = d14 + MathHelper.clampedLerp(0.5D, 0.0D, d15);

                    d5 = d16 + d12;
                }

                d13 = this.getSpaghetti2D(i, j, k);
                d6 = Math.min(d11, d13 + d9);
                d7 = this.getPillars(i, j, k);
            }
        } else {
            d5 = d2;
            d6 = 64.0D;
            d7 = -64.0D;
        }

        d4 = Math.max(Math.min(d5, d6), d7);
        d4 = this.applySlide(d4, j / this.noiseSettings.getCellHeight());
        d4 = blender.blendDensity(i, j, k, d4);
        d4 = MathHelper.clamp(d4, -64.0D, 64.0D);
        return d4;
    }

    private double sampleJaggedNoise(double d0, double d1, double d2) {
        if (d0 == 0.0D) {
            return 0.0D;
        } else {
            float f = 1500.0F;
            double d3 = this.jaggedNoise.getValue(d1 * 1500.0D, 0.0D, d2 * 1500.0D);

            return d3 > 0.0D ? d0 * d3 : d0 / 2.0D * d3;
        }
    }

    private double computeBaseDensity(int i, TerrainInfo terraininfo) {
        double d0 = 1.0D - (double) i / 128.0D;

        return d0 + terraininfo.offset();
    }

    private double applySlide(double d0, int i) {
        int j = i - this.noiseSettings.getMinCellY();

        d0 = this.noiseSettings.topSlideSettings().applySlide(d0, this.noiseSettings.getCellCountY() - j);
        d0 = this.noiseSettings.bottomSlideSettings().applySlide(d0, j);
        return d0;
    }

    protected NoiseChunk.a makeBaseNoiseFiller(NoiseChunk noisechunk, NoiseChunk.c noisechunk_c, boolean flag) {
        NoiseChunk.e noisechunk_e = this.baseNoise.instantiate(noisechunk);
        NoiseChunk.e noisechunk_e1 = flag ? this.noodleToggle.instantiate(noisechunk) : () -> {
            return -1.0D;
        };
        NoiseChunk.e noisechunk_e2 = flag ? this.noodleThickness.instantiate(noisechunk) : () -> {
            return 0.0D;
        };
        NoiseChunk.e noisechunk_e3 = flag ? this.noodleRidgeA.instantiate(noisechunk) : () -> {
            return 0.0D;
        };
        NoiseChunk.e noisechunk_e4 = flag ? this.noodleRidgeB.instantiate(noisechunk) : () -> {
            return 0.0D;
        };

        return (i, j, k) -> {
            double d0 = noisechunk_e.sample();
            double d1 = MathHelper.clamp(d0 * 0.64D, -1.0D, 1.0D);

            d1 = d1 / 2.0D - d1 * d1 * d1 / 24.0D;
            if (noisechunk_e1.sample() >= 0.0D) {
                double d2 = 0.05D;
                double d3 = 0.1D;
                double d4 = MathHelper.clampedMap(noisechunk_e2.sample(), -1.0D, 1.0D, 0.05D, 0.1D);
                double d5 = Math.abs(1.5D * noisechunk_e3.sample()) - d4;
                double d6 = Math.abs(1.5D * noisechunk_e4.sample()) - d4;

                d1 = Math.min(d1, Math.max(d5, d6));
            }

            d1 += noisechunk_c.calculateNoise(i, j, k);
            return noisechunk.aquifer().computeSubstance(i, j, k, d0, d1);
        };
    }

    protected NoiseChunk.a makeOreVeinifier(NoiseChunk noisechunk, boolean flag) {
        if (!flag) {
            return (i, j, k) -> {
                return null;
            };
        } else {
            NoiseChunk.e noisechunk_e = this.veininess.instantiate(noisechunk);
            NoiseChunk.e noisechunk_e1 = this.veinA.instantiate(noisechunk);
            NoiseChunk.e noisechunk_e2 = this.veinB.instantiate(noisechunk);
            Object object = null;

            return (i, j, k) -> {
                RandomSource randomsource = this.oreVeinsPositionalRandomFactory.at(i, j, k);
                double d0 = noisechunk_e.sample();
                NoiseSampler.c noisesampler_c = this.getVeinType(d0, j);

                if (noisesampler_c == null) {
                    return object;
                } else if (randomsource.nextFloat() > 0.7F) {
                    return object;
                } else if (this.isVein(noisechunk_e1.sample(), noisechunk_e2.sample())) {
                    double d1 = MathHelper.clampedMap(Math.abs(d0), 0.4000000059604645D, 0.6000000238418579D, 0.10000000149011612D, 0.30000001192092896D);

                    return (double) randomsource.nextFloat() < d1 && this.gapNoise.getValue((double) i, (double) j, (double) k) > -0.30000001192092896D ? (randomsource.nextFloat() < 0.02F ? noisesampler_c.rawOreBlock : noisesampler_c.ore) : noisesampler_c.filler;
                } else {
                    return object;
                }
            };
        }
    }

    protected int getPreliminarySurfaceLevel(int i, int j, TerrainInfo terraininfo) {
        for (int k = this.noiseSettings.getMinCellY() + this.noiseSettings.getCellCountY(); k >= this.noiseSettings.getMinCellY(); --k) {
            int l = k * this.noiseSettings.getCellHeight();
            double d0 = -0.703125D;
            double d1 = this.calculateBaseNoise(i, l, j, terraininfo, -0.703125D, true, false, Blender.empty());

            if (d1 > 0.390625D) {
                return l;
            }
        }

        return Integer.MAX_VALUE;
    }

    protected Aquifer createAquifer(NoiseChunk noisechunk, int i, int j, int k, int l, Aquifer.a aquifer_a, boolean flag) {
        if (!flag) {
            return Aquifer.createDisabled(aquifer_a);
        } else {
            int i1 = SectionPosition.blockToSectionCoord(i);
            int j1 = SectionPosition.blockToSectionCoord(j);

            return Aquifer.create(noisechunk, new ChunkCoordIntPair(i1, j1), this.barrierNoise, this.fluidLevelFloodednessNoise, this.fluidLevelSpreadNoise, this.lavaNoise, this.aquiferPositionalRandomFactory, k * this.noiseSettings.getCellHeight(), l * this.noiseSettings.getCellHeight(), aquifer_a);
        }
    }

    @VisibleForDebug
    public NoiseSampler.a noiseData(int i, int j, Blender blender) {
        double d0 = (double) i + this.getOffset(i, 0, j);
        double d1 = (double) j + this.getOffset(j, i, 0);
        double d2 = this.getContinentalness(d0, 0.0D, d1);
        double d3 = this.getWeirdness(d0, 0.0D, d1);
        double d4 = this.getErosion(d0, 0.0D, d1);
        TerrainInfo terraininfo = this.terrainInfo(QuartPos.toBlock(i), QuartPos.toBlock(j), (float) d2, (float) d3, (float) d4, blender);

        return new NoiseSampler.a(d0, d1, d2, d3, d4, terraininfo);
    }

    @Override
    public Climate.h sample(int i, int j, int k) {
        return this.target(i, j, k, this.noiseData(i, k, Blender.empty()));
    }

    @VisibleForDebug
    public Climate.h target(int i, int j, int k, NoiseSampler.a noisesampler_a) {
        double d0 = noisesampler_a.shiftedX();
        double d1 = (double) j + this.getOffset(j, k, i);
        double d2 = noisesampler_a.shiftedZ();
        double d3 = this.computeBaseDensity(QuartPos.toBlock(j), noisesampler_a.terrainInfo());

        return Climate.target((float) this.getTemperature(d0, d1, d2), (float) this.getHumidity(d0, d1, d2), (float) noisesampler_a.continentalness(), (float) noisesampler_a.erosion(), (float) d3, (float) noisesampler_a.weirdness());
    }

    public TerrainInfo terrainInfo(int i, int j, float f, float f1, float f2, Blender blender) {
        TerrainShaper terrainshaper = this.noiseSettings.terrainShaper();
        TerrainShaper.b terrainshaper_b = terrainshaper.makePoint(f, f2, f1);
        float f3 = terrainshaper.offset(terrainshaper_b);
        float f4 = terrainshaper.factor(terrainshaper_b);
        float f5 = terrainshaper.jaggedness(terrainshaper_b);
        TerrainInfo terraininfo = new TerrainInfo((double) f3, (double) f4, (double) f5);

        return blender.blendOffsetAndFactor(i, j, terraininfo);
    }

    @Override
    public BlockPosition findSpawnPosition() {
        return Climate.findSpawnPosition(this.spawnTarget, this);
    }

    @VisibleForDebug
    public double getOffset(int i, int j, int k) {
        return this.offsetNoise.getValue((double) i, (double) j, (double) k) * 4.0D;
    }

    private double getTemperature(double d0, double d1, double d2) {
        return this.temperatureNoise.getValue(d0, 0.0D, d2);
    }

    private double getHumidity(double d0, double d1, double d2) {
        return this.humidityNoise.getValue(d0, 0.0D, d2);
    }

    @VisibleForDebug
    public double getContinentalness(double d0, double d1, double d2) {
        double d3;

        if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
            if (SharedConstants.debugVoidTerrain(new ChunkCoordIntPair(QuartPos.toSection(MathHelper.floor(d0)), QuartPos.toSection(MathHelper.floor(d2))))) {
                return -1.0D;
            } else {
                d3 = MathHelper.frac(d0 / 2048.0D) * 2.0D - 1.0D;
                return d3 * d3 * (double) (d3 < 0.0D ? -1 : 1);
            }
        } else if (SharedConstants.debugGenerateStripedTerrainWithoutNoise) {
            d3 = d0 * 0.005D;
            return Math.sin(d3 + 0.5D * Math.sin(d3));
        } else {
            return this.continentalnessNoise.getValue(d0, d1, d2);
        }
    }

    @VisibleForDebug
    public double getErosion(double d0, double d1, double d2) {
        double d3;

        if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
            if (SharedConstants.debugVoidTerrain(new ChunkCoordIntPair(QuartPos.toSection(MathHelper.floor(d0)), QuartPos.toSection(MathHelper.floor(d2))))) {
                return -1.0D;
            } else {
                d3 = MathHelper.frac(d2 / 256.0D) * 2.0D - 1.0D;
                return d3 * d3 * (double) (d3 < 0.0D ? -1 : 1);
            }
        } else if (SharedConstants.debugGenerateStripedTerrainWithoutNoise) {
            d3 = d2 * 0.005D;
            return Math.sin(d3 + 0.5D * Math.sin(d3));
        } else {
            return this.erosionNoise.getValue(d0, d1, d2);
        }
    }

    @VisibleForDebug
    public double getWeirdness(double d0, double d1, double d2) {
        return this.weirdnessNoise.getValue(d0, d1, d2);
    }

    private double getBigEntrances(int i, int j, int k) {
        double d0 = 0.75D;
        double d1 = 0.5D;
        double d2 = 0.37D;
        double d3 = this.bigEntranceNoiseSource.getValue((double) i * 0.75D, (double) j * 0.5D, (double) k * 0.75D) + 0.37D;
        boolean flag = true;
        double d4 = (double) (j - -10) / 40.0D;
        double d5 = 0.3D;

        return d3 + MathHelper.clampedLerp(0.3D, 0.0D, d4);
    }

    private double getPillars(int i, int j, int k) {
        double d0 = 0.0D;
        double d1 = 2.0D;
        double d2 = NoiseUtils.sampleNoiseAndMapToRange(this.pillarRarenessModulator, (double) i, (double) j, (double) k, 0.0D, 2.0D);
        double d3 = 0.0D;
        double d4 = 1.1D;
        double d5 = NoiseUtils.sampleNoiseAndMapToRange(this.pillarThicknessModulator, (double) i, (double) j, (double) k, 0.0D, 1.1D);

        d5 = Math.pow(d5, 3.0D);
        double d6 = 25.0D;
        double d7 = 0.3D;
        double d8 = this.pillarNoiseSource.getValue((double) i * 25.0D, (double) j * 0.3D, (double) k * 25.0D);

        d8 = d5 * (d8 * 2.0D - d2);
        return d8 > 0.03D ? d8 : Double.NEGATIVE_INFINITY;
    }

    private double getLayerizedCaverns(int i, int j, int k) {
        double d0 = this.layerNoiseSource.getValue((double) i, (double) (j * 8), (double) k);

        return MathHelper.square(d0) * 4.0D;
    }

    private double getSpaghetti3D(int i, int j, int k) {
        double d0 = this.spaghetti3DRarityModulator.getValue((double) (i * 2), (double) j, (double) (k * 2));
        double d1 = NoiseSampler.b.getSpaghettiRarity3D(d0);
        double d2 = 0.065D;
        double d3 = 0.088D;
        double d4 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti3DThicknessModulator, (double) i, (double) j, (double) k, 0.065D, 0.088D);
        double d5 = sampleWithRarity(this.spaghetti3DNoiseSource1, (double) i, (double) j, (double) k, d1);
        double d6 = Math.abs(d1 * d5) - d4;
        double d7 = sampleWithRarity(this.spaghetti3DNoiseSource2, (double) i, (double) j, (double) k, d1);
        double d8 = Math.abs(d1 * d7) - d4;

        return clampToUnit(Math.max(d6, d8));
    }

    private double getSpaghetti2D(int i, int j, int k) {
        double d0 = this.spaghetti2DRarityModulator.getValue((double) (i * 2), (double) j, (double) (k * 2));
        double d1 = NoiseSampler.b.getSphaghettiRarity2D(d0);
        double d2 = 0.6D;
        double d3 = 1.3D;
        double d4 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2DThicknessModulator, (double) (i * 2), (double) j, (double) (k * 2), 0.6D, 1.3D);
        double d5 = sampleWithRarity(this.spaghetti2DNoiseSource, (double) i, (double) j, (double) k, d1);
        double d6 = 0.083D;
        double d7 = Math.abs(d1 * d5) - 0.083D * d4;
        int l = this.noiseSettings.getMinCellY();
        boolean flag = true;
        double d8 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2DElevationModulator, (double) i, 0.0D, (double) k, (double) l, 8.0D);
        double d9 = Math.abs(d8 - (double) j / 8.0D) - 1.0D * d4;

        d9 = d9 * d9 * d9;
        return clampToUnit(Math.max(d9, d7));
    }

    private double spaghettiRoughness(int i, int j, int k) {
        double d0 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghettiRoughnessModulator, (double) i, (double) j, (double) k, 0.0D, 0.1D);

        return (0.4D - Math.abs(this.spaghettiRoughnessNoise.getValue((double) i, (double) j, (double) k))) * d0;
    }

    public PositionalRandomFactory getDepthBasedLayerPositionalRandom() {
        return this.depthBasedLayerPositionalRandomFactory;
    }

    private static double clampToUnit(double d0) {
        return MathHelper.clamp(d0, -1.0D, 1.0D);
    }

    private static double sampleWithRarity(NoiseGeneratorNormal noisegeneratornormal, double d0, double d1, double d2, double d3) {
        return noisegeneratornormal.getValue(d0 / d3, d1 / d3, d2 / d3);
    }

    private boolean isVein(double d0, double d1) {
        double d2 = Math.abs(1.0D * d0) - 0.07999999821186066D;
        double d3 = Math.abs(1.0D * d1) - 0.07999999821186066D;

        return Math.max(d2, d3) < 0.0D;
    }

    @Nullable
    private NoiseSampler.c getVeinType(double d0, int i) {
        NoiseSampler.c noisesampler_c = d0 > 0.0D ? NoiseSampler.c.COPPER : NoiseSampler.c.IRON;
        int j = noisesampler_c.maxY - i;
        int k = i - noisesampler_c.minY;

        if (k >= 0 && j >= 0) {
            int l = Math.min(j, k);
            double d1 = MathHelper.clampedMap((double) l, 0.0D, 20.0D, -0.2D, 0.0D);

            return Math.abs(d0) + d1 < 0.4000000059604645D ? null : noisesampler_c;
        } else {
            return null;
        }
    }

    private static enum c {

        COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50), IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

        final IBlockData ore;
        final IBlockData rawOreBlock;
        final IBlockData filler;
        final int minY;
        final int maxY;

        private c(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, int i, int j) {
            this.ore = iblockdata;
            this.rawOreBlock = iblockdata1;
            this.filler = iblockdata2;
            this.minY = i;
            this.maxY = j;
        }
    }

    public static record a(double a, double b, double c, double d, double e, TerrainInfo f) {

        private final double shiftedX;
        private final double shiftedZ;
        private final double continentalness;
        private final double weirdness;
        private final double erosion;
        private final TerrainInfo terrainInfo;

        public a(double d0, double d1, double d2, double d3, double d4, TerrainInfo terraininfo) {
            this.shiftedX = d0;
            this.shiftedZ = d1;
            this.continentalness = d2;
            this.weirdness = d3;
            this.erosion = d4;
            this.terrainInfo = terraininfo;
        }

        public double shiftedX() {
            return this.shiftedX;
        }

        public double shiftedZ() {
            return this.shiftedZ;
        }

        public double continentalness() {
            return this.continentalness;
        }

        public double weirdness() {
            return this.weirdness;
        }

        public double erosion() {
            return this.erosion;
        }

        public TerrainInfo terrainInfo() {
            return this.terrainInfo;
        }
    }

    private static final class b {

        private b() {}

        static double getSphaghettiRarity2D(double d0) {
            return d0 < -0.75D ? 0.5D : (d0 < -0.5D ? 0.75D : (d0 < 0.5D ? 1.0D : (d0 < 0.75D ? 2.0D : 3.0D)));
        }

        static double getSpaghettiRarity3D(double d0) {
            return d0 < -0.5D ? 0.75D : (d0 < 0.0D ? 1.0D : (d0 < 0.5D ? 1.5D : 2.0D));
        }
    }
}
