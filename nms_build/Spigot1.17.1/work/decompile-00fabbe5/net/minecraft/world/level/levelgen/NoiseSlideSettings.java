package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public class NoiseSlideSettings {

    public static final Codec<NoiseSlideSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("target").forGetter(NoiseSlideSettings::a), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(NoiseSlideSettings::b), Codec.INT.fieldOf("offset").forGetter(NoiseSlideSettings::c)).apply(instance, NoiseSlideSettings::new);
    });
    private final int target;
    private final int size;
    private final int offset;

    public NoiseSlideSettings(int i, int j, int k) {
        this.target = i;
        this.size = j;
        this.offset = k;
    }

    public int a() {
        return this.target;
    }

    public int b() {
        return this.size;
    }

    public int c() {
        return this.offset;
    }
}
