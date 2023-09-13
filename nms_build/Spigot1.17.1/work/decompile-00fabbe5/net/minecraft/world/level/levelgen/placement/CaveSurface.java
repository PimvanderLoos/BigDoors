package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.INamable;

public enum CaveSurface implements INamable {

    CEILING(EnumDirection.UP, 1, "ceiling"), FLOOR(EnumDirection.DOWN, -1, "floor");

    public static final Codec<CaveSurface> CODEC = INamable.a(CaveSurface::values, CaveSurface::a);
    private final EnumDirection direction;
    private final int y;
    private final String id;
    private static final CaveSurface[] VALUES = values();

    private CaveSurface(EnumDirection enumdirection, int i, String s) {
        this.direction = enumdirection;
        this.y = i;
        this.id = s;
    }

    public EnumDirection a() {
        return this.direction;
    }

    public int b() {
        return this.y;
    }

    public static CaveSurface a(String s) {
        CaveSurface[] acavesurface = CaveSurface.VALUES;
        int i = acavesurface.length;

        for (int j = 0; j < i; ++j) {
            CaveSurface cavesurface = acavesurface[j];

            if (cavesurface.getName().equals(s)) {
                return cavesurface;
            }
        }

        throw new IllegalArgumentException("Unknown Surface type: " + s);
    }

    @Override
    public String getName() {
        return this.id;
    }
}
