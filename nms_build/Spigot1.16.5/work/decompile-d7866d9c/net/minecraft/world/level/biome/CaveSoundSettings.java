package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;

public class CaveSoundSettings {

    public static final Codec<CaveSoundSettings> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.a.fieldOf("sound").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.c;
        }), Codec.INT.fieldOf("tick_delay").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.d;
        }), Codec.INT.fieldOf("block_search_extent").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.e;
        }), Codec.DOUBLE.fieldOf("offset").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.f;
        })).apply(instance, CaveSoundSettings::new);
    });
    public static final CaveSoundSettings b = new CaveSoundSettings(SoundEffects.AMBIENT_CAVE, 6000, 8, 2.0D);
    private SoundEffect c;
    private int d;
    private int e;
    private double f;

    public CaveSoundSettings(SoundEffect soundeffect, int i, int j, double d0) {
        this.c = soundeffect;
        this.d = i;
        this.e = j;
        this.f = d0;
    }
}
