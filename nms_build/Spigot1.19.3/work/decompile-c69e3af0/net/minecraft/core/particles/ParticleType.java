package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;

public class ParticleType extends Particle<ParticleType> implements ParticleParam {

    private static final ParticleParam.a<ParticleType> DESERIALIZER = new ParticleParam.a<ParticleType>() {
        @Override
        public ParticleType fromCommand(Particle<ParticleType> particle, StringReader stringreader) {
            return (ParticleType) particle;
        }

        @Override
        public ParticleType fromNetwork(Particle<ParticleType> particle, PacketDataSerializer packetdataserializer) {
            return (ParticleType) particle;
        }
    };
    private final Codec<ParticleType> codec = Codec.unit(this::getType);

    protected ParticleType(boolean flag) {
        super(flag, ParticleType.DESERIALIZER);
    }

    @Override
    public ParticleType getType() {
        return this;
    }

    @Override
    public Codec<ParticleType> codec() {
        return this.codec;
    }

    @Override
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {}

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this).toString();
    }
}
