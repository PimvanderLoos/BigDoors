package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;

public class DataPaletteGlobal<T> implements DataPalette<T> {

    private final RegistryBlockID<T> a;
    private final T b;

    public DataPaletteGlobal(RegistryBlockID<T> registryblockid, T t0) {
        this.a = registryblockid;
        this.b = t0;
    }

    @Override
    public int a(T t0) {
        int i = this.a.getId(t0);

        return i == -1 ? 0 : i;
    }

    @Override
    public boolean a(Predicate<T> predicate) {
        return true;
    }

    @Override
    public T a(int i) {
        T t0 = this.a.fromId(i);

        return t0 == null ? this.b : t0;
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) {}

    @Override
    public int a() {
        return PacketDataSerializer.a(0);
    }

    @Override
    public void a(NBTTagList nbttaglist) {}
}
