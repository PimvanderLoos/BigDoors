package net.minecraft.util;

import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.phys.Vec3D;

public class CubicSampler {

    private static final int a = 2;
    private static final int b = 6;
    private static final double[] c = new double[]{0.0D, 1.0D, 4.0D, 6.0D, 4.0D, 1.0D, 0.0D};

    private CubicSampler() {}

    public static Vec3D a(Vec3D vec3d, CubicSampler.Vec3Fetcher cubicsampler_vec3fetcher) {
        int i = MathHelper.floor(vec3d.getX());
        int j = MathHelper.floor(vec3d.getY());
        int k = MathHelper.floor(vec3d.getZ());
        double d0 = vec3d.getX() - (double) i;
        double d1 = vec3d.getY() - (double) j;
        double d2 = vec3d.getZ() - (double) k;
        double d3 = 0.0D;
        Vec3D vec3d1 = Vec3D.ZERO;

        for (int l = 0; l < 6; ++l) {
            double d4 = MathHelper.d(d0, CubicSampler.c[l + 1], CubicSampler.c[l]);
            int i1 = i - 2 + l;

            for (int j1 = 0; j1 < 6; ++j1) {
                double d5 = MathHelper.d(d1, CubicSampler.c[j1 + 1], CubicSampler.c[j1]);
                int k1 = j - 2 + j1;

                for (int l1 = 0; l1 < 6; ++l1) {
                    double d6 = MathHelper.d(d2, CubicSampler.c[l1 + 1], CubicSampler.c[l1]);
                    int i2 = k - 2 + l1;
                    double d7 = d4 * d5 * d6;

                    d3 += d7;
                    vec3d1 = vec3d1.e(cubicsampler_vec3fetcher.fetch(i1, k1, i2).a(d7));
                }
            }
        }

        vec3d1 = vec3d1.a(1.0D / d3);
        return vec3d1;
    }

    @DontObfuscate
    public interface Vec3Fetcher {

        Vec3D fetch(int i, int j, int k);
    }
}
