package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
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
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.apache.commons.lang3.mutable.MutableObject;

public final class ChunkGeneratorAbstract extends ChunkGenerator {

    public static final Codec<ChunkGeneratorAbstract> CODEC = RecordCodecBuilder.create((instance) -> {
        return commonCodec(instance).and(instance.group(RegistryOps.retrieveRegistry(IRegistry.NOISE_REGISTRY).forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.noises;
        }), WorldChunkManager.CODEC.fieldOf("biome_source").forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.biomeSource;
        }), GeneratorSettingBase.CODEC.fieldOf("settings").forGetter((chunkgeneratorabstract) -> {
            return chunkgeneratorabstract.settings;
        }))).apply(instance, instance.stable(ChunkGeneratorAbstract::new));
    });
    private static final IBlockData AIR = Blocks.AIR.defaultBlockState();
    protected final IBlockData defaultBlock;
    public final IRegistry<NoiseGeneratorNormal.a> noises;
    public final Holder<GeneratorSettingBase> settings;
    private final Aquifer.a globalFluidPicker;

    public ChunkGeneratorAbstract(IRegistry<StructureSet> iregistry, IRegistry<NoiseGeneratorNormal.a> iregistry1, WorldChunkManager worldchunkmanager, Holder<GeneratorSettingBase> holder) {
        super(iregistry, Optional.empty(), worldchunkmanager);
        this.noises = iregistry1;
        this.settings = holder;
        GeneratorSettingBase generatorsettingbase = (GeneratorSettingBase) this.settings.value();

        this.defaultBlock = generatorsettingbase.defaultBlock();
        Aquifer.b aquifer_b = new Aquifer.b(-54, Blocks.LAVA.defaultBlockState());
        int i = generatorsettingbase.seaLevel();
        Aquifer.b aquifer_b1 = new Aquifer.b(i, generatorsettingbase.defaultFluid());
        Aquifer.b aquifer_b2 = new Aquifer.b(DimensionManager.MIN_Y * 2, Blocks.AIR.defaultBlockState());

        this.globalFluidPicker = (j, k, l) -> {
            return k < Math.min(-54, i) ? aquifer_b : aquifer_b1;
        };
    }

    @Override
    public CompletableFuture<IChunkAccess> createBiomes(IRegistry<BiomeBase> iregistry, Executor executor, RandomState randomstate, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        return CompletableFuture.supplyAsync(SystemUtils.wrapThreadWithTaskName("init_biomes", () -> {
            this.doCreateBiomes(blender, randomstate, structuremanager, ichunkaccess);
            return ichunkaccess;
        }), SystemUtils.backgroundExecutor());
    }

    private void doCreateBiomes(Blender blender, RandomState randomstate, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk((ichunkaccess1) -> {
            return this.createNoiseChunk(ichunkaccess1, structuremanager, blender, randomstate);
        });
        BiomeResolver biomeresolver = BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(this.biomeSource), ichunkaccess);

        ichunkaccess.fillBiomesFromNoise(biomeresolver, noisechunk.cachedClimateSampler(randomstate.router(), ((GeneratorSettingBase) this.settings.value()).spawnTarget()));
    }

    private NoiseChunk createNoiseChunk(IChunkAccess ichunkaccess, StructureManager structuremanager, Blender blender, RandomState randomstate) {
        return NoiseChunk.forChunk(ichunkaccess, randomstate, Beardifier.forStructuresInChunk(structuremanager, ichunkaccess.getPos()), (GeneratorSettingBase) this.settings.value(), this.globalFluidPicker, blender);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorAbstract.CODEC;
    }

    public Holder<GeneratorSettingBase> generatorSettings() {
        return this.settings;
    }

    public boolean stable(ResourceKey<GeneratorSettingBase> resourcekey) {
        return this.settings.is(resourcekey);
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
        return this.iterateNoiseColumn(levelheightaccessor, randomstate, i, j, (MutableObject) null, heightmap_type.isOpaque()).orElse(levelheightaccessor.getMinBuildHeight());
    }

    @Override
    public BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
        MutableObject<BlockColumn> mutableobject = new MutableObject();

        this.iterateNoiseColumn(levelheightaccessor, randomstate, i, j, mutableobject, (Predicate) null);
        return (BlockColumn) mutableobject.getValue();
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomstate, BlockPosition blockposition) {
        DecimalFormat decimalformat = new DecimalFormat("0.000");
        NoiseRouter noiserouter = randomstate.router();
        DensityFunction.e densityfunction_e = new DensityFunction.e(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        double d0 = noiserouter.ridges().compute(densityfunction_e);
        String s = decimalformat.format(noiserouter.temperature().compute(densityfunction_e));

        list.add("NoiseRouter T: " + s + " V: " + decimalformat.format(noiserouter.vegetation().compute(densityfunction_e)) + " C: " + decimalformat.format(noiserouter.continents().compute(densityfunction_e)) + " E: " + decimalformat.format(noiserouter.erosion().compute(densityfunction_e)) + " D: " + decimalformat.format(noiserouter.depth().compute(densityfunction_e)) + " W: " + decimalformat.format(d0) + " PV: " + decimalformat.format((double) NoiseRouterData.peaksAndValleys((float) d0)) + " AS: " + decimalformat.format(noiserouter.initialDensityWithoutJaggedness().compute(densityfunction_e)) + " N: " + decimalformat.format(noiserouter.finalDensity().compute(densityfunction_e)));
    }

    private OptionalInt iterateNoiseColumn(LevelHeightAccessor levelheightaccessor, RandomState randomstate, int i, int j, @Nullable MutableObject<BlockColumn> mutableobject, @Nullable Predicate<IBlockData> predicate) {
        NoiseSettings noisesettings = ((GeneratorSettingBase) this.settings.value()).noiseSettings().clampToHeightAccessor(levelheightaccessor);
        int k = noisesettings.getCellHeight();
        int l = noisesettings.minY();
        int i1 = MathHelper.intFloorDiv(l, k);
        int j1 = MathHelper.intFloorDiv(noisesettings.height(), k);

        if (j1 <= 0) {
            return OptionalInt.empty();
        } else {
            IBlockData[] aiblockdata;

            if (mutableobject == null) {
                aiblockdata = null;
            } else {
                aiblockdata = new IBlockData[noisesettings.height()];
                mutableobject.setValue(new BlockColumn(l, aiblockdata));
            }

            int k1 = noisesettings.getCellWidth();
            int l1 = Math.floorDiv(i, k1);
            int i2 = Math.floorDiv(j, k1);
            int j2 = Math.floorMod(i, k1);
            int k2 = Math.floorMod(j, k1);
            int l2 = l1 * k1;
            int i3 = i2 * k1;
            double d0 = (double) j2 / (double) k1;
            double d1 = (double) k2 / (double) k1;
            NoiseChunk noisechunk = new NoiseChunk(1, randomstate, l2, i3, noisesettings, DensityFunctions.b.INSTANCE, (GeneratorSettingBase) this.settings.value(), this.globalFluidPicker, Blender.empty());

            noisechunk.initializeForFirstCellX();
            noisechunk.advanceCellX(0);

            for (int j3 = j1 - 1; j3 >= 0; --j3) {
                noisechunk.selectCellYZ(j3, 0);

                for (int k3 = k - 1; k3 >= 0; --k3) {
                    int l3 = (i1 + j3) * k + k3;
                    double d2 = (double) k3 / (double) k;

                    noisechunk.updateForY(l3, d2);
                    noisechunk.updateForX(i, d0);
                    noisechunk.updateForZ(j, d1);
                    IBlockData iblockdata = noisechunk.getInterpolatedState();
                    IBlockData iblockdata1 = iblockdata == null ? this.defaultBlock : iblockdata;

                    if (aiblockdata != null) {
                        int i4 = j3 * k + k3;

                        aiblockdata[i4] = iblockdata1;
                    }

                    if (predicate != null && predicate.test(iblockdata1)) {
                        noisechunk.stopInterpolation();
                        return OptionalInt.of(l3 + 1);
                    }
                }
            }

            noisechunk.stopInterpolation();
            return OptionalInt.empty();
        }
    }

    @Override
    public void buildSurface(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager, RandomState randomstate, IChunkAccess ichunkaccess) {
        if (!SharedConstants.debugVoidTerrain(ichunkaccess.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, regionlimitedworldaccess);

            this.buildSurface(ichunkaccess, worldgenerationcontext, randomstate, structuremanager, regionlimitedworldaccess.getBiomeManager(), regionlimitedworldaccess.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY), Blender.of(regionlimitedworldaccess));
        }
    }

    @VisibleForTesting
    public void buildSurface(IChunkAccess ichunkaccess, WorldGenerationContext worldgenerationcontext, RandomState randomstate, StructureManager structuremanager, BiomeManager biomemanager, IRegistry<BiomeBase> iregistry, Blender blender) {
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk((ichunkaccess1) -> {
            return this.createNoiseChunk(ichunkaccess1, structuremanager, blender, randomstate);
        });
        GeneratorSettingBase generatorsettingbase = (GeneratorSettingBase) this.settings.value();

        randomstate.surfaceSystem().buildSurface(randomstate, biomemanager, iregistry, generatorsettingbase.useLegacyRandomSource(), worldgenerationcontext, ichunkaccess, noisechunk, generatorsettingbase.surfaceRule());
    }

    @Override
    public void applyCarvers(RegionLimitedWorldAccess regionlimitedworldaccess, long i, RandomState randomstate, BiomeManager biomemanager, StructureManager structuremanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        BiomeManager biomemanager1 = biomemanager.withDifferentSource((j, k, l) -> {
            return this.biomeSource.getNoiseBiome(j, k, l, randomstate.sampler());
        });
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk((ichunkaccess1) -> {
            return this.createNoiseChunk(ichunkaccess1, structuremanager, Blender.of(regionlimitedworldaccess), randomstate);
        });
        Aquifer aquifer = noisechunk.aquifer();
        CarvingContext carvingcontext = new CarvingContext(this, regionlimitedworldaccess.registryAccess(), ichunkaccess.getHeightAccessorForGeneration(), noisechunk, randomstate, ((GeneratorSettingBase) this.settings.value()).surfaceRule());
        CarvingMask carvingmask = ((ProtoChunk) ichunkaccess).getOrCreateCarvingMask(worldgenstage_features);

        for (int j = -8; j <= 8; ++j) {
            for (int k = -8; k <= 8; ++k) {
                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(chunkcoordintpair.x + j, chunkcoordintpair.z + k);
                IChunkAccess ichunkaccess1 = regionlimitedworldaccess.getChunk(chunkcoordintpair1.x, chunkcoordintpair1.z);
                BiomeSettingsGeneration biomesettingsgeneration = ichunkaccess1.carverBiome(() -> {
                    return this.getBiomeGenerationSettings(this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkcoordintpair1.getMinBlockX()), 0, QuartPos.fromBlock(chunkcoordintpair1.getMinBlockZ()), randomstate.sampler()));
                });
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
    public CompletableFuture<IChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomstate, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        NoiseSettings noisesettings = ((GeneratorSettingBase) this.settings.value()).noiseSettings().clampToHeightAccessor(ichunkaccess.getHeightAccessorForGeneration());
        int i = noisesettings.minY();
        int j = MathHelper.intFloorDiv(i, noisesettings.getCellHeight());
        int k = MathHelper.intFloorDiv(noisesettings.height(), noisesettings.getCellHeight());

        if (k <= 0) {
            return CompletableFuture.completedFuture(ichunkaccess);
        } else {
            int l = ichunkaccess.getSectionIndex(k * noisesettings.getCellHeight() - 1 + i);
            int i1 = ichunkaccess.getSectionIndex(i);
            Set<ChunkSection> set = Sets.newHashSet();

            for (int j1 = l; j1 >= i1; --j1) {
                ChunkSection chunksection = ichunkaccess.getSection(j1);

                chunksection.acquire();
                set.add(chunksection);
            }

            return CompletableFuture.supplyAsync(SystemUtils.wrapThreadWithTaskName("wgen_fill_noise", () -> {
                return this.doFill(blender, structuremanager, randomstate, ichunkaccess, j, k);
            }), SystemUtils.backgroundExecutor()).whenCompleteAsync((ichunkaccess1, throwable) -> {
                Iterator iterator = set.iterator();

                while (iterator.hasNext()) {
                    ChunkSection chunksection1 = (ChunkSection) iterator.next();

                    chunksection1.release();
                }

            }, executor);
        }
    }

    private IChunkAccess doFill(Blender blender, StructureManager structuremanager, RandomState randomstate, IChunkAccess ichunkaccess, int i, int j) {
        NoiseChunk noisechunk = ichunkaccess.getOrCreateNoiseChunk((ichunkaccess1) -> {
            return this.createNoiseChunk(ichunkaccess1, structuremanager, blender, randomstate);
        });
        HeightMap heightmap = ichunkaccess.getOrCreateHeightmapUnprimed(HeightMap.Type.OCEAN_FLOOR_WG);
        HeightMap heightmap1 = ichunkaccess.getOrCreateHeightmapUnprimed(HeightMap.Type.WORLD_SURFACE_WG);
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int k = chunkcoordintpair.getMinBlockX();
        int l = chunkcoordintpair.getMinBlockZ();
        Aquifer aquifer = noisechunk.aquifer();

        noisechunk.initializeForFirstCellX();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i1 = noisechunk.cellWidth();
        int j1 = noisechunk.cellHeight();
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
            SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));

            seededrandom.setDecorationSeed(regionlimitedworldaccess.getSeed(), chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMinBlockZ());
            SpawnerCreature.spawnMobsForChunkGeneration(regionlimitedworldaccess, holder, chunkcoordintpair, seededrandom);
        }
    }
}
