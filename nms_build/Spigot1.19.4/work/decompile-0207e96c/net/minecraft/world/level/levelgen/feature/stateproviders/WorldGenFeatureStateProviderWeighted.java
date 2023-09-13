package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderWeighted extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderWeighted> CODEC = SimpleWeightedRandomList.wrappedCodec(IBlockData.CODEC).comapFlatMap(WorldGenFeatureStateProviderWeighted::create, (worldgenfeaturestateproviderweighted) -> {
        return worldgenfeaturestateproviderweighted.weightedList;
    }).fieldOf("entries").codec();
    private final SimpleWeightedRandomList<IBlockData> weightedList;

    private static DataResult<WorldGenFeatureStateProviderWeighted> create(SimpleWeightedRandomList<IBlockData> simpleweightedrandomlist) {
        return simpleweightedrandomlist.isEmpty() ? DataResult.error(() -> {
            return "WeightedStateProvider with no states";
        }) : DataResult.success(new WorldGenFeatureStateProviderWeighted(simpleweightedrandomlist));
    }

    public WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList<IBlockData> simpleweightedrandomlist) {
        this.weightedList = simpleweightedrandomlist;
    }

    public WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.a<IBlockData> simpleweightedrandomlist_a) {
        this(simpleweightedrandomlist_a.build());
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.WEIGHTED_STATE_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        return (IBlockData) this.weightedList.getRandomValue(randomsource).orElseThrow(IllegalStateException::new);
    }
}
