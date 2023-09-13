package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;

public final class OverworldBiomeBuilder {

    private static final float VALLEY_SIZE = 0.05F;
    private static final float LOW_START = 0.26666668F;
    public static final float HIGH_START = 0.4F;
    private static final float HIGH_END = 0.93333334F;
    private static final float PEAK_SIZE = 0.1F;
    public static final float PEAK_START = 0.56666666F;
    private static final float PEAK_END = 0.7666667F;
    public static final float NEAR_INLAND_START = -0.11F;
    public static final float MID_INLAND_START = 0.03F;
    public static final float FAR_INLAND_START = 0.3F;
    public static final float EROSION_INDEX_1_START = -0.78F;
    public static final float EROSION_INDEX_2_START = -0.375F;
    private final Climate.b FULL_RANGE = Climate.b.span(-1.0F, 1.0F);
    private final Climate.b[] temperatures = new Climate.b[]{Climate.b.span(-1.0F, -0.45F), Climate.b.span(-0.45F, -0.15F), Climate.b.span(-0.15F, 0.2F), Climate.b.span(0.2F, 0.55F), Climate.b.span(0.55F, 1.0F)};
    private final Climate.b[] humidities = new Climate.b[]{Climate.b.span(-1.0F, -0.35F), Climate.b.span(-0.35F, -0.1F), Climate.b.span(-0.1F, 0.1F), Climate.b.span(0.1F, 0.3F), Climate.b.span(0.3F, 1.0F)};
    private final Climate.b[] erosions = new Climate.b[]{Climate.b.span(-1.0F, -0.78F), Climate.b.span(-0.78F, -0.375F), Climate.b.span(-0.375F, -0.2225F), Climate.b.span(-0.2225F, 0.05F), Climate.b.span(0.05F, 0.45F), Climate.b.span(0.45F, 0.55F), Climate.b.span(0.55F, 1.0F)};
    private final Climate.b FROZEN_RANGE;
    private final Climate.b UNFROZEN_RANGE;
    private final Climate.b mushroomFieldsContinentalness;
    private final Climate.b deepOceanContinentalness;
    private final Climate.b oceanContinentalness;
    private final Climate.b coastContinentalness;
    private final Climate.b inlandContinentalness;
    private final Climate.b nearInlandContinentalness;
    private final Climate.b midInlandContinentalness;
    private final Climate.b farInlandContinentalness;
    private final ResourceKey<BiomeBase>[][] OCEANS;
    private final ResourceKey<BiomeBase>[][] MIDDLE_BIOMES;
    private final ResourceKey<BiomeBase>[][] MIDDLE_BIOMES_VARIANT;
    private final ResourceKey<BiomeBase>[][] PLATEAU_BIOMES;
    private final ResourceKey<BiomeBase>[][] PLATEAU_BIOMES_VARIANT;
    private final ResourceKey<BiomeBase>[][] EXTREME_HILLS;

    public OverworldBiomeBuilder() {
        this.FROZEN_RANGE = this.temperatures[0];
        this.UNFROZEN_RANGE = Climate.b.span(this.temperatures[1], this.temperatures[4]);
        this.mushroomFieldsContinentalness = Climate.b.span(-1.2F, -1.05F);
        this.deepOceanContinentalness = Climate.b.span(-1.05F, -0.455F);
        this.oceanContinentalness = Climate.b.span(-0.455F, -0.19F);
        this.coastContinentalness = Climate.b.span(-0.19F, -0.11F);
        this.inlandContinentalness = Climate.b.span(-0.11F, 0.55F);
        this.nearInlandContinentalness = Climate.b.span(-0.11F, 0.03F);
        this.midInlandContinentalness = Climate.b.span(0.03F, 0.3F);
        this.farInlandContinentalness = Climate.b.span(0.3F, 1.0F);
        this.OCEANS = new ResourceKey[][]{{Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN}, {Biomes.FROZEN_OCEAN, Biomes.COLD_OCEAN, Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN}};
        this.MIDDLE_BIOMES = new ResourceKey[][]{{Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.TAIGA}, {Biomes.PLAINS, Biomes.PLAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA}, {Biomes.FLOWER_FOREST, Biomes.PLAINS, Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST}, {Biomes.SAVANNA, Biomes.SAVANNA, Biomes.FOREST, Biomes.JUNGLE, Biomes.JUNGLE}, {Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.DESERT}};
        this.MIDDLE_BIOMES_VARIANT = new ResourceKey[][]{{Biomes.ICE_SPIKES, null, Biomes.SNOWY_TAIGA, null, null}, {null, null, null, null, Biomes.OLD_GROWTH_PINE_TAIGA}, {Biomes.SUNFLOWER_PLAINS, null, null, Biomes.OLD_GROWTH_BIRCH_FOREST, null}, {null, null, Biomes.PLAINS, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE}, {null, null, null, null, null}};
        this.PLATEAU_BIOMES = new ResourceKey[][]{{Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA}, {Biomes.MEADOW, Biomes.MEADOW, Biomes.FOREST, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA}, {Biomes.MEADOW, Biomes.MEADOW, Biomes.MEADOW, Biomes.MEADOW, Biomes.DARK_FOREST}, {Biomes.SAVANNA_PLATEAU, Biomes.SAVANNA_PLATEAU, Biomes.FOREST, Biomes.FOREST, Biomes.JUNGLE}, {Biomes.BADLANDS, Biomes.BADLANDS, Biomes.BADLANDS, Biomes.WOODED_BADLANDS, Biomes.WOODED_BADLANDS}};
        this.PLATEAU_BIOMES_VARIANT = new ResourceKey[][]{{Biomes.ICE_SPIKES, null, null, null, null}, {null, null, Biomes.MEADOW, Biomes.MEADOW, Biomes.OLD_GROWTH_PINE_TAIGA}, {null, null, Biomes.FOREST, Biomes.BIRCH_FOREST, null}, {null, null, null, null, null}, {Biomes.ERODED_BADLANDS, Biomes.ERODED_BADLANDS, null, null, null}};
        this.EXTREME_HILLS = new ResourceKey[][]{{Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST}, {Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST}, {Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST}, {null, null, null, null, null}, {null, null, null, null, null}};
    }

    public List<Climate.d> spawnTarget() {
        Climate.b climate_b = Climate.b.point(0.0F);
        float f = 0.16F;

        return List.of(new Climate.d(this.FULL_RANGE, this.FULL_RANGE, Climate.b.span(this.inlandContinentalness, this.FULL_RANGE), this.FULL_RANGE, climate_b, Climate.b.span(-1.0F, -0.16F), 0L), new Climate.d(this.FULL_RANGE, this.FULL_RANGE, Climate.b.span(this.inlandContinentalness, this.FULL_RANGE), this.FULL_RANGE, climate_b, Climate.b.span(0.16F, 1.0F), 0L));
    }

    protected void addBiomes(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer) {
        if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
            TerrainProvider.overworld(false).addDebugBiomesToVisualizeSplinePoints(consumer);
        } else {
            this.addOffCoastBiomes(consumer);
            this.addInlandBiomes(consumer);
            this.addUndergroundBiomes(consumer);
        }
    }

    private void addOffCoastBiomes(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer) {
        this.addSurfaceBiome(consumer, this.FULL_RANGE, this.FULL_RANGE, this.mushroomFieldsContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.MUSHROOM_FIELDS);

        for (int i = 0; i < this.temperatures.length; ++i) {
            Climate.b climate_b = this.temperatures[i];

            this.addSurfaceBiome(consumer, climate_b, this.FULL_RANGE, this.deepOceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[0][i]);
            this.addSurfaceBiome(consumer, climate_b, this.FULL_RANGE, this.oceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[1][i]);
        }

    }

    private void addInlandBiomes(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer) {
        this.addMidSlice(consumer, Climate.b.span(-1.0F, -0.93333334F));
        this.addHighSlice(consumer, Climate.b.span(-0.93333334F, -0.7666667F));
        this.addPeaks(consumer, Climate.b.span(-0.7666667F, -0.56666666F));
        this.addHighSlice(consumer, Climate.b.span(-0.56666666F, -0.4F));
        this.addMidSlice(consumer, Climate.b.span(-0.4F, -0.26666668F));
        this.addLowSlice(consumer, Climate.b.span(-0.26666668F, -0.05F));
        this.addValleys(consumer, Climate.b.span(-0.05F, 0.05F));
        this.addLowSlice(consumer, Climate.b.span(0.05F, 0.26666668F));
        this.addMidSlice(consumer, Climate.b.span(0.26666668F, 0.4F));
        this.addHighSlice(consumer, Climate.b.span(0.4F, 0.56666666F));
        this.addPeaks(consumer, Climate.b.span(0.56666666F, 0.7666667F));
        this.addHighSlice(consumer, Climate.b.span(0.7666667F, 0.93333334F));
        this.addMidSlice(consumer, Climate.b.span(0.93333334F, 1.0F));
    }

    private void addPeaks(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b) {
        for (int i = 0; i < this.temperatures.length; ++i) {
            Climate.b climate_b1 = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j) {
                Climate.b climate_b2 = this.humidities[j];
                ResourceKey<BiomeBase> resourcekey = this.pickMiddleBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey1 = this.pickMiddleBiomeOrBadlandsIfHot(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey2 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey3 = this.pickPlateauBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey4 = this.pickExtremeHillsBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey5 = this.maybePickShatteredBiome(i, j, climate_b, resourcekey4);
                ResourceKey<BiomeBase> resourcekey6 = this.pickPeakBiome(i, j, climate_b);

                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[0], climate_b, 0.0F, resourcekey6);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[1], climate_b, 0.0F, resourcekey2);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], climate_b, 0.0F, resourcekey6);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.b.span(this.erosions[2], this.erosions[3]), climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], climate_b, 0.0F, resourcekey3);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.midInlandContinentalness, this.erosions[3], climate_b, 0.0F, resourcekey1);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.farInlandContinentalness, this.erosions[3], climate_b, 0.0F, resourcekey3);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], climate_b, 0.0F, resourcekey5);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], climate_b, 0.0F, resourcekey4);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, resourcekey);
            }
        }

    }

    private void addHighSlice(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b) {
        for (int i = 0; i < this.temperatures.length; ++i) {
            Climate.b climate_b1 = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j) {
                Climate.b climate_b2 = this.humidities[j];
                ResourceKey<BiomeBase> resourcekey = this.pickMiddleBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey1 = this.pickMiddleBiomeOrBadlandsIfHot(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey2 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey3 = this.pickPlateauBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey4 = this.pickExtremeHillsBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey5 = this.maybePickShatteredBiome(i, j, climate_b, resourcekey);
                ResourceKey<BiomeBase> resourcekey6 = this.pickSlopeBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey7 = this.pickPeakBiome(i, j, climate_b);

                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, this.erosions[0], climate_b, 0.0F, resourcekey6);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[0], climate_b, 0.0F, resourcekey7);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, this.erosions[1], climate_b, 0.0F, resourcekey2);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], climate_b, 0.0F, resourcekey6);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.b.span(this.erosions[2], this.erosions[3]), climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], climate_b, 0.0F, resourcekey3);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.midInlandContinentalness, this.erosions[3], climate_b, 0.0F, resourcekey1);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.farInlandContinentalness, this.erosions[3], climate_b, 0.0F, resourcekey3);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], climate_b, 0.0F, resourcekey5);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], climate_b, 0.0F, resourcekey4);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, resourcekey);
            }
        }

    }

    private void addMidSlice(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b) {
        this.addSurfaceBiome(consumer, this.FULL_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.b.span(this.erosions[0], this.erosions[2]), climate_b, 0.0F, Biomes.STONY_SHORE);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, Biomes.SWAMP);

        for (int i = 0; i < this.temperatures.length; ++i) {
            Climate.b climate_b1 = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j) {
                Climate.b climate_b2 = this.humidities[j];
                ResourceKey<BiomeBase> resourcekey = this.pickMiddleBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey1 = this.pickMiddleBiomeOrBadlandsIfHot(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey2 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey3 = this.pickExtremeHillsBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey4 = this.pickPlateauBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey5 = this.pickBeachBiome(i, j);
                ResourceKey<BiomeBase> resourcekey6 = this.maybePickShatteredBiome(i, j, climate_b, resourcekey);
                ResourceKey<BiomeBase> resourcekey7 = this.pickShatteredCoastBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey8 = this.pickSlopeBiome(i, j, climate_b);

                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[0], climate_b, 0.0F, resourcekey8);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosions[1], climate_b, 0.0F, resourcekey2);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.farInlandContinentalness, this.erosions[1], climate_b, 0.0F, i == 0 ? resourcekey8 : resourcekey4);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, this.erosions[2], climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.midInlandContinentalness, this.erosions[2], climate_b, 0.0F, resourcekey1);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.farInlandContinentalness, this.erosions[2], climate_b, 0.0F, resourcekey4);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[3], climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[3], climate_b, 0.0F, resourcekey1);
                if (climate_b.max() < 0L) {
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, this.erosions[4], climate_b, 0.0F, resourcekey5);
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], climate_b, 0.0F, resourcekey);
                } else {
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], climate_b, 0.0F, resourcekey);
                }

                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, this.erosions[5], climate_b, 0.0F, resourcekey7);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, this.erosions[5], climate_b, 0.0F, resourcekey6);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], climate_b, 0.0F, resourcekey3);
                if (climate_b.max() < 0L) {
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, this.erosions[6], climate_b, 0.0F, resourcekey5);
                } else {
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, this.erosions[6], climate_b, 0.0F, resourcekey);
                }

                if (i == 0) {
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, resourcekey);
                }
            }
        }

    }

    private void addLowSlice(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b) {
        this.addSurfaceBiome(consumer, this.FULL_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.b.span(this.erosions[0], this.erosions[2]), climate_b, 0.0F, Biomes.STONY_SHORE);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, Biomes.SWAMP);

        for (int i = 0; i < this.temperatures.length; ++i) {
            Climate.b climate_b1 = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j) {
                Climate.b climate_b2 = this.humidities[j];
                ResourceKey<BiomeBase> resourcekey = this.pickMiddleBiome(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey1 = this.pickMiddleBiomeOrBadlandsIfHot(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey2 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, climate_b);
                ResourceKey<BiomeBase> resourcekey3 = this.pickBeachBiome(i, j);
                ResourceKey<BiomeBase> resourcekey4 = this.maybePickShatteredBiome(i, j, climate_b, resourcekey);
                ResourceKey<BiomeBase> resourcekey5 = this.pickShatteredCoastBiome(i, j, climate_b);

                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, resourcekey1);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, resourcekey2);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, Climate.b.span(this.erosions[2], this.erosions[3]), climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.b.span(this.erosions[2], this.erosions[3]), climate_b, 0.0F, resourcekey1);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, Climate.b.span(this.erosions[3], this.erosions[4]), climate_b, 0.0F, resourcekey3);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, this.erosions[5], climate_b, 0.0F, resourcekey5);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.nearInlandContinentalness, this.erosions[5], climate_b, 0.0F, resourcekey4);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], climate_b, 0.0F, resourcekey);
                this.addSurfaceBiome(consumer, climate_b1, climate_b2, this.coastContinentalness, this.erosions[6], climate_b, 0.0F, resourcekey3);
                if (i == 0) {
                    this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, resourcekey);
                }
            }
        }

    }

    private void addValleys(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b) {
        this.addSurfaceBiome(consumer, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, climate_b.max() < 0L ? Biomes.STONY_SHORE : Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, climate_b.max() < 0L ? Biomes.STONY_SHORE : Biomes.RIVER);
        this.addSurfaceBiome(consumer, this.FROZEN_RANGE, this.FULL_RANGE, this.nearInlandContinentalness, Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, this.nearInlandContinentalness, Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, Biomes.RIVER);
        this.addSurfaceBiome(consumer, this.FROZEN_RANGE, this.FULL_RANGE, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), Climate.b.span(this.erosions[2], this.erosions[5]), climate_b, 0.0F, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, Climate.b.span(this.coastContinentalness, this.farInlandContinentalness), Climate.b.span(this.erosions[2], this.erosions[5]), climate_b, 0.0F, Biomes.RIVER);
        this.addSurfaceBiome(consumer, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], climate_b, 0.0F, Biomes.FROZEN_RIVER);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], climate_b, 0.0F, Biomes.RIVER);
        this.addSurfaceBiome(consumer, this.UNFROZEN_RANGE, this.FULL_RANGE, Climate.b.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, Biomes.SWAMP);
        this.addSurfaceBiome(consumer, this.FROZEN_RANGE, this.FULL_RANGE, Climate.b.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], climate_b, 0.0F, Biomes.FROZEN_RIVER);

        for (int i = 0; i < this.temperatures.length; ++i) {
            Climate.b climate_b1 = this.temperatures[i];

            for (int j = 0; j < this.humidities.length; ++j) {
                Climate.b climate_b2 = this.humidities[j];
                ResourceKey<BiomeBase> resourcekey = this.pickMiddleBiomeOrBadlandsIfHot(i, j, climate_b);

                this.addSurfaceBiome(consumer, climate_b1, climate_b2, Climate.b.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.b.span(this.erosions[0], this.erosions[1]), climate_b, 0.0F, resourcekey);
            }
        }

    }

    private void addUndergroundBiomes(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer) {
        this.addUndergroundBiome(consumer, this.FULL_RANGE, this.FULL_RANGE, Climate.b.span(0.8F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.DRIPSTONE_CAVES);
        this.addUndergroundBiome(consumer, this.FULL_RANGE, Climate.b.span(0.7F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.LUSH_CAVES);
    }

    private ResourceKey<BiomeBase> pickMiddleBiome(int i, int j, Climate.b climate_b) {
        if (climate_b.max() < 0L) {
            return this.MIDDLE_BIOMES[i][j];
        } else {
            ResourceKey<BiomeBase> resourcekey = this.MIDDLE_BIOMES_VARIANT[i][j];

            return resourcekey == null ? this.MIDDLE_BIOMES[i][j] : resourcekey;
        }
    }

    private ResourceKey<BiomeBase> pickMiddleBiomeOrBadlandsIfHot(int i, int j, Climate.b climate_b) {
        return i == 4 ? this.pickBadlandsBiome(j, climate_b) : this.pickMiddleBiome(i, j, climate_b);
    }

    private ResourceKey<BiomeBase> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(int i, int j, Climate.b climate_b) {
        return i == 0 ? this.pickSlopeBiome(i, j, climate_b) : this.pickMiddleBiomeOrBadlandsIfHot(i, j, climate_b);
    }

    private ResourceKey<BiomeBase> maybePickShatteredBiome(int i, int j, Climate.b climate_b, ResourceKey<BiomeBase> resourcekey) {
        return i > 1 && j < 4 && climate_b.max() >= 0L ? Biomes.WINDSWEPT_SAVANNA : resourcekey;
    }

    private ResourceKey<BiomeBase> pickShatteredCoastBiome(int i, int j, Climate.b climate_b) {
        ResourceKey<BiomeBase> resourcekey = climate_b.max() >= 0L ? this.pickMiddleBiome(i, j, climate_b) : this.pickBeachBiome(i, j);

        return this.maybePickShatteredBiome(i, j, climate_b, resourcekey);
    }

    private ResourceKey<BiomeBase> pickBeachBiome(int i, int j) {
        return i == 0 ? Biomes.SNOWY_BEACH : (i == 4 ? Biomes.DESERT : Biomes.BEACH);
    }

    private ResourceKey<BiomeBase> pickBadlandsBiome(int i, Climate.b climate_b) {
        return i < 2 ? (climate_b.max() < 0L ? Biomes.ERODED_BADLANDS : Biomes.BADLANDS) : (i < 3 ? Biomes.BADLANDS : Biomes.WOODED_BADLANDS);
    }

    private ResourceKey<BiomeBase> pickPlateauBiome(int i, int j, Climate.b climate_b) {
        if (climate_b.max() < 0L) {
            return this.PLATEAU_BIOMES[i][j];
        } else {
            ResourceKey<BiomeBase> resourcekey = this.PLATEAU_BIOMES_VARIANT[i][j];

            return resourcekey == null ? this.PLATEAU_BIOMES[i][j] : resourcekey;
        }
    }

    private ResourceKey<BiomeBase> pickPeakBiome(int i, int j, Climate.b climate_b) {
        return i <= 2 ? (climate_b.max() < 0L ? Biomes.JAGGED_PEAKS : Biomes.FROZEN_PEAKS) : (i == 3 ? Biomes.STONY_PEAKS : this.pickBadlandsBiome(j, climate_b));
    }

    private ResourceKey<BiomeBase> pickSlopeBiome(int i, int j, Climate.b climate_b) {
        return i >= 3 ? this.pickPlateauBiome(i, j, climate_b) : (j <= 1 ? Biomes.SNOWY_SLOPES : Biomes.GROVE);
    }

    private ResourceKey<BiomeBase> pickExtremeHillsBiome(int i, int j, Climate.b climate_b) {
        ResourceKey<BiomeBase> resourcekey = this.EXTREME_HILLS[i][j];

        return resourcekey == null ? this.pickMiddleBiome(i, j, climate_b) : resourcekey;
    }

    private void addSurfaceBiome(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b, Climate.b climate_b1, Climate.b climate_b2, Climate.b climate_b3, Climate.b climate_b4, float f, ResourceKey<BiomeBase> resourcekey) {
        consumer.accept(Pair.of(Climate.parameters(climate_b, climate_b1, climate_b2, climate_b3, Climate.b.point(0.0F), climate_b4, f), resourcekey));
        consumer.accept(Pair.of(Climate.parameters(climate_b, climate_b1, climate_b2, climate_b3, Climate.b.point(1.0F), climate_b4, f), resourcekey));
    }

    private void addUndergroundBiome(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer, Climate.b climate_b, Climate.b climate_b1, Climate.b climate_b2, Climate.b climate_b3, Climate.b climate_b4, float f, ResourceKey<BiomeBase> resourcekey) {
        consumer.accept(Pair.of(Climate.parameters(climate_b, climate_b1, climate_b2, climate_b3, Climate.b.span(0.2F, 0.9F), climate_b4, f), resourcekey));
    }

    public static String getDebugStringForPeaksAndValleys(double d0) {
        return d0 < (double) TerrainShaper.peaksAndValleys(0.05F) ? "Valley" : (d0 < (double) TerrainShaper.peaksAndValleys(0.26666668F) ? "Low" : (d0 < (double) TerrainShaper.peaksAndValleys(0.4F) ? "Mid" : (d0 < (double) TerrainShaper.peaksAndValleys(0.56666666F) ? "High" : "Peak")));
    }

    public String getDebugStringForContinentalness(double d0) {
        double d1 = (double) Climate.quantizeCoord((float) d0);

        return d1 < (double) this.mushroomFieldsContinentalness.max() ? "Mushroom fields" : (d1 < (double) this.deepOceanContinentalness.max() ? "Deep ocean" : (d1 < (double) this.oceanContinentalness.max() ? "Ocean" : (d1 < (double) this.coastContinentalness.max() ? "Coast" : (d1 < (double) this.nearInlandContinentalness.max() ? "Near inland" : (d1 < (double) this.midInlandContinentalness.max() ? "Mid inland" : "Far inland")))));
    }

    public String getDebugStringForErosion(double d0) {
        return getDebugStringForNoiseValue(d0, this.erosions);
    }

    public String getDebugStringForTemperature(double d0) {
        return getDebugStringForNoiseValue(d0, this.temperatures);
    }

    public String getDebugStringForHumidity(double d0) {
        return getDebugStringForNoiseValue(d0, this.humidities);
    }

    private static String getDebugStringForNoiseValue(double d0, Climate.b[] aclimate_b) {
        double d1 = (double) Climate.quantizeCoord((float) d0);

        for (int i = 0; i < aclimate_b.length; ++i) {
            if (d1 < (double) aclimate_b[i].max()) {
                return i.makeConcatWithConstants < invokedynamic > (i);
            }
        }

        return "?";
    }
}
