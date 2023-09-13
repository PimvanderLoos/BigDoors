package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;

public class ParticleType extends Particle<ParticleType> implements ParticleParam {

    private static final ParticleParam.a<ParticleType> a = new ParticleParam.a<ParticleType>() {
        @Override
        public ParticleType b(Particle<ParticleType> particle, StringReader stringreader) throws CommandSyntaxException {
            return (ParticleType) particle;
        }

        @Override
        public ParticleType b(Particle<ParticleType> particle, PacketDataSerializer packetdataserializer) {
            return (ParticleType) particle;
        }
    };
    private final Codec<ParticleType> b = Codec.unit(this::getParticle);

    protected ParticleType(boolean flag) {
        super(flag, ParticleType.a);
    }

    @Override
    public ParticleType getParticle() {
        return this;
    }

    @Override
    public Codec<ParticleType> e() {
        return this.b;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {}

    @Override
    public String a() {
        return IRegistry.PARTICLE_TYPE.getKey(this).toString();
    }
}
