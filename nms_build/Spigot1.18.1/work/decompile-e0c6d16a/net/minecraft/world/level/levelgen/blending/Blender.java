package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.EnumDirection8;
import net.minecraft.core.QuartPos;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.TerrainInfo;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {

    private static final Blender EMPTY = new Blender((RegionLimitedWorldAccess) null, List.of(), List.of()) {
        @Override
        public TerrainInfo blendOffsetAndFactor(int i, int j, TerrainInfo terraininfo) {
            return terraininfo;
        }

        @Override
        public double blendDensity(int i, int j, int k, double d0) {
            return d0;
        }

        @Override
        public BiomeResolver getBiomeResolver(BiomeResolver biomeresolver) {
            return biomeresolver;
        }
    };
    private static final NoiseGeneratorNormal SHIFT_NOISE = NoiseGeneratorNormal.create(new XoroshiroRandomSource(42L), (NoiseGeneratorNormal.a) RegistryGeneration.NOISE.getOrThrow(Noises.SHIFT));
    private static final int HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
    private static final int HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(Blender.HEIGHT_BLENDING_RANGE_CELLS + 3);
    private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
    private static final int DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
    private static final double BLENDING_FACTOR = 10.0D;
    private static final double BLENDING_JAGGEDNESS = 0.0D;
    private static final double OLD_CHUNK_Y_RADIUS = (double) BlendingData.AREA_WITH_OLD_GENERATION.getHeight() / 2.0D;
    private static final double OLD_CHUNK_CENTER_Y = (double) BlendingData.AREA_WITH_OLD_GENERATION.getMinBuildHeight() + Blender.OLD_CHUNK_Y_RADIUS;
    private static final double OLD_CHUNK_XZ_RADIUS = 8.0D;
    private final RegionLimitedWorldAccess region;
    private final List<Blender.c> heightData;
    private final List<Blender.c> densityData;

    public static Blender empty() {
        return Blender.EMPTY;
    }

    public static Blender of(@Nullable RegionLimitedWorldAccess regionlimitedworldaccess) {
        if (regionlimitedworldaccess == null) {
            return Blender.EMPTY;
        } else {
            List<Blender.c> list = Lists.newArrayList();
            List<Blender.c> list1 = Lists.newArrayList();
            ChunkCoordIntPair chunkcoordintpair = regionlimitedworldaccess.getCenter();

            for (int i = -Blender.HEIGHT_BLENDING_RANGE_CHUNKS; i <= Blender.HEIGHT_BLENDING_RANGE_CHUNKS; ++i) {
                for (int j = -Blender.HEIGHT_BLENDING_RANGE_CHUNKS; j <= Blender.HEIGHT_BLENDING_RANGE_CHUNKS; ++j) {
                    int k = chunkcoordintpair.x + i;
                    int l = chunkcoordintpair.z + j;
                    BlendingData blendingdata = BlendingData.getOrUpdateBlendingData(regionlimitedworldaccess, k, l);

                    if (blendingdata != null) {
                        Blender.c blender_c = new Blender.c(k, l, blendingdata);

                        list.add(blender_c);
                        if (i >= -Blender.DENSITY_BLENDING_RANGE_CHUNKS && i <= Blender.DENSITY_BLENDING_RANGE_CHUNKS && j >= -Blender.DENSITY_BLENDING_RANGE_CHUNKS && j <= Blender.DENSITY_BLENDING_RANGE_CHUNKS) {
                            list1.add(blender_c);
                        }
                    }
                }
            }

            if (list.isEmpty() && list1.isEmpty()) {
                return Blender.EMPTY;
            } else {
                return new Blender(regionlimitedworldaccess, list, list1);
            }
        }
    }

    Blender(RegionLimitedWorldAccess regionlimitedworldaccess, List<Blender.c> list, List<Blender.c> list1) {
        this.region = regionlimitedworldaccess;
        this.heightData = list;
        this.densityData = list1;
    }

    public TerrainInfo blendOffsetAndFactor(int i, int j, TerrainInfo terraininfo) {
        int k = QuartPos.fromBlock(i);
        int l = QuartPos.fromBlock(j);
        double d0 = this.getBlendingDataValue(k, 0, l, BlendingData::getHeight);

        if (d0 != Double.MAX_VALUE) {
            return new TerrainInfo(heightToOffset(d0), 10.0D, 0.0D);
        } else {
            MutableDouble mutabledouble = new MutableDouble(0.0D);
            MutableDouble mutabledouble1 = new MutableDouble(0.0D);
            MutableDouble mutabledouble2 = new MutableDouble(Double.POSITIVE_INFINITY);
            Iterator iterator = this.heightData.iterator();

            while (iterator.hasNext()) {
                Blender.c blender_c = (Blender.c) iterator.next();

                blender_c.blendingData.iterateHeights(QuartPos.fromSection(blender_c.chunkX), QuartPos.fromSection(blender_c.chunkZ), (i1, j1, d1) -> {
                    double d2 = MathHelper.length((double) (k - i1), (double) (l - j1));

                    if (d2 <= (double) Blender.HEIGHT_BLENDING_RANGE_CELLS) {
                        if (d2 < mutabledouble2.doubleValue()) {
                            mutabledouble2.setValue(d2);
                        }

                        double d3 = 1.0D / (d2 * d2 * d2 * d2);

                        mutabledouble1.add(d1 * d3);
                        mutabledouble.add(d3);
                    }
                });
            }

            if (mutabledouble2.doubleValue() == Double.POSITIVE_INFINITY) {
                return terraininfo;
            } else {
                double d1 = mutabledouble1.doubleValue() / mutabledouble.doubleValue();
                double d2 = MathHelper.clamp(mutabledouble2.doubleValue() / (double) (Blender.HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);

                d2 = 3.0D * d2 * d2 - 2.0D * d2 * d2 * d2;
                double d3 = MathHelper.lerp(d2, heightToOffset(d1), terraininfo.offset());
                double d4 = MathHelper.lerp(d2, 10.0D, terraininfo.factor());
                double d5 = MathHelper.lerp(d2, 0.0D, terraininfo.jaggedness());

                return new TerrainInfo(d3, d4, d5);
            }
        }
    }

    private static double heightToOffset(double d0) {
        double d1 = 1.0D;
        double d2 = d0 + 0.5D;
        double d3 = MathHelper.positiveModulo(d2, 8.0D);

        return 1.0D * (32.0D * (d2 - 128.0D) - 3.0D * (d2 - 120.0D) * d3 + 3.0D * d3 * d3) / (128.0D * (32.0D - 3.0D * d3));
    }

    public double blendDensity(int i, int j, int k, double d0) {
        int l = QuartPos.fromBlock(i);
        int i1 = j / 8;
        int j1 = QuartPos.fromBlock(k);
        double d1 = this.getBlendingDataValue(l, i1, j1, BlendingData::getDensity);

        if (d1 != Double.MAX_VALUE) {
            return d1;
        } else {
            MutableDouble mutabledouble = new MutableDouble(0.0D);
            MutableDouble mutabledouble1 = new MutableDouble(0.0D);
            MutableDouble mutabledouble2 = new MutableDouble(Double.POSITIVE_INFINITY);
            Iterator iterator = this.densityData.iterator();

            while (iterator.hasNext()) {
                Blender.c blender_c = (Blender.c) iterator.next();

                blender_c.blendingData.iterateDensities(QuartPos.fromSection(blender_c.chunkX), QuartPos.fromSection(blender_c.chunkZ), i1 - 1, i1 + 1, (k1, l1, i2, d2) -> {
                    double d3 = MathHelper.length((double) (l - k1), (double) ((i1 - l1) * 2), (double) (j1 - i2));

                    if (d3 <= 2.0D) {
                        if (d3 < mutabledouble2.doubleValue()) {
                            mutabledouble2.setValue(d3);
                        }

                        double d4 = 1.0D / (d3 * d3 * d3 * d3);

                        mutabledouble1.add(d2 * d4);
                        mutabledouble.add(d4);
                    }
                });
            }

            if (mutabledouble2.doubleValue() == Double.POSITIVE_INFINITY) {
                return d0;
            } else {
                double d2 = mutabledouble1.doubleValue() / mutabledouble.doubleValue();
                double d3 = MathHelper.clamp(mutabledouble2.doubleValue() / 3.0D, 0.0D, 1.0D);

                return MathHelper.lerp(d3, d2, d0);
            }
        }
    }

    private double getBlendingDataValue(int i, int j, int k, Blender.a blender_a) {
        int l = QuartPos.toSection(i);
        int i1 = QuartPos.toSection(k);
        boolean flag = (i & 3) == 0;
        boolean flag1 = (k & 3) == 0;
        double d0 = this.getBlendingDataValue(blender_a, l, i1, i, j, k);

        if (d0 == Double.MAX_VALUE) {
            if (flag && flag1) {
                d0 = this.getBlendingDataValue(blender_a, l - 1, i1 - 1, i, j, k);
            }

            if (d0 == Double.MAX_VALUE) {
                if (flag) {
                    d0 = this.getBlendingDataValue(blender_a, l - 1, i1, i, j, k);
                }

                if (d0 == Double.MAX_VALUE && flag1) {
                    d0 = this.getBlendingDataValue(blender_a, l, i1 - 1, i, j, k);
                }
            }
        }

        return d0;
    }

    private double getBlendingDataValue(Blender.a blender_a, int i, int j, int k, int l, int i1) {
        BlendingData blendingdata = BlendingData.getOrUpdateBlendingData(this.region, i, j);

        return blendingdata != null ? blender_a.get(blendingdata, k - QuartPos.fromSection(i), l, i1 - QuartPos.fromSection(j)) : Double.MAX_VALUE;
    }

    public BiomeResolver getBiomeResolver(BiomeResolver biomeresolver) {
        return (i, j, k, climate_sampler) -> {
            BiomeBase biomebase = this.blendBiome(i, j, k);

            return biomebase == null ? biomeresolver.getNoiseBiome(i, j, k, climate_sampler) : biomebase;
        };
    }

    @Nullable
    private BiomeBase blendBiome(int i, int j, int k) {
        double d0 = (double) i + Blender.SHIFT_NOISE.getValue((double) i, 0.0D, (double) k) * 12.0D;
        double d1 = (double) k + Blender.SHIFT_NOISE.getValue((double) k, (double) i, 0.0D) * 12.0D;
        MutableDouble mutabledouble = new MutableDouble(Double.POSITIVE_INFINITY);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        MutableObject<ChunkCoordIntPair> mutableobject = new MutableObject();
        Iterator iterator = this.heightData.iterator();

        while (iterator.hasNext()) {
            Blender.c blender_c = (Blender.c) iterator.next();

            blender_c.blendingData.iterateHeights(QuartPos.fromSection(blender_c.chunkX), QuartPos.fromSection(blender_c.chunkZ), (l, i1, d2) -> {
                double d3 = MathHelper.length(d0 - (double) l, d1 - (double) i1);

                if (d3 <= (double) Blender.HEIGHT_BLENDING_RANGE_CELLS) {
                    if (d3 < mutabledouble.doubleValue()) {
                        mutableobject.setValue(new ChunkCoordIntPair(blender_c.chunkX, blender_c.chunkZ));
                        blockposition_mutableblockposition.set(l, QuartPos.fromBlock(MathHelper.floor(d2)), i1);
                        mutabledouble.setValue(d3);
                    }

                }
            });
        }

        if (mutabledouble.doubleValue() == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            double d2 = MathHelper.clamp(mutabledouble.doubleValue() / (double) (Blender.HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);

            if (d2 > 0.5D) {
                return null;
            } else {
                IChunkAccess ichunkaccess = this.region.getChunk(((ChunkCoordIntPair) mutableobject.getValue()).x, ((ChunkCoordIntPair) mutableobject.getValue()).z);

                return ichunkaccess.getNoiseBiome(Math.min(blockposition_mutableblockposition.getX() & 3, 3), blockposition_mutableblockposition.getY(), Math.min(blockposition_mutableblockposition.getZ() & 3, 3));
            }
        }
    }

    public static void generateBorderTicks(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        boolean flag = ichunkaccess.isOldNoiseGeneration();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMinBlockX(), 0, chunkcoordintpair.getMinBlockZ());
        int i = BlendingData.AREA_WITH_OLD_GENERATION.getMinBuildHeight();
        int j = BlendingData.AREA_WITH_OLD_GENERATION.getMaxBuildHeight() - 1;

        if (flag) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    generateBorderTick(ichunkaccess, blockposition_mutableblockposition.setWithOffset(blockposition, k, i - 1, l));
                    generateBorderTick(ichunkaccess, blockposition_mutableblockposition.setWithOffset(blockposition, k, i, l));
                    generateBorderTick(ichunkaccess, blockposition_mutableblockposition.setWithOffset(blockposition, k, j, l));
                    generateBorderTick(ichunkaccess, blockposition_mutableblockposition.setWithOffset(blockposition, k, j + 1, l));
                }
            }
        }

        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (regionlimitedworldaccess.getChunk(chunkcoordintpair.x + enumdirection.getStepX(), chunkcoordintpair.z + enumdirection.getStepZ()).isOldNoiseGeneration() != flag) {
                int i1 = enumdirection == EnumDirection.EAST ? 15 : 0;
                int j1 = enumdirection == EnumDirection.WEST ? 0 : 15;
                int k1 = enumdirection == EnumDirection.SOUTH ? 15 : 0;
                int l1 = enumdirection == EnumDirection.NORTH ? 0 : 15;

                for (int i2 = i1; i2 <= j1; ++i2) {
                    for (int j2 = k1; j2 <= l1; ++j2) {
                        int k2 = Math.min(j, ichunkaccess.getHeight(HeightMap.Type.MOTION_BLOCKING, i2, j2)) + 1;

                        for (int l2 = i; l2 < k2; ++l2) {
                            generateBorderTick(ichunkaccess, blockposition_mutableblockposition.setWithOffset(blockposition, i2, l2, j2));
                        }
                    }
                }
            }
        }

    }

    private static void generateBorderTick(IChunkAccess ichunkaccess, BlockPosition blockposition) {
        IBlockData iblockdata = ichunkaccess.getBlockState(blockposition);

        if (iblockdata.is((Tag) TagsBlock.LEAVES)) {
            ichunkaccess.markPosForPostprocessing(blockposition);
        }

        Fluid fluid = ichunkaccess.getFluidState(blockposition);

        if (!fluid.isEmpty()) {
            ichunkaccess.markPosForPostprocessing(blockposition);
        }

    }

    public static void addAroundOldChunksCarvingMaskFilter(GeneratorAccessSeed generatoraccessseed, ProtoChunk protochunk) {
        ChunkCoordIntPair chunkcoordintpair = protochunk.getPos();
        Blender.b blender_b = makeOldChunkDistanceGetter(protochunk.isOldNoiseGeneration(), BlendingData.sideByGenerationAge(generatoraccessseed, chunkcoordintpair.x, chunkcoordintpair.z, true));

        if (blender_b != null) {
            CarvingMask.a carvingmask_a = (i, j, k) -> {
                double d0 = (double) i + 0.5D + Blender.SHIFT_NOISE.getValue((double) i, (double) j, (double) k) * 4.0D;
                double d1 = (double) j + 0.5D + Blender.SHIFT_NOISE.getValue((double) j, (double) k, (double) i) * 4.0D;
                double d2 = (double) k + 0.5D + Blender.SHIFT_NOISE.getValue((double) k, (double) i, (double) j) * 4.0D;

                return blender_b.getDistance(d0, d1, d2) < 4.0D;
            };
            Stream stream = Stream.of(WorldGenStage.Features.values());

            Objects.requireNonNull(protochunk);
            stream.map(protochunk::getOrCreateCarvingMask).forEach((carvingmask) -> {
                carvingmask.setAdditionalMask(carvingmask_a);
            });
        }
    }

    @Nullable
    public static Blender.b makeOldChunkDistanceGetter(boolean flag, Set<EnumDirection8> set) {
        if (!flag && set.isEmpty()) {
            return null;
        } else {
            List<Blender.b> list = Lists.newArrayList();

            if (flag) {
                list.add(makeOffsetOldChunkDistanceGetter((EnumDirection8) null));
            }

            set.forEach((enumdirection8) -> {
                list.add(makeOffsetOldChunkDistanceGetter(enumdirection8));
            });
            return (d0, d1, d2) -> {
                double d3 = Double.POSITIVE_INFINITY;
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Blender.b blender_b = (Blender.b) iterator.next();
                    double d4 = blender_b.getDistance(d0, d1, d2);

                    if (d4 < d3) {
                        d3 = d4;
                    }
                }

                return d3;
            };
        }
    }

    private static Blender.b makeOffsetOldChunkDistanceGetter(@Nullable EnumDirection8 enumdirection8) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        EnumDirection enumdirection;

        if (enumdirection8 != null) {
            for (Iterator iterator = enumdirection8.getDirections().iterator(); iterator.hasNext(); d1 += (double) (enumdirection.getStepZ() * 16)) {
                enumdirection = (EnumDirection) iterator.next();
                d0 += (double) (enumdirection.getStepX() * 16);
            }
        }

        return (d2, d3, d4) -> {
            return distanceToCube(d2 - 8.0D - d0, d3 - Blender.OLD_CHUNK_CENTER_Y, d4 - 8.0D - d1, 8.0D, Blender.OLD_CHUNK_Y_RADIUS, 8.0D);
        };
    }

    private static double distanceToCube(double d0, double d1, double d2, double d3, double d4, double d5) {
        double d6 = Math.abs(d0) - d3;
        double d7 = Math.abs(d1) - d4;
        double d8 = Math.abs(d2) - d5;

        return MathHelper.length(Math.max(0.0D, d6), Math.max(0.0D, d7), Math.max(0.0D, d8));
    }

    private static record c(int a, int b, BlendingData c) {

        final int chunkX;
        final int chunkZ;
        final BlendingData blendingData;

        c(int i, int j, BlendingData blendingdata) {
            this.chunkX = i;
            this.chunkZ = j;
            this.blendingData = blendingdata;
        }

        public int chunkX() {
            return this.chunkX;
        }

        public int chunkZ() {
            return this.chunkZ;
        }

        public BlendingData blendingData() {
            return this.blendingData;
        }
    }

    private interface a {

        double get(BlendingData blendingdata, int i, int j, int k);
    }

    public interface b {

        double getDistance(double d0, double d1, double d2);
    }
}
