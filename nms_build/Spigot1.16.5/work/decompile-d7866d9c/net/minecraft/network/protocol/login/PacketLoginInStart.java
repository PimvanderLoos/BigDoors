package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginInStart implements Packet<PacketLoginInListener> {

    private GameProfile a;

    public PacketLoginInStart() {}

    public PacketLoginInStart(GameProfile gameprofile) {
        this.a = gameprofile;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = new GameProfile((UUID) null, packetdataserializer.e(16));
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a.getName());
    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }

    public GameProfile b() {
        return this.a;
    }
}
