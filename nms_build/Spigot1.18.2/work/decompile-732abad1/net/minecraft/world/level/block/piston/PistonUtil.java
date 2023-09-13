package net.minecraft.world.level.block.piston;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.phys.AxisAlignedBB;

public class PistonUtil {

    public PistonUtil() {}

    public static AxisAlignedBB getMovementArea(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, double d0) {
        double d1 = d0 * (double) enumdirection.getAxisDirection().getStep();
        double d2 = Math.min(d1, 0.0D);
        double d3 = Math.max(d1, 0.0D);

        switch (enumdirection) {
            case WEST:
                return new AxisAlignedBB(axisalignedbb.minX + d2, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + d3, axisalignedbb.maxY, axisalignedbb.maxZ);
            case EAST:
                return new AxisAlignedBB(axisalignedbb.maxX + d2, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX + d3, axisalignedbb.maxY, axisalignedbb.maxZ);
            case DOWN:
                return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY + d2, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY + d3, axisalignedbb.maxZ);
            case UP:
            default:
                return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.maxY + d2, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.maxY + d3, axisalignedbb.maxZ);
            case NORTH:
                return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ + d2, axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ + d3);
            case SOUTH:
                return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ + d2, axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ + d3);
        }
    }
}
