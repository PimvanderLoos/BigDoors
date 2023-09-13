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
        this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
        this.blockHit = packetdataserializer.readBlockHitResult();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.hand);
        packetdataserializer.writeBlockHitResult(this.blockHit);
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
}
