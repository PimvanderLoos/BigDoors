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
        this.owner = packetdataserializer.e(40);
        this.method = (ScoreboardServer.Action) packetdataserializer.a(ScoreboardServer.Action.class);
        String s = packetdataserializer.e(16);

        this.objectiveName = Objects.equals(s, "") ? null : s;
        if (this.method != ScoreboardServer.Action.REMOVE) {
            this.score = packetdataserializer.j();
        } else {
            this.score = 0;
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.owner);
        packetdataserializer.a((Enum) this.method);
        packetdataserializer.a(this.objectiveName == null ? "" : this.objectiveName);
        if (this.method != ScoreboardServer.Action.REMOVE) {
            packetdataserializer.d(this.score);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public String b() {
        return this.owner;
    }

    @Nullable
    public String c() {
        return this.objectiveName;
    }

    public int d() {
        return this.score;
    }

    public ScoreboardServer.Action e() {
        return this.method;
    }
}
