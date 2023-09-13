package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ParticleType extends Particle<ParticleType> implements ParticleParam {

    private static final ParticleParam.a<ParticleType> a = new ParticleParam.a<ParticleType>() {
        public ParticleType b(Particle<ParticleType> particle, StringReader stringreader) throws CommandSyntaxException {
            return (ParticleType) particle;
        }

        public ParticleType b(Particle<ParticleType> particle, PacketDataSerializer packetdataserializer) {
            return (ParticleType) particle;
        }
    };

    protected ParticleType(MinecraftKey minecraftkey, boolean flag) {
        super(minecraftkey, flag, ParticleType.a);
    }

    public Particle<ParticleType> b() {
        return this;
    }

    public void a(PacketDataSerializer packetdataserializer) {}

    public String a() {
        return this.d().toString();
    }
}
