package net.minecraft.server;

public class BiomeLayoutOverworldConfiguration implements BiomeLayoutConfiguration {

    private final long a;
    private final WorldType b;
    private GeneratorSettingsOverworld c = new GeneratorSettingsOverworld();

    public BiomeLayoutOverworldConfiguration(WorldData worlddata) {
        this.a = worlddata.getSeed();
        this.b = worlddata.getType();
    }

    public BiomeLayoutOverworldConfiguration a(GeneratorSettingsOverworld generatorsettingsoverworld) {
        this.c = generatorsettingsoverworld;
        return this;
    }

    public long a() {
        return this.a;
    }

    public WorldType b() {
        return this.b;
    }

    public GeneratorSettingsOverworld c() {
        return this.c;
    }
}
