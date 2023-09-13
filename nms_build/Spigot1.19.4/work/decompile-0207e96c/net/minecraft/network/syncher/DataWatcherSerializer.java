package net.minecraft.network.syncher;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;

public interface DataWatcherSerializer<T> {

    void write(PacketDataSerializer packetdataserializer, T t0);

    T read(PacketDataSerializer packetdataserializer);

    default DataWatcherObject<T> createAccessor(int i) {
        return new DataWatcherObject<>(i, this);
    }

    T copy(T t0);

    static <T> DataWatcherSerializer<T> simple(final PacketDataSerializer.b<T> packetdataserializer_b, final PacketDataSerializer.a<T> packetdataserializer_a) {
        return new DataWatcherSerializer.a<T>() {
            @Override
            public void write(PacketDataSerializer packetdataserializer, T t0) {
                packetdataserializer_b.accept(packetdataserializer, t0);
            }

            @Override
            public T read(PacketDataSerializer packetdataserializer) {
                return packetdataserializer_a.apply(packetdataserializer);
            }
        };
    }

    static <T> DataWatcherSerializer<Optional<T>> optional(PacketDataSerializer.b<T> packetdataserializer_b, PacketDataSerializer.a<T> packetdataserializer_a) {
        return simple(packetdataserializer_b.asOptional(), packetdataserializer_a.asOptional());
    }

    static <T extends Enum<T>> DataWatcherSerializer<T> simpleEnum(Class<T> oclass) {
        return simple(PacketDataSerializer::writeEnum, (packetdataserializer) -> {
            return packetdataserializer.readEnum(oclass);
        });
    }

    static <T> DataWatcherSerializer<T> simpleId(Registry<T> registry) {
        return simple((packetdataserializer, object) -> {
            packetdataserializer.writeId(registry, object);
        }, (packetdataserializer) -> {
            return packetdataserializer.readById(registry);
        });
    }

    public interface a<T> extends DataWatcherSerializer<T> {

        @Override
        default T copy(T t0) {
            return t0;
        }
    }
}
