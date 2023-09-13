package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginInStart implements Packet<PacketLoginInListener> {

    private final GameProfile gameProfile;

    public PacketLoginInStart(GameProfile gameprofile) {
        this.gameProfile = gameprofile;
    }

    public PacketLoginInStart(PacketDataSerializer packetdataserializer) {
        this.gameProfile = new GameProfile((UUID) null, packetdataserializer.e(16));
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.gameProfile.getName());
    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }

    public GameProfile b() {
        return this.gameProfile;
    }
}
