package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;

public class PacketPlayOutEntityEffect implements Packet<PacketListenerPlayOut> {

    private static final int FLAG_AMBIENT = 1;
    private static final int FLAG_VISIBLE = 2;
    private static final int FLAG_SHOW_ICON = 4;
    private final int entityId;
    private final MobEffectList effect;
    private final byte effectAmplifier;
    private final int effectDurationTicks;
    private final byte flags;
    @Nullable
    private final MobEffect.a factorData;

    public PacketPlayOutEntityEffect(int i, MobEffect mobeffect) {
        this.entityId = i;
        this.effect = mobeffect.getEffect();
        this.effectAmplifier = (byte) (mobeffect.getAmplifier() & 255);
        this.effectDurationTicks = mobeffect.getDuration();
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
        this.factorData = (MobEffect.a) mobeffect.getFactorData().orElse((Object) null);
    }

    public PacketPlayOutEntityEffect(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readVarInt();
        this.effect = (MobEffectList) packetdataserializer.readById(BuiltInRegistries.MOB_EFFECT);
        this.effectAmplifier = packetdataserializer.readByte();
        this.effectDurationTicks = packetdataserializer.readVarInt();
        this.flags = packetdataserializer.readByte();
        this.factorData = (MobEffect.a) packetdataserializer.readNullable((packetdataserializer1) -> {
            return (MobEffect.a) packetdataserializer1.readWithCodec(DynamicOpsNBT.INSTANCE, MobEffect.a.CODEC);
        });
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeId(BuiltInRegistries.MOB_EFFECT, this.effect);
        packetdataserializer.writeByte(this.effectAmplifier);
        packetdataserializer.writeVarInt(this.effectDurationTicks);
        packetdataserializer.writeByte(this.flags);
        packetdataserializer.writeNullable(this.factorData, (packetdataserializer1, mobeffect_a) -> {
            packetdataserializer1.writeWithCodec(DynamicOpsNBT.INSTANCE, MobEffect.a.CODEC, mobeffect_a);
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateMobEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public MobEffectList getEffect() {
        return this.effect;
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

    @Nullable
    public MobEffect.a getFactorData() {
        return this.factorData;
    }
}
