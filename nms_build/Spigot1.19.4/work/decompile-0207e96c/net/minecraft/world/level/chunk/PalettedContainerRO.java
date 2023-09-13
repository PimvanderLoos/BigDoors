package net.minecraft.world.level.chunk;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;

public interface PalettedContainerRO<T> {

    T get(int i, int j, int k);

    void getAll(Consumer<T> consumer);

    void write(PacketDataSerializer packetdataserializer);

    int getSerializedSize();

    boolean maybeHas(Predicate<T> predicate);

    void count(DataPaletteBlock.b<T> datapaletteblock_b);

    DataPaletteBlock<T> recreate();

    PalettedContainerRO.a<T> pack(Registry<T> registry, DataPaletteBlock.d datapaletteblock_d);

    public interface b<T, C extends PalettedContainerRO<T>> {

        DataResult<C> read(Registry<T> registry, DataPaletteBlock.d datapaletteblock_d, PalettedContainerRO.a<T> palettedcontainerro_a);
    }

    public static record a<T> (List<T> paletteEntries, Optional<LongStream> storage) {

    }
}
