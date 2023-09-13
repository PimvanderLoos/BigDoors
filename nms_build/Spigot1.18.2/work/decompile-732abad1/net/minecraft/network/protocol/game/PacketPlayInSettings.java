package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;

public record PacketPlayInSettings(String b, int c, EnumChatVisibility d, boolean e, int f, EnumMainHand g, boolean h, boolean i) implements Packet<PacketListenerPlayIn> {

    public final String language;
    public final int viewDistance;
    private final EnumChatVisibility chatVisibility;
    private final boolean chatColors;
    private final int modelCustomisation;
    private final EnumMainHand mainHand;
    private final boolean textFilteringEnabled;
    private final boolean allowsListing;
    public static final int MAX_LANGUAGE_LENGTH = 16;

    public PacketPlayInSettings(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(16), packetdataserializer.readByte(), (EnumChatVisibility) packetdataserializer.readEnum(EnumChatVisibility.class), packetdataserializer.readBoolean(), packetdataserializer.readUnsignedByte(), (EnumMainHand) packetdataserializer.readEnum(EnumMainHand.class), packetdataserializer.readBoolean(), packetdataserializer.readBoolean());
    }

    public PacketPlayInSettings(String s, int i, EnumChatVisibility enumchatvisibility, boolean flag, int j, EnumMainHand enummainhand, boolean flag1, boolean flag2) {
        this.language = s;
        this.viewDistance = i;
        this.chatVisibility = enumchatvisibility;
        this.chatColors = flag;
        this.modelCustomisation = j;
        this.mainHand = enummainhand;
        this.textFilteringEnabled = flag1;
        this.allowsListing = flag2;
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.language);
        packetdataserializer.writeByte(this.viewDistance);
        packetdataserializer.writeEnum(this.chatVisibility);
        packetdataserializer.writeBoolean(this.chatColors);
        packetdataserializer.writeByte(this.modelCustomisation);
        packetdataserializer.writeEnum(this.mainHand);
        packetdataserializer.writeBoolean(this.textFilteringEnabled);
        packetdataserializer.writeBoolean(this.allowsListing);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleClientInformation(this);
    }

    public String language() {
        return this.language;
    }

    public int viewDistance() {
        return this.viewDistance;
    }

    public EnumChatVisibility chatVisibility() {
        return this.chatVisibility;
    }

    public boolean chatColors() {
        return this.chatColors;
    }

    public int modelCustomisation() {
        return this.modelCustomisation;
    }

    public EnumMainHand mainHand() {
        return this.mainHand;
    }

    public boolean textFilteringEnabled() {
        return this.textFilteringEnabled;
    }

    public boolean allowsListing() {
        return this.allowsListing;
    }
}
