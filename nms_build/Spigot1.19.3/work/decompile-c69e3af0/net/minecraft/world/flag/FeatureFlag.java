package net.minecraft.world.flag;

public class FeatureFlag {

    final FeatureFlagUniverse universe;
    final long mask;

    FeatureFlag(FeatureFlagUniverse featureflaguniverse, int i) {
        this.universe = featureflaguniverse;
        this.mask = 1L << i;
    }
}
