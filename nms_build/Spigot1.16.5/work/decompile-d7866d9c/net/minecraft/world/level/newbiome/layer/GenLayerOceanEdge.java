package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.levelgen.synth.NoiseGeneratorPerlin;
import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum GenLayerOceanEdge implements AreaTransformer1 {

    INSTANCE;

    private GenLayerOceanEdge() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j) {
        NoiseGeneratorPerlin noisegeneratorperlin = worldgencontext.b();
        double d0 = noisegeneratorperlin.a((double) i / 8.0D, (double) j / 8.0D, 0.0D, 0.0D, 0.0D);

        return d0 > 0.4D ? 44 : (d0 > 0.2D ? 45 : (d0 < -0.4D ? 10 : (d0 < -0.2D ? 46 : 0)));
    }
}
