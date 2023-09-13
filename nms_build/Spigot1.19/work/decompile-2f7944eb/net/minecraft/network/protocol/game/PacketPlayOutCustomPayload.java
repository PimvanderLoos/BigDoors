package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayOutCustomPayload implements Packet<PacketListenerPlayOut> {

    private static final int MAX_PAYLOAD_SIZE = 1048576;
    public static final MinecraftKey BRAND = new MinecraftKey("brand");
    public static final MinecraftKey DEBUG_PATHFINDING_PACKET = new MinecraftKey("debug/path");
    public static final MinecraftKey DEBUG_NEIGHBORSUPDATE_PACKET = new MinecraftKey("debug/neighbors_update");
    public static final MinecraftKey DEBUG_STRUCTURES_PACKET = new MinecraftKey("debug/structures");
    public static final MinecraftKey DEBUG_WORLDGENATTEMPT_PACKET = new MinecraftKey("debug/worldgen_attempt");
    public static final MinecraftKey DEBUG_POI_TICKET_COUNT_PACKET = new MinecraftKey("debug/poi_ticket_count");
    public static final MinecraftKey DEBUG_POI_ADDED_PACKET = new MinecraftKey("debug/poi_added");
    public static final MinecraftKey DEBUG_POI_REMOVED_PACKET = new MinecraftKey("debug/poi_removed");
    public static final MinecraftKey DEBUG_VILLAGE_SECTIONS = new MinecraftKey("debug/village_sections");
    public static final MinecraftKey DEBUG_GOAL_SELECTOR = new MinecraftKey("debug/goal_selector");
    public static final MinecraftKey DEBUG_BRAIN = new MinecraftKey("debug/brain");
    public static final MinecraftKey DEBUG_BEE = new MinecraftKey("debug/bee");
    public static final MinecraftKey DEBUG_HIVE = new MinecraftKey("debug/hive");
    public static final MinecraftKey DEBUG_GAME_TEST_ADD_MARKER = new MinecraftKey("debug/game_test_add_marker");
    public static final MinecraftKey DEBUG_GAME_TEST_CLEAR = new MinecraftKey("debug/game_test_clear");
    public static final MinecraftKey DEBUG_RAIDS = new MinecraftKey("debug/raids");
    public static final MinecraftKey DEBUG_GAME_EVENT = new MinecraftKey("debug/game_event");
    public static final MinecraftKey DEBUG_GAME_EVENT_LISTENER = new MinecraftKey("debug/game_event_listeners");
    private final MinecraftKey identifier;
    private final PacketDataSerializer data;

    public PacketPlayOutCustomPayload(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        this.identifier = minecraftkey;
        this.data = packetdataserializer;
        if (packetdataserializer.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    public PacketPlayOutCustomPayload(PacketDataSerializer packetdataserializer) {
        this.identifier = packetdataserializer.readResourceLocation();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.data = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeResourceLocation(this.identifier);
        packetdataserializer.writeBytes(this.data.copy());
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleCustomPayload(this);
    }

    public MinecraftKey getIdentifier() {
        return this.identifier;
    }

    public PacketDataSerializer getData() {
        return new PacketDataSerializer(this.data.copy());
    }
}
