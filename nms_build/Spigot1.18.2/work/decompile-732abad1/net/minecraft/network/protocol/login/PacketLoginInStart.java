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
        this.gameProfile = new GameProfile((UUID) null, packetdataserializer.readUtf(16));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.gameProfile.getName());
    }

    public void handle(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.handleHello(this);
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}
