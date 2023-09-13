package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFeatureStateProviders<P extends WorldGenFeatureStateProvider> {

    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderSimpl> a = a("simple_state_provider", WorldGenFeatureStateProviderSimpl::new);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderWeighted> b = a("weighted_state_provider", WorldGenFeatureStateProviderWeighted::new);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderPlainFlower> c = a("plain_flower_provider", WorldGenFeatureStateProviderPlainFlower::new);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderForestFlower> d = a("forest_flower_provider", WorldGenFeatureStateProviderForestFlower::new);
    private final Function<Dynamic<?>, P> e;

    private static <P extends WorldGenFeatureStateProvider> WorldGenFeatureStateProviders<P> a(String s, Function<Dynamic<?>, P> function) {
        return (WorldGenFeatureStateProviders) IRegistry.a(IRegistry.t, s, (Object) (new WorldGenFeatureStateProviders<>(function)));
    }

    private WorldGenFeatureStateProviders(Function<Dynamic<?>, P> function) {
        this.e = function;
    }

    public P a(Dynamic<?> dynamic) {
        return (WorldGenFeatureStateProvider) this.e.apply(dynamic);
    }
}
