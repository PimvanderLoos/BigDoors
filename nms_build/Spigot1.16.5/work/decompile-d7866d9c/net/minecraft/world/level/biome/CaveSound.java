package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEffect;

public class CaveSound {

    public static final Codec<CaveSound> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.a.fieldOf("sound").forGetter((cavesound) -> {
            return cavesound.b;
        }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((cavesound) -> {
            return cavesound.c;
        })).apply(instance, CaveSound::new);
    });
    private SoundEffect b;
    private double c;

    public CaveSound(SoundEffect soundeffect, double d0) {
        this.b = soundeffect;
        this.c = d0;
    }
}
