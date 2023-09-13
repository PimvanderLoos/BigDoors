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
        this.effectId = (byte) (MobEffectList.getId(mobeffect.getEffect()) & 255);
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

        if (mobeffect.isVisible()) {
            b0 = (byte) (b0 | 2);
        }

        if (mobeffect.showIcon()) {
            b0 = (byte) (b0 | 4);
        }

        this.flags = b0;
    }

    public PacketPlayOutEntityEffect(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readVarInt();
        this.effectId = packetdataserializer.readByte();
        this.effectAmplifier = packetdataserializer.readByte();
        this.effectDurationTicks = packetdataserializer.readVarInt();
        this.flags = packetdataserializer.readByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeByte(this.effectId);
        packetdataserializer.writeByte(this.effectAmplifier);
        packetdataserializer.writeVarInt(this.effectDurationTicks);
        packetdataserializer.writeByte(this.flags);
    }

    public boolean isSuperLongDuration() {
        return this.effectDurationTicks == 32767;
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateMobEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getEffectAmplifier() {
        return this.effectAmplifier;
    }

    public int getEffectDurationTicks() {
        return this.effectDurationTicks;
    }

    public boolean isEffectVisible() {
        return (this.flags & 2) == 2;
    }

    public boolean isEffectAmbient() {
        return (this.flags & 1) == 1;
    }

    public boolean effectShowsIcon() {
        return (this.flags & 4) == 4;
    }
}
