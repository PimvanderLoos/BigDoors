package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;

public class RotationSegment {

    private static final int MAX_SEGMENT_INDEX = 15;
    private static final int NORTH_0 = 0;
    private static final int EAST_90 = 4;
    private static final int SOUTH_180 = 8;
    private static final int WEST_270 = 12;

    public RotationSegment() {}

    public static int getMaxSegmentIndex() {
        return 15;
    }

    public static int convertToSegment(EnumDirection enumdirection) {
        return enumdirection.getAxis().isVertical() ? 0 : enumdirection.getOpposite().get2DDataValue() * 4;
    }

    public static int convertToSegment(float f) {
        return MathHelper.floor((double) ((180.0F + f) * 16.0F / 360.0F) + 0.5D) & 15;
    }

    public static Optional<EnumDirection> convertToDirection(int i) {
        EnumDirection enumdirection;

        switch (i) {
            case 0:
                enumdirection = EnumDirection.NORTH;
                break;
            case 4:
                enumdirection = EnumDirection.EAST;
                break;
            case 8:
                enumdirection = EnumDirection.SOUTH;
                break;
            case 12:
                enumdirection = EnumDirection.WEST;
                break;
            default:
                enumdirection = null;
        }

        EnumDirection enumdirection1 = enumdirection;

        return Optional.ofNullable(enumdirection1);
    }

    public static float convertToDegrees(int i) {
        return (float) i * 22.5F;
    }
}
