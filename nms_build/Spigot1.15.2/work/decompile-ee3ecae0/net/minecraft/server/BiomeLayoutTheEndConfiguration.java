package net.minecraft.server;

public class BiomeLayoutTheEndConfiguration implements BiomeLayoutConfiguration {

    private final long a;

    public BiomeLayoutTheEndConfiguration(WorldData worlddata) {
        this.a = worlddata.getSeed();
    }

    public long a() {
        return this.a;
    }
}
