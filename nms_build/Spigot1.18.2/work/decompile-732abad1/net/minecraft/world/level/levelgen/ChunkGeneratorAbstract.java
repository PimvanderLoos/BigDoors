package net.minecraft.world.level.levelgen;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.util.MathHelper;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public final class ChunkGeneratorAbstract extends ChunkGenerator {

    public static final Codec<ChunkGeneratorAbstract> CODEC = RecordCodecBuilder.create((instance) -> {
        return commonCodec(instance).and(instance.group(RegistryOps.retrieveRegistry(IRegistry.NOISE_REGISTRY).forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.noises;
        }), WorldChunkManager.CODEC.fieldOf("biome_source").forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.biomeSource;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.seed;
        }), GeneratorSettingBase.CODEC.fieldOf("settings").forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.settings;
        }))).apply(instance, instance.stable(ChunkGeneratorAbstract::new));
    });
    private static final IBlockData AIR = Blocks.AIR.defaultBlockState();
    private static final IBlockData[] EMPTY_COLUMN = new IBlockData[0];
    protected final IBlockData defaultBlock;
    public final IRegistry<NoiseGeneratorNormal.a> noises;
    private final long seed;
    public final Holder<GeneratorSettingBase> settings;
    private final NoiseRouter router;
    private final Climate.Sampler sampler;
    private final SurfaceSystem surfaceSystem;
    private final Aquifer.a globalFluidPicker;

    public ChunkGeneratorAbstract(IRegistry<StructureSet> iregistry, IRegistry<NoiseGeneratorNormal.a> iregistry1, WorldChunkManager worldchunkmanager, long i, Holder<GeneratorSettingBase> holder) {
        this(iregistry, iregistry1, worldchunkmanager, worldchunkmanager, i, holder);
    }

    private ChunkGeneratorAbstract(IRegistry<StructureSet> iregistry, IRegistry<NoiseGeneratorNormal.a> iregistry1, WorldChunkManager worldchunkmanager, WorldChunkManager worldchunkmanager1, long i, Holder<GeneratorSettingBase> holder) {
        super(iregistry, Optional.empty(), worldchunkmanager, worldchunkmanager1, i);
        this.noises = iregistry1;
        this.seed = i;
        this.settings = holder;
        GeneratorSettingBase generatorsettingbase = (GeneratorSettingBase) this.settings.value();

        this.defaultBlock = generatorsettingbase.defaultBlock();
        NoiseSettings noisesettings = generatorsettingbase.noiseSettings();

        this.router = generatorsettingbase.createNoiseRouter(iregistry1, i);
        this.sampler = new Climate.Sampler(this.router.temperature(), this.router.humidity(), this.router.continents(), this.router.erosion(), this.router.depth(), this.router.ridges(), this.router.spawnTarget());
        Aquifer.b aquifer_b = new Aquifer.b(-54, Blocks.LAVA.defaultBlockState());
        int j = generatorsettingbase.seaLevel();
        Aquifer.b aquifer_b1 = new Aquifer.b(j, generatorsettingbase.defaultFluid());
        Aquifer.b aquifer_b2 = new Aquifer.b(noisesettings.minY() - 1, Blocks.AIR.defaultBlockState());

        this.globalFluidPicker = (k, l, i1) -> {
            return l < Math.min(-54, j) ? aquifer_b : aquifer_b1;
        };
        this.surfaceSystem = new SurfaceSystem(iregistry1, this.defaultBlock, j, i, generatorsettingbase.getRandomSource());
    }

    @Override
    public CompletableFuture<IChunkAccess> createBiomes(IRegistry<BiomeBase> iregistry, Executor executor, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        return CompletableFuture.supplyAsync(SystemUtils.wrapThreadWithTaskName("init_biomes", () -> {
            this.doCreateBiomes(blender, structuremanager, ichunkaccess);
            return ichunkaccess;
        }), SystemUtils.backgroundExecutor());
    }

    private void doCreateBiomes(Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk(this.router, () -> {
            return new Beardifier(structuremanager, ichunkaccess);
        }, (GeneratorSettingBase) this.settings.value(), this.globalFluidPicker, blender);
        BiomeResolver biomeresolver = BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(this.runtimeBiomeSource), ichunkaccess);

        ichunkaccess.fillBiomesFromNoise(biomeresolver, noisechunk.cachedClimateSampler(this.router));
    }

    @VisibleForDebug
    public NoiseRouter router() {
        return this.router;
    }

    @Override
    public Climate.Sampler climateSampler() {
        return this.sampler;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorAbstract.CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long i) {
        return new ChunkGeneratorAbstract(this.structureSets, this.noises, this.biomeSource.withSeed(i), i, this.settings);
    }

    public boolean stable(long i, ResourceKey<GeneratorSettingBase> resourcekey) {
        return this.seed == i && this.settings.is(resourcekey);
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        NoiseSettings noisesettings = ((GeneratorSettingBase) this.settings.value()).noiseSettings();
        int k = Math.max(noisesettings.minY(), levelheightaccessor.getMinBuildHeight());
        int l = Math.min(noisesettings.minY() + noisesettings.height(), levelheightaccessor.getMaxBuildHeight());
        int i1 = MathHelper.intFloorDiv(k, noisesettings.getCellHeight());
        int j1 = MathHelper.intFloorDiv(l - k, noisesettings.getCellHeight());

        return j1 <= 0 ? levelheightaccessor.getMinBuildHeight() : this.iterateNoiseColumn(i, j, (IBlockData[]) null, heightmap_type.isOpaque(), i1, j1).orElse(levelheightaccessor.getMinBuildHeight());
    }

    @Override
    public BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor) {
        NoiseSettings noisesettings = ((GeneratorSettingBase) this.settings.value()).noiseSettings();
        int k = Math.max(noisesettings.minY(), levelheightaccessor.getMinBuildHeight());
        int l = Math.min(noisesettings.minY() + noisesettings.height(), levelheightaccessor.getMaxBuildHeight());
        int i1 = MathHelper.intFloorDiv(k, noisesettings.getCellHeight());
        int j1 = MathHelper.intFloorDiv(l - k, noisesettings.getCellHeight());

        if (j1 <= 0) {
            return new BlockColumn(k, ChunkGeneratorAbstract.EMPTY_COLUMN);
        } else {
            IBlockData[] aiblockdata = new IBlockData[j1 * noisesettings.getCellHeight()];

            this.iterateNoiseColumn(i, j, aiblockdata, (Predicate) null, i1, j1);
            return new BlockColumn(k, aiblockdata);
        }
    }

    @Override
    public void addDebugScreenInfo(List<String> list, BlockPosition blockposition) {
        DecimalFormat decimalformat = new DecimalFormat("0.000");
        DensityFunction.d densityfunction_d = new DensityFunction.d(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        double d0 = this.router.ridges().compute(densityfunction_d);
        String s = decimalformat.format(this.router.temperature().compute(densityfunction_d));

        list.add("NoiseRouter T: " + s + " H: " + decimalformat.format(this.router.humidity().compute(densityfunction_d)) + " C: " + decimalformat.format(this.router.continents().compute(densityfunction_d)) + " E: " + decimalformat.format(this.router.erosion().compute(densityfunction_d)) + " D: " + decimalformat.format(this.router.depth().compute(densityfunction_d)) + " W: " + decimalformat.format(d0) + " PV: " + decimalformat.format((double) TerrainShaper.peaksAndValleys((float) d0)) + " AS: " + decimalformat.format(this.router.initialDensityWithoutJaggedness().compute(densityfunction_d)) + " N: " + decimalformat.format(this.router.finalDensity().compute(densityfunction_d)));
    }

    private OptionalInt iterateNoiseColumn(int i, int j, @Nullable IBlockData[] aiblockdata, @Nullable Predicate<IBlockData> predicate, int k, int l) {
        NoiseSettings noisesettings = ((GeneratorSettingBase) this.settings.value()).noiseSettings();
        int i1 = noisesettings.getCellWidth();
        int j1 = noisesettings.getCellHeight();
        int k1 = Math.floorDiv(i, i1);
        int l1 = Math.floorDiv(j, i1);
        int i2 = Math.floorMod(i, i1);
        int j2 = Math.floorMod(j, i1);
        int k2 = k1 * i1;
        int l2 = l1 * i1;
        double d0 = (double) i2 / (double) i1;
        double d1 = (double) j2 / (double) i1;
        NoiseChunk noisechunk = NoiseChunk.forColumn(k2, l2, k, l, this.router, (GeneratorSettingBase) this.settings.value(), this.globalFluidPicker);

        noisechunk.initializeForFirstCellX();
        noisechunk.advanceCellX(0);

        for (int i3 = l - 1; i3 >= 0; --i3) {
            noisechunk.selectCellYZ(i3, 0);

            for (int j3 = j1 - 1; j3 >= 0; --j3) {
                int k3 = (k + i3) * j1 + j3;
                double d2 = (double) j3 / (double) j1;

                noisechunk.updateForY(k3, d2);
                noisechunk.updateForX(i, d0);
                noisechunk.updateForZ(j, d1);
                IBlockData iblockdata = noisechunk.getInterpolatedState();
                IBlockData iblockdata1 = iblockdata == null ? this.defaultBlock : iblockdata;

                if (aiblockdata != null) {
                    int l3 = i3 * j1 + j3;

                    aiblockdata[l3] = iblockdata1;
                }

                if (predicate != null && predicate.test(iblockdata1)) {
                    noisechunk.stopInterpolation();
                    return OptionalInt.of(k3 + 1);
                }
            }
        }

        noisechunk.stopInterpolation();
        return OptionalInt.empty();
    }

    @Override
    public void buildSurface(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        if (!SharedConstants.debugVoidTerrain(ichunkaccess.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, regionlimitedworldaccess);
            GeneratorSettingBase generatorsettingbase = (GeneratorSettingBase) this.settings.value();
            NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk(this.router, () -> {
                return new Beardifier(structuremanager, ichunkaccess);
            }, generatorsettingbase, this.globalFluidPicker, Blender.of(regionlimitedworldaccess));

            this.surfaceSystem.buildSurface(regionlimitedworldaccess.getBiomeManager(), regionlimitedworldaccess.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY), generatorsettingbase.useLegacyRandomSource(), worldgenerationcontext, ichunkaccess, noisechunk, generatorsettingbase.surfaceRule());
        }
    }

    @Override
    public void applyCarvers(RegionLimitedWorldAccess regionlimitedworldaccess, long i, BiomeManager biomemanager, StructureManager structuremanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        BiomeManager biomemanager1 = biomemanager.withDifferentSource((j, k, l) -> {
            return this.biomeSource.getNoiseBiome(j, k, l, this.climateSampler());
        });
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk(this.router, () -> {
            return new Beardifier(structuremanager, ichunkaccess);
        }, (GeneratorSettingBase) this.settings.value(), this.globalFluidPicker, Blender.of(regionlimitedworldaccess));
        Aquifer aquifer = noisechunk.aquifer();
        CarvingContext carvingcontext = new CarvingContext(this, regionlimitedworldaccess.registryAccess(), ichunkaccess.getHeightAccessorForGeneration(), noisechunk);
        CarvingMask carvingmask = ((ProtoChunk) ichunkaccess).getOrCreateCarvingMask(worldgenstage_features);

        for (int j = -8; j <= 8; ++j) {
            for (int k = -8; k <= 8; ++k) {
                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(chunkcoordintpair.x + j, chunkcoordintpair.z + k);
                IChunkAccess ichunkaccess1 = regionlimitedworldaccess.getChunk(chunkcoordintpair1.x, chunkcoordintpair1.z);
                BiomeSettingsGeneration biomesettingsgeneration = ((BiomeBase) ichunkaccess1.carverBiome(() -> {
                    return this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkcoordintpair1.getMinBlockX()), 0, QuartPos.fromBlock(chunkcoordintpair1.getMinBlockZ()), this.climateSampler());
                }).value()).getGenerationSettings();
                Iterable<Holder<WorldGenCarverWrapper<?>>> iterable = biomesettingsgeneration.getCarvers(worldgenstage_features);
                int l = 0;

                for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ++l) {
                    Holder<WorldGenCarverWrapper<?>> holder = (Holder) iterator.next();
                    WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper) holder.value();

                    seededrandom.setLargeFeatureSeed(i + (long) l, chunkcoordintpair1.x, chunkcoordintpair1.z);
                    if (worldgencarverwrapper.isStartChunk(seededrandom)) {
                        Objects.requireNonNull(biomemanager1);
                        worldgencarverwrapper.carve(carvingcontext, ichunkaccess, biomemanager1::getBiome, seededrandom, aquifer, chunkcoordintpair1, carvingmask);
                    }
                }
            }
        }

    }

    @Override
    public CompletableFuture<IChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        NoiseSettings noisesettings = ((GeneratorSettingBase) this.settings.value()).noiseSettings();
        LevelHeightAccessor levelheightaccessor = ichunkaccess.getHeightAccessorForGeneration();
        int i = Math.max(noisesettings.minY(), levelheightaccessor.getMinBuildHeight());
        int j = Math.min(noisesettings.minY() + noisesettings.height(), levelheightaccessor.getMaxBuildHeight());
        int k = MathHelper.intFloorDiv(i, noisesettings.getCellHeight());
        int l = MathHelper.intFloorDiv(j - i, noisesettings.getCellHeight());

        if (l <= 0) {
            return CompletableFuture.completedFuture(ichunkaccess);
        } else {
            int i1 = ichunkaccess.getSectionIndex(l * noisesettings.getCellHeight() - 1 + i);
            int j1 = ichunkaccess.getSectionIndex(i);
            Set<ChunkSection> set = Sets.newHashSet();

            for (int k1 = i1; k1 >= j1; --k1) {
                ChunkSection chunksection = ichunkaccess.getSection(k1);

                chunksection.acquire();
                set.add(chunksection);
            }

            return CompletableFuture.supplyAsync(SystemUtils.wrapThreadWithTaskName("wgen_fill_noise", () -> {
                return this.doFill(blender, structuremanager, ichunkaccess, k, l);
            }), SystemUtils.backgroundExecutor()).whenCompleteAsync((ichunkaccess1, throwable) -> {
                Iterator iterator = set.iterator();

                while (iterator.hasNext()) {
                    ChunkSection chunksection1 = (ChunkSection) iterator.next();

                    chunksection1.release();
                }

            }, executor);
        }
    }

    private IChunkAccess doFill(Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess, int i, int j) {
        GeneratorSettingBase generatorsettingbase = (GeneratorSettingBase) this.settings.value();
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk(this.router, () -> {
            return new Beardifier(structuremanager, ichunkaccess);
        }, generatorsettingbase, this.globalFluidPicker, blender);
        HeightMap heightmap = ichunkaccess.getOrCreateHeightmapUnprimed(HeightMap.Type.OCEAN_FLOOR_WG);
        HeightMap heightmap1 = ichunkaccess.getOrCreateHeightmapUnprimed(HeightMap.Type.WORLD_SURFACE_WG);
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int k = chunkcoordintpair.getMinBlockX();
        int l = chunkcoordintpair.getMinBlockZ();
        Aquifer aquifer = noisechunk.aquifer();

        noisechunk.initializeForFirstCellX();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        NoiseSettings noisesettings = generatorsettingbase.noiseSettings();
        int i1 = noisesettings.getCellWidth();
        int j1 = noisesettings.getCellHeight();
        int k1 = 16 / i1;
        int l1 = 16 / i1;

        for (int i2 = 0; i2 < k1; ++i2) {
            noisechunk.advanceCellX(i2);

            for (int j2 = 0; j2 < l1; ++j2) {
                ChunkSection chunksection = ichunkaccess.getSection(ichunkaccess.getSectionsCount() - 1);

                for (int k2 = j - 1; k2 >= 0; --k2) {
                    noisechunk.selectCellYZ(k2, j2);

                    for (int l2 = j1 - 1; l2 >= 0; --l2) {
                        int i3 = (i + k2) * j1 + l2;
                        int j3 = i3 & 15;
                        int k3 = ichunkaccess.getSectionIndex(i3);

                        if (ichunkaccess.getSectionIndex(chunksection.bottomBlockY()) != k3) {
                            chunksection = ichunkaccess.getSection(k3);
                        }

                        double d0 = (double) l2 / (double) j1;

                        noisechunk.updateForY(i3, d0);

                        for (int l3 = 0; l3 < i1; ++l3) {
                            int i4 = k + i2 * i1 + l3;
                            int j4 = i4 & 15;
                            double d1 = (double) l3 / (double) i1;

                            noisechunk.updateForX(i4, d1);

                            for (int k4 = 0; k4 < i1; ++k4) {
                                int l4 = l + j2 * i1 + k4;
                                int i5 = l4 & 15;
                                double d2 = (double) k4 / (double) i1;

                                noisechunk.updateForZ(l4, d2);
                                IBlockData iblockdata = noisechunk.getInterpolatedState();

                                if (iblockdata == null) {
                                    iblockdata = this.defaultBlock;
                                }

                                iblockdata = this.debugPreliminarySurfaceLevel(noisechunk, i4, i3, l4, iblockdata);
                                if (iblockdata != ChunkGeneratorAbstract.AIR && !SharedConstants.debugVoidTerrain(ichunkaccess.getPos())) {
                                    if (iblockdata.getLightEmission() != 0 && ichunkaccess instanceof ProtoChunk) {
                                        blockposition_mutableblockposition.set(i4, i3, l4);
                                        ((ProtoChunk) ichunkaccess).addLight(blockposition_mutableblockposition);
                                    }

                                    chunksection.setBlockState(j4, j3, i5, iblockdata, false);
                                    heightmap.update(j4, i3, i5, iblockdata);
                                    heightmap1.update(j4, i3, i5, iblockdata);
                                    if (aquifer.shouldScheduleFluidUpdate() && !iblockdata.getFluidState().isEmpty()) {
                                        blockposition_mutableblockposition.set(i4, i3, l4);
                                        ichunkaccess.markPosForPostprocessing(blockposition_mutableblockposition);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            noisechunk.swapSlices();
        }

        noisechunk.stopInterpolation();
        return ichunkaccess;
    }

    private IBlockData debugPreliminarySurfaceLevel(NoiseChunk noisechunk, int i, int j, int k, IBlockData iblockdata) {
        return iblockdata;
    }

    @Override
    public int getGenDepth() {
        return ((GeneratorSettingBase) this.settings.value()).noiseSettings().height();
    }

    @Override
    public int getSeaLevel() {
        return ((GeneratorSettingBase) this.settings.value()).seaLevel();
    }

    @Override
    public int getMinY() {
        return ((GeneratorSettingBase) this.settings.value()).noiseSettings().minY();
    }

    @Override
    public void spawnOriginalMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {
        if (!((GeneratorSettingBase) this.settings.value()).disableMobGeneration()) {
            ChunkCoordIntPair chunkcoordintpair = regionlimitedworldaccess.getCenter();
            Holder<BiomeBase> holder = regionlimitedworldaccess.getBiome(chunkcoordintpair.getWorldPosition().atY(regionlimitedworldaccess.getMaxBuildHeight() - 1));
            SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));

            seededrandom.setDecorationSeed(regionlimitedworldaccess.getSeed(), chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMinBlockZ());
            SpawnerCreature.spawnMobsForChunkGeneration(regionlimitedworldaccess, holder, chunkcoordintpair, seededrandom);
        }
    }

    /** @deprecated */
    @Deprecated
    public Optional<IBlockData> topMaterial(CarvingContext carvingcontext, Function<BlockPosition, Holder<BiomeBase>> function, IChunkAccess ichunkaccess, NoiseChunk noisechunk, BlockPosition blockposition, boolean flag) {
        return this.surfaceSystem.topMaterial(((GeneratorSettingBase) this.settings.value()).surfaceRule(), carvingcontext, function, ichunkaccess, noisechunk, blockposition, flag);
    }
}
