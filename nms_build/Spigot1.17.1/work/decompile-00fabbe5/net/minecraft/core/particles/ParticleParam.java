package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketDataSerializer;

public interface ParticleParam {

    Particle<?> getParticle();

    void a(PacketDataSerializer packetdataserializer);

    String a();

    @Deprecated
    public interface a<T extends ParticleParam> {

        T b(Particle<T> particle, StringReader stringreader) throws CommandSyntaxException;

        T b(Particle<T> particle, PacketDataSerializer packetdataserializer);
    }
}
