package net.minecraft.data.worldgen;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverAbstract;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;

public class WorldGenCarvers {

    public static final WorldGenCarverWrapper<WorldGenFeatureConfigurationChance> a = a("cave", WorldGenCarverAbstract.a.a((WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.14285715F))));
    public static final WorldGenCarverWrapper<WorldGenFeatureConfigurationChance> b = a("canyon", WorldGenCarverAbstract.c.a((WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
    public static final WorldGenCarverWrapper<WorldGenFeatureConfigurationChance> c = a("ocean_cave", WorldGenCarverAbstract.a.a((WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
    public static final WorldGenCarverWrapper<WorldGenFeatureConfigurationChance> d = a("underwater_canyon", WorldGenCarverAbstract.d.a((WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
    public static final WorldGenCarverWrapper<WorldGenFeatureConfigurationChance> e = a("underwater_cave", WorldGenCarverAbstract.e.a((WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
    public static final WorldGenCarverWrapper<WorldGenFeatureConfigurationChance> f = a("nether_cave", WorldGenCarverAbstract.b.a((WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))));

    private static <WC extends WorldGenCarverConfiguration> WorldGenCarverWrapper<WC> a(String s, WorldGenCarverWrapper<WC> worldgencarverwrapper) {
        return (WorldGenCarverWrapper) RegistryGeneration.a(RegistryGeneration.d, s, (Object) worldgencarverwrapper);
    }
}
