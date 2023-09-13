package net.minecraft.server;

public enum EnumBlockRotation {

    NONE("rotate_0"), CLOCKWISE_90("rotate_90"), CLOCKWISE_180("rotate_180"), COUNTERCLOCKWISE_90("rotate_270");

    private final String e;
    private static final String[] f = new String[values().length];

    private EnumBlockRotation(String s) {
        this.e = s;
    }

    public EnumBlockRotation a(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch (this) {
            case NONE:
                return EnumBlockRotation.CLOCKWISE_180;

            case CLOCKWISE_90:
                return EnumBlockRotation.COUNTERCLOCKWISE_90;

            case CLOCKWISE_180:
                return EnumBlockRotation.NONE;

            case COUNTERCLOCKWISE_90:
                return EnumBlockRotation.CLOCKWISE_90;
            }

        case COUNTERCLOCKWISE_90:
            switch (this) {
            case NONE:
                return EnumBlockRotation.COUNTERCLOCKWISE_90;

            case CLOCKWISE_90:
                return EnumBlockRotation.NONE;

            case CLOCKWISE_180:
                return EnumBlockRotation.CLOCKWISE_90;

            case COUNTERCLOCKWISE_90:
                return EnumBlockRotation.CLOCKWISE_180;
            }

        case CLOCKWISE_90:
            switch (this) {
            case NONE:
                return EnumBlockRotation.CLOCKWISE_90;

            case CLOCKWISE_90:
                return EnumBlockRotation.CLOCKWISE_180;

            case CLOCKWISE_180:
                return EnumBlockRotation.COUNTERCLOCKWISE_90;

            case COUNTERCLOCKWISE_90:
                return EnumBlockRotation.NONE;
            }

        default:
            return this;
        }
    }

    public EnumDirection a(EnumDirection enumdirection) {
        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            return enumdirection;
        } else {
            switch (this) {
            case CLOCKWISE_90:
                return enumdirection.e();

            case CLOCKWISE_180:
                return enumdirection.opposite();

            case COUNTERCLOCKWISE_90:
                return enumdirection.f();

            default:
                return enumdirection;
            }
        }
    }

    public int a(int i, int j) {
        switch (this) {
        case CLOCKWISE_90:
            return (i + j / 4) % j;

        case CLOCKWISE_180:
            return (i + j / 2) % j;

        case COUNTERCLOCKWISE_90:
            return (i + j * 3 / 4) % j;

        default:
            return i;
        }
    }

    static {
        int i = 0;
        EnumBlockRotation[] aenumblockrotation = values();
        int j = aenumblockrotation.length;

        for (int k = 0; k < j; ++k) {
            EnumBlockRotation enumblockrotation = aenumblockrotation[k];

            EnumBlockRotation.f[i++] = enumblockrotation.e;
        }

    }
}
