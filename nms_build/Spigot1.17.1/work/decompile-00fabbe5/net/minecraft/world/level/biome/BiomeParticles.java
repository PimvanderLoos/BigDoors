package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;

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

    public ParticleParam a() {
        return this.options;
    }

    public boolean a(Random random) {
        return random.nextFloat() <= this.probability;
    }
}
