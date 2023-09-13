package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.saveddata.maps.MapIcon;
import net.minecraft.world.level.saveddata.maps.WorldMap;

public class PacketPlayOutMap implements Packet<PacketListenerPlayOut> {

    private final int mapId;
    private final byte scale;
    private final boolean locked;
    @Nullable
    private final List<MapIcon> decorations;
    @Nullable
    private final WorldMap.b colorPatch;

    public PacketPlayOutMap(int i, byte b0, boolean flag, @Nullable Collection<MapIcon> collection, @Nullable WorldMap.b worldmap_b) {
        this.mapId = i;
        this.scale = b0;
        this.locked = flag;
        this.decorations = collection != null ? Lists.newArrayList(collection) : null;
        this.colorPatch = worldmap_b;
    }

    public PacketPlayOutMap(PacketDataSerializer packetdataserializer) {
        this.mapId = packetdataserializer.readVarInt();
        this.scale = packetdataserializer.readByte();
        this.locked = packetdataserializer.readBoolean();
        this.decorations = (List) packetdataserializer.readNullable((packetdataserializer1) -> {
            return packetdataserializer1.readList((packetdataserializer2) -> {
                MapIcon.Type mapicon_type = (MapIcon.Type) packetdataserializer2.readEnum(MapIcon.Type.class);
                byte b0 = packetdataserializer2.readByte();
                byte b1 = packetdataserializer2.readByte();
                byte b2 = (byte) (packetdataserializer2.readByte() & 15);
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) packetdataserializer2.readNullable(PacketDataSerializer::readComponent);

                return new MapIcon(mapicon_type, b0, b1, b2, ichatbasecomponent);
            });
        });
        short short0 = packetdataserializer.readUnsignedByte();

        if (short0 > 0) {
            short short1 = packetdataserializer.readUnsignedByte();
            short short2 = packetdataserializer.readUnsignedByte();
            short short3 = packetdataserializer.readUnsignedByte();
            byte[] abyte = packetdataserializer.readByteArray();

            this.colorPatch = new WorldMap.b(short2, short3, short0, short1, abyte);
        } else {
            this.colorPatch = null;
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.mapId);
        packetdataserializer.writeByte(this.scale);
        packetdataserializer.writeBoolean(this.locked);
        packetdataserializer.writeNullable(this.decorations, (packetdataserializer1, list) -> {
            packetdataserializer1.writeCollection(list, (packetdataserializer2, mapicon) -> {
                packetdataserializer2.writeEnum(mapicon.getType());
                packetdataserializer2.writeByte(mapicon.getX());
                packetdataserializer2.writeByte(mapicon.getY());
                packetdataserializer2.writeByte(mapicon.getRot() & 15);
                packetdataserializer2.writeNullable(mapicon.getName(), PacketDataSerializer::writeComponent);
            });
        });
        if (this.colorPatch != null) {
            packetdataserializer.writeByte(this.colorPatch.width);
            packetdataserializer.writeByte(this.colorPatch.height);
            packetdataserializer.writeByte(this.colorPatch.startX);
            packetdataserializer.writeByte(this.colorPatch.startY);
            packetdataserializer.writeByteArray(this.colorPatch.mapColors);
        } else {
            packetdataserializer.writeByte(0);
        }

    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleMapItemData(this);
    }

    public int getMapId() {
        return this.mapId;
    }

    public void applyToMap(WorldMap worldmap) {
        if (this.decorations != null) {
            worldmap.addClientSideDecorations(this.decorations);
        }

        if (this.colorPatch != null) {
            this.colorPatch.applyToMap(worldmap);
        }

    }

    public byte getScale() {
        return this.scale;
    }

    public boolean isLocked() {
        return this.locked;
    }
}
