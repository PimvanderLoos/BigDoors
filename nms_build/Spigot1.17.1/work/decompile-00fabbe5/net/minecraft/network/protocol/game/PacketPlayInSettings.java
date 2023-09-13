package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;

public class PacketPlayInSettings implements Packet<PacketListenerPlayIn> {

    public static final int MAX_LANGUAGE_LENGTH = 16;
    public final String language;
    public final int viewDistance;
    private final EnumChatVisibility chatVisibility;
    private final boolean chatColors;
    private final int modelCustomisation;
    private final EnumMainHand mainHand;
    private final boolean textFilteringEnabled;

    public PacketPlayInSettings(String s, int i, EnumChatVisibility enumchatvisibility, boolean flag, int j, EnumMainHand enummainhand, boolean flag1) {
        this.language = s;
        this.viewDistance = i;
        this.chatVisibility = enumchatvisibility;
        this.chatColors = flag;
        this.modelCustomisation = j;
        this.mainHand = enummainhand;
        this.textFilteringEnabled = flag1;
    }

    public PacketPlayInSettings(PacketDataSerializer packetdataserializer) {
        this.language = packetdataserializer.e(16);
        this.viewDistance = packetdataserializer.readByte();
        this.chatVisibility = (EnumChatVisibility) packetdataserializer.a(EnumChatVisibility.class);
        this.chatColors = packetdataserializer.readBoolean();
        this.modelCustomisation = packetdataserializer.readUnsignedByte();
        this.mainHand = (EnumMainHand) packetdataserializer.a(EnumMainHand.class);
        this.textFilteringEnabled = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.language);
        packetdataserializer.writeByte(this.viewDistance);
        packetdataserializer.a((Enum) this.chatVisibility);
        packetdataserializer.writeBoolean(this.chatColors);
        packetdataserializer.writeByte(this.modelCustomisation);
        packetdataserializer.a((Enum) this.mainHand);
        packetdataserializer.writeBoolean(this.textFilteringEnabled);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public String b() {
        return this.language;
    }

    public int c() {
        return this.viewDistance;
    }

    public EnumChatVisibility d() {
        return this.chatVisibility;
    }

    public boolean e() {
        return this.chatColors;
    }

    public int f() {
        return this.modelCustomisation;
    }

    public EnumMainHand getMainHand() {
        return this.mainHand;
    }

    public boolean h() {
        return this.textFilteringEnabled;
    }
}
