package net.minecraft.world.level.material;

import com.google.common.base.Preconditions;

public class MaterialMapColor {

    public static final MaterialMapColor[] MATERIAL_COLORS = new MaterialMapColor[64];
    public static final MaterialMapColor NONE = new MaterialMapColor(0, 0);
    public static final MaterialMapColor GRASS = new MaterialMapColor(1, 8368696);
    public static final MaterialMapColor SAND = new MaterialMapColor(2, 16247203);
    public static final MaterialMapColor WOOL = new MaterialMapColor(3, 13092807);
    public static final MaterialMapColor FIRE = new MaterialMapColor(4, 16711680);
    public static final MaterialMapColor ICE = new MaterialMapColor(5, 10526975);
    public static final MaterialMapColor METAL = new MaterialMapColor(6, 10987431);
    public static final MaterialMapColor PLANT = new MaterialMapColor(7, 31744);
    public static final MaterialMapColor SNOW = new MaterialMapColor(8, 16777215);
    public static final MaterialMapColor CLAY = new MaterialMapColor(9, 10791096);
    public static final MaterialMapColor DIRT = new MaterialMapColor(10, 9923917);
    public static final MaterialMapColor STONE = new MaterialMapColor(11, 7368816);
    public static final MaterialMapColor WATER = new MaterialMapColor(12, 4210943);
    public static final MaterialMapColor WOOD = new MaterialMapColor(13, 9402184);
    public static final MaterialMapColor QUARTZ = new MaterialMapColor(14, 16776437);
    public static final MaterialMapColor COLOR_ORANGE = new MaterialMapColor(15, 14188339);
    public static final MaterialMapColor COLOR_MAGENTA = new MaterialMapColor(16, 11685080);
    public static final MaterialMapColor COLOR_LIGHT_BLUE = new MaterialMapColor(17, 6724056);
    public static final MaterialMapColor COLOR_YELLOW = new MaterialMapColor(18, 15066419);
    public static final MaterialMapColor COLOR_LIGHT_GREEN = new MaterialMapColor(19, 8375321);
    public static final MaterialMapColor COLOR_PINK = new MaterialMapColor(20, 15892389);
    public static final MaterialMapColor COLOR_GRAY = new MaterialMapColor(21, 5000268);
    public static final MaterialMapColor COLOR_LIGHT_GRAY = new MaterialMapColor(22, 10066329);
    public static final MaterialMapColor COLOR_CYAN = new MaterialMapColor(23, 5013401);
    public static final MaterialMapColor COLOR_PURPLE = new MaterialMapColor(24, 8339378);
    public static final MaterialMapColor COLOR_BLUE = new MaterialMapColor(25, 3361970);
    public static final MaterialMapColor COLOR_BROWN = new MaterialMapColor(26, 6704179);
    public static final MaterialMapColor COLOR_GREEN = new MaterialMapColor(27, 6717235);
    public static final MaterialMapColor COLOR_RED = new MaterialMapColor(28, 10040115);
    public static final MaterialMapColor COLOR_BLACK = new MaterialMapColor(29, 1644825);
    public static final MaterialMapColor GOLD = new MaterialMapColor(30, 16445005);
    public static final MaterialMapColor DIAMOND = new MaterialMapColor(31, 6085589);
    public static final MaterialMapColor LAPIS = new MaterialMapColor(32, 4882687);
    public static final MaterialMapColor EMERALD = new MaterialMapColor(33, 55610);
    public static final MaterialMapColor PODZOL = new MaterialMapColor(34, 8476209);
    public static final MaterialMapColor NETHER = new MaterialMapColor(35, 7340544);
    public static final MaterialMapColor TERRACOTTA_WHITE = new MaterialMapColor(36, 13742497);
    public static final MaterialMapColor TERRACOTTA_ORANGE = new MaterialMapColor(37, 10441252);
    public static final MaterialMapColor TERRACOTTA_MAGENTA = new MaterialMapColor(38, 9787244);
    public static final MaterialMapColor TERRACOTTA_LIGHT_BLUE = new MaterialMapColor(39, 7367818);
    public static final MaterialMapColor TERRACOTTA_YELLOW = new MaterialMapColor(40, 12223780);
    public static final MaterialMapColor TERRACOTTA_LIGHT_GREEN = new MaterialMapColor(41, 6780213);
    public static final MaterialMapColor TERRACOTTA_PINK = new MaterialMapColor(42, 10505550);
    public static final MaterialMapColor TERRACOTTA_GRAY = new MaterialMapColor(43, 3746083);
    public static final MaterialMapColor TERRACOTTA_LIGHT_GRAY = new MaterialMapColor(44, 8874850);
    public static final MaterialMapColor TERRACOTTA_CYAN = new MaterialMapColor(45, 5725276);
    public static final MaterialMapColor TERRACOTTA_PURPLE = new MaterialMapColor(46, 8014168);
    public static final MaterialMapColor TERRACOTTA_BLUE = new MaterialMapColor(47, 4996700);
    public static final MaterialMapColor TERRACOTTA_BROWN = new MaterialMapColor(48, 4993571);
    public static final MaterialMapColor TERRACOTTA_GREEN = new MaterialMapColor(49, 5001770);
    public static final MaterialMapColor TERRACOTTA_RED = new MaterialMapColor(50, 9321518);
    public static final MaterialMapColor TERRACOTTA_BLACK = new MaterialMapColor(51, 2430480);
    public static final MaterialMapColor CRIMSON_NYLIUM = new MaterialMapColor(52, 12398641);
    public static final MaterialMapColor CRIMSON_STEM = new MaterialMapColor(53, 9715553);
    public static final MaterialMapColor CRIMSON_HYPHAE = new MaterialMapColor(54, 6035741);
    public static final MaterialMapColor WARPED_NYLIUM = new MaterialMapColor(55, 1474182);
    public static final MaterialMapColor WARPED_STEM = new MaterialMapColor(56, 3837580);
    public static final MaterialMapColor WARPED_HYPHAE = new MaterialMapColor(57, 5647422);
    public static final MaterialMapColor WARPED_WART_BLOCK = new MaterialMapColor(58, 1356933);
    public static final MaterialMapColor DEEPSLATE = new MaterialMapColor(59, 6579300);
    public static final MaterialMapColor RAW_IRON = new MaterialMapColor(60, 14200723);
    public static final MaterialMapColor GLOW_LICHEN = new MaterialMapColor(61, 8365974);
    public final int col;
    public final int id;

    private MaterialMapColor(int i, int j) {
        if (i >= 0 && i <= 63) {
            this.id = i;
            this.col = j;
            MaterialMapColor.MATERIAL_COLORS[i] = this;
        } else {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
    }

    public int calculateRGBColor(MaterialMapColor.a materialmapcolor_a) {
        if (this == MaterialMapColor.NONE) {
            return 0;
        } else {
            int i = materialmapcolor_a.modifier;
            int j = (this.col >> 16 & 255) * i / 255;
            int k = (this.col >> 8 & 255) * i / 255;
            int l = (this.col & 255) * i / 255;

            return -16777216 | l << 16 | k << 8 | j;
        }
    }

    public static MaterialMapColor byId(int i) {
        Preconditions.checkPositionIndex(i, MaterialMapColor.MATERIAL_COLORS.length, "material id");
        return byIdUnsafe(i);
    }

    private static MaterialMapColor byIdUnsafe(int i) {
        MaterialMapColor materialmapcolor = MaterialMapColor.MATERIAL_COLORS[i];

        return materialmapcolor != null ? materialmapcolor : MaterialMapColor.NONE;
    }

    public static int getColorFromPackedId(int i) {
        int j = i & 255;

        return byIdUnsafe(j >> 2).calculateRGBColor(MaterialMapColor.a.byIdUnsafe(j & 3));
    }

    public byte getPackedId(MaterialMapColor.a materialmapcolor_a) {
        return (byte) (this.id << 2 | materialmapcolor_a.id & 3);
    }

    public static enum a {

        LOW(0, 180), NORMAL(1, 220), HIGH(2, 255), LOWEST(3, 135);

        private static final MaterialMapColor.a[] VALUES = new MaterialMapColor.a[]{MaterialMapColor.a.LOW, MaterialMapColor.a.NORMAL, MaterialMapColor.a.HIGH, MaterialMapColor.a.LOWEST};
        public final int id;
        public final int modifier;

        private a(int i, int j) {
            this.id = i;
            this.modifier = j;
        }

        public static MaterialMapColor.a byId(int i) {
            Preconditions.checkPositionIndex(i, MaterialMapColor.a.VALUES.length, "brightness id");
            return byIdUnsafe(i);
        }

        static MaterialMapColor.a byIdUnsafe(int i) {
            return MaterialMapColor.a.VALUES[i];
        }
    }
}
