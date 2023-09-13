package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketDataSerializer;

public interface ParticleParam {

    Particle<?> getType();

    void writeToNetwork(PacketDataSerializer packetdataserializer);

    String writeToString();

    /** @deprecated */
    @Deprecated
    public interface a<T extends ParticleParam> {

        T fromCommand(Particle<T> particle, StringReader stringreader) throws CommandSyntaxException;

        T fromNetwork(Particle<T> particle, PacketDataSerializer packetdataserializer);
    }
}
