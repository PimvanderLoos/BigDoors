package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.ScoreboardTeam;

public class PacketPlayOutScoreboardTeam implements Packet<PacketListenerPlayOut> {

    private static final int METHOD_ADD = 0;
    private static final int METHOD_REMOVE = 1;
    private static final int METHOD_CHANGE = 2;
    private static final int METHOD_JOIN = 3;
    private static final int METHOD_LEAVE = 4;
    private static final int MAX_VISIBILITY_LENGTH = 40;
    private static final int MAX_COLLISION_LENGTH = 40;
    private final int method;
    private final String name;
    private final Collection<String> players;
    private final Optional<PacketPlayOutScoreboardTeam.b> parameters;

    private PacketPlayOutScoreboardTeam(String s, int i, Optional<PacketPlayOutScoreboardTeam.b> optional, Collection<String> collection) {
        this.name = s;
        this.method = i;
        this.parameters = optional;
        this.players = ImmutableList.copyOf(collection);
    }

    public static PacketPlayOutScoreboardTeam a(ScoreboardTeam scoreboardteam, boolean flag) {
        return new PacketPlayOutScoreboardTeam(scoreboardteam.getName(), flag ? 0 : 2, Optional.of(new PacketPlayOutScoreboardTeam.b(scoreboardteam)), (Collection) (flag ? scoreboardteam.getPlayerNameSet() : ImmutableList.of()));
    }

    public static PacketPlayOutScoreboardTeam a(ScoreboardTeam scoreboardteam) {
        return new PacketPlayOutScoreboardTeam(scoreboardteam.getName(), 1, Optional.empty(), ImmutableList.of());
    }

    public static PacketPlayOutScoreboardTeam a(ScoreboardTeam scoreboardteam, String s, PacketPlayOutScoreboardTeam.a packetplayoutscoreboardteam_a) {
        return new PacketPlayOutScoreboardTeam(scoreboardteam.getName(), packetplayoutscoreboardteam_a == PacketPlayOutScoreboardTeam.a.ADD ? 3 : 4, Optional.empty(), ImmutableList.of(s));
    }

    public PacketPlayOutScoreboardTeam(PacketDataSerializer packetdataserializer) {
        this.name = packetdataserializer.e(16);
        this.method = packetdataserializer.readByte();
        if (b(this.method)) {
            this.parameters = Optional.of(new PacketPlayOutScoreboardTeam.b(packetdataserializer));
        } else {
            this.parameters = Optional.empty();
        }

        if (a(this.method)) {
            this.players = packetdataserializer.a(PacketDataSerializer::p);
        } else {
            this.players = ImmutableList.of();
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.name);
        packetdataserializer.writeByte(this.method);
        if (b(this.method)) {
            ((PacketPlayOutScoreboardTeam.b) this.parameters.orElseThrow(() -> {
                return new IllegalStateException("Parameters not present, but method is" + this.method);
            })).a(packetdataserializer);
        }

        if (a(this.method)) {
            packetdataserializer.a(this.players, PacketDataSerializer::a);
        }

    }

    private static boolean a(int i) {
        return i == 0 || i == 3 || i == 4;
    }

    private static boolean b(int i) {
        return i == 0 || i == 2;
    }

    @Nullable
    public PacketPlayOutScoreboardTeam.a b() {
        switch (this.method) {
            case 0:
            case 3:
                return PacketPlayOutScoreboardTeam.a.ADD;
            case 1:
            case 2:
            default:
                return null;
            case 4:
                return PacketPlayOutScoreboardTeam.a.REMOVE;
        }
    }

    @Nullable
    public PacketPlayOutScoreboardTeam.a c() {
        switch (this.method) {
            case 0:
                return PacketPlayOutScoreboardTeam.a.ADD;
            case 1:
                return PacketPlayOutScoreboardTeam.a.REMOVE;
            default:
                return null;
        }
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public String d() {
        return this.name;
    }

    public Collection<String> e() {
        return this.players;
    }

    public Optional<PacketPlayOutScoreboardTeam.b> f() {
        return this.parameters;
    }

    public static class b {

        private final IChatBaseComponent displayName;
        private final IChatBaseComponent playerPrefix;
        private final IChatBaseComponent playerSuffix;
        private final String nametagVisibility;
        private final String collisionRule;
        private final EnumChatFormat color;
        private final int options;

        public b(ScoreboardTeam scoreboardteam) {
            this.displayName = scoreboardteam.getDisplayName();
            this.options = scoreboardteam.packOptionData();
            this.nametagVisibility = scoreboardteam.getNameTagVisibility().name;
            this.collisionRule = scoreboardteam.getCollisionRule().name;
            this.color = scoreboardteam.getColor();
            this.playerPrefix = scoreboardteam.getPrefix();
            this.playerSuffix = scoreboardteam.getSuffix();
        }

        public b(PacketDataSerializer packetdataserializer) {
            this.displayName = packetdataserializer.i();
            this.options = packetdataserializer.readByte();
            this.nametagVisibility = packetdataserializer.e(40);
            this.collisionRule = packetdataserializer.e(40);
            this.color = (EnumChatFormat) packetdataserializer.a(EnumChatFormat.class);
            this.playerPrefix = packetdataserializer.i();
            this.playerSuffix = packetdataserializer.i();
        }

        public IChatBaseComponent a() {
            return this.displayName;
        }

        public int b() {
            return this.options;
        }

        public EnumChatFormat c() {
            return this.color;
        }

        public String d() {
            return this.nametagVisibility;
        }

        public String e() {
            return this.collisionRule;
        }

        public IChatBaseComponent f() {
            return this.playerPrefix;
        }

        public IChatBaseComponent g() {
            return this.playerSuffix;
        }

        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.a(this.displayName);
            packetdataserializer.writeByte(this.options);
            packetdataserializer.a(this.nametagVisibility);
            packetdataserializer.a(this.collisionRule);
            packetdataserializer.a((Enum) this.color);
            packetdataserializer.a(this.playerPrefix);
            packetdataserializer.a(this.playerSuffix);
        }
    }

    public static enum a {

        ADD, REMOVE;

        private a() {}
    }
}
