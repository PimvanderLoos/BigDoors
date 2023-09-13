package net.minecraft.server;

import javax.annotation.Nullable;

public interface DataPalette {

    int a(IBlockData iblockdata);

    @Nullable
    IBlockData a(int i);

    void b(PacketDataSerializer packetdataserializer);

    int a();
}
