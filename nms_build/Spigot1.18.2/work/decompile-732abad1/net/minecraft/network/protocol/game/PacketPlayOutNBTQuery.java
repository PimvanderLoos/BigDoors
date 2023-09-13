package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutNBTQuery implements Packet<PacketListenerPlayOut> {

    private final int transactionId;
    @Nullable
    private final NBTTagCompound tag;

    public PacketPlayOutNBTQuery(int i, @Nullable NBTTagCompound nbttagcompound) {
        this.transactionId = i;
        this.tag = nbttagcompound;
    }

    public PacketPlayOutNBTQuery(PacketDataSerializer packetdataserializer) {
        this.transactionId = packetdataserializer.readVarInt();
        this.tag = packetdataserializer.readNbt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.transactionId);
        packetdataserializer.writeNbt(this.tag);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleTagQueryPacket(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public NBTTagCompound getTag() {
        return this.tag;
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
