package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.EnumGamemode;

public class ClientboundPlayerInfoUpdatePacket implements Packet<PacketListenerPlayOut> {

    private final EnumSet<ClientboundPlayerInfoUpdatePacket.a> actions;
    private final List<ClientboundPlayerInfoUpdatePacket.b> entries;

    public ClientboundPlayerInfoUpdatePacket(EnumSet<ClientboundPlayerInfoUpdatePacket.a> enumset, Collection<EntityPlayer> collection) {
        this.actions = enumset;
        this.entries = collection.stream().map(ClientboundPlayerInfoUpdatePacket.b::new).toList();
    }

    public ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a clientboundplayerinfoupdatepacket_a, EntityPlayer entityplayer) {
        this.actions = EnumSet.of(clientboundplayerinfoupdatepacket_a);
        this.entries = List.of(new ClientboundPlayerInfoUpdatePacket.b(entityplayer));
    }

    public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<EntityPlayer> collection) {
        EnumSet<ClientboundPlayerInfoUpdatePacket.a> enumset = EnumSet.of(ClientboundPlayerInfoUpdatePacket.a.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.a.INITIALIZE_CHAT, ClientboundPlayerInfoUpdatePacket.a.UPDATE_GAME_MODE, ClientboundPlayerInfoUpdatePacket.a.UPDATE_LISTED, ClientboundPlayerInfoUpdatePacket.a.UPDATE_LATENCY, ClientboundPlayerInfoUpdatePacket.a.UPDATE_DISPLAY_NAME);

        return new ClientboundPlayerInfoUpdatePacket(enumset, collection);
    }

    public ClientboundPlayerInfoUpdatePacket(PacketDataSerializer packetdataserializer) {
        this.actions = packetdataserializer.readEnumSet(ClientboundPlayerInfoUpdatePacket.a.class);
        this.entries = packetdataserializer.readList((packetdataserializer1) -> {
            ClientboundPlayerInfoUpdatePacket.c clientboundplayerinfoupdatepacket_c = new ClientboundPlayerInfoUpdatePacket.c(packetdataserializer1.readUUID());
            Iterator iterator = this.actions.iterator();

            while (iterator.hasNext()) {
                ClientboundPlayerInfoUpdatePacket.a clientboundplayerinfoupdatepacket_a = (ClientboundPlayerInfoUpdatePacket.a) iterator.next();

                clientboundplayerinfoupdatepacket_a.reader.read(clientboundplayerinfoupdatepacket_c, packetdataserializer1);
            }

            return clientboundplayerinfoupdatepacket_c.build();
        });
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnumSet(this.actions, ClientboundPlayerInfoUpdatePacket.a.class);
        packetdataserializer.writeCollection(this.entries, (packetdataserializer1, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer1.writeUUID(clientboundplayerinfoupdatepacket_b.profileId());
            Iterator iterator = this.actions.iterator();

            while (iterator.hasNext()) {
                ClientboundPlayerInfoUpdatePacket.a clientboundplayerinfoupdatepacket_a = (ClientboundPlayerInfoUpdatePacket.a) iterator.next();

                clientboundplayerinfoupdatepacket_a.writer.write(packetdataserializer1, clientboundplayerinfoupdatepacket_b);
            }

        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerInfoUpdate(this);
    }

    public EnumSet<ClientboundPlayerInfoUpdatePacket.a> actions() {
        return this.actions;
    }

    public List<ClientboundPlayerInfoUpdatePacket.b> entries() {
        return this.entries;
    }

    public List<ClientboundPlayerInfoUpdatePacket.b> newEntries() {
        return this.actions.contains(ClientboundPlayerInfoUpdatePacket.a.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public static record b(UUID profileId, GameProfile profile, boolean listed, int latency, EnumGamemode gameMode, @Nullable IChatBaseComponent displayName, @Nullable RemoteChatSession.a chatSession) {

        b(EntityPlayer entityplayer) {
            this(entityplayer.getUUID(), entityplayer.getGameProfile(), true, entityplayer.latency, entityplayer.gameMode.getGameModeForPlayer(), entityplayer.getTabListDisplayName(), (RemoteChatSession.a) Optionull.map(entityplayer.getChatSession(), RemoteChatSession::asData));
        }
    }

    public static enum a {

        ADD_PLAYER((clientboundplayerinfoupdatepacket_c, packetdataserializer) -> {
            GameProfile gameprofile = new GameProfile(clientboundplayerinfoupdatepacket_c.profileId, packetdataserializer.readUtf(16));

            gameprofile.getProperties().putAll(packetdataserializer.readGameProfileProperties());
            clientboundplayerinfoupdatepacket_c.profile = gameprofile;
        }, (packetdataserializer, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer.writeUtf(clientboundplayerinfoupdatepacket_b.profile().getName(), 16);
            packetdataserializer.writeGameProfileProperties(clientboundplayerinfoupdatepacket_b.profile().getProperties());
        }), INITIALIZE_CHAT((clientboundplayerinfoupdatepacket_c, packetdataserializer) -> {
            clientboundplayerinfoupdatepacket_c.chatSession = (RemoteChatSession.a) packetdataserializer.readNullable(RemoteChatSession.a::read);
        }, (packetdataserializer, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer.writeNullable(clientboundplayerinfoupdatepacket_b.chatSession, RemoteChatSession.a::write);
        }), UPDATE_GAME_MODE((clientboundplayerinfoupdatepacket_c, packetdataserializer) -> {
            clientboundplayerinfoupdatepacket_c.gameMode = EnumGamemode.byId(packetdataserializer.readVarInt());
        }, (packetdataserializer, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer.writeVarInt(clientboundplayerinfoupdatepacket_b.gameMode().getId());
        }), UPDATE_LISTED((clientboundplayerinfoupdatepacket_c, packetdataserializer) -> {
            clientboundplayerinfoupdatepacket_c.listed = packetdataserializer.readBoolean();
        }, (packetdataserializer, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer.writeBoolean(clientboundplayerinfoupdatepacket_b.listed());
        }), UPDATE_LATENCY((clientboundplayerinfoupdatepacket_c, packetdataserializer) -> {
            clientboundplayerinfoupdatepacket_c.latency = packetdataserializer.readVarInt();
        }, (packetdataserializer, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer.writeVarInt(clientboundplayerinfoupdatepacket_b.latency());
        }), UPDATE_DISPLAY_NAME((clientboundplayerinfoupdatepacket_c, packetdataserializer) -> {
            clientboundplayerinfoupdatepacket_c.displayName = (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent);
        }, (packetdataserializer, clientboundplayerinfoupdatepacket_b) -> {
            packetdataserializer.writeNullable(clientboundplayerinfoupdatepacket_b.displayName(), PacketDataSerializer::writeComponent);
        });

        final ClientboundPlayerInfoUpdatePacket.a.a reader;
        final ClientboundPlayerInfoUpdatePacket.a.b writer;

        private a(ClientboundPlayerInfoUpdatePacket.a.a clientboundplayerinfoupdatepacket_a_a, ClientboundPlayerInfoUpdatePacket.a.b clientboundplayerinfoupdatepacket_a_b) {
            this.reader = clientboundplayerinfoupdatepacket_a_a;
            this.writer = clientboundplayerinfoupdatepacket_a_b;
        }

        public interface a {

            void read(ClientboundPlayerInfoUpdatePacket.c clientboundplayerinfoupdatepacket_c, PacketDataSerializer packetdataserializer);
        }

        public interface b {

            void write(PacketDataSerializer packetdataserializer, ClientboundPlayerInfoUpdatePacket.b clientboundplayerinfoupdatepacket_b);
        }
    }

    private static class c {

        final UUID profileId;
        GameProfile profile;
        boolean listed;
        int latency;
        EnumGamemode gameMode;
        @Nullable
        IChatBaseComponent displayName;
        @Nullable
        RemoteChatSession.a chatSession;

        c(UUID uuid) {
            this.gameMode = EnumGamemode.DEFAULT_MODE;
            this.profileId = uuid;
            this.profile = new GameProfile(uuid, (String) null);
        }

        ClientboundPlayerInfoUpdatePacket.b build() {
            return new ClientboundPlayerInfoUpdatePacket.b(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.chatSession);
        }
    }
}
