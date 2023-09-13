package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;

public class ParticleType extends Particle<ParticleType> implements ParticleParam {

    private static final ParticleParam.a<ParticleType> DESERIALIZER = new ParticleParam.a<ParticleType>() {
        @Override
        public ParticleType b(Particle<ParticleType> particle, StringReader stringreader) {
            return (ParticleType) particle;
        }

        @Override
        public ParticleType b(Particle<ParticleType> particle, PacketDataSerializer packetdataserializer) {
            return (ParticleType) particle;
        }
    };
    private final Codec<ParticleType> codec = Codec.unit(this::getParticle);

    protected ParticleType(boolean flag) {
        super(flag, ParticleType.DESERIALIZER);
    }

    @Override
    public ParticleType getParticle() {
        return this;
    }

    @Override
    public Codec<ParticleType> e() {
        return this.codec;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {}

    @Override
    public String a() {
        return IRegistry.PARTICLE_TYPE.getKey(this).toString();
    }
}
