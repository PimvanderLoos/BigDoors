package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.DataWatcher;

public record PacketPlayOutEntityMetadata(int id, List<DataWatcher.b<?>> packedItems) implements Packet<PacketListenerPlayOut> {

    public static final int EOF_MARKER = 255;

    public PacketPlayOutEntityMetadata(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readVarInt(), unpack(packetdataserializer));
    }

    private static void pack(List<DataWatcher.b<?>> list, PacketDataSerializer packetdataserializer) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            DataWatcher.b<?> datawatcher_b = (DataWatcher.b) iterator.next();

            datawatcher_b.write(packetdataserializer);
        }

        packetdataserializer.writeByte(255);
    }

    private static List<DataWatcher.b<?>> unpack(PacketDataSerializer packetdataserializer) {
        ArrayList arraylist = new ArrayList();

        short short0;

        while ((short0 = packetdataserializer.readUnsignedByte()) != 255) {
            arraylist.add(DataWatcher.b.read(packetdataserializer, short0));
        }

        return arraylist;
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        pack(this.packedItems, packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetEntityData(this);
    }
}
