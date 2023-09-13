package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.Containers;

public class PacketPlayOutOpenWindow implements Packet<PacketListenerPlayOut> {

    private final int containerId;
    private final int type;
    private final IChatBaseComponent title;

    public PacketPlayOutOpenWindow(int i, Containers<?> containers, IChatBaseComponent ichatbasecomponent) {
        this.containerId = i;
        this.type = IRegistry.MENU.getId(containers);
        this.title = ichatbasecomponent;
    }

    public PacketPlayOutOpenWindow(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readVarInt();
        this.type = packetdataserializer.readVarInt();
        this.title = packetdataserializer.readComponent();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.containerId);
        packetdataserializer.writeVarInt(this.type);
        packetdataserializer.writeComponent(this.title);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleOpenScreen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    @Nullable
    public Containers<?> getType() {
        return (Containers) IRegistry.MENU.byId(this.type);
    }

    public IChatBaseComponent getTitle() {
        return this.title;
    }
}
