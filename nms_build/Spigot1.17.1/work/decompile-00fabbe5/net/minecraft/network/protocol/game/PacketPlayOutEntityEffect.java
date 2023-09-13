package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;

public class PacketPlayOutEntityEffect implements Packet<PacketListenerPlayOut> {

    private static final int FLAG_AMBIENT = 1;
    private static final int FLAG_VISIBLE = 2;
    private static final int FLAG_SHOW_ICON = 4;
    private final int entityId;
    private final byte effectId;
    private final byte effectAmplifier;
    private final int effectDurationTicks;
    private final byte flags;

    public PacketPlayOutEntityEffect(int i, MobEffect mobeffect) {
        this.entityId = i;
        this.effectId = (byte) (MobEffectList.getId(mobeffect.getMobEffect()) & 255);
        this.effectAmplifier = (byte) (mobeffect.getAmplifier() & 255);
        if (mobeffect.getDuration() > 32767) {
            this.effectDurationTicks = 32767;
        } else {
            this.effectDurationTicks = mobeffect.getDuration();
        }

        byte b0 = 0;

        if (mobeffect.isAmbient()) {
            b0 = (byte) (b0 | 1);
        }

        if (mobeffect.isShowParticles()) {
            b0 = (byte) (b0 | 2);
        }

        if (mobeffect.isShowIcon()) {
            b0 = (byte) (b0 | 4);
        }

        this.flags = b0;
    }

    public PacketPlayOutEntityEffect(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.j();
        this.effectId = packetdataserializer.readByte();
        this.effectAmplifier = packetdataserializer.readByte();
        this.effectDurationTicks = packetdataserializer.j();
        this.flags = packetdataserializer.readByte();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.entityId);
        packetdataserializer.writeByte(this.effectId);
        packetdataserializer.writeByte(this.effectAmplifier);
        packetdataserializer.d(this.effectDurationTicks);
        packetdataserializer.writeByte(this.flags);
    }

    public boolean b() {
        return this.effectDurationTicks == 32767;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int c() {
        return this.entityId;
    }

    public byte d() {
        return this.effectId;
    }

    public byte e() {
        return this.effectAmplifier;
    }

    public int f() {
        return this.effectDurationTicks;
    }

    public boolean g() {
        return (this.flags & 2) == 2;
    }

    public boolean h() {
        return (this.flags & 1) == 1;
    }

    public boolean i() {
        return (this.flags & 4) == 4;
    }
}
