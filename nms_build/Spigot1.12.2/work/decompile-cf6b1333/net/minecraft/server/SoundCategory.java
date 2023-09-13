package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;

public enum SoundCategory {

    MASTER("master"), MUSIC("music"), RECORDS("record"), WEATHER("weather"), BLOCKS("block"), HOSTILE("hostile"), NEUTRAL("neutral"), PLAYERS("player"), AMBIENT("ambient"), VOICE("voice");

    private static final Map<String, SoundCategory> k = Maps.newHashMap();
    private final String l;

    private SoundCategory(String s) {
        this.l = s;
    }

    public String a() {
        return this.l;
    }

    public static SoundCategory a(String s) {
        return (SoundCategory) SoundCategory.k.get(s);
    }

    public static Set<String> b() {
        return SoundCategory.k.keySet();
    }

    static {
        SoundCategory[] asoundcategory = values();
        int i = asoundcategory.length;

        for (int j = 0; j < i; ++j) {
            SoundCategory soundcategory = asoundcategory[j];

            if (SoundCategory.k.containsKey(soundcategory.a())) {
                throw new Error("Clash in Sound Category name pools! Cannot insert " + soundcategory);
            }

            SoundCategory.k.put(soundcategory.a(), soundcategory);
        }

    }
}
