package net.minecraft.core.particles;

import com.mojang.serialization.Codec;

public abstract class Particle<T extends ParticleParam> {

    private final boolean overrideLimiter;
    private final ParticleParam.a<T> deserializer;

    protected Particle(boolean flag, ParticleParam.a<T> particleparam_a) {
        this.overrideLimiter = flag;
        this.deserializer = particleparam_a;
    }

    public boolean getOverrideLimiter() {
        return this.overrideLimiter;
    }

    public ParticleParam.a<T> getDeserializer() {
        return this.deserializer;
    }

    public abstract Codec<T> codec();
}
