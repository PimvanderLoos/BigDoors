package net.minecraft.server;

import javax.annotation.Nullable;

public class DataPaletteLinear implements DataPalette {

    private final IBlockData[] a;
    private final DataPaletteExpandable b;
    private final int c;
    private int d;

    public DataPaletteLinear(int i, DataPaletteExpandable datapaletteexpandable) {
        this.a = new IBlockData[1 << i];
        this.c = i;
        this.b = datapaletteexpandable;
    }

    public int a(IBlockData iblockdata) {
        int i;

        for (i = 0; i < this.d; ++i) {
            if (this.a[i] == iblockdata) {
                return i;
            }
        }

        i = this.d;
        if (i < this.a.length) {
            this.a[i] = iblockdata;
            ++this.d;
            return i;
        } else {
            return this.b.a(this.c + 1, iblockdata);
        }
    }

    @Nullable
    public IBlockData a(int i) {
        return i >= 0 && i < this.d ? this.a[i] : null;
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.d);

        for (int i = 0; i < this.d; ++i) {
            packetdataserializer.d(Block.REGISTRY_ID.getId(this.a[i]));
        }

    }

    public int a() {
        int i = PacketDataSerializer.a(this.d);

        for (int j = 0; j < this.d; ++j) {
            i += PacketDataSerializer.a(Block.REGISTRY_ID.getId(this.a[j]));
        }

        return i;
    }
}
