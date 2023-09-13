package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.RandomSource;

public class BiomeParticles {

    public static final Codec<BiomeParticles> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Particles.CODEC.fieldOf("options").forGetter((biomeparticles) -> {
            return biomeparticles.options;
        }), Codec.FLOAT.fieldOf("probability").forGetter((biomeparticles) -> {
            return biomeparticles.probability;
        })).apply(instance, BiomeParticles::new);
    });
    private final ParticleParam options;
    private final float probability;

    public BiomeParticles(ParticleParam particleparam, float f) {
        this.options = particleparam;
        this.probability = f;
    }

    public ParticleParam getOptions() {
        return this.options;
    }

    public boolean canSpawn(RandomSource randomsource) {
        return randomsource.nextFloat() <= this.probability;
    }
}
