package net.minecraft.server;

@Deprecated
public class LocaleI18n {

    private static final LocaleLanguage a = LocaleLanguage.a();
    private static final LocaleLanguage b = new LocaleLanguage();

    @Deprecated
    public static String get(String s) {
        return LocaleI18n.a.a(s);
    }

    @Deprecated
    public static String a(String s, Object... aobject) {
        return LocaleI18n.a.a(s, aobject);
    }

    @Deprecated
    public static String b(String s) {
        return LocaleI18n.b.a(s);
    }

    @Deprecated
    public static boolean c(String s) {
        return LocaleI18n.a.b(s);
    }

    public static long a() {
        return LocaleI18n.a.c();
    }
}
