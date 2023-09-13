package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.EnumColor;

public enum EnumBannerPatternType {

    BASE("base", "b", false), SQUARE_BOTTOM_LEFT("square_bottom_left", "bl"), SQUARE_BOTTOM_RIGHT("square_bottom_right", "br"), SQUARE_TOP_LEFT("square_top_left", "tl"), SQUARE_TOP_RIGHT("square_top_right", "tr"), STRIPE_BOTTOM("stripe_bottom", "bs"), STRIPE_TOP("stripe_top", "ts"), STRIPE_LEFT("stripe_left", "ls"), STRIPE_RIGHT("stripe_right", "rs"), STRIPE_CENTER("stripe_center", "cs"), STRIPE_MIDDLE("stripe_middle", "ms"), STRIPE_DOWNRIGHT("stripe_downright", "drs"), STRIPE_DOWNLEFT("stripe_downleft", "dls"), STRIPE_SMALL("small_stripes", "ss"), CROSS("cross", "cr"), STRAIGHT_CROSS("straight_cross", "sc"), TRIANGLE_BOTTOM("triangle_bottom", "bt"), TRIANGLE_TOP("triangle_top", "tt"), TRIANGLES_BOTTOM("triangles_bottom", "bts"), TRIANGLES_TOP("triangles_top", "tts"), DIAGONAL_LEFT("diagonal_left", "ld"), DIAGONAL_RIGHT("diagonal_up_right", "rd"), DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud"), DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud"), CIRCLE_MIDDLE("circle", "mc"), RHOMBUS_MIDDLE("rhombus", "mr"), HALF_VERTICAL("half_vertical", "vh"), HALF_HORIZONTAL("half_horizontal", "hh"), HALF_VERTICAL_MIRROR("half_vertical_right", "vhr"), HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb"), BORDER("border", "bo"), CURLY_BORDER("curly_border", "cbo"), GRADIENT("gradient", "gra"), GRADIENT_UP("gradient_up", "gru"), BRICKS("bricks", "bri"), GLOBE("globe", "glb", true), CREEPER("creeper", "cre", true), SKULL("skull", "sku", true), FLOWER("flower", "flo", true), MOJANG("mojang", "moj", true), PIGLIN("piglin", "pig", true);

    private static final EnumBannerPatternType[] VALUES = values();
    public static final int COUNT = EnumBannerPatternType.VALUES.length;
    public static final int PATTERN_ITEM_COUNT = (int) Arrays.stream(EnumBannerPatternType.VALUES).filter((enumbannerpatterntype) -> {
        return enumbannerpatterntype.hasPatternItem;
    }).count();
    public static final int AVAILABLE_PATTERNS = EnumBannerPatternType.COUNT - EnumBannerPatternType.PATTERN_ITEM_COUNT - 1;
    private final boolean hasPatternItem;
    private final String filename;
    final String hashname;

    private EnumBannerPatternType(String s, String s1) {
        this(s, s1, false);
    }

    private EnumBannerPatternType(String s, String s1, boolean flag) {
        this.filename = s;
        this.hashname = s1;
        this.hasPatternItem = flag;
    }

    public MinecraftKey a(boolean flag) {
        String s = flag ? "banner" : "shield";

        return new MinecraftKey("entity/" + s + "/" + this.a());
    }

    public String a() {
        return this.filename;
    }

    public String b() {
        return this.hashname;
    }

    @Nullable
    public static EnumBannerPatternType a(String s) {
        EnumBannerPatternType[] aenumbannerpatterntype = values();
        int i = aenumbannerpatterntype.length;

        for (int j = 0; j < i; ++j) {
            EnumBannerPatternType enumbannerpatterntype = aenumbannerpatterntype[j];

            if (enumbannerpatterntype.hashname.equals(s)) {
                return enumbannerpatterntype;
            }
        }

        return null;
    }

    @Nullable
    public static EnumBannerPatternType b(String s) {
        EnumBannerPatternType[] aenumbannerpatterntype = values();
        int i = aenumbannerpatterntype.length;

        for (int j = 0; j < i; ++j) {
            EnumBannerPatternType enumbannerpatterntype = aenumbannerpatterntype[j];

            if (enumbannerpatterntype.filename.equals(s)) {
                return enumbannerpatterntype;
            }
        }

        return null;
    }

    public static class a {

        private final List<Pair<EnumBannerPatternType, EnumColor>> patterns = Lists.newArrayList();

        public a() {}

        public EnumBannerPatternType.a a(EnumBannerPatternType enumbannerpatterntype, EnumColor enumcolor) {
            return this.a(Pair.of(enumbannerpatterntype, enumcolor));
        }

        public EnumBannerPatternType.a a(Pair<EnumBannerPatternType, EnumColor> pair) {
            this.patterns.add(pair);
            return this;
        }

        public NBTTagList a() {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.patterns.iterator();

            while (iterator.hasNext()) {
                Pair<EnumBannerPatternType, EnumColor> pair = (Pair) iterator.next();
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setString("Pattern", ((EnumBannerPatternType) pair.getFirst()).hashname);
                nbttagcompound.setInt("Color", ((EnumColor) pair.getSecond()).getColorIndex());
                nbttaglist.add(nbttagcompound);
            }

            return nbttaglist;
        }
    }
}
