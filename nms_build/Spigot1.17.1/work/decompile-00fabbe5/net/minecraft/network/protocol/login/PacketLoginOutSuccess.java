package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutSuccess implements Packet<PacketLoginOutListener> {

    private final GameProfile gameProfile;

    public PacketLoginOutSuccess(GameProfile gameprofile) {
        this.gameProfile = gameprofile;
    }

    public PacketLoginOutSuccess(PacketDataSerializer packetdataserializer) {
        int[] aint = new int[4];

        for (int i = 0; i < aint.length; ++i) {
            aint[i] = packetdataserializer.readInt();
        }

        UUID uuid = MinecraftSerializableUUID.a(aint);
        String s = packetdataserializer.e(16);

        this.gameProfile = new GameProfile(uuid, s);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        int[] aint = MinecraftSerializableUUID.a(this.gameProfile.getId());
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint[j];

            packetdataserializer.writeInt(k);
        }

        packetdataserializer.a(this.gameProfile.getName());
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }

    public GameProfile b() {
        return this.gameProfile;
    }
}
