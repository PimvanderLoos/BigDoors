package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class ParticleUtils {

    public ParticleUtils() {}

    public static void spawnParticlesOnBlockFaces(World world, BlockPosition blockposition, ParticleParam particleparam, IntProvider intprovider) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            spawnParticlesOnBlockFace(world, blockposition, particleparam, intprovider, enumdirection, () -> {
                return getRandomSpeedRanges(world.random);
            }, 0.55D);
        }

    }

    public static void spawnParticlesOnBlockFace(World world, BlockPosition blockposition, ParticleParam particleparam, IntProvider intprovider, EnumDirection enumdirection, Supplier<Vec3D> supplier, double d0) {
        int i = intprovider.sample(world.random);

        for (int j = 0; j < i; ++j) {
            spawnParticleOnFace(world, blockposition, enumdirection, particleparam, (Vec3D) supplier.get(), d0);
        }

    }

    private static Vec3D getRandomSpeedRanges(RandomSource randomsource) {
        return new Vec3D(MathHelper.nextDouble(randomsource, -0.5D, 0.5D), MathHelper.nextDouble(randomsource, -0.5D, 0.5D), MathHelper.nextDouble(randomsource, -0.5D, 0.5D));
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

    public static void spawnParticleOnFace(World world, BlockPosition blockposition, EnumDirection enumdirection, ParticleParam particleparam, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = Vec3D.atCenterOf(blockposition);
        int i = enumdirection.getStepX();
        int j = enumdirection.getStepY();
        int k = enumdirection.getStepZ();
        double d1 = vec3d1.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5D, 0.5D) : (double) i * d0);
        double d2 = vec3d1.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5D, 0.5D) : (double) j * d0);
        double d3 = vec3d1.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5D, 0.5D) : (double) k * d0);
        double d4 = i == 0 ? vec3d.x() : 0.0D;
        double d5 = j == 0 ? vec3d.y() : 0.0D;
        double d6 = k == 0 ? vec3d.z() : 0.0D;

        world.addParticle(particleparam, d1, d2, d3, d4, d5, d6);
    }

    public static void spawnParticleBelow(World world, BlockPosition blockposition, RandomSource randomsource, ParticleParam particleparam) {
        double d0 = (double) blockposition.getX() + randomsource.nextDouble();
        double d1 = (double) blockposition.getY() - 0.05D;
        double d2 = (double) blockposition.getZ() + randomsource.nextDouble();

        world.addParticle(particleparam, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }
}
