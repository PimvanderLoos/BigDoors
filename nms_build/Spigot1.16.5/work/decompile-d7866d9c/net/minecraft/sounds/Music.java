package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Music {

    public static final Codec<Music> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.a.fieldOf("sound").forGetter((music) -> {
            return music.b;
        }), Codec.INT.fieldOf("min_delay").forGetter((music) -> {
            return music.c;
        }), Codec.INT.fieldOf("max_delay").forGetter((music) -> {
            return music.d;
        }), Codec.BOOL.fieldOf("replace_current_music").forGetter((music) -> {
            return music.e;
        })).apply(instance, Music::new);
    });
    private final SoundEffect b;
    private final int c;
    private final int d;
    private final boolean e;

    public Music(SoundEffect soundeffect, int i, int j, boolean flag) {
        this.b = soundeffect;
        this.c = i;
        this.d = j;
        this.e = flag;
    }
}
