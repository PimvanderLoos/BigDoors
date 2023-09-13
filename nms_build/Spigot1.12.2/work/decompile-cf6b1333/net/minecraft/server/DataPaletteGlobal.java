package net.minecraft.server;

public class DataPaletteGlobal implements DataPalette {

    public DataPaletteGlobal() {}

    public int a(IBlockData iblockdata) {
        int i = Block.REGISTRY_ID.getId(iblockdata);

        return i == -1 ? 0 : i;
    }

    public IBlockData a(int i) {
        IBlockData iblockdata = (IBlockData) Block.REGISTRY_ID.fromId(i);

        return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(0);
    }

    public int a() {
        return PacketDataSerializer.a(0);
    }
}
