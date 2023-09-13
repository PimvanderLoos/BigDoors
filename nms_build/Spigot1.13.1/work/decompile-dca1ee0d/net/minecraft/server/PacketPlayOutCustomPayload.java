package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutCustomPayload implements Packet<PacketListenerPlayOut> {

    public static final MinecraftKey a = new MinecraftKey("minecraft:trader_list");
    public static final MinecraftKey b = new MinecraftKey("minecraft:brand");
    public static final MinecraftKey c = new MinecraftKey("minecraft:book_open");
    public static final MinecraftKey d = new MinecraftKey("minecraft:debug/path");
    public static final MinecraftKey e = new MinecraftKey("minecraft:debug/neighbors_update");
    public static final MinecraftKey f = new MinecraftKey("minecraft:debug/caves");
    public static final MinecraftKey g = new MinecraftKey("minecraft:debug/structures");
    public static final MinecraftKey h = new MinecraftKey("minecraft:debug/worldgen_attempt");
    private MinecraftKey i;
    private PacketDataSerializer j;

    public PacketPlayOutCustomPayload() {}

    public PacketPlayOutCustomPayload(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        this.i = minecraftkey;
        this.j = packetdataserializer;
        if (packetdataserializer.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.i = packetdataserializer.l();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.j = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.i);
        packetdataserializer.writeBytes(this.j.copy());
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
