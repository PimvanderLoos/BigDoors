package net.minecraft.world.level.newbiome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.AreaLazy;
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
