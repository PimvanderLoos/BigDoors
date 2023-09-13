package net.minecraft.core.particles;

import com.mojang.serialization.Codec;

public abstract class Particle<T extends ParticleParam> {

    private final boolean a;
    private final ParticleParam.a<T> b;

    protected Particle(boolean flag, ParticleParam.a<T> particleparam_a) {
        this.a = flag;
        this.b = particleparam_a;
    }

    public ParticleParam.a<T> d() {
        return this.b;
    }

    public abstract Codec<T> e();
}
