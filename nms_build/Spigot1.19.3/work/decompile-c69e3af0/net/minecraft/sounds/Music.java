package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public class Music {

    public static final Codec<Music> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.CODEC.fieldOf("sound").forGetter((music) -> {
            return music.event;
        }), Codec.INT.fieldOf("min_delay").forGetter((music) -> {
            return music.minDelay;
        }), Codec.INT.fieldOf("max_delay").forGetter((music) -> {
            return music.maxDelay;
        }), Codec.BOOL.fieldOf("replace_current_music").forGetter((music) -> {
            return music.replaceCurrentMusic;
        })).apply(instance, Music::new);
    });
    private final Holder<SoundEffect> event;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public Music(Holder<SoundEffect> holder, int i, int j, boolean flag) {
        this.event = holder;
        this.minDelay = i;
        this.maxDelay = j;
        this.replaceCurrentMusic = flag;
    }

    public Holder<SoundEffect> getEvent() {
        return this.event;
    }

    public int getMinDelay() {
        return this.minDelay;
    }

    public int getMaxDelay() {
        return this.maxDelay;
    }

    public boolean replaceCurrentMusic() {
        return this.replaceCurrentMusic;
    }
}
