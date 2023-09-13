package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;

public class DataPaletteGlobal<T> implements DataPalette<T> {

    private final RegistryBlockID<T> registry;
    private final T defaultValue;

    public DataPaletteGlobal(RegistryBlockID<T> registryblockid, T t0) {
        this.registry = registryblockid;
        this.defaultValue = t0;
    }

    @Override
    public int a(T t0) {
        int i = this.registry.getId(t0);

        return i == -1 ? 0 : i;
    }

    @Override
    public boolean a(Predicate<T> predicate) {
        return true;
    }

    @Override
    public T a(int i) {
        T t0 = this.registry.fromId(i);

        return t0 == null ? this.defaultValue : t0;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {}

    @Override
    public void b(PacketDataSerializer packetdataserializer) {}

    @Override
    public int a() {
        return PacketDataSerializer.a(0);
    }

    @Override
    public int b() {
        return this.registry.a();
    }

    @Override
    public void a(NBTTagList nbttaglist) {}
}
