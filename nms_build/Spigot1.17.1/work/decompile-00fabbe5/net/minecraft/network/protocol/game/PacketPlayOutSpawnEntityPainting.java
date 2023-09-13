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
        this.uuid = entitypainting.getUniqueID();
        this.pos = entitypainting.getBlockPosition();
        this.direction = entitypainting.getDirection();
        this.motive = IRegistry.MOTIVE.getId(entitypainting.motive);
    }

    public PacketPlayOutSpawnEntityPainting(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.uuid = packetdataserializer.l();
        this.motive = packetdataserializer.j();
        this.pos = packetdataserializer.f();
        this.direction = EnumDirection.fromType2(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.a(this.uuid);
        packetdataserializer.d(this.motive);
        packetdataserializer.a(this.pos);
        packetdataserializer.writeByte(this.direction.get2DRotationValue());
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.id;
    }

    public UUID c() {
        return this.uuid;
    }

    public BlockPosition d() {
        return this.pos;
    }

    public EnumDirection e() {
        return this.direction;
    }

    public Paintings f() {
        return (Paintings) IRegistry.MOTIVE.fromId(this.motive);
    }
}
