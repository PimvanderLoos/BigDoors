package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutGameStateChange implements Packet<PacketListenerPlayOut> {

    public static final PacketPlayOutGameStateChange.a NO_RESPAWN_BLOCK_AVAILABLE = new PacketPlayOutGameStateChange.a(0);
    public static final PacketPlayOutGameStateChange.a START_RAINING = new PacketPlayOutGameStateChange.a(1);
    public static final PacketPlayOutGameStateChange.a STOP_RAINING = new PacketPlayOutGameStateChange.a(2);
    public static final PacketPlayOutGameStateChange.a CHANGE_GAME_MODE = new PacketPlayOutGameStateChange.a(3);
    public static final PacketPlayOutGameStateChange.a WIN_GAME = new PacketPlayOutGameStateChange.a(4);
    public static final PacketPlayOutGameStateChange.a DEMO_EVENT = new PacketPlayOutGameStateChange.a(5);
    public static final PacketPlayOutGameStateChange.a ARROW_HIT_PLAYER = new PacketPlayOutGameStateChange.a(6);
    public static final PacketPlayOutGameStateChange.a RAIN_LEVEL_CHANGE = new PacketPlayOutGameStateChange.a(7);
    public static final PacketPlayOutGameStateChange.a THUNDER_LEVEL_CHANGE = new PacketPlayOutGameStateChange.a(8);
    public static final PacketPlayOutGameStateChange.a PUFFER_FISH_STING = new PacketPlayOutGameStateChange.a(9);
    public static final PacketPlayOutGameStateChange.a GUARDIAN_ELDER_EFFECT = new PacketPlayOutGameStateChange.a(10);
    public static final PacketPlayOutGameStateChange.a IMMEDIATE_RESPAWN = new PacketPlayOutGameStateChange.a(11);
    public static final int DEMO_PARAM_INTRO = 0;
    public static final int DEMO_PARAM_HINT_1 = 101;
    public static final int DEMO_PARAM_HINT_2 = 102;
    public static final int DEMO_PARAM_HINT_3 = 103;
    public static final int DEMO_PARAM_HINT_4 = 104;
    private final PacketPlayOutGameStateChange.a event;
    private final float param;

    public PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.a packetplayoutgamestatechange_a, float f) {
        this.event = packetplayoutgamestatechange_a;
        this.param = f;
    }

    public PacketPlayOutGameStateChange(PacketDataSerializer packetdataserializer) {
        this.event = (PacketPlayOutGameStateChange.a) PacketPlayOutGameStateChange.a.TYPES.get(packetdataserializer.readUnsignedByte());
        this.param = packetdataserializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.event.id);
        packetdataserializer.writeFloat(this.param);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleGameEvent(this);
    }

    public PacketPlayOutGameStateChange.a getEvent() {
        return this.event;
    }

    public float getParam() {
        return this.param;
    }

    public static class a {

        static final Int2ObjectMap<PacketPlayOutGameStateChange.a> TYPES = new Int2ObjectOpenHashMap();
        final int id;

        public a(int i) {
            this.id = i;
            PacketPlayOutGameStateChange.a.TYPES.put(i, this);
        }
    }
}
