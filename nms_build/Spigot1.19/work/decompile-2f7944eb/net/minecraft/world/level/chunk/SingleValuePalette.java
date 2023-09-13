package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;
import org.apache.commons.lang3.Validate;

public class SingleValuePalette<T> implements DataPalette<T> {

    private final Registry<T> registry;
    @Nullable
    private T value;
    private final DataPaletteExpandable<T> resizeHandler;

    public SingleValuePalette(Registry<T> registry, DataPaletteExpandable<T> datapaletteexpandable, List<T> list) {
        this.registry = registry;
        this.resizeHandler = datapaletteexpandable;
        if (list.size() > 0) {
            Validate.isTrue(list.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long) list.size());
            this.value = list.get(0);
        }

    }

    public static <A> DataPalette<A> create(int i, Registry<A> registry, DataPaletteExpandable<A> datapaletteexpandable, List<A> list) {
        return new SingleValuePalette<>(registry, datapaletteexpandable, list);
    }

    @Override
    public int idFor(T t0) {
        if (this.value != null && this.value != t0) {
            return this.resizeHandler.onResize(1, t0);
        } else {
            this.value = t0;
            return 0;
        }
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        } else {
            return predicate.test(this.value);
        }
    }

    @Override
    public T valueFor(int i) {
        if (this.value != null && i == 0) {
            return this.value;
        } else {
            throw new IllegalStateException("Missing Palette entry for id " + i + ".");
        }
    }

    @Override
    public void read(PacketDataSerializer packetdataserializer) {
        this.value = this.registry.byIdOrThrow(packetdataserializer.readVarInt());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        } else {
            packetdataserializer.writeVarInt(this.registry.getId(this.value));
        }
    }

    @Override
    public int getSerializedSize() {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        } else {
            return PacketDataSerializer.getVarIntSize(this.registry.getId(this.value));
        }
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public DataPalette<T> copy() {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        } else {
            return this;
        }
    }
}
