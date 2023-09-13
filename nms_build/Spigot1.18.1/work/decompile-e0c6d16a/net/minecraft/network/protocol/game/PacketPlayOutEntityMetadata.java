package net.minecraft.network.protocol.game;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.DataWatcher;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {

    private final int id;
    @Nullable
    private final List<DataWatcher.Item<?>> packedItems;

    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.id = i;
        if (flag) {
            this.packedItems = datawatcher.getAll();
            datawatcher.clearDirty();
        } else {
            this.packedItems = datawatcher.packDirty();
        }

    }

    public PacketPlayOutEntityMetadata(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.packedItems = DataWatcher.unpack(packetdataserializer);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        DataWatcher.pack(this.packedItems, packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetEntityData(this);
    }

    @Nullable
    public List<DataWatcher.Item<?>> getUnpackedData() {
        return this.packedItems;
    }

    public int getId() {
        return this.id;
    }
}
