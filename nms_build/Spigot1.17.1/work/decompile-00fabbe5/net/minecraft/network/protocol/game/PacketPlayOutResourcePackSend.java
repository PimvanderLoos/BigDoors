package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutResourcePackSend implements Packet<PacketListenerPlayOut> {

    public static final int MAX_HASH_LENGTH = 40;
    private final String url;
    private final String hash;
    private final boolean required;
    @Nullable
    private final IChatBaseComponent prompt;

    public PacketPlayOutResourcePackSend(String s, String s1, boolean flag, @Nullable IChatBaseComponent ichatbasecomponent) {
        if (s1.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + s1.length() + ")");
        } else {
            this.url = s;
            this.hash = s1;
            this.required = flag;
            this.prompt = ichatbasecomponent;
        }
    }

    public PacketPlayOutResourcePackSend(PacketDataSerializer packetdataserializer) {
        this.url = packetdataserializer.p();
        this.hash = packetdataserializer.e(40);
        this.required = packetdataserializer.readBoolean();
        if (packetdataserializer.readBoolean()) {
            this.prompt = packetdataserializer.i();
        } else {
            this.prompt = null;
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.url);
        packetdataserializer.a(this.hash);
        packetdataserializer.writeBoolean(this.required);
        if (this.prompt != null) {
            packetdataserializer.writeBoolean(true);
            packetdataserializer.a(this.prompt);
        } else {
            packetdataserializer.writeBoolean(false);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public String b() {
        return this.url;
    }

    public String c() {
        return this.hash;
    }

    public boolean d() {
        return this.required;
    }

    @Nullable
    public IChatBaseComponent e() {
        return this.prompt;
    }
}
