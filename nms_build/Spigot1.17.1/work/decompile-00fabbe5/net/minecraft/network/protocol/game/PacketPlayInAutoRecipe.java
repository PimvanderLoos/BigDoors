package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.crafting.IRecipe;

public class PacketPlayInAutoRecipe implements Packet<PacketListenerPlayIn> {

    private final int containerId;
    private final MinecraftKey recipe;
    private final boolean shiftDown;

    public PacketPlayInAutoRecipe(int i, IRecipe<?> irecipe, boolean flag) {
        this.containerId = i;
        this.recipe = irecipe.getKey();
        this.shiftDown = flag;
    }

    public PacketPlayInAutoRecipe(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.recipe = packetdataserializer.q();
        this.shiftDown = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.a(this.recipe);
        packetdataserializer.writeBoolean(this.shiftDown);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.containerId;
    }

    public MinecraftKey c() {
        return this.recipe;
    }

    public boolean d() {
        return this.shiftDown;
    }
}
