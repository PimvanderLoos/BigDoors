package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class PacketPlayOutScoreboardTeam implements Packet<PacketListenerPlayOut> {

    private String a = "";
    private String b = "";
    private String c = "";
    private String d = "";
    private String e;
    private String f;
    private int g;
    private final Collection<String> h;
    private int i;
    private int j;

    public PacketPlayOutScoreboardTeam() {
        this.e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
        this.f = ScoreboardTeamBase.EnumTeamPush.ALWAYS.e;
        this.g = -1;
        this.h = Lists.newArrayList();
    }

    public PacketPlayOutScoreboardTeam(ScoreboardTeam scoreboardteam, int i) {
        this.e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
        this.f = ScoreboardTeamBase.EnumTeamPush.ALWAYS.e;
        this.g = -1;
        this.h = Lists.newArrayList();
        this.a = scoreboardteam.getName();
        this.i = i;
        if (i == 0 || i == 2) {
            this.b = scoreboardteam.getDisplayName();
            this.c = scoreboardteam.getPrefix();
            this.d = scoreboardteam.getSuffix();
            this.j = scoreboardteam.packOptionData();
            this.e = scoreboardteam.getNameTagVisibility().e;
            this.f = scoreboardteam.getCollisionRule().e;
            this.g = scoreboardteam.getColor().b();
        }

        if (i == 0) {
            this.h.addAll(scoreboardteam.getPlayerNameSet());
        }

    }

    public PacketPlayOutScoreboardTeam(ScoreboardTeam scoreboardteam, Collection<String> collection, int i) {
        this.e = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e;
        this.f = ScoreboardTeamBase.EnumTeamPush.ALWAYS.e;
        this.g = -1;
        this.h = Lists.newArrayList();
        if (i != 3 && i != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        } else if (collection != null && !collection.isEmpty()) {
            this.i = i;
            this.a = scoreboardteam.getName();
            this.h.addAll(collection);
        } else {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e(16);
        this.i = packetdataserializer.readByte();
        if (this.i == 0 || this.i == 2) {
            this.b = packetdataserializer.e(32);
            this.c = packetdataserializer.e(16);
            this.d = packetdataserializer.e(16);
            this.j = packetdataserializer.readByte();
            this.e = packetdataserializer.e(32);
            this.f = packetdataserializer.e(32);
            this.g = packetdataserializer.readByte();
        }

        if (this.i == 0 || this.i == 3 || this.i == 4) {
            int i = packetdataserializer.g();

            for (int j = 0; j < i; ++j) {
                this.h.add(packetdataserializer.e(40));
            }
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeByte(this.i);
        if (this.i == 0 || this.i == 2) {
            packetdataserializer.a(this.b);
            packetdataserializer.a(this.c);
            packetdataserializer.a(this.d);
            packetdataserializer.writeByte(this.j);
            packetdataserializer.a(this.e);
            packetdataserializer.a(this.f);
            packetdataserializer.writeByte(this.g);
        }

        if (this.i == 0 || this.i == 3 || this.i == 4) {
            packetdataserializer.d(this.h.size());
            Iterator iterator = this.h.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                packetdataserializer.a(s);
            }
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
