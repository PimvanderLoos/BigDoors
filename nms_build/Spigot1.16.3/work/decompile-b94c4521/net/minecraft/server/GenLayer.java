package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayer {

    private static final Logger LOGGER = LogManager.getLogger();
    private final AreaLazy b;

    public GenLayer(AreaFactory<AreaLazy> areafactory) {
        this.b = (AreaLazy) areafactory.make();
    }

    public BiomeBase a(IRegistry<BiomeBase> iregistry, int i, int j) {
        int k = this.b.a(i, j);
        ResourceKey<BiomeBase> resourcekey = BiomeRegistry.a(k);

        if (resourcekey == null) {
            throw new IllegalStateException("Unknown biome id emitted by layers: " + k);
        } else {
            BiomeBase biomebase = (BiomeBase) iregistry.a(resourcekey);

            if (biomebase == null) {
                if (SharedConstants.d) {
                    throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("Unknown biome id: " + k)));
                } else {
                    GenLayer.LOGGER.warn("Unknown biome id: ", k);
                    return (BiomeBase) iregistry.a(BiomeRegistry.a(0));
                }
            } else {
                return biomebase;
            }
        }
    }
}
