package net.minecraft.server;

public class BiomeOcean extends BiomeBase {

    public BiomeOcean(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
    }

    public BiomeBase.EnumTemperature h() {
        return BiomeBase.EnumTemperature.OCEAN;
    }
}
