package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ScoreboardServer;

public class PacketPlayOutScoreboardScore implements Packet<PacketListenerPlayOut> {

    private final String owner;
    @Nullable
    private final String objectiveName;
    private final int score;
    private final ScoreboardServer.Action method;

    public PacketPlayOutScoreboardScore(ScoreboardServer.Action scoreboardserver_action, @Nullable String s, String s1, int i) {
        if (scoreboardserver_action != ScoreboardServer.Action.REMOVE && s == null) {
            throw new IllegalArgumentException("Need an objective name");
        } else {
            this.owner = s1;
            this.objectiveName = s;
            this.score = i;
            this.method = scoreboardserver_action;
        }
    }

    public PacketPlayOutScoreboardScore(PacketDataSerializer packetdataserializer) {
        this.owner = packetdataserializer.readUtf();
        this.method = (ScoreboardServer.Action) packetdataserializer.readEnum(ScoreboardServer.Action.class);
        String s = packetdataserializer.readUtf();

        this.objectiveName = Objects.equals(s, "") ? null : s;
        if (this.method != ScoreboardServer.Action.REMOVE) {
            this.score = packetdataserializer.readVarInt();
        } else {
            this.score = 0;
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.owner);
        packetdataserializer.writeEnum(this.method);
        packetdataserializer.writeUtf(this.objectiveName == null ? "" : this.objectiveName);
        if (this.method != ScoreboardServer.Action.REMOVE) {
            packetdataserializer.writeVarInt(this.score);
        }

    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetScore(this);
    }

    public String getOwner() {
        return this.owner;
    }

    @Nullable
    public String getObjectiveName() {
        return this.objectiveName;
    }

    public int getScore() {
        return this.score;
    }

    public ScoreboardServer.Action getMethod() {
        return this.method;
    }
}
