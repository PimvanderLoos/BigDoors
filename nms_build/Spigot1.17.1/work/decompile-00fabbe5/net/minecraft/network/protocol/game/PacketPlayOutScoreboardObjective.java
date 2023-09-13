package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class PacketPlayOutScoreboardObjective implements Packet<PacketListenerPlayOut> {

    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    private final String objectiveName;
    private final IChatBaseComponent displayName;
    private final IScoreboardCriteria.EnumScoreboardHealthDisplay renderType;
    private final int method;

    public PacketPlayOutScoreboardObjective(ScoreboardObjective scoreboardobjective, int i) {
        this.objectiveName = scoreboardobjective.getName();
        this.displayName = scoreboardobjective.getDisplayName();
        this.renderType = scoreboardobjective.getRenderType();
        this.method = i;
    }

    public PacketPlayOutScoreboardObjective(PacketDataSerializer packetdataserializer) {
        this.objectiveName = packetdataserializer.e(16);
        this.method = packetdataserializer.readByte();
        if (this.method != 0 && this.method != 2) {
            this.displayName = ChatComponentText.EMPTY;
            this.renderType = IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
        } else {
            this.displayName = packetdataserializer.i();
            this.renderType = (IScoreboardCriteria.EnumScoreboardHealthDisplay) packetdataserializer.a(IScoreboardCriteria.EnumScoreboardHealthDisplay.class);
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.objectiveName);
        packetdataserializer.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            packetdataserializer.a(this.displayName);
            packetdataserializer.a((Enum) this.renderType);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public String b() {
        return this.objectiveName;
    }

    public IChatBaseComponent c() {
        return this.displayName;
    }

    public int d() {
        return this.method;
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay e() {
        return this.renderType;
    }
}
