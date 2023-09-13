package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.INamable;

public enum CaveSurface implements INamable {

    CEILING(EnumDirection.UP, 1, "ceiling"), FLOOR(EnumDirection.DOWN, -1, "floor");

    public static final Codec<CaveSurface> CODEC = INamable.fromEnum(CaveSurface::values);
    private final EnumDirection direction;
    private final int y;
    private final String id;

    private CaveSurface(EnumDirection enumdirection, int i, String s) {
        this.direction = enumdirection;
        this.y = i;
        this.id = s;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
