package net.minecraft.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public enum EnumColor implements INamable {

    WHITE(0, "white", 16383998, MaterialMapColor.j, 15790320), ORANGE(1, "orange", 16351261, MaterialMapColor.q, 15435844), MAGENTA(2, "magenta", 13061821, MaterialMapColor.r, 12801229), LIGHT_BLUE(3, "light_blue", 3847130, MaterialMapColor.s, 6719955), YELLOW(4, "yellow", 16701501, MaterialMapColor.t, 14602026), LIME(5, "lime", 8439583, MaterialMapColor.u, 4312372), PINK(6, "pink", 15961002, MaterialMapColor.v, 14188952), GRAY(7, "gray", 4673362, MaterialMapColor.w, 4408131), LIGHT_GRAY(8, "light_gray", 10329495, MaterialMapColor.x, 11250603), CYAN(9, "cyan", 1481884, MaterialMapColor.y, 2651799), PURPLE(10, "purple", 8991416, MaterialMapColor.z, 8073150), BLUE(11, "blue", 3949738, MaterialMapColor.A, 2437522), BROWN(12, "brown", 8606770, MaterialMapColor.B, 5320730), GREEN(13, "green", 6192150, MaterialMapColor.C, 3887386), RED(14, "red", 11546150, MaterialMapColor.D, 11743532), BLACK(15, "black", 1908001, MaterialMapColor.E, 1973019);

    private static final EnumColor[] q = (EnumColor[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EnumColor::getColorIndex)).toArray((i) -> {
        return new EnumColor[i];
    });
    private static final Int2ObjectOpenHashMap<EnumColor> r = new Int2ObjectOpenHashMap((Map) Arrays.stream(values()).collect(Collectors.toMap((enumcolor) -> {
        return enumcolor.y;
    }, (enumcolor) -> {
        return enumcolor;
    })));
    private final int s;
    private final String t;
    private final MaterialMapColor u;
    private final int v;
    private final int w;
    private final float[] x;
    private final int y;

    private EnumColor(int i, String s, int j, MaterialMapColor materialmapcolor, int k) {
        this.s = i;
        this.t = s;
        this.v = j;
        this.u = materialmapcolor;
        int l = (j & 16711680) >> 16;
        int i1 = (j & '\uff00') >> 8;
        int j1 = (j & 255) >> 0;

        this.w = j1 << 16 | i1 << 8 | l << 0;
        this.x = new float[] { (float) l / 255.0F, (float) i1 / 255.0F, (float) j1 / 255.0F};
        this.y = k;
    }

    public int getColorIndex() {
        return this.s;
    }

    public String b() {
        return this.t;
    }

    public float[] d() {
        return this.x;
    }

    public MaterialMapColor e() {
        return this.u;
    }

    public int f() {
        return this.y;
    }

    public static EnumColor fromColorIndex(int i) {
        if (i < 0 || i >= EnumColor.q.length) {
            i = 0;
        }

        return EnumColor.q[i];
    }

    public static EnumColor a(String s) {
        EnumColor[] aenumcolor = values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            if (enumcolor.t.equals(s)) {
                return enumcolor;
            }
        }

        return EnumColor.WHITE;
    }

    public String toString() {
        return this.t;
    }

    public String getName() {
        return this.t;
    }
}
