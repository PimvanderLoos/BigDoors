package net.minecraft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class UtilColor {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_PATTERN = Pattern.compile("\\r\\n|\\v");
    private static final Pattern LINE_END_PATTERN = Pattern.compile("(?:\\r\\n|\\v)$");

    public UtilColor() {}

    public static String a(int i) {
        int j = i / 20;
        int k = j / 60;

        j %= 60;
        return j < 10 ? k + ":0" + j : k + ":" + j;
    }

    public static String a(String s) {
        return UtilColor.STRIP_COLOR_PATTERN.matcher(s).replaceAll("");
    }

    public static boolean b(@Nullable String s) {
        return StringUtils.isEmpty(s);
    }

    public static String a(String s, int i, boolean flag) {
        if (s.length() <= i) {
            return s;
        } else if (flag && i > 3) {
            String s1 = s.substring(0, i - 3);

            return s1 + "...";
        } else {
            return s.substring(0, i);
        }
    }

    public static int c(String s) {
        if (s.isEmpty()) {
            return 0;
        } else {
            Matcher matcher = UtilColor.LINE_PATTERN.matcher(s);

            int i;

            for (i = 1; matcher.find(); ++i) {
                ;
            }

            return i;
        }
    }

    public static boolean d(String s) {
        return UtilColor.LINE_END_PATTERN.matcher(s).find();
    }
}
