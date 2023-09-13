package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInSteerVehicle implements Packet<PacketListenerPlayIn> {

    private static final int FLAG_JUMPING = 1;
    private static final int FLAG_SHIFT_KEY_DOWN = 2;
    private final float xxa;
    private final float zza;
    private final boolean isJumping;
    private final boolean isShiftKeyDown;

    public PacketPlayInSteerVehicle(float f, float f1, boolean flag, boolean flag1) {
        this.xxa = f;
        this.zza = f1;
        this.isJumping = flag;
        this.isShiftKeyDown = flag1;
    }

    public PacketPlayInSteerVehicle(PacketDataSerializer packetdataserializer) {
        this.xxa = packetdataserializer.readFloat();
        this.zza = packetdataserializer.readFloat();
        byte b0 = packetdataserializer.readByte();

        this.isJumping = (b0 & 1) > 0;
        this.isShiftKeyDown = (b0 & 2) > 0;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.xxa);
        packetdataserializer.writeFloat(this.zza);
        byte b0 = 0;

        if (this.isJumping) {
            b0 = (byte) (b0 | 1);
        }

        if (this.isShiftKeyDown) {
            b0 = (byte) (b0 | 2);
        }

        packetdataserializer.writeByte(b0);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public float b() {
        return this.xxa;
    }

    public float c() {
        return this.zza;
    }

    public boolean d() {
        return this.isJumping;
    }

    public boolean e() {
        return this.isShiftKeyDown;
    }
}
