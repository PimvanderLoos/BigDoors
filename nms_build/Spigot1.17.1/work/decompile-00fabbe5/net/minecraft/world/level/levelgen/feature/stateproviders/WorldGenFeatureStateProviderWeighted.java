package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderWeighted extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderWeighted> CODEC = SimpleWeightedRandomList.a(IBlockData.CODEC).comapFlatMap(WorldGenFeatureStateProviderWeighted::a, (worldgenfeaturestateproviderweighted) -> {
        return worldgenfeaturestateproviderweighted.weightedList;
    }).fieldOf("entries").codec();
    private final SimpleWeightedRandomList<IBlockData> weightedList;

    private static DataResult<WorldGenFeatureStateProviderWeighted> a(SimpleWeightedRandomList<IBlockData> simpleweightedrandomlist) {
        return simpleweightedrandomlist.c() ? DataResult.error("WeightedStateProvider with no states") : DataResult.success(new WorldGenFeatureStateProviderWeighted(simpleweightedrandomlist));
    }

    public WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList<IBlockData> simpleweightedrandomlist) {
        this.weightedList = simpleweightedrandomlist;
    }

    public WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.a<IBlockData> simpleweightedrandomlist_a) {
        this(simpleweightedrandomlist_a.a());
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.WEIGHTED_STATE_PROVIDER;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        return (IBlockData) this.weightedList.a(random).orElseThrow(IllegalStateException::new);
    }
}
