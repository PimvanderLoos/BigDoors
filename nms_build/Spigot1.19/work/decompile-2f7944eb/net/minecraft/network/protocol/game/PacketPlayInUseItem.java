package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class PacketPlayInUseItem implements Packet<PacketListenerPlayIn> {

    private final MovingObjectPositionBlock blockHit;
    private final EnumHand hand;
    private final int sequence;

    public PacketPlayInUseItem(EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock, int i) {
        this.hand = enumhand;
        this.blockHit = movingobjectpositionblock;
        this.sequence = i;
    }

    public PacketPlayInUseItem(PacketDataSerializer packetdataserializer) {
        this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
        this.blockHit = packetdataserializer.readBlockHitResult();
        this.sequence = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.hand);
        packetdataserializer.writeBlockHitResult(this.blockHit);
        packetdataserializer.writeVarInt(this.sequence);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleUseItemOn(this);
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public MovingObjectPositionBlock getHitResult() {
        return this.blockHit;
    }

    public int getSequence() {
        return this.sequence;
    }
}
