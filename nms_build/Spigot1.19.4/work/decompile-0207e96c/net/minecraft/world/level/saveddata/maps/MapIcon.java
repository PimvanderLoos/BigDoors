package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.MathHelper;

public class MapIcon {

    private final MapIcon.Type type;
    private final byte x;
    private final byte y;
    private final byte rot;
    @Nullable
    private final IChatBaseComponent name;

    public MapIcon(MapIcon.Type mapicon_type, byte b0, byte b1, byte b2, @Nullable IChatBaseComponent ichatbasecomponent) {
        this.type = mapicon_type;
        this.x = b0;
        this.y = b1;
        this.rot = b2;
        this.name = ichatbasecomponent;
    }

    public byte getImage() {
        return this.type.getIcon();
    }

    public MapIcon.Type getType() {
        return this.type;
    }

    public byte getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public byte getRot() {
        return this.rot;
    }

    public boolean renderOnFrame() {
        return this.type.isRenderedOnFrame();
    }

    @Nullable
    public IChatBaseComponent getName() {
        return this.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MapIcon)) {
            return false;
        } else {
            MapIcon mapicon = (MapIcon) object;

            return this.type == mapicon.type && this.rot == mapicon.rot && this.x == mapicon.x && this.y == mapicon.y && Objects.equals(this.name, mapicon.name);
        }
    }

    public int hashCode() {
        byte b0 = this.type.getIcon();
        int i = 31 * b0 + this.x;

        i = 31 * i + this.y;
        i = 31 * i + this.rot;
        i = 31 * i + Objects.hashCode(this.name);
        return i;
    }

    public static enum Type {

        PLAYER(false, true), FRAME(true, true), RED_MARKER(false, true), BLUE_MARKER(false, true), TARGET_X(true, false), TARGET_POINT(true, false), PLAYER_OFF_MAP(false, true), PLAYER_OFF_LIMITS(false, true), MANSION(true, 5393476, false), MONUMENT(true, 3830373, false), BANNER_WHITE(true, true), BANNER_ORANGE(true, true), BANNER_MAGENTA(true, true), BANNER_LIGHT_BLUE(true, true), BANNER_YELLOW(true, true), BANNER_LIME(true, true), BANNER_PINK(true, true), BANNER_GRAY(true, true), BANNER_LIGHT_GRAY(true, true), BANNER_CYAN(true, true), BANNER_PURPLE(true, true), BANNER_BLUE(true, true), BANNER_BROWN(true, true), BANNER_GREEN(true, true), BANNER_RED(true, true), BANNER_BLACK(true, true), RED_X(true, false);

        private final byte icon;
        private final boolean renderedOnFrame;
        private final int mapColor;
        private final boolean trackCount;

        private Type(boolean flag, boolean flag1) {
            this(flag, -1, flag1);
        }

        private Type(boolean flag, int i, boolean flag1) {
            this.trackCount = flag1;
            this.icon = (byte) this.ordinal();
            this.renderedOnFrame = flag;
            this.mapColor = i;
        }

        public byte getIcon() {
            return this.icon;
        }

        public boolean isRenderedOnFrame() {
            return this.renderedOnFrame;
        }

        public boolean hasMapColor() {
            return this.mapColor >= 0;
        }

        public int getMapColor() {
            return this.mapColor;
        }

        public static MapIcon.Type byIcon(byte b0) {
            return values()[MathHelper.clamp(b0, 0, values().length - 1)];
        }

        public boolean shouldTrackCount() {
            return this.trackCount;
        }
    }
}
