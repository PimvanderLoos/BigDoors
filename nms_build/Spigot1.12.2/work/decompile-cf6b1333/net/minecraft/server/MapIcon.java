package net.minecraft.server;

public class MapIcon {

    private final MapIcon.Type type;
    private byte x;
    private byte y;
    private byte rotation;

    public MapIcon(MapIcon.Type mapicon_type, byte b0, byte b1, byte b2) {
        this.type = mapicon_type;
        this.x = b0;
        this.y = b1;
        this.rotation = b2;
    }

    public byte getType() {
        return this.type.a();
    }

    public MapIcon.Type b() {
        return this.type;
    }

    public byte getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public byte getRotation() {
        return this.rotation;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MapIcon)) {
            return false;
        } else {
            MapIcon mapicon = (MapIcon) object;

            return this.type != mapicon.type ? false : (this.rotation != mapicon.rotation ? false : (this.x != mapicon.x ? false : this.y == mapicon.y));
        }
    }

    public int hashCode() {
        byte b0 = this.type.a();
        int i = 31 * b0 + this.x;

        i = 31 * i + this.y;
        i = 31 * i + this.rotation;
        return i;
    }

    public static enum Type {

        PLAYER(false), FRAME(true), RED_MARKER(false), BLUE_MARKER(false), TARGET_X(true), TARGET_POINT(true), PLAYER_OFF_MAP(false), PLAYER_OFF_LIMITS(false), MANSION(true, 5393476), MONUMENT(true, 3830373);

        private final byte k;
        private final boolean l;
        private final int m;

        private Type(boolean flag) {
            this(flag, -1);
        }

        private Type(boolean flag, int i) {
            this.k = (byte) this.ordinal();
            this.l = flag;
            this.m = i;
        }

        public byte a() {
            return this.k;
        }

        public boolean c() {
            return this.m >= 0;
        }

        public int d() {
            return this.m;
        }

        public static MapIcon.Type a(byte b0) {
            return values()[MathHelper.clamp(b0, 0, values().length - 1)];
        }
    }
}
