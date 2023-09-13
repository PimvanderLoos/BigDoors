package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;

public interface PositionSourceType<T extends PositionSource> {

    PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.a());
    PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.a());

    T read(PacketDataSerializer packetdataserializer);

    void write(PacketDataSerializer packetdataserializer, T t0);

    Codec<T> codec();

    static <S extends PositionSourceType<T>, T extends PositionSource> S register(String s, S s0) {
        return (PositionSourceType) IRegistry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, s, s0);
    }

    static PositionSource fromNetwork(PacketDataSerializer packetdataserializer) {
        MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

        return ((PositionSourceType) BuiltInRegistries.POSITION_SOURCE_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown position source type " + minecraftkey);
        })).read(packetdataserializer);
    }

    static <T extends PositionSource> void toNetwork(T t0, PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeResourceLocation(BuiltInRegistries.POSITION_SOURCE_TYPE.getKey(t0.getType()));
        t0.getType().write(packetdataserializer, t0);
    }
}
