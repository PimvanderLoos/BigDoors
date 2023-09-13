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

    public static PacketPlayOutScoreboardTeam createAddOrModifyPacket(ScoreboardTeam scoreboardteam, boolean flag) {
        return new PacketPlayOutScoreboardTeam(scoreboardteam.getName(), flag ? 0 : 2, Optional.of(new PacketPlayOutScoreboardTeam.b(scoreboardteam)), (Collection) (flag ? scoreboardteam.getPlayers() : ImmutableList.of()));
    }

    public static PacketPlayOutScoreboardTeam createRemovePacket(ScoreboardTeam scoreboardteam) {
        return new PacketPlayOutScoreboardTeam(scoreboardteam.getName(), 1, Optional.empty(), ImmutableList.of());
    }

    public static PacketPlayOutScoreboardTeam createPlayerPacket(ScoreboardTeam scoreboardteam, String s, PacketPlayOutScoreboardTeam.a packetplayoutscoreboardteam_a) {
        return new PacketPlayOutScoreboardTeam(scoreboardteam.getName(), packetplayoutscoreboardteam_a == PacketPlayOutScoreboardTeam.a.ADD ? 3 : 4, Optional.empty(), ImmutableList.of(s));
    }

    public PacketPlayOutScoreboardTeam(PacketDataSerializer packetdataserializer) {
        this.name = packetdataserializer.readUtf();
        this.method = packetdataserializer.readByte();
        if (shouldHaveParameters(this.method)) {
            this.parameters = Optional.of(new PacketPlayOutScoreboardTeam.b(packetdataserializer));
        } else {
            this.parameters = Optional.empty();
        }

        if (shouldHavePlayerList(this.method)) {
            this.players = packetdataserializer.readList(PacketDataSerializer::readUtf);
        } else {
            this.players = ImmutableList.of();
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.name);
        packetdataserializer.writeByte(this.method);
        if (shouldHaveParameters(this.method)) {
            ((PacketPlayOutScoreboardTeam.b) this.parameters.orElseThrow(() -> {
                return new IllegalStateException("Parameters not present, but method is" + this.method);
            })).write(packetdataserializer);
        }

        if (shouldHavePlayerList(this.method)) {
            packetdataserializer.writeCollection(this.players, PacketDataSerializer::writeUtf);
        }

    }

    private static boolean shouldHavePlayerList(int i) {
        return i == 0 || i == 3 || i == 4;
    }

    private static boolean shouldHaveParameters(int i) {
        return i == 0 || i == 2;
    }

    @Nullable
    public PacketPlayOutScoreboardTeam.a getPlayerAction() {
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
    public PacketPlayOutScoreboardTeam.a getTeamAction() {
        switch (this.method) {
            case 0:
                return PacketPlayOutScoreboardTeam.a.ADD;
            case 1:
                return PacketPlayOutScoreboardTeam.a.REMOVE;
            default:
                return null;
        }
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetPlayerTeamPacket(this);
    }

    public String getName() {
        return this.name;
    }

    public Collection<String> getPlayers() {
        return this.players;
    }

    public Optional<PacketPlayOutScoreboardTeam.b> getParameters() {
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
            this.options = scoreboardteam.packOptions();
            this.nametagVisibility = scoreboardteam.getNameTagVisibility().name;
            this.collisionRule = scoreboardteam.getCollisionRule().name;
            this.color = scoreboardteam.getColor();
            this.playerPrefix = scoreboardteam.getPlayerPrefix();
            this.playerSuffix = scoreboardteam.getPlayerSuffix();
        }

        public b(PacketDataSerializer packetdataserializer) {
            this.displayName = packetdataserializer.readComponent();
            this.options = packetdataserializer.readByte();
            this.nametagVisibility = packetdataserializer.readUtf(40);
            this.collisionRule = packetdataserializer.readUtf(40);
            this.color = (EnumChatFormat) packetdataserializer.readEnum(EnumChatFormat.class);
            this.playerPrefix = packetdataserializer.readComponent();
            this.playerSuffix = packetdataserializer.readComponent();
        }

        public IChatBaseComponent getDisplayName() {
            return this.displayName;
        }

        public int getOptions() {
            return this.options;
        }

        public EnumChatFormat getColor() {
            return this.color;
        }

        public String getNametagVisibility() {
            return this.nametagVisibility;
        }

        public String getCollisionRule() {
            return this.collisionRule;
        }

        public IChatBaseComponent getPlayerPrefix() {
            return this.playerPrefix;
        }

        public IChatBaseComponent getPlayerSuffix() {
            return this.playerSuffix;
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeComponent(this.displayName);
            packetdataserializer.writeByte(this.options);
            packetdataserializer.writeUtf(this.nametagVisibility);
            packetdataserializer.writeUtf(this.collisionRule);
            packetdataserializer.writeEnum(this.color);
            packetdataserializer.writeComponent(this.playerPrefix);
            packetdataserializer.writeComponent(this.playerSuffix);
        }
    }

    public static enum a {

        ADD, REMOVE;

        private a() {}
    }
}
