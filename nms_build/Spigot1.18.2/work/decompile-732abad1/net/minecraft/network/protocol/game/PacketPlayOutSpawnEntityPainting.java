package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.decoration.EntityPainting;
import net.minecraft.world.entity.decoration.Paintings;

public class PacketPlayOutSpawnEntityPainting implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final UUID uuid;
    private final BlockPosition pos;
    private final EnumDirection direction;
    private final int motive;

    public PacketPlayOutSpawnEntityPainting(EntityPainting entitypainting) {
        this.id = entitypainting.getId();
        this.uuid = entitypainting.getUUID();
        this.pos = entitypainting.getPos();
        this.direction = entitypainting.getDirection();
        this.motive = IRegistry.MOTIVE.getId(entitypainting.motive);
    }

    public PacketPlayOutSpawnEntityPainting(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.uuid = packetdataserializer.readUUID();
        this.motive = packetdataserializer.readVarInt();
        this.pos = packetdataserializer.readBlockPos();
        this.direction = EnumDirection.from2DDataValue(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeUUID(this.uuid);
        packetdataserializer.writeVarInt(this.motive);
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeByte(this.direction.get2DDataValue());
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddPainting(this);
    }

    public int getId() {
        return this.id;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    public Paintings getMotive() {
        return (Paintings) IRegistry.MOTIVE.byId(this.motive);
    }
}
