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
        this.recipe = irecipe.getId();
        this.shiftDown = flag;
    }

    public PacketPlayInAutoRecipe(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.recipe = packetdataserializer.readResourceLocation();
        this.shiftDown = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeResourceLocation(this.recipe);
        packetdataserializer.writeBoolean(this.shiftDown);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handlePlaceRecipe(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public MinecraftKey getRecipe() {
        return this.recipe;
    }

    public boolean isShiftDown() {
        return this.shiftDown;
    }
}
