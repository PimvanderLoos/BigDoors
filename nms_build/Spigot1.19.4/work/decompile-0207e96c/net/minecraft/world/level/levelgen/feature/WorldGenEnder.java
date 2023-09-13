package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockIronBars;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;
import net.minecraft.world.phys.AxisAlignedBB;

public class WorldGenEnder extends WorldGenerator<WorldGenFeatureEndSpikeConfiguration> {

    public static final int NUMBER_OF_SPIKES = 10;
    private static final int SPIKE_DISTANCE = 42;
    private static final LoadingCache<Long, List<WorldGenEnder.Spike>> SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new WorldGenEnder.b());

    public WorldGenEnder(Codec<WorldGenFeatureEndSpikeConfiguration> codec) {
        super(codec);
    }

    public static List<WorldGenEnder.Spike> getSpikesForLevel(GeneratorAccessSeed generatoraccessseed) {
        RandomSource randomsource = RandomSource.create(generatoraccessseed.getSeed());
        long i = randomsource.nextLong() & 65535L;

        return (List) WorldGenEnder.SPIKE_CACHE.getUnchecked(i);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEndSpikeConfiguration> featureplacecontext) {
        WorldGenFeatureEndSpikeConfiguration worldgenfeatureendspikeconfiguration = (WorldGenFeatureEndSpikeConfiguration) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        BlockPosition blockposition = featureplacecontext.origin();
        List<WorldGenEnder.Spike> list = worldgenfeatureendspikeconfiguration.getSpikes();

        if (list.isEmpty()) {
            list = getSpikesForLevel(generatoraccessseed);
        }

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenEnder.Spike worldgenender_spike = (WorldGenEnder.Spike) iterator.next();

            if (worldgenender_spike.isCenterWithinChunk(blockposition)) {
                this.placeSpike(generatoraccessseed, randomsource, worldgenfeatureendspikeconfiguration, worldgenender_spike);
            }
        }

        return true;
    }

    private void placeSpike(WorldAccess worldaccess, RandomSource randomsource, WorldGenFeatureEndSpikeConfiguration worldgenfeatureendspikeconfiguration, WorldGenEnder.Spike worldgenender_spike) {
        int i = worldgenender_spike.getRadius();
        Iterator iterator = BlockPosition.betweenClosed(new BlockPosition(worldgenender_spike.getCenterX() - i, worldaccess.getMinBuildHeight(), worldgenender_spike.getCenterZ() - i), new BlockPosition(worldgenender_spike.getCenterX() + i, worldgenender_spike.getHeight() + 10, worldgenender_spike.getCenterZ() + i)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (blockposition.distToLowCornerSqr((double) worldgenender_spike.getCenterX(), (double) blockposition.getY(), (double) worldgenender_spike.getCenterZ()) <= (double) (i * i + 1) && blockposition.getY() < worldgenender_spike.getHeight()) {
                this.setBlock(worldaccess, blockposition, Blocks.OBSIDIAN.defaultBlockState());
            } else if (blockposition.getY() > 65) {
                this.setBlock(worldaccess, blockposition, Blocks.AIR.defaultBlockState());
            }
        }

        if (worldgenender_spike.isGuarded()) {
            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int j = -2; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    for (int l = 0; l <= 3; ++l) {
                        boolean flag3 = MathHelper.abs(j) == 2;
                        boolean flag4 = MathHelper.abs(k) == 2;
                        boolean flag5 = l == 3;

                        if (flag3 || flag4 || flag5) {
                            boolean flag6 = j == -2 || j == 2 || flag5;
                            boolean flag7 = k == -2 || k == 2 || flag5;
                            IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) Blocks.IRON_BARS.defaultBlockState().setValue(BlockIronBars.NORTH, flag6 && k != -2)).setValue(BlockIronBars.SOUTH, flag6 && k != 2)).setValue(BlockIronBars.WEST, flag7 && j != -2)).setValue(BlockIronBars.EAST, flag7 && j != 2);

                            this.setBlock(worldaccess, blockposition_mutableblockposition.set(worldgenender_spike.getCenterX() + j, worldgenender_spike.getHeight() + l, worldgenender_spike.getCenterZ() + k), iblockdata);
                        }
                    }
                }
            }
        }

        EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) EntityTypes.END_CRYSTAL.create(worldaccess.getLevel());

        if (entityendercrystal != null) {
            entityendercrystal.setBeamTarget(worldgenfeatureendspikeconfiguration.getCrystalBeamTarget());
            entityendercrystal.setInvulnerable(worldgenfeatureendspikeconfiguration.isCrystalInvulnerable());
            entityendercrystal.moveTo((double) worldgenender_spike.getCenterX() + 0.5D, (double) (worldgenender_spike.getHeight() + 1), (double) worldgenender_spike.getCenterZ() + 0.5D, randomsource.nextFloat() * 360.0F, 0.0F);
            worldaccess.addFreshEntity(entityendercrystal);
            this.setBlock(worldaccess, new BlockPosition(worldgenender_spike.getCenterX(), worldgenender_spike.getHeight(), worldgenender_spike.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
        }

    }

    public static class Spike {

        public static final Codec<WorldGenEnder.Spike> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("centerX").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.centerX;
            }), Codec.INT.fieldOf("centerZ").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.centerZ;
            }), Codec.INT.fieldOf("radius").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.radius;
            }), Codec.INT.fieldOf("height").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.height;
            }), Codec.BOOL.fieldOf("guarded").orElse(false).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.guarded;
            })).apply(instance, WorldGenEnder.Spike::new);
        });
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AxisAlignedBB topBoundingBox;

        public Spike(int i, int j, int k, int l, boolean flag) {
            this.centerX = i;
            this.centerZ = j;
            this.radius = k;
            this.height = l;
            this.guarded = flag;
            this.topBoundingBox = new AxisAlignedBB((double) (i - k), (double) DimensionManager.MIN_Y, (double) (j - k), (double) (i + k), (double) DimensionManager.MAX_Y, (double) (j + k));
        }

        public boolean isCenterWithinChunk(BlockPosition blockposition) {
            return SectionPosition.blockToSectionCoord(blockposition.getX()) == SectionPosition.blockToSectionCoord(this.centerX) && SectionPosition.blockToSectionCoord(blockposition.getZ()) == SectionPosition.blockToSectionCoord(this.centerZ);
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public AxisAlignedBB getTopBoundingBox() {
            return this.topBoundingBox;
        }
    }

    private static class b extends CacheLoader<Long, List<WorldGenEnder.Spike>> {

        b() {}

        public List<WorldGenEnder.Spike> load(Long olong) {
            IntArrayList intarraylist = SystemUtils.toShuffledList(IntStream.range(0, 10), RandomSource.create(olong));
            List<WorldGenEnder.Spike> list = Lists.newArrayList();

            for (int i = 0; i < 10; ++i) {
                int j = MathHelper.floor(42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int k = MathHelper.floor(42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int l = intarraylist.get(i);
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;

                list.add(new WorldGenEnder.Spike(j, k, i1, j1, flag));
            }

            return list;
        }
    }
}
