package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;

public class CaveSoundSettings {

    public static final Codec<CaveSoundSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SoundEffect.CODEC.fieldOf("sound").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.soundEvent;
        }), Codec.INT.fieldOf("tick_delay").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.tickDelay;
        }), Codec.INT.fieldOf("block_search_extent").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.blockSearchExtent;
        }), Codec.DOUBLE.fieldOf("offset").forGetter((cavesoundsettings) -> {
            return cavesoundsettings.soundPositionOffset;
        })).apply(instance, CaveSoundSettings::new);
    });
    public static final CaveSoundSettings LEGACY_CAVE_SETTINGS = new CaveSoundSettings(SoundEffects.AMBIENT_CAVE, 6000, 8, 2.0D);
    private final Holder<SoundEffect> soundEvent;
    private final int tickDelay;
    private final int blockSearchExtent;
    private final double soundPositionOffset;

    public CaveSoundSettings(Holder<SoundEffect> holder, int i, int j, double d0) {
        this.soundEvent = holder;
        this.tickDelay = i;
        this.blockSearchExtent = j;
        this.soundPositionOffset = d0;
    }

    public Holder<SoundEffect> getSoundEvent() {
        return this.soundEvent;
    }

    public int getTickDelay() {
        return this.tickDelay;
    }

    public int getBlockSearchExtent() {
        return this.blockSearchExtent;
    }

    public double getSoundPositionOffset() {
        return this.soundPositionOffset;
    }
}
