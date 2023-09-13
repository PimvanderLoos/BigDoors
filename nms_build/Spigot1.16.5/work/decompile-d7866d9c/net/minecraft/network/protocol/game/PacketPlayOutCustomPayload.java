package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayOutCustomPayload implements Packet<PacketListenerPlayOut> {

    public static final MinecraftKey a = new MinecraftKey("brand");
    public static final MinecraftKey b = new MinecraftKey("debug/path");
    public static final MinecraftKey c = new MinecraftKey("debug/neighbors_update");
    public static final MinecraftKey d = new MinecraftKey("debug/caves");
    public static final MinecraftKey e = new MinecraftKey("debug/structures");
    public static final MinecraftKey f = new MinecraftKey("debug/worldgen_attempt");
    public static final MinecraftKey g = new MinecraftKey("debug/poi_ticket_count");
    public static final MinecraftKey h = new MinecraftKey("debug/poi_added");
    public static final MinecraftKey i = new MinecraftKey("debug/poi_removed");
    public static final MinecraftKey j = new MinecraftKey("debug/village_sections");
    public static final MinecraftKey k = new MinecraftKey("debug/goal_selector");
    public static final MinecraftKey l = new MinecraftKey("debug/brain");
    public static final MinecraftKey m = new MinecraftKey("debug/bee");
    public static final MinecraftKey n = new MinecraftKey("debug/hive");
    public static final MinecraftKey o = new MinecraftKey("debug/game_test_add_marker");
    public static final MinecraftKey p = new MinecraftKey("debug/game_test_clear");
    public static final MinecraftKey q = new MinecraftKey("debug/raids");
    private MinecraftKey r;
    private PacketDataSerializer s;

    public PacketPlayOutCustomPayload() {}

    public PacketPlayOutCustomPayload(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        this.r = minecraftkey;
        this.s = packetdataserializer;
        if (packetdataserializer.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.r = packetdataserializer.p();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.s = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.r);
        packetdataserializer.writeBytes(this.s.copy());
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
