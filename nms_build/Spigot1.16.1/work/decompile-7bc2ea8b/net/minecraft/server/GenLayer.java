package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayer {

    private static final Logger LOGGER = LogManager.getLogger();
    private final AreaLazy b;

    public GenLayer(AreaFactory<AreaLazy> areafactory) {
        this.b = (AreaLazy) areafactory.make();
    }

    private BiomeBase a(int i) {
        BiomeBase biomebase = (BiomeBase) IRegistry.BIOME.fromId(i);

        if (biomebase == null) {
            if (SharedConstants.d) {
                throw (IllegalStateException) SystemUtils.c(new IllegalStateException("Unknown biome id: " + i));
            } else {
                GenLayer.LOGGER.warn("Unknown biome id: ", i);
                return Biomes.b;
            }
        } else {
            return biomebase;
        }
    }

    public BiomeBase a(int i, int j) {
        return this.a(this.b.a(i, j));
    }
}
