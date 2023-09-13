package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.RecipeBookType;

public class PacketPlayInRecipeSettings implements Packet<PacketListenerPlayIn> {

    private final RecipeBookType bookType;
    private final boolean isOpen;
    private final boolean isFiltering;

    public PacketPlayInRecipeSettings(RecipeBookType recipebooktype, boolean flag, boolean flag1) {
        this.bookType = recipebooktype;
        this.isOpen = flag;
        this.isFiltering = flag1;
    }

    public PacketPlayInRecipeSettings(PacketDataSerializer packetdataserializer) {
        this.bookType = (RecipeBookType) packetdataserializer.readEnum(RecipeBookType.class);
        this.isOpen = packetdataserializer.readBoolean();
        this.isFiltering = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.bookType);
        packetdataserializer.writeBoolean(this.isOpen);
        packetdataserializer.writeBoolean(this.isFiltering);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleRecipeBookChangeSettingsPacket(this);
    }

    public RecipeBookType getBookType() {
        return this.bookType;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean isFiltering() {
        return this.isFiltering;
    }
}
