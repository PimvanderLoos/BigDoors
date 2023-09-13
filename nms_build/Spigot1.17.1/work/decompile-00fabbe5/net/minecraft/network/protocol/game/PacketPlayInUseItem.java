package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class PacketPlayInUseItem implements Packet<PacketListenerPlayIn> {

    private final MovingObjectPositionBlock blockHit;
    private final EnumHand hand;

    public PacketPlayInUseItem(EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        this.hand = enumhand;
        this.blockHit = movingobjectpositionblock;
    }

    public PacketPlayInUseItem(PacketDataSerializer packetdataserializer) {
        this.hand = (EnumHand) packetdataserializer.a(EnumHand.class);
        this.blockHit = packetdataserializer.s();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.hand);
        packetdataserializer.a(this.blockHit);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public EnumHand b() {
        return this.hand;
    }

    public MovingObjectPositionBlock c() {
        return this.blockHit;
    }
}
