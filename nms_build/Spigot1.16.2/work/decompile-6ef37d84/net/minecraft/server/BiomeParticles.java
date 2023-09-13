package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BiomeParticles {

    public static final Codec<BiomeParticles> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Particles.au.fieldOf("options").forGetter((biomeparticles) -> {
            return biomeparticles.b;
        }), Codec.FLOAT.fieldOf("probability").forGetter((biomeparticles) -> {
            return biomeparticles.c;
        })).apply(instance, BiomeParticles::new);
    });
    private final ParticleParam b;
    private final float c;

    public BiomeParticles(ParticleParam particleparam, float f) {
        this.b = particleparam;
        this.c = f;
    }
}
