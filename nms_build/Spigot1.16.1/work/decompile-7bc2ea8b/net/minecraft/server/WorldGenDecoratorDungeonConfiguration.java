package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenDecoratorDungeonConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorDungeonConfiguration> a = Codec.INT.fieldOf("chance").xmap(WorldGenDecoratorDungeonConfiguration::new, (worldgendecoratordungeonconfiguration) -> {
        return worldgendecoratordungeonconfiguration.b;
    }).codec();
    public final int b;

    public WorldGenDecoratorDungeonConfiguration(int i) {
        this.b = i;
    }
}
