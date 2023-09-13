package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutSuccess implements Packet<PacketLoginOutListener> {

    private final GameProfile gameProfile;

    public PacketLoginOutSuccess(GameProfile gameprofile) {
        this.gameProfile = gameprofile;
    }

    public PacketLoginOutSuccess(PacketDataSerializer packetdataserializer) {
        this.gameProfile = packetdataserializer.readGameProfile();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeGameProfile(this.gameProfile);
    }

    public void handle(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.handleGameProfile(this);
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}
