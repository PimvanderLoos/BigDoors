package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayOutSelectAdvancementTab implements Packet<PacketListenerPlayOut> {

    @Nullable
    private MinecraftKey a;

    public PacketPlayOutSelectAdvancementTab() {}

    public PacketPlayOutSelectAdvancementTab(@Nullable MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        if (packetdataserializer.readBoolean()) {
            this.a = packetdataserializer.p();
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeBoolean(this.a != null);
        if (this.a != null) {
            packetdataserializer.a(this.a);
        }

    }
}
