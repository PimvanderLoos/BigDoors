package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.SegmentedAnglePrecision;

public class RotationSegment {

    private static final SegmentedAnglePrecision SEGMENTED_ANGLE16 = new SegmentedAnglePrecision(4);
    private static final int MAX_SEGMENT_INDEX = RotationSegment.SEGMENTED_ANGLE16.getMask();
    private static final int NORTH_0 = 0;
    private static final int EAST_90 = 4;
    private static final int SOUTH_180 = 8;
    private static final int WEST_270 = 12;

    public RotationSegment() {}

    public static int getMaxSegmentIndex() {
        return RotationSegment.MAX_SEGMENT_INDEX;
    }

    public static int convertToSegment(EnumDirection enumdirection) {
        return RotationSegment.SEGMENTED_ANGLE16.fromDirection(enumdirection);
    }

    public static int convertToSegment(float f) {
        return RotationSegment.SEGMENTED_ANGLE16.fromDegrees(f);
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
        return RotationSegment.SEGMENTED_ANGLE16.toDegrees(i);
    }
}
