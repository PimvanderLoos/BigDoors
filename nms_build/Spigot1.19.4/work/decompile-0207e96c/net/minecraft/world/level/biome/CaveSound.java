package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEffect;

public class CaveSound {

    public static final Codec<CaveSound> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.CODEC.fieldOf("sound").forGetter((cavesound) -> {
            return cavesound.soundEvent;
        }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((cavesound) -> {
            return cavesound.tickChance;
        })).apply(instance, CaveSound::new);
    });
    private final Holder<SoundEffect> soundEvent;
    private final double tickChance;

    public CaveSound(Holder<SoundEffect> holder, double d0) {
        this.soundEvent = holder;
        this.tickChance = d0;
    }

    public Holder<SoundEffect> getSoundEvent() {
        return this.soundEvent;
    }

    public double getTickChance() {
        return this.tickChance;
    }
}
