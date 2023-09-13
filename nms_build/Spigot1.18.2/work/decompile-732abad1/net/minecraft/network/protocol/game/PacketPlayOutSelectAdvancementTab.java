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

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSelectAdvancementsTab(this);
    }

    public PacketPlayOutSelectAdvancementTab(PacketDataSerializer packetdataserializer) {
        if (packetdataserializer.readBoolean()) {
            this.tab = packetdataserializer.readResourceLocation();
        } else {
            this.tab = null;
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.tab != null);
        if (this.tab != null) {
            packetdataserializer.writeResourceLocation(this.tab);
        }

    }

    @Nullable
    public MinecraftKey getTab() {
        return this.tab;
    }
}
