package net.minecraft.server;

public enum EnumColor implements INamable {

    WHITE(0, 15, "white", "white", 16383998, EnumChatFormat.WHITE), ORANGE(1, 14, "orange", "orange", 16351261, EnumChatFormat.GOLD), MAGENTA(2, 13, "magenta", "magenta", 13061821, EnumChatFormat.AQUA), LIGHT_BLUE(3, 12, "light_blue", "lightBlue", 3847130, EnumChatFormat.BLUE), YELLOW(4, 11, "yellow", "yellow", 16701501, EnumChatFormat.YELLOW), LIME(5, 10, "lime", "lime", 8439583, EnumChatFormat.GREEN), PINK(6, 9, "pink", "pink", 15961002, EnumChatFormat.LIGHT_PURPLE), GRAY(7, 8, "gray", "gray", 4673362, EnumChatFormat.DARK_GRAY), SILVER(8, 7, "silver", "silver", 10329495, EnumChatFormat.GRAY), CYAN(9, 6, "cyan", "cyan", 1481884, EnumChatFormat.DARK_AQUA), PURPLE(10, 5, "purple", "purple", 8991416, EnumChatFormat.DARK_PURPLE), BLUE(11, 4, "blue", "blue", 3949738, EnumChatFormat.DARK_BLUE), BROWN(12, 3, "brown", "brown", 8606770, EnumChatFormat.GOLD), GREEN(13, 2, "green", "green", 6192150, EnumChatFormat.DARK_GREEN), RED(14, 1, "red", "red", 11546150, EnumChatFormat.DARK_RED), BLACK(15, 0, "black", "black", 1908001, EnumChatFormat.BLACK);

    private static final EnumColor[] q = new EnumColor[values().length];
    private static final EnumColor[] r = new EnumColor[values().length];
    private final int s;
    private final int t;
    private final String u;
    private final String v;
    private final int w;
    private final float[] x;
    private final EnumChatFormat y;

    private EnumColor(int i, int j, String s, String s1, int k, EnumChatFormat enumchatformat) {
        this.s = i;
        this.t = j;
        this.u = s;
        this.v = s1;
        this.w = k;
        this.y = enumchatformat;
        int l = (k & 16711680) >> 16;
        int i1 = (k & '\uff00') >> 8;
        int j1 = (k & 255) >> 0;

        this.x = new float[] { (float) l / 255.0F, (float) i1 / 255.0F, (float) j1 / 255.0F};
    }

    public int getColorIndex() {
        return this.s;
    }

    public int getInvColorIndex() {
        return this.t;
    }

    public String d() {
        return this.v;
    }

    public float[] f() {
        return this.x;
    }

    public static EnumColor fromInvColorIndex(int i) {
        if (i < 0 || i >= EnumColor.r.length) {
            i = 0;
        }

        return EnumColor.r[i];
    }

    public static EnumColor fromColorIndex(int i) {
        if (i < 0 || i >= EnumColor.q.length) {
            i = 0;
        }

        return EnumColor.q[i];
    }

    public String toString() {
        return this.v;
    }

    public String getName() {
        return this.u;
    }

    static {
        EnumColor[] aenumcolor = values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            EnumColor.q[enumcolor.getColorIndex()] = enumcolor;
            EnumColor.r[enumcolor.getInvColorIndex()] = enumcolor;
        }

    }
}
