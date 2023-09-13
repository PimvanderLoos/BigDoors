package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorDungeonConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorDungeonConfiguration> a = Codec.INT.fieldOf("chance").xmap(WorldGenDecoratorDungeonConfiguration::new, (worldgendecoratordungeonconfiguration) -> {
        return worldgendecoratordungeonconfiguration.c;
    }).codec();
    public final int c;

    public WorldGenDecoratorDungeonConfiguration(int i) {
        this.c = i;
    }
}
