package net.minecraft.world.level.levelgen;

@FunctionalInterface
public interface NoiseModifier {

    NoiseModifier PASSTHROUGH = (d0, i, j, k) -> {
        return d0;
    };

    double modifyNoise(double d0, int i, int j, int k);
}
