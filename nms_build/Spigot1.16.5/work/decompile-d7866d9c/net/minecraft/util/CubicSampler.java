package net.minecraft.util;

import net.minecraft.world.phys.Vec3D;

public class CubicSampler {

    private static final double[] a = new double[]{0.0D, 1.0D, 4.0D, 6.0D, 4.0D, 1.0D, 0.0D};

    public interface Vec3Fetcher {

        Vec3D fetch(int i, int j, int k);
    }
}
