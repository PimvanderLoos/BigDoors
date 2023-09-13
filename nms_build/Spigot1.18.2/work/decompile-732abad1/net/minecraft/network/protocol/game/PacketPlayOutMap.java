package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
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
        if (packetdataserializer.readBoolean()) {
            this.decorations = packetdataserializer.readList((packetdataserializer1) -> {
                MapIcon.Type mapicon_type = (MapIcon.Type) packetdataserializer1.readEnum(MapIcon.Type.class);

                return new MapIcon(mapicon_type, packetdataserializer1.readByte(), packetdataserializer1.readByte(), (byte) (packetdataserializer1.readByte() & 15), packetdataserializer1.readBoolean() ? packetdataserializer1.readComponent() : null);
            });
        } else {
            this.decorations = null;
        }

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
        if (this.decorations != null) {
            packetdataserializer.writeBoolean(true);
            packetdataserializer.writeCollection(this.decorations, (packetdataserializer1, mapicon) -> {
                packetdataserializer1.writeEnum(mapicon.getType());
                packetdataserializer1.writeByte(mapicon.getX());
                packetdataserializer1.writeByte(mapicon.getY());
                packetdataserializer1.writeByte(mapicon.getRot() & 15);
                if (mapicon.getName() != null) {
                    packetdataserializer1.writeBoolean(true);
                    packetdataserializer1.writeComponent(mapicon.getName());
                } else {
                    packetdataserializer1.writeBoolean(false);
                }

            });
        } else {
            packetdataserializer.writeBoolean(false);
        }

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
