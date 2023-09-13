package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEffect;

public class CaveSound {

    public static final Codec<CaveSound> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.CODEC.fieldOf("sound").forGetter((cavesound) -> {
            return cavesound.soundEvent;
        }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((cavesound) -> {
            return cavesound.tickChance;
        })).apply(instance, CaveSound::new);
    });
    private final SoundEffect soundEvent;
    private final double tickChance;

    public CaveSound(SoundEffect soundeffect, double d0) {
        this.soundEvent = soundeffect;
        this.tickChance = d0;
    }

    public SoundEffect a() {
        return this.soundEvent;
    }

    public double b() {
        return this.tickChance;
    }
}
