package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ParticleType extends Particle<ParticleType> implements ParticleParam {

    private static final ParticleParam.a<ParticleType> b = new ParticleParam.a() {
        public ParticleType a(Particle<ParticleType> particle, StringReader stringreader) throws CommandSyntaxException {
            return (ParticleType) particle;
        }

        public ParticleType a(Particle<ParticleType> particle, PacketDataSerializer packetdataserializer) {
            return (ParticleType) particle;
        }

        public ParticleParam b(Particle particle, PacketDataSerializer packetdataserializer) {
            return this.a(particle, packetdataserializer);
        }

        public ParticleParam b(Particle particle, StringReader stringreader) throws CommandSyntaxException {
            return this.a(particle, stringreader);
        }
    };

    protected ParticleType(MinecraftKey minecraftkey, boolean flag) {
        super(minecraftkey, flag, ParticleType.b);
    }

    public Particle<ParticleType> b() {
        return this;
    }

    public void a(PacketDataSerializer packetdataserializer) {}

    public String a() {
        return this.d().toString();
    }
}
