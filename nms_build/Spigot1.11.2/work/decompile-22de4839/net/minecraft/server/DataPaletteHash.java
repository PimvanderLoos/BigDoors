package net.minecraft.server;

import javax.annotation.Nullable;

public class DataPaletteHash implements DataPalette {

    private final RegistryID<IBlockData> a;
    private final DataPaletteExpandable b;
    private final int c;

    public DataPaletteHash(int i, DataPaletteExpandable datapaletteexpandable) {
        this.c = i;
        this.b = datapaletteexpandable;
        this.a = new RegistryID(1 << i);
    }

    public int a(IBlockData iblockdata) {
        int i = this.a.getId(iblockdata);

        if (i == -1) {
            i = this.a.c(iblockdata);
            if (i >= 1 << this.c) {
                i = this.b.a(this.c + 1, iblockdata);
            }
        }

        return i;
    }

    @Nullable
    public IBlockData a(int i) {
        return (IBlockData) this.a.fromId(i);
    }

    public void b(PacketDataSerializer packetdataserializer) {
        int i = this.a.b();

        packetdataserializer.d(i);

        for (int j = 0; j < i; ++j) {
            packetdataserializer.d(Block.REGISTRY_ID.getId(this.a.fromId(j)));
        }

    }

    public int a() {
        int i = PacketDataSerializer.a(this.a.b());

        for (int j = 0; j < this.a.b(); ++j) {
            i += PacketDataSerializer.a(Block.REGISTRY_ID.getId(this.a.fromId(j)));
        }

        return i;
    }
}
