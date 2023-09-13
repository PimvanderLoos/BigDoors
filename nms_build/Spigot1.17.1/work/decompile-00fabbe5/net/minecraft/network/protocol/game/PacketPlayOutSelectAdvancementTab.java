package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayOutSelectAdvancementTab implements Packet<PacketListenerPlayOut> {

    @Nullable
    private final MinecraftKey tab;

    public PacketPlayOutSelectAdvancementTab(@Nullable MinecraftKey minecraftkey) {
        this.tab = minecraftkey;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public PacketPlayOutSelectAdvancementTab(PacketDataSerializer packetdataserializer) {
        if (packetdataserializer.readBoolean()) {
            this.tab = packetdataserializer.q();
        } else {
            this.tab = null;
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.tab != null);
        if (this.tab != null) {
            packetdataserializer.a(this.tab);
        }

    }

    @Nullable
    public MinecraftKey b() {
        return this.tab;
    }
}
