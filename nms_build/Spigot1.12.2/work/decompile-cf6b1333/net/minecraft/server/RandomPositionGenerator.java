package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class RandomPositionGenerator {

    private static Vec3D a = Vec3D.a;

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j) {
        return c(entitycreature, i, j, (Vec3D) null);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j) {
        return a(entitycreature, i, j, (Vec3D) null, false);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        RandomPositionGenerator.a = vec3d.a(entitycreature.locX, entitycreature.locY, entitycreature.locZ);
        return c(entitycreature, i, j, RandomPositionGenerator.a);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        RandomPositionGenerator.a = (new Vec3D(entitycreature.locX, entitycreature.locY, entitycreature.locZ)).d(vec3d);
        return c(entitycreature, i, j, RandomPositionGenerator.a);
    }

    @Nullable
    private static Vec3D c(EntityCreature entitycreature, int i, int j, @Nullable Vec3D vec3d) {
        return a(entitycreature, i, j, vec3d, true);
    }

    @Nullable
    private static Vec3D a(EntityCreature entitycreature, int i, int j, @Nullable Vec3D vec3d, boolean flag) {
        NavigationAbstract navigationabstract = entitycreature.getNavigation();
        Random random = entitycreature.getRandom();
        boolean flag1;

        if (entitycreature.dj()) {
            double d0 = entitycreature.dg().distanceSquared((double) MathHelper.floor(entitycreature.locX), (double) MathHelper.floor(entitycreature.locY), (double) MathHelper.floor(entitycreature.locZ)) + 4.0D;
            double d1 = (double) (entitycreature.dh() + (float) i);

            flag1 = d0 < d1 * d1;
        } else {
            flag1 = false;
        }

        boolean flag2 = false;
        float f = -99999.0F;
        int k = 0;
        int l = 0;
        int i1 = 0;

        for (int j1 = 0; j1 < 10; ++j1) {
            int k1 = random.nextInt(2 * i + 1) - i;
            int l1 = random.nextInt(2 * j + 1) - j;
            int i2 = random.nextInt(2 * i + 1) - i;

            if (vec3d == null || (double) k1 * vec3d.x + (double) i2 * vec3d.z >= 0.0D) {
                BlockPosition blockposition;

                if (entitycreature.dj() && i > 1) {
                    blockposition = entitycreature.dg();
                    if (entitycreature.locX > (double) blockposition.getX()) {
                        k1 -= random.nextInt(i / 2);
                    } else {
                        k1 += random.nextInt(i / 2);
                    }

                    if (entitycreature.locZ > (double) blockposition.getZ()) {
                        i2 -= random.nextInt(i / 2);
                    } else {
                        i2 += random.nextInt(i / 2);
                    }
                }

                blockposition = new BlockPosition((double) k1 + entitycreature.locX, (double) l1 + entitycreature.locY, (double) i2 + entitycreature.locZ);
                if ((!flag1 || entitycreature.f(blockposition)) && navigationabstract.a(blockposition)) {
                    if (!flag) {
                        blockposition = a(blockposition, entitycreature);
                        if (b(blockposition, entitycreature)) {
                            continue;
                        }
                    }

                    float f1 = entitycreature.a(blockposition);

                    if (f1 > f) {
                        f = f1;
                        k = k1;
                        l = l1;
                        i1 = i2;
                        flag2 = true;
                    }
                }
            }
        }

        if (flag2) {
            return new Vec3D((double) k + entitycreature.locX, (double) l + entitycreature.locY, (double) i1 + entitycreature.locZ);
        } else {
            return null;
        }
    }

    private static BlockPosition a(BlockPosition blockposition, EntityCreature entitycreature) {
        if (!entitycreature.world.getType(blockposition).getMaterial().isBuildable()) {
            return blockposition;
        } else {
            BlockPosition blockposition1;

            for (blockposition1 = blockposition.up(); blockposition1.getY() < entitycreature.world.getHeight() && entitycreature.world.getType(blockposition1).getMaterial().isBuildable(); blockposition1 = blockposition1.up()) {
                ;
            }

            return blockposition1;
        }
    }

    private static boolean b(BlockPosition blockposition, EntityCreature entitycreature) {
        return entitycreature.world.getType(blockposition).getMaterial() == Material.WATER;
    }
}
