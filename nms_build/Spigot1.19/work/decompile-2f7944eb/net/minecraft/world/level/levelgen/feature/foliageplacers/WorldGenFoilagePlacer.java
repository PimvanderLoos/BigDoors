package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.material.FluidTypes;

public abstract class WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacer> CODEC = IRegistry.FOLIAGE_PLACER_TYPES.byNameCodec().dispatch(WorldGenFoilagePlacer::type, WorldGenFoilagePlacers::codec);
    protected final IntProvider radius;
    protected final IntProvider offset;

    protected static <P extends WorldGenFoilagePlacer> P2<Mu<P>, IntProvider, IntProvider> foliagePlacerParts(Instance<P> instance) {
        return instance.group(IntProvider.codec(0, 16).fieldOf("radius").forGetter((worldgenfoilageplacer) -> {
            return worldgenfoilageplacer.radius;
        }), IntProvider.codec(0, 16).fieldOf("offset").forGetter((worldgenfoilageplacer) -> {
            return worldgenfoilageplacer.offset;
        }));
    }

    public WorldGenFoilagePlacer(IntProvider intprovider, IntProvider intprovider1) {
        this.radius = intprovider;
        this.offset = intprovider1;
    }

    protected abstract WorldGenFoilagePlacers<?> type();

    public void createFoliage(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k) {
        this.createFoliage(virtuallevelreadable, biconsumer, randomsource, worldgenfeaturetreeconfiguration, i, worldgenfoilageplacer_a, j, k, this.offset(randomsource));
    }

    protected abstract void createFoliage(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l);

    public abstract int foliageHeight(RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration);

    public int foliageRadius(RandomSource randomsource, int i) {
        return this.radius.sample(randomsource);
    }

    private int offset(RandomSource randomsource) {
        return this.offset.sample(randomsource);
    }

    protected abstract boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag);

    protected boolean shouldSkipLocationSigned(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        int i1;
        int j1;

        if (flag) {
            i1 = Math.min(Math.abs(i), Math.abs(i - 1));
            j1 = Math.min(Math.abs(k), Math.abs(k - 1));
        } else {
            i1 = Math.abs(i);
            j1 = Math.abs(k);
        }

        return this.shouldSkipLocation(randomsource, i1, j, j1, l, flag);
    }

    protected void placeLeavesRow(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition, int i, int j, boolean flag) {
        int k = flag ? 1 : 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = -i; l <= i + k; ++l) {
            for (int i1 = -i; i1 <= i + k; ++i1) {
                if (!this.shouldSkipLocationSigned(randomsource, l, j, i1, i, flag)) {
                    blockposition_mutableblockposition.setWithOffset(blockposition, l, j, i1);
                    tryPlaceLeaf(virtuallevelreadable, biconsumer, randomsource, worldgenfeaturetreeconfiguration, blockposition_mutableblockposition);
                }
            }
        }

    }

    protected static void tryPlaceLeaf(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition) {
        if (WorldGenTrees.validTreePos(virtuallevelreadable, blockposition)) {
            IBlockData iblockdata = worldgenfeaturetreeconfiguration.foliageProvider.getState(randomsource, blockposition);

            if (iblockdata.hasProperty(BlockProperties.WATERLOGGED)) {
                iblockdata = (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, virtuallevelreadable.isFluidAtPosition(blockposition, (fluid) -> {
                    return fluid.isSourceOfType(FluidTypes.WATER);
                }));
            }

            biconsumer.accept(blockposition, iblockdata);
        }

    }

    public static final class a {

        private final BlockPosition pos;
        private final int radiusOffset;
        private final boolean doubleTrunk;

        public a(BlockPosition blockposition, int i, boolean flag) {
            this.pos = blockposition;
            this.radiusOffset = i;
            this.doubleTrunk = flag;
        }

        public BlockPosition pos() {
            return this.pos;
        }

        public int radiusOffset() {
            return this.radiusOffset;
        }

        public boolean doubleTrunk() {
            return this.doubleTrunk;
        }
    }
}
