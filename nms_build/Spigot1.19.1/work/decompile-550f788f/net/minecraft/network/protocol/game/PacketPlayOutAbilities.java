package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.PlayerAbilities;

public class PacketPlayOutAbilities implements Packet<PacketListenerPlayOut> {

    private static final int FLAG_INVULNERABLE = 1;
    private static final int FLAG_FLYING = 2;
    private static final int FLAG_CAN_FLY = 4;
    private static final int FLAG_INSTABUILD = 8;
    private final boolean invulnerable;
    private final boolean isFlying;
    private final boolean canFly;
    private final boolean instabuild;
    private final float flyingSpeed;
    private final float walkingSpeed;

    public PacketPlayOutAbilities(PlayerAbilities playerabilities) {
        this.invulnerable = playerabilities.invulnerable;
        this.isFlying = playerabilities.flying;
        this.canFly = playerabilities.mayfly;
        this.instabuild = playerabilities.instabuild;
        this.flyingSpeed = playerabilities.getFlyingSpeed();
        this.walkingSpeed = playerabilities.getWalkingSpeed();
    }

    public PacketPlayOutAbilities(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();

        this.invulnerable = (b0 & 1) != 0;
        this.isFlying = (b0 & 2) != 0;
        this.canFly = (b0 & 4) != 0;
        this.instabuild = (b0 & 8) != 0;
        this.flyingSpeed = packetdataserializer.readFloat();
        this.walkingSpeed = packetdataserializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        byte b0 = 0;

        if (this.invulnerable) {
            b0 = (byte) (b0 | 1);
        }

        if (this.isFlying) {
            b0 = (byte) (b0 | 2);
        }

        if (this.canFly) {
            b0 = (byte) (b0 | 4);
        }

        if (this.instabuild) {
            b0 = (byte) (b0 | 8);
        }

        packetdataserializer.writeByte(b0);
        packetdataserializer.writeFloat(this.flyingSpeed);
        packetdataserializer.writeFloat(this.walkingSpeed);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public boolean canInstabuild() {
        return this.instabuild;
    }

    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }
}
