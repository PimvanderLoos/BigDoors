package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSlideSettings {

    public static final Codec<NoiseSlideSettings> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("target").forGetter(NoiseSlideSettings::a), Codec.intRange(0, 256).fieldOf("size").forGetter(NoiseSlideSettings::b), Codec.INT.fieldOf("offset").forGetter(NoiseSlideSettings::c)).apply(instance, NoiseSlideSettings::new);
    });
    private final int b;
    private final int c;
    private final int d;

    public NoiseSlideSettings(int i, int j, int k) {
        this.b = i;
        this.c = j;
        this.d = k;
    }

    public int a() {
        return this.b;
    }

    public int b() {
        return this.c;
    }

    public int c() {
        return this.d;
    }
}
