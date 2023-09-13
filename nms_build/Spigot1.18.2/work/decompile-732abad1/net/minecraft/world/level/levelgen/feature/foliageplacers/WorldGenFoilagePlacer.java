package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

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

    public void createFoliage(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k) {
        this.createFoliage(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, i, worldgenfoilageplacer_a, j, k, this.offset(random));
    }

    protected abstract void createFoliage(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l);

    public abstract int foliageHeight(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration);

    public int foliageRadius(Random random, int i) {
        return this.radius.sample(random);
    }

    private int offset(Random random) {
        return this.offset.sample(random);
    }

    protected abstract boolean shouldSkipLocation(Random random, int i, int j, int k, int l, boolean flag);

    protected boolean shouldSkipLocationSigned(Random random, int i, int j, int k, int l, boolean flag) {
        int i1;
        int j1;

        if (flag) {
            i1 = Math.min(Math.abs(i), Math.abs(i - 1));
            j1 = Math.min(Math.abs(k), Math.abs(k - 1));
        } else {
            i1 = Math.abs(i);
            j1 = Math.abs(k);
        }

        return this.shouldSkipLocation(random, i1, j, j1, l, flag);
    }

    protected void placeLeavesRow(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition, int i, int j, boolean flag) {
        int k = flag ? 1 : 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = -i; l <= i + k; ++l) {
            for (int i1 = -i; i1 <= i + k; ++i1) {
                if (!this.shouldSkipLocationSigned(random, l, j, i1, i, flag)) {
                    blockposition_mutableblockposition.setWithOffset(blockposition, l, j, i1);
                    tryPlaceLeaf(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition_mutableblockposition);
                }
            }
        }

    }

    protected static void tryPlaceLeaf(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition) {
        if (WorldGenTrees.validTreePos(virtuallevelreadable, blockposition)) {
            biconsumer.accept(blockposition, worldgenfeaturetreeconfiguration.foliageProvider.getState(random, blockposition));
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
