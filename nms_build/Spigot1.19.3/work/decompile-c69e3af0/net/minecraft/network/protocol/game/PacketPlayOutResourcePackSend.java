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
        this.url = packetdataserializer.readUtf();
        this.hash = packetdataserializer.readUtf(40);
        this.required = packetdataserializer.readBoolean();
        this.prompt = (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.url);
        packetdataserializer.writeUtf(this.hash);
        packetdataserializer.writeBoolean(this.required);
        packetdataserializer.writeNullable(this.prompt, PacketDataSerializer::writeComponent);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleResourcePack(this);
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    public boolean isRequired() {
        return this.required;
    }

    @Nullable
    public IChatBaseComponent getPrompt() {
        return this.prompt;
    }
}
