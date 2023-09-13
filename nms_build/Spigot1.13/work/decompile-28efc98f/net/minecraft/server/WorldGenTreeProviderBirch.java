package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderBirch extends WorldGenTreeProvider {

    public WorldGenTreeProviderBirch() {}

    @Nullable
    protected WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> b(Random random) {
        return new WorldGenForest(true, false);
    }
}
