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
        this.tab = (MinecraftKey) packetdataserializer.readNullable(PacketDataSerializer::readResourceLocation);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeNullable(this.tab, PacketDataSerializer::writeResourceLocation);
    }

    @Nullable
    public MinecraftKey getTab() {
        return this.tab;
    }
}
