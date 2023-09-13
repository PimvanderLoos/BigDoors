package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutSuccess implements Packet<PacketLoginOutListener> {

    private GameProfile a;

    public PacketLoginOutSuccess() {}

    public PacketLoginOutSuccess(GameProfile gameprofile) {
        this.a = gameprofile;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        int[] aint = new int[4];

        for (int i = 0; i < aint.length; ++i) {
            aint[i] = packetdataserializer.readInt();
        }

        UUID uuid = MinecraftSerializableUUID.a(aint);
        String s = packetdataserializer.e(16);

        this.a = new GameProfile(uuid, s);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        int[] aint = MinecraftSerializableUUID.a(this.a.getId());
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint[j];

            packetdataserializer.writeInt(k);
        }

        packetdataserializer.a(this.a.getName());
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }
}
