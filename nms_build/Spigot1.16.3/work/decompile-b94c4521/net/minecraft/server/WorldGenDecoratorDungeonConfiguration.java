package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenDecoratorDungeonConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorDungeonConfiguration> a = Codec.INT.fieldOf("chance").xmap(WorldGenDecoratorDungeonConfiguration::new, (worldgendecoratordungeonconfiguration) -> {
        return worldgendecoratordungeonconfiguration.c;
    }).codec();
    public final int c;

    public WorldGenDecoratorDungeonConfiguration(int i) {
        this.c = i;
    }
}
