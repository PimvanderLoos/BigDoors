package net.minecraft.data.worldgen;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverAbstract;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class WorldGenCarvers {

    public static final WorldGenCarverWrapper<CaveCarverConfiguration> CAVE = a("cave", WorldGenCarverAbstract.CAVE.a((WorldGenCarverConfiguration) (new CaveCarverConfiguration(0.14285715F, BiasedToBottomHeight.a(VerticalAnchor.a(0), VerticalAnchor.a(127), 8), ConstantFloat.a(0.5F), VerticalAnchor.b(10), false, CarverDebugSettings.a(false, Blocks.CRIMSON_BUTTON.getBlockData()), ConstantFloat.a(1.0F), ConstantFloat.a(1.0F), ConstantFloat.a(-0.7F)))));
    public static final WorldGenCarverWrapper<CaveCarverConfiguration> PROTOTYPE_CAVE = a("prototype_cave", WorldGenCarverAbstract.CAVE.a((WorldGenCarverConfiguration) (new CaveCarverConfiguration(0.33333334F, UniformHeight.a(VerticalAnchor.b(8), VerticalAnchor.a(126)), UniformFloat.b(0.1F, 0.9F), VerticalAnchor.b(8), false, CarverDebugSettings.a(false, Blocks.CRIMSON_BUTTON.getBlockData()), UniformFloat.b(0.7F, 1.4F), UniformFloat.b(0.8F, 1.3F), UniformFloat.b(-1.0F, -0.4F)))));
    public static final WorldGenCarverWrapper<CanyonCarverConfiguration> CANYON = a("canyon", WorldGenCarverAbstract.CANYON.a((WorldGenCarverConfiguration) (new CanyonCarverConfiguration(0.02F, BiasedToBottomHeight.a(VerticalAnchor.a(20), VerticalAnchor.a(67), 8), ConstantFloat.a(3.0F), VerticalAnchor.b(10), false, CarverDebugSettings.a(false, Blocks.WARPED_BUTTON.getBlockData()), UniformFloat.b(-0.125F, 0.125F), new CanyonCarverConfiguration.a(UniformFloat.b(0.75F, 1.0F), TrapezoidFloat.a(0.0F, 6.0F, 2.0F), 3, UniformFloat.b(0.75F, 1.0F), 1.0F, 0.0F)))));
    public static final WorldGenCarverWrapper<CanyonCarverConfiguration> PROTOTYPE_CANYON = a("prototype_canyon", WorldGenCarverAbstract.CANYON.a((WorldGenCarverConfiguration) (new CanyonCarverConfiguration(0.02F, UniformHeight.a(VerticalAnchor.a(10), VerticalAnchor.a(67)), ConstantFloat.a(3.0F), VerticalAnchor.b(8), false, CarverDebugSettings.a(false, Blocks.WARPED_BUTTON.getBlockData()), UniformFloat.b(-0.125F, 0.125F), new CanyonCarverConfiguration.a(UniformFloat.b(0.75F, 1.0F), TrapezoidFloat.a(0.0F, 6.0F, 2.0F), 3, UniformFloat.b(0.75F, 1.0F), 1.0F, 0.0F)))));
    public static final WorldGenCarverWrapper<CaveCarverConfiguration> OCEAN_CAVE = a("ocean_cave", WorldGenCarverAbstract.CAVE.a((WorldGenCarverConfiguration) (new CaveCarverConfiguration(0.06666667F, BiasedToBottomHeight.a(VerticalAnchor.a(0), VerticalAnchor.a(127), 8), ConstantFloat.a(0.5F), VerticalAnchor.b(10), false, CarverDebugSettings.a(false, Blocks.CRIMSON_BUTTON.getBlockData()), ConstantFloat.a(1.0F), ConstantFloat.a(1.0F), ConstantFloat.a(-0.7F)))));
    public static final WorldGenCarverWrapper<CanyonCarverConfiguration> UNDERWATER_CANYON = a("underwater_canyon", WorldGenCarverAbstract.UNDERWATER_CANYON.a((WorldGenCarverConfiguration) (new CanyonCarverConfiguration(0.02F, BiasedToBottomHeight.a(VerticalAnchor.a(20), VerticalAnchor.a(67), 8), ConstantFloat.a(3.0F), VerticalAnchor.b(10), false, CarverDebugSettings.a(false, Blocks.WARPED_BUTTON.getBlockData()), UniformFloat.b(-0.125F, 0.125F), new CanyonCarverConfiguration.a(UniformFloat.b(0.75F, 1.0F), TrapezoidFloat.a(0.0F, 6.0F, 2.0F), 3, UniformFloat.b(0.75F, 1.0F), 1.0F, 0.0F)))));
    public static final WorldGenCarverWrapper<CaveCarverConfiguration> UNDERWATER_CAVE = a("underwater_cave", WorldGenCarverAbstract.UNDERWATER_CAVE.a((WorldGenCarverConfiguration) (new CaveCarverConfiguration(0.06666667F, BiasedToBottomHeight.a(VerticalAnchor.a(0), VerticalAnchor.a(127), 8), ConstantFloat.a(0.5F), VerticalAnchor.b(10), false, CarverDebugSettings.a(false, Blocks.CRIMSON_BUTTON.getBlockData()), ConstantFloat.a(1.0F), ConstantFloat.a(1.0F), ConstantFloat.a(-0.7F)))));
    public static final WorldGenCarverWrapper<CaveCarverConfiguration> NETHER_CAVE = a("nether_cave", WorldGenCarverAbstract.NETHER_CAVE.a((WorldGenCarverConfiguration) (new CaveCarverConfiguration(0.2F, UniformHeight.a(VerticalAnchor.a(0), VerticalAnchor.c(1)), ConstantFloat.a(0.5F), VerticalAnchor.b(10), false, ConstantFloat.a(1.0F), ConstantFloat.a(1.0F), ConstantFloat.a(-0.7F)))));
    public static final WorldGenCarverWrapper<CanyonCarverConfiguration> PROTOTYPE_CREVICE = a("prototype_crevice", WorldGenCarverAbstract.CANYON.a((WorldGenCarverConfiguration) (new CanyonCarverConfiguration(0.00125F, UniformHeight.a(VerticalAnchor.a(40), VerticalAnchor.a(80)), UniformFloat.b(6.0F, 8.0F), VerticalAnchor.b(8), false, CarverDebugSettings.a(false, Blocks.OAK_BUTTON.getBlockData()), UniformFloat.b(-0.125F, 0.125F), new CanyonCarverConfiguration.a(UniformFloat.b(0.5F, 1.0F), UniformFloat.b(0.0F, 1.0F), 6, UniformFloat.b(0.25F, 1.0F), 0.0F, 5.0F)))));

    public WorldGenCarvers() {}

    private static <WC extends WorldGenCarverConfiguration> WorldGenCarverWrapper<WC> a(String s, WorldGenCarverWrapper<WC> worldgencarverwrapper) {
        return (WorldGenCarverWrapper) RegistryGeneration.a(RegistryGeneration.CONFIGURED_CARVER, s, (Object) worldgencarverwrapper);
    }
}
