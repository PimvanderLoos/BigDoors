package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;

public interface PositionSourceType<T extends PositionSource> {

    PositionSourceType<BlockPositionSource> BLOCK = a("block", (PositionSourceType) (new BlockPositionSource.a()));
    PositionSourceType<EntityPositionSource> ENTITY = a("entity", (PositionSourceType) (new EntityPositionSource.a()));

    T b(PacketDataSerializer packetdataserializer);

    void a(PacketDataSerializer packetdataserializer, T t0);

    Codec<T> a();

    static <S extends PositionSourceType<T>, T extends PositionSource> S a(String s, S s0) {
        return (PositionSourceType) IRegistry.a(IRegistry.POSITION_SOURCE_TYPE, s, (Object) s0);
    }

    static PositionSource c(PacketDataSerializer packetdataserializer) {
        MinecraftKey minecraftkey = packetdataserializer.q();

        return ((PositionSourceType) IRegistry.POSITION_SOURCE_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown position source type " + minecraftkey);
        })).b(packetdataserializer);
    }

    static <T extends PositionSource> void a(T t0, PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(IRegistry.POSITION_SOURCE_TYPE.getKey(t0.a()));
        t0.a().a(packetdataserializer, t0);
    }
}
