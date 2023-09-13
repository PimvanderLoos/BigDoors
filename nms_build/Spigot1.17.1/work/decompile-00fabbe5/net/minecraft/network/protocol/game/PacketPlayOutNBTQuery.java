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
        this.transactionId = packetdataserializer.j();
        this.tag = packetdataserializer.m();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.transactionId);
        packetdataserializer.a(this.tag);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.transactionId;
    }

    @Nullable
    public NBTTagCompound c() {
        return this.tag;
    }

    @Override
    public boolean a() {
        return true;
    }
}
