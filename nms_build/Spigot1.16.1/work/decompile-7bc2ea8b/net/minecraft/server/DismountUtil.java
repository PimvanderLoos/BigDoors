package net.minecraft.server;

public class DismountUtil {

    public static int[][] a(EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.g();
        EnumDirection enumdirection2 = enumdirection1.opposite();
        EnumDirection enumdirection3 = enumdirection.opposite();

        return new int[][]{{enumdirection1.getAdjacentX(), enumdirection1.getAdjacentZ()}, {enumdirection2.getAdjacentX(), enumdirection2.getAdjacentZ()}, {enumdirection3.getAdjacentX() + enumdirection1.getAdjacentX(), enumdirection3.getAdjacentZ() + enumdirection1.getAdjacentZ()}, {enumdirection3.getAdjacentX() + enumdirection2.getAdjacentX(), enumdirection3.getAdjacentZ() + enumdirection2.getAdjacentZ()}, {enumdirection.getAdjacentX() + enumdirection1.getAdjacentX(), enumdirection.getAdjacentZ() + enumdirection1.getAdjacentZ()}, {enumdirection.getAdjacentX() + enumdirection2.getAdjacentX(), enumdirection.getAdjacentZ() + enumdirection2.getAdjacentZ()}, {enumdirection3.getAdjacentX(), enumdirection3.getAdjacentZ()}, {enumdirection.getAdjacentX(), enumdirection.getAdjacentZ()}};
    }

    public static boolean a(double d0) {
        return !Double.isInfinite(d0) && d0 < 1.0D;
    }

    public static boolean a(World world, EntityLiving entityliving, AxisAlignedBB axisalignedbb) {
        return world.b((Entity) entityliving, axisalignedbb).allMatch(VoxelShape::isEmpty);
    }
}
