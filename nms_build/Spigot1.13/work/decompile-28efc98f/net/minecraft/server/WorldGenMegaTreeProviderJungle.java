package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenMegaTreeProviderJungle extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderJungle() {}

    @Nullable
    protected WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> b(Random random) {
        return new WorldGenTrees(true, 4 + random.nextInt(7), Blocks.JUNGLE_LOG.getBlockData(), Blocks.JUNGLE_LEAVES.getBlockData(), false);
    }

    @Nullable
    protected WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> a(Random random) {
        return new WorldGenJungleTree(true, 10, 20, Blocks.JUNGLE_LOG.getBlockData(), Blocks.JUNGLE_LEAVES.getBlockData());
    }
}
