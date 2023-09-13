package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public abstract class TrunkPlacer {

    public static final Codec<TrunkPlacer> CODEC = BuiltInRegistries.TRUNK_PLACER_TYPE.byNameCodec().dispatch(TrunkPlacer::type, TrunkPlacers::codec);
    private static final int MAX_BASE_HEIGHT = 32;
    private static final int MAX_RAND = 24;
    public static final int MAX_HEIGHT = 80;
    protected final int baseHeight;
    protected final int heightRandA;
    protected final int heightRandB;

    protected static <P extends TrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> trunkPlacerParts(Instance<P> instance) {
        return instance.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((trunkplacer) -> {
            return trunkplacer.baseHeight;
        }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((trunkplacer) -> {
            return trunkplacer.heightRandA;
        }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((trunkplacer) -> {
            return trunkplacer.heightRandB;
        }));
    }

    public TrunkPlacer(int i, int j, int k) {
        this.baseHeight = i;
        this.heightRandA = j;
        this.heightRandB = k;
    }

    protected abstract TrunkPlacers<?> type();

    public abstract List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration);

    public int getTreeHeight(RandomSource randomsource) {
        return this.baseHeight + randomsource.nextInt(this.heightRandA + 1) + randomsource.nextInt(this.heightRandB + 1);
    }

    private static boolean isDirt(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return WorldGenerator.isDirt(iblockdata) && !iblockdata.is(Blocks.GRASS_BLOCK) && !iblockdata.is(Blocks.MYCELIUM);
        });
    }

    protected static void setDirtAt(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (worldgenfeaturetreeconfiguration.forceDirt || !isDirt(virtuallevelreadable, blockposition)) {
            biconsumer.accept(blockposition, worldgenfeaturetreeconfiguration.dirtProvider.getState(randomsource, blockposition));
        }

    }

    protected boolean placeLog(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition, worldgenfeaturetreeconfiguration, Function.identity());
    }

    protected boolean placeLog(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, Function<IBlockData, IBlockData> function) {
        if (this.validTreePos(virtuallevelreadable, blockposition)) {
            biconsumer.accept(blockposition, (IBlockData) function.apply(worldgenfeaturetreeconfiguration.trunkProvider.getState(randomsource, blockposition)));
            return true;
        } else {
            return false;
        }
    }

    protected void placeLogIfFree(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (this.isFree(virtuallevelreadable, blockposition_mutableblockposition)) {
            this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
        }

    }

    protected boolean validTreePos(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return WorldGenTrees.validTreePos(virtuallevelreadable, blockposition);
    }

    public boolean isFree(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return this.validTreePos(virtuallevelreadable, blockposition) || virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is(TagsBlock.LOGS);
        });
    }
}
