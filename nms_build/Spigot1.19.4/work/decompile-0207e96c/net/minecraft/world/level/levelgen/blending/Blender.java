package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.EnumDirection8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.server.level.RegionLimitedWorldAccess;
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
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {

    private static final Blender EMPTY = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()) {
        @Override
        public Blender.a blendOffsetAndFactor(int i, int j) {
            return new Blender.a(1.0D, 0.0D);
        }

        @Override
        public double blendDensity(DensityFunction.b densityfunction_b, double d0) {
            return d0;
        }

        @Override
        public BiomeResolver getBiomeResolver(BiomeResolver biomeresolver) {
            return biomeresolver;
        }
    };
    private static final NoiseGeneratorNormal SHIFT_NOISE = NoiseGeneratorNormal.create(new XoroshiroRandomSource(42L), NoiseData.DEFAULT_SHIFT);
    private static final int HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
    private static final int HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(Blender.HEIGHT_BLENDING_RANGE_CELLS + 3);
    private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
    private static final int DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
    private static final double OLD_CHUNK_XZ_RADIUS = 8.0D;
    private final Long2ObjectOpenHashMap<BlendingData> heightAndBiomeBlendingData;
    private final Long2ObjectOpenHashMap<BlendingData> densityBlendingData;

    public static Blender empty() {
        return Blender.EMPTY;
    }

    public static Blender of(@Nullable RegionLimitedWorldAccess regionlimitedworldaccess) {
        if (regionlimitedworldaccess == null) {
            return Blender.EMPTY;
        } else {
            ChunkCoordIntPair chunkcoordintpair = regionlimitedworldaccess.getCenter();

            if (!regionlimitedworldaccess.isOldChunkAround(chunkcoordintpair, Blender.HEIGHT_BLENDING_RANGE_CHUNKS)) {
                return Blender.EMPTY;
            } else {
                Long2ObjectOpenHashMap<BlendingData> long2objectopenhashmap = new Long2ObjectOpenHashMap();
                Long2ObjectOpenHashMap<BlendingData> long2objectopenhashmap1 = new Long2ObjectOpenHashMap();
                int i = MathHelper.square(Blender.HEIGHT_BLENDING_RANGE_CHUNKS + 1);

                for (int j = -Blender.HEIGHT_BLENDING_RANGE_CHUNKS; j <= Blender.HEIGHT_BLENDING_RANGE_CHUNKS; ++j) {
                    for (int k = -Blender.HEIGHT_BLENDING_RANGE_CHUNKS; k <= Blender.HEIGHT_BLENDING_RANGE_CHUNKS; ++k) {
                        if (j * j + k * k <= i) {
                            int l = chunkcoordintpair.x + j;
                            int i1 = chunkcoordintpair.z + k;
                            BlendingData blendingdata = BlendingData.getOrUpdateBlendingData(regionlimitedworldaccess, l, i1);

                            if (blendingdata != null) {
                                long2objectopenhashmap.put(ChunkCoordIntPair.asLong(l, i1), blendingdata);
                                if (j >= -Blender.DENSITY_BLENDING_RANGE_CHUNKS && j <= Blender.DENSITY_BLENDING_RANGE_CHUNKS && k >= -Blender.DENSITY_BLENDING_RANGE_CHUNKS && k <= Blender.DENSITY_BLENDING_RANGE_CHUNKS) {
                                    long2objectopenhashmap1.put(ChunkCoordIntPair.asLong(l, i1), blendingdata);
                                }
                            }
                        }
                    }
                }

                if (long2objectopenhashmap.isEmpty() && long2objectopenhashmap1.isEmpty()) {
                    return Blender.EMPTY;
                } else {
                    return new Blender(long2objectopenhashmap, long2objectopenhashmap1);
                }
            }
        }
    }

    Blender(Long2ObjectOpenHashMap<BlendingData> long2objectopenhashmap, Long2ObjectOpenHashMap<BlendingData> long2objectopenhashmap1) {
        this.heightAndBiomeBlendingData = long2objectopenhashmap;
        this.densityBlendingData = long2objectopenhashmap1;
    }

    public Blender.a blendOffsetAndFactor(int i, int j) {
        int k = QuartPos.fromBlock(i);
        int l = QuartPos.fromBlock(j);
        double d0 = this.getBlendingDataValue(k, 0, l, BlendingData::getHeight);

        if (d0 != Double.MAX_VALUE) {
            return new Blender.a(0.0D, heightToOffset(d0));
        } else {
            MutableDouble mutabledouble = new MutableDouble(0.0D);
            MutableDouble mutabledouble1 = new MutableDouble(0.0D);
            MutableDouble mutabledouble2 = new MutableDouble(Double.POSITIVE_INFINITY);

            this.heightAndBiomeBlendingData.forEach((olong, blendingdata) -> {
                blendingdata.iterateHeights(QuartPos.fromSection(ChunkCoordIntPair.getX(olong)), QuartPos.fromSection(ChunkCoordIntPair.getZ(olong)), (i1, j1, d1) -> {
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
            });
            if (mutabledouble2.doubleValue() == Double.POSITIVE_INFINITY) {
                return new Blender.a(1.0D, 0.0D);
            } else {
                double d1 = mutabledouble1.doubleValue() / mutabledouble.doubleValue();
                double d2 = MathHelper.clamp(mutabledouble2.doubleValue() / (double) (Blender.HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);

                d2 = 3.0D * d2 * d2 - 2.0D * d2 * d2 * d2;
                return new Blender.a(d2, heightToOffset(d1));
            }
        }
    }

    private static double heightToOffset(double d0) {
        double d1 = 1.0D;
        double d2 = d0 + 0.5D;
        double d3 = MathHelper.positiveModulo(d2, 8.0D);

        return 1.0D * (32.0D * (d2 - 128.0D) - 3.0D * (d2 - 120.0D) * d3 + 3.0D * d3 * d3) / (128.0D * (32.0D - 3.0D * d3));
    }

    public double blendDensity(DensityFunction.b densityfunction_b, double d0) {
        int i = QuartPos.fromBlock(densityfunction_b.blockX());
        int j = densityfunction_b.blockY() / 8;
        int k = QuartPos.fromBlock(densityfunction_b.blockZ());
        double d1 = this.getBlendingDataValue(i, j, k, BlendingData::getDensity);

        if (d1 != Double.MAX_VALUE) {
            return d1;
        } else {
            MutableDouble mutabledouble = new MutableDouble(0.0D);
            MutableDouble mutabledouble1 = new MutableDouble(0.0D);
            MutableDouble mutabledouble2 = new MutableDouble(Double.POSITIVE_INFINITY);

            this.densityBlendingData.forEach((olong, blendingdata) -> {
                blendingdata.iterateDensities(QuartPos.fromSection(ChunkCoordIntPair.getX(olong)), QuartPos.fromSection(ChunkCoordIntPair.getZ(olong)), j - 1, j + 1, (l, i1, j1, d2) -> {
                    double d3 = MathHelper.length((double) (i - l), (double) ((j - i1) * 2), (double) (k - j1));

                    if (d3 <= 2.0D) {
                        if (d3 < mutabledouble2.doubleValue()) {
                            mutabledouble2.setValue(d3);
                        }

                        double d4 = 1.0D / (d3 * d3 * d3 * d3);

                        mutabledouble1.add(d2 * d4);
                        mutabledouble.add(d4);
                    }
                });
            });
            if (mutabledouble2.doubleValue() == Double.POSITIVE_INFINITY) {
                return d0;
            } else {
                double d2 = mutabledouble1.doubleValue() / mutabledouble.doubleValue();
                double d3 = MathHelper.clamp(mutabledouble2.doubleValue() / 3.0D, 0.0D, 1.0D);

                return MathHelper.lerp(d3, d2, d0);
            }
        }
    }

    private double getBlendingDataValue(int i, int j, int k, Blender.b blender_b) {
        int l = QuartPos.toSection(i);
        int i1 = QuartPos.toSection(k);
        boolean flag = (i & 3) == 0;
        boolean flag1 = (k & 3) == 0;
        double d0 = this.getBlendingDataValue(blender_b, l, i1, i, j, k);

        if (d0 == Double.MAX_VALUE) {
            if (flag && flag1) {
                d0 = this.getBlendingDataValue(blender_b, l - 1, i1 - 1, i, j, k);
            }

            if (d0 == Double.MAX_VALUE) {
                if (flag) {
                    d0 = this.getBlendingDataValue(blender_b, l - 1, i1, i, j, k);
                }

                if (d0 == Double.MAX_VALUE && flag1) {
                    d0 = this.getBlendingDataValue(blender_b, l, i1 - 1, i, j, k);
                }
            }
        }

        return d0;
    }

    private double getBlendingDataValue(Blender.b blender_b, int i, int j, int k, int l, int i1) {
        BlendingData blendingdata = (BlendingData) this.heightAndBiomeBlendingData.get(ChunkCoordIntPair.asLong(i, j));

        return blendingdata != null ? blender_b.get(blendingdata, k - QuartPos.fromSection(i), l, i1 - QuartPos.fromSection(j)) : Double.MAX_VALUE;
    }

    public BiomeResolver getBiomeResolver(BiomeResolver biomeresolver) {
        return (i, j, k, climate_sampler) -> {
            Holder<BiomeBase> holder = this.blendBiome(i, j, k);

            return holder == null ? biomeresolver.getNoiseBiome(i, j, k, climate_sampler) : holder;
        };
    }

    @Nullable
    private Holder<BiomeBase> blendBiome(int i, int j, int k) {
        MutableDouble mutabledouble = new MutableDouble(Double.POSITIVE_INFINITY);
        MutableObject<Holder<BiomeBase>> mutableobject = new MutableObject();

        this.heightAndBiomeBlendingData.forEach((olong, blendingdata) -> {
            blendingdata.iterateBiomes(QuartPos.fromSection(ChunkCoordIntPair.getX(olong)), j, QuartPos.fromSection(ChunkCoordIntPair.getZ(olong)), (l, i1, holder) -> {
                double d0 = MathHelper.length((double) (i - l), (double) (k - i1));

                if (d0 <= (double) Blender.HEIGHT_BLENDING_RANGE_CELLS) {
                    if (d0 < mutabledouble.doubleValue()) {
                        mutableobject.setValue(holder);
                        mutabledouble.setValue(d0);
                    }

                }
            });
        });
        if (mutabledouble.doubleValue() == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            double d0 = Blender.SHIFT_NOISE.getValue((double) i, 0.0D, (double) k) * 12.0D;
            double d1 = MathHelper.clamp((mutabledouble.doubleValue() + d0) / (double) (Blender.HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);

            return d1 > 0.5D ? null : (Holder) mutableobject.getValue();
        }
    }

    public static void generateBorderTicks(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        boolean flag = ichunkaccess.isOldNoiseGeneration();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMinBlockX(), 0, chunkcoordintpair.getMinBlockZ());
        BlendingData blendingdata = ichunkaccess.getBlendingData();

        if (blendingdata != null) {
            int i = blendingdata.getAreaWithOldGeneration().getMinBuildHeight();
            int j = blendingdata.getAreaWithOldGeneration().getMaxBuildHeight() - 1;

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
    }

    private static void generateBorderTick(IChunkAccess ichunkaccess, BlockPosition blockposition) {
        IBlockData iblockdata = ichunkaccess.getBlockState(blockposition);

        if (iblockdata.is(TagsBlock.LEAVES)) {
            ichunkaccess.markPosForPostprocessing(blockposition);
        }

        Fluid fluid = ichunkaccess.getFluidState(blockposition);

        if (!fluid.isEmpty()) {
            ichunkaccess.markPosForPostprocessing(blockposition);
        }

    }

    public static void addAroundOldChunksCarvingMaskFilter(GeneratorAccessSeed generatoraccessseed, ProtoChunk protochunk) {
        ChunkCoordIntPair chunkcoordintpair = protochunk.getPos();
        Builder<EnumDirection8, BlendingData> builder = ImmutableMap.builder();
        EnumDirection8[] aenumdirection8 = EnumDirection8.values();
        int i = aenumdirection8.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection8 enumdirection8 = aenumdirection8[j];
            int k = chunkcoordintpair.x + enumdirection8.getStepX();
            int l = chunkcoordintpair.z + enumdirection8.getStepZ();
            BlendingData blendingdata = generatoraccessseed.getChunk(k, l).getBlendingData();

            if (blendingdata != null) {
                builder.put(enumdirection8, blendingdata);
            }
        }

        ImmutableMap<EnumDirection8, BlendingData> immutablemap = builder.build();

        if (protochunk.isOldNoiseGeneration() || !immutablemap.isEmpty()) {
            Blender.c blender_c = makeOldChunkDistanceGetter(protochunk.getBlendingData(), immutablemap);
            CarvingMask.a carvingmask_a = (i1, j1, k1) -> {
                double d0 = (double) i1 + 0.5D + Blender.SHIFT_NOISE.getValue((double) i1, (double) j1, (double) k1) * 4.0D;
                double d1 = (double) j1 + 0.5D + Blender.SHIFT_NOISE.getValue((double) j1, (double) k1, (double) i1) * 4.0D;
                double d2 = (double) k1 + 0.5D + Blender.SHIFT_NOISE.getValue((double) k1, (double) i1, (double) j1) * 4.0D;

                return blender_c.getDistance(d0, d1, d2) < 4.0D;
            };
            Stream stream = Stream.of(WorldGenStage.Features.values());

            Objects.requireNonNull(protochunk);
            stream.map(protochunk::getOrCreateCarvingMask).forEach((carvingmask) -> {
                carvingmask.setAdditionalMask(carvingmask_a);
            });
        }
    }

    public static Blender.c makeOldChunkDistanceGetter(@Nullable BlendingData blendingdata, Map<EnumDirection8, BlendingData> map) {
        List<Blender.c> list = Lists.newArrayList();

        if (blendingdata != null) {
            list.add(makeOffsetOldChunkDistanceGetter((EnumDirection8) null, blendingdata));
        }

        map.forEach((enumdirection8, blendingdata1) -> {
            list.add(makeOffsetOldChunkDistanceGetter(enumdirection8, blendingdata1));
        });
        return (d0, d1, d2) -> {
            double d3 = Double.POSITIVE_INFINITY;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Blender.c blender_c = (Blender.c) iterator.next();
                double d4 = blender_c.getDistance(d0, d1, d2);

                if (d4 < d3) {
                    d3 = d4;
                }
            }

            return d3;
        };
    }

    private static Blender.c makeOffsetOldChunkDistanceGetter(@Nullable EnumDirection8 enumdirection8, BlendingData blendingdata) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        EnumDirection enumdirection;

        if (enumdirection8 != null) {
            for (Iterator iterator = enumdirection8.getDirections().iterator(); iterator.hasNext(); d1 += (double) (enumdirection.getStepZ() * 16)) {
                enumdirection = (EnumDirection) iterator.next();
                d0 += (double) (enumdirection.getStepX() * 16);
            }
        }

        double d2 = (double) blendingdata.getAreaWithOldGeneration().getHeight() / 2.0D;
        double d3 = (double) blendingdata.getAreaWithOldGeneration().getMinBuildHeight() + d2;

        return (d4, d5, d6) -> {
            return distanceToCube(d4 - 8.0D - d0, d5 - d3, d6 - 8.0D - d1, 8.0D, d2, 8.0D);
        };
    }

    private static double distanceToCube(double d0, double d1, double d2, double d3, double d4, double d5) {
        double d6 = Math.abs(d0) - d3;
        double d7 = Math.abs(d1) - d4;
        double d8 = Math.abs(d2) - d5;

        return MathHelper.length(Math.max(0.0D, d6), Math.max(0.0D, d7), Math.max(0.0D, d8));
    }

    private interface b {

        double get(BlendingData blendingdata, int i, int j, int k);
    }

    public static record a(double alpha, double blendingOffset) {

    }

    public interface c {

        double getDistance(double d0, double d1, double d2);
    }
}
