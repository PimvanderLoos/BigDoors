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
        this.containerId = packetdataserializer.j();
        this.type = packetdataserializer.j();
        this.title = packetdataserializer.i();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.containerId);
        packetdataserializer.d(this.type);
        packetdataserializer.a(this.title);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.containerId;
    }

    @Nullable
    public Containers<?> c() {
        return (Containers) IRegistry.MENU.fromId(this.type);
    }

    public IChatBaseComponent d() {
        return this.title;
    }
}
