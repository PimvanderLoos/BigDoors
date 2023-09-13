package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.behavior.WeightedList;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderWeighted extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderWeighted> b = WeightedList.a(IBlockData.b).comapFlatMap(WorldGenFeatureStateProviderWeighted::a, (worldgenfeaturestateproviderweighted) -> {
        return worldgenfeaturestateproviderweighted.c;
    }).fieldOf("entries").codec();
    private final WeightedList<IBlockData> c;

    private static DataResult<WorldGenFeatureStateProviderWeighted> a(WeightedList<IBlockData> weightedlist) {
        return weightedlist.b() ? DataResult.error("WeightedStateProvider with no states") : DataResult.success(new WorldGenFeatureStateProviderWeighted(weightedlist));
    }

    private WorldGenFeatureStateProviderWeighted(WeightedList<IBlockData> weightedlist) {
        this.c = weightedlist;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.b;
    }

    public WorldGenFeatureStateProviderWeighted() {
        this(new WeightedList<>());
    }

    public WorldGenFeatureStateProviderWeighted a(IBlockData iblockdata, int i) {
        this.c.a(iblockdata, i);
        return this;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        return (IBlockData) this.c.b(random);
    }
}
