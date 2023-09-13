package net.minecraft.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class ParticleUtils {

    public ParticleUtils() {}

    public static void spawnParticlesOnBlockFaces(World world, BlockPosition blockposition, ParticleParam particleparam, UniformInt uniformint) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            int k = uniformint.sample(world.random);

            for (int l = 0; l < k; ++l) {
                spawnParticleOnFace(world, blockposition, enumdirection, particleparam);
            }
        }

    }

    public static void spawnParticlesAlongAxis(EnumDirection.EnumAxis enumdirection_enumaxis, World world, BlockPosition blockposition, double d0, ParticleParam particleparam, UniformInt uniformint) {
        Vec3D vec3d = Vec3D.atCenterOf(blockposition);
        boolean flag = enumdirection_enumaxis == EnumDirection.EnumAxis.X;
        boolean flag1 = enumdirection_enumaxis == EnumDirection.EnumAxis.Y;
        boolean flag2 = enumdirection_enumaxis == EnumDirection.EnumAxis.Z;
        int i = uniformint.sample(world.random);

        for (int j = 0; j < i; ++j) {
            double d1 = vec3d.x + MathHelper.nextDouble(world.random, -1.0D, 1.0D) * (flag ? 0.5D : d0);
            double d2 = vec3d.y + MathHelper.nextDouble(world.random, -1.0D, 1.0D) * (flag1 ? 0.5D : d0);
            double d3 = vec3d.z + MathHelper.nextDouble(world.random, -1.0D, 1.0D) * (flag2 ? 0.5D : d0);
            double d4 = flag ? MathHelper.nextDouble(world.random, -1.0D, 1.0D) : 0.0D;
            double d5 = flag1 ? MathHelper.nextDouble(world.random, -1.0D, 1.0D) : 0.0D;
            double d6 = flag2 ? MathHelper.nextDouble(world.random, -1.0D, 1.0D) : 0.0D;

            world.addParticle(particleparam, d1, d2, d3, d4, d5, d6);
        }

    }

    public static void spawnParticleOnFace(World world, BlockPosition blockposition, EnumDirection enumdirection, ParticleParam particleparam) {
        Vec3D vec3d = Vec3D.atCenterOf(blockposition);
        int i = enumdirection.getStepX();
        int j = enumdirection.getStepY();
        int k = enumdirection.getStepZ();
        double d0 = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5D, 0.5D) : (double) i * 0.55D);
        double d1 = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5D, 0.5D) : (double) j * 0.55D);
        double d2 = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5D, 0.5D) : (double) k * 0.55D);
        double d3 = i == 0 ? MathHelper.nextDouble(world.random, -1.0D, 1.0D) : 0.0D;
        double d4 = j == 0 ? MathHelper.nextDouble(world.random, -1.0D, 1.0D) : 0.0D;
        double d5 = k == 0 ? MathHelper.nextDouble(world.random, -1.0D, 1.0D) : 0.0D;

        world.addParticle(particleparam, d0, d1, d2, d3, d4, d5);
    }
}
