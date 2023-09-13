package net.minecraft.server;

public class ShulkerUtil {

    public static AxisAlignedBB a(BlockPosition blockposition, EnumDirection enumdirection) {
        return VoxelShapes.b().getBoundingBox().b((double) (0.5F * (float) enumdirection.getAdjacentX()), (double) (0.5F * (float) enumdirection.getAdjacentY()), (double) (0.5F * (float) enumdirection.getAdjacentZ())).a((double) enumdirection.getAdjacentX(), (double) enumdirection.getAdjacentY(), (double) enumdirection.getAdjacentZ()).a(blockposition.shift(enumdirection));
    }
}
