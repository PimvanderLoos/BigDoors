package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;

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
