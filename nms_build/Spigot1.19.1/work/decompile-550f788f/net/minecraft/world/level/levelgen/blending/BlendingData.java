package net.minecraft.world.level.levelgen.blending;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.EnumDirection8;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.HeightMap;

public class BlendingData {

    private static final double BLENDING_DENSITY_FACTOR = 0.1D;
    protected static final int CELL_WIDTH = 4;
    protected static final int CELL_HEIGHT = 8;
    protected static final int CELL_RATIO = 2;
    private static final double SOLID_DENSITY = 1.0D;
    private static final double AIR_DENSITY = -1.0D;
    private static final int CELLS_PER_SECTION_Y = 2;
    private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
    private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE = BlendingData.QUARTS_PER_SECTION - 1;
    private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = BlendingData.QUARTS_PER_SECTION;
    private static final int CELL_COLUMN_INSIDE_COUNT = 2 * BlendingData.CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
    private static final int CELL_COLUMN_OUTSIDE_COUNT = 2 * BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
    private static final int CELL_COLUMN_COUNT = BlendingData.CELL_COLUMN_INSIDE_COUNT + BlendingData.CELL_COLUMN_OUTSIDE_COUNT;
    private final LevelHeightAccessor areaWithOldGeneration;
    private static final List<Block> SURFACE_BLOCKS = List.of(Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT);
    protected static final double NO_VALUE = Double.MAX_VALUE;
    private boolean hasCalculatedData;
    private final double[] heights;
    private final List<List<Holder<BiomeBase>>> biomes;
    private final transient double[][] densities;
    private static final Codec<double[]> DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
    public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("min_section").forGetter((blendingdata) -> {
            return blendingdata.areaWithOldGeneration.getMinSection();
        }), Codec.INT.fieldOf("max_section").forGetter((blendingdata) -> {
            return blendingdata.areaWithOldGeneration.getMaxSection();
        }), BlendingData.DOUBLE_ARRAY_CODEC.optionalFieldOf("heights").forGetter((blendingdata) -> {
            return DoubleStream.of(blendingdata.heights).anyMatch((d0) -> {
                return d0 != Double.MAX_VALUE;
            }) ? Optional.of(blendingdata.heights) : Optional.empty();
        })).apply(instance, BlendingData::new);
    }).comapFlatMap(BlendingData::validateArraySize, Function.identity());

    private static DataResult<BlendingData> validateArraySize(BlendingData blendingdata) {
        return blendingdata.heights.length != BlendingData.CELL_COLUMN_COUNT ? DataResult.error("heights has to be of length " + BlendingData.CELL_COLUMN_COUNT) : DataResult.success(blendingdata);
    }

    private BlendingData(int i, int j, Optional<double[]> optional) {
        this.heights = (double[]) optional.orElse((double[]) SystemUtils.make(new double[BlendingData.CELL_COLUMN_COUNT], (adouble) -> {
            Arrays.fill(adouble, Double.MAX_VALUE);
        }));
        this.densities = new double[BlendingData.CELL_COLUMN_COUNT][];
        ObjectArrayList<List<Holder<BiomeBase>>> objectarraylist = new ObjectArrayList(BlendingData.CELL_COLUMN_COUNT);

        objectarraylist.size(BlendingData.CELL_COLUMN_COUNT);
        this.biomes = objectarraylist;
        int k = SectionPosition.sectionToBlockCoord(i);
        int l = SectionPosition.sectionToBlockCoord(j) - k;

        this.areaWithOldGeneration = LevelHeightAccessor.create(k, l);
    }

    @Nullable
    public static BlendingData getOrUpdateBlendingData(RegionLimitedWorldAccess regionlimitedworldaccess, int i, int j) {
        IChunkAccess ichunkaccess = regionlimitedworldaccess.getChunk(i, j);
        BlendingData blendingdata = ichunkaccess.getBlendingData();

        if (blendingdata == null) {
            return null;
        } else {
            blendingdata.calculateData(ichunkaccess, sideByGenerationAge(regionlimitedworldaccess, i, j, false));
            return blendingdata;
        }
    }

    public static Set<EnumDirection8> sideByGenerationAge(GeneratorAccessSeed generatoraccessseed, int i, int j, boolean flag) {
        Set<EnumDirection8> set = EnumSet.noneOf(EnumDirection8.class);
        EnumDirection8[] aenumdirection8 = EnumDirection8.values();
        int k = aenumdirection8.length;

        for (int l = 0; l < k; ++l) {
            EnumDirection8 enumdirection8 = aenumdirection8[l];
            int i1 = i + enumdirection8.getStepX();
            int j1 = j + enumdirection8.getStepZ();

            if (generatoraccessseed.getChunk(i1, j1).isOldNoiseGeneration() == flag) {
                set.add(enumdirection8);
            }
        }

        return set;
    }

    private void calculateData(IChunkAccess ichunkaccess, Set<EnumDirection8> set) {
        if (!this.hasCalculatedData) {
            if (set.contains(EnumDirection8.NORTH) || set.contains(EnumDirection8.WEST) || set.contains(EnumDirection8.NORTH_WEST)) {
                this.addValuesForColumn(getInsideIndex(0, 0), ichunkaccess, 0, 0);
            }

            int i;

            if (set.contains(EnumDirection8.NORTH)) {
                for (i = 1; i < BlendingData.QUARTS_PER_SECTION; ++i) {
                    this.addValuesForColumn(getInsideIndex(i, 0), ichunkaccess, 4 * i, 0);
                }
            }

            if (set.contains(EnumDirection8.WEST)) {
                for (i = 1; i < BlendingData.QUARTS_PER_SECTION; ++i) {
                    this.addValuesForColumn(getInsideIndex(0, i), ichunkaccess, 0, 4 * i);
                }
            }

            if (set.contains(EnumDirection8.EAST)) {
                for (i = 1; i < BlendingData.QUARTS_PER_SECTION; ++i) {
                    this.addValuesForColumn(getOutsideIndex(BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, i), ichunkaccess, 15, 4 * i);
                }
            }

            if (set.contains(EnumDirection8.SOUTH)) {
                for (i = 0; i < BlendingData.QUARTS_PER_SECTION; ++i) {
                    this.addValuesForColumn(getOutsideIndex(i, BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), ichunkaccess, 4 * i, 15);
                }
            }

            if (set.contains(EnumDirection8.EAST) && set.contains(EnumDirection8.NORTH_EAST)) {
                this.addValuesForColumn(getOutsideIndex(BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, 0), ichunkaccess, 15, 0);
            }

            if (set.contains(EnumDirection8.EAST) && set.contains(EnumDirection8.SOUTH) && set.contains(EnumDirection8.SOUTH_EAST)) {
                this.addValuesForColumn(getOutsideIndex(BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), ichunkaccess, 15, 15);
            }

            this.hasCalculatedData = true;
        }
    }

    private void addValuesForColumn(int i, IChunkAccess ichunkaccess, int j, int k) {
        if (this.heights[i] == Double.MAX_VALUE) {
            this.heights[i] = (double) this.getHeightAtXZ(ichunkaccess, j, k);
        }

        this.densities[i] = this.getDensityColumn(ichunkaccess, j, k, MathHelper.floor(this.heights[i]));
        this.biomes.set(i, this.getBiomeColumn(ichunkaccess, j, k));
    }

    private int getHeightAtXZ(IChunkAccess ichunkaccess, int i, int j) {
        int k;

        if (ichunkaccess.hasPrimedHeightmap(HeightMap.Type.WORLD_SURFACE_WG)) {
            k = Math.min(ichunkaccess.getHeight(HeightMap.Type.WORLD_SURFACE_WG, i, j) + 1, this.areaWithOldGeneration.getMaxBuildHeight());
        } else {
            k = this.areaWithOldGeneration.getMaxBuildHeight();
        }

        int l = this.areaWithOldGeneration.getMinBuildHeight();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, k, j);

        do {
            if (blockposition_mutableblockposition.getY() <= l) {
                return l;
            }

            blockposition_mutableblockposition.move(EnumDirection.DOWN);
        } while (!BlendingData.SURFACE_BLOCKS.contains(ichunkaccess.getBlockState(blockposition_mutableblockposition).getBlock()));

        return blockposition_mutableblockposition.getY();
    }

    private static double read1(IChunkAccess ichunkaccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        return isGround(ichunkaccess, blockposition_mutableblockposition.move(EnumDirection.DOWN)) ? 1.0D : -1.0D;
    }

    private static double read7(IChunkAccess ichunkaccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        double d0 = 0.0D;

        for (int i = 0; i < 7; ++i) {
            d0 += read1(ichunkaccess, blockposition_mutableblockposition);
        }

        return d0;
    }

    private double[] getDensityColumn(IChunkAccess ichunkaccess, int i, int j, int k) {
        double[] adouble = new double[this.cellCountPerColumn()];

        Arrays.fill(adouble, -1.0D);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, this.areaWithOldGeneration.getMaxBuildHeight(), j);
        double d0 = read7(ichunkaccess, blockposition_mutableblockposition);

        double d1;
        double d2;
        int l;

        for (l = adouble.length - 2; l >= 0; --l) {
            d1 = read1(ichunkaccess, blockposition_mutableblockposition);
            d2 = read7(ichunkaccess, blockposition_mutableblockposition);
            adouble[l] = (d0 + d1 + d2) / 15.0D;
            d0 = d2;
        }

        l = this.getCellYIndex(MathHelper.intFloorDiv(k, 8));
        if (l >= 0 && l < adouble.length - 1) {
            d1 = ((double) k + 0.5D) % 8.0D / 8.0D;
            d2 = (1.0D - d1) / d1;
            double d3 = Math.max(d2, 1.0D) * 0.25D;

            adouble[l + 1] = -d2 / d3;
            adouble[l] = 1.0D / d3;
        }

        return adouble;
    }

    private List<Holder<BiomeBase>> getBiomeColumn(IChunkAccess ichunkaccess, int i, int j) {
        ObjectArrayList<Holder<BiomeBase>> objectarraylist = new ObjectArrayList(this.quartCountPerColumn());

        objectarraylist.size(this.quartCountPerColumn());

        for (int k = 0; k < objectarraylist.size(); ++k) {
            int l = k + QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());

            objectarraylist.set(k, ichunkaccess.getNoiseBiome(QuartPos.fromBlock(i), l, QuartPos.fromBlock(j)));
        }

        return objectarraylist;
    }

    private static boolean isGround(IChunkAccess ichunkaccess, BlockPosition blockposition) {
        IBlockData iblockdata = ichunkaccess.getBlockState(blockposition);

        return iblockdata.isAir() ? false : (iblockdata.is(TagsBlock.LEAVES) ? false : (iblockdata.is(TagsBlock.LOGS) ? false : (!iblockdata.is(Blocks.BROWN_MUSHROOM_BLOCK) && !iblockdata.is(Blocks.RED_MUSHROOM_BLOCK) ? !iblockdata.getCollisionShape(ichunkaccess, blockposition).isEmpty() : false)));
    }

    protected double getHeight(int i, int j, int k) {
        return i != BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && k != BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE ? (i != 0 && k != 0 ? Double.MAX_VALUE : this.heights[getInsideIndex(i, k)]) : this.heights[getOutsideIndex(i, k)];
    }

    private double getDensity(@Nullable double[] adouble, int i) {
        if (adouble == null) {
            return Double.MAX_VALUE;
        } else {
            int j = this.getCellYIndex(i);

            return j >= 0 && j < adouble.length ? adouble[j] * 0.1D : Double.MAX_VALUE;
        }
    }

    protected double getDensity(int i, int j, int k) {
        return j == this.getMinY() ? 0.1D : (i != BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && k != BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE ? (i != 0 && k != 0 ? Double.MAX_VALUE : this.getDensity(this.densities[getInsideIndex(i, k)], j)) : this.getDensity(this.densities[getOutsideIndex(i, k)], j));
    }

    protected void iterateBiomes(int i, int j, int k, BlendingData.a blendingdata_a) {
        if (j >= QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight()) && j < QuartPos.fromBlock(this.areaWithOldGeneration.getMaxBuildHeight())) {
            int l = j - QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());

            for (int i1 = 0; i1 < this.biomes.size(); ++i1) {
                if (this.biomes.get(i1) != null) {
                    Holder<BiomeBase> holder = (Holder) ((List) this.biomes.get(i1)).get(l);

                    if (holder != null) {
                        blendingdata_a.consume(i + getX(i1), k + getZ(i1), holder);
                    }
                }
            }

        }
    }

    protected void iterateHeights(int i, int j, BlendingData.c blendingdata_c) {
        for (int k = 0; k < this.heights.length; ++k) {
            double d0 = this.heights[k];

            if (d0 != Double.MAX_VALUE) {
                blendingdata_c.consume(i + getX(k), j + getZ(k), d0);
            }
        }

    }

    protected void iterateDensities(int i, int j, int k, int l, BlendingData.b blendingdata_b) {
        int i1 = this.getColumnMinY();
        int j1 = Math.max(0, k - i1);
        int k1 = Math.min(this.cellCountPerColumn(), l - i1);

        for (int l1 = 0; l1 < this.densities.length; ++l1) {
            double[] adouble = this.densities[l1];

            if (adouble != null) {
                int i2 = i + getX(l1);
                int j2 = j + getZ(l1);

                for (int k2 = j1; k2 < k1; ++k2) {
                    blendingdata_b.consume(i2, k2 + i1, j2, adouble[k2] * 0.1D);
                }
            }
        }

    }

    private int cellCountPerColumn() {
        return this.areaWithOldGeneration.getSectionsCount() * 2;
    }

    private int quartCountPerColumn() {
        return QuartPos.fromSection(this.areaWithOldGeneration.getSectionsCount());
    }

    private int getColumnMinY() {
        return this.getMinY() + 1;
    }

    private int getMinY() {
        return this.areaWithOldGeneration.getMinSection() * 2;
    }

    private int getCellYIndex(int i) {
        return i - this.getColumnMinY();
    }

    private static int getInsideIndex(int i, int j) {
        return BlendingData.CELL_HORIZONTAL_MAX_INDEX_INSIDE - i + j;
    }

    private static int getOutsideIndex(int i, int j) {
        return BlendingData.CELL_COLUMN_INSIDE_COUNT + i + BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - j;
    }

    private static int getX(int i) {
        if (i < BlendingData.CELL_COLUMN_INSIDE_COUNT) {
            return zeroIfNegative(BlendingData.CELL_HORIZONTAL_MAX_INDEX_INSIDE - i);
        } else {
            int j = i - BlendingData.CELL_COLUMN_INSIDE_COUNT;

            return BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - j);
        }
    }

    private static int getZ(int i) {
        if (i < BlendingData.CELL_COLUMN_INSIDE_COUNT) {
            return zeroIfNegative(i - BlendingData.CELL_HORIZONTAL_MAX_INDEX_INSIDE);
        } else {
            int j = i - BlendingData.CELL_COLUMN_INSIDE_COUNT;

            return BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(j - BlendingData.CELL_HORIZONTAL_MAX_INDEX_OUTSIDE);
        }
    }

    private static int zeroIfNegative(int i) {
        return i & ~(i >> 31);
    }

    public LevelHeightAccessor getAreaWithOldGeneration() {
        return this.areaWithOldGeneration;
    }

    protected interface a {

        void consume(int i, int j, Holder<BiomeBase> holder);
    }

    protected interface c {

        void consume(int i, int j, double d0);
    }

    protected interface b {

        void consume(int i, int j, int k, double d0);
    }
}
