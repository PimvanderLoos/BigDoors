package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.EnumGamemode;

public class PacketPlayOutPlayerInfo implements Packet<PacketListenerPlayOut> {

    private final PacketPlayOutPlayerInfo.EnumPlayerInfoAction action;
    private final List<PacketPlayOutPlayerInfo.PlayerInfoData> entries;

    public PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction, EntityPlayer... aentityplayer) {
        this.action = packetplayoutplayerinfo_enumplayerinfoaction;
        this.entries = Lists.newArrayListWithCapacity(aentityplayer.length);
        EntityPlayer[] aentityplayer1 = aentityplayer;
        int i = aentityplayer.length;

        for (int j = 0; j < i; ++j) {
            EntityPlayer entityplayer = aentityplayer1[j];

            this.entries.add(createPlayerUpdate(entityplayer));
        }

    }

    public PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction, Collection<EntityPlayer> collection) {
        this.action = packetplayoutplayerinfo_enumplayerinfoaction;
        this.entries = Lists.newArrayListWithCapacity(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            this.entries.add(createPlayerUpdate(entityplayer));
        }

    }

    public PacketPlayOutPlayerInfo(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) packetdataserializer.readEnum(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction = this.action;

        Objects.requireNonNull(this.action);
        this.entries = packetdataserializer.readList(packetplayoutplayerinfo_enumplayerinfoaction::read);
    }

    private static PacketPlayOutPlayerInfo.PlayerInfoData createPlayerUpdate(EntityPlayer entityplayer) {
        ProfilePublicKey profilepublickey = entityplayer.getProfilePublicKey();
        ProfilePublicKey.a profilepublickey_a = profilepublickey != null ? profilepublickey.data() : null;

        return new PacketPlayOutPlayerInfo.PlayerInfoData(entityplayer.getGameProfile(), entityplayer.latency, entityplayer.gameMode.getGameModeForPlayer(), entityplayer.getTabListDisplayName(), profilepublickey_a);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
        List list = this.entries;
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction = this.action;

        Objects.requireNonNull(this.action);
        packetdataserializer.writeCollection(list, packetplayoutplayerinfo_enumplayerinfoaction::write);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerInfo(this);
    }

    public List<PacketPlayOutPlayerInfo.PlayerInfoData> getEntries() {
        return this.entries;
    }

    public PacketPlayOutPlayerInfo.EnumPlayerInfoAction getAction() {
        return this.action;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
    }

    public static enum EnumPlayerInfoAction {

        ADD_PLAYER {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = packetdataserializer.readGameProfile();
                EnumGamemode enumgamemode = EnumGamemode.byId(packetdataserializer.readVarInt());
                int i = packetdataserializer.readVarInt();
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent);
                ProfilePublicKey.a profilepublickey_a = (ProfilePublicKey.a) packetdataserializer.readNullable(ProfilePublicKey.a::new);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, i, enumgamemode, ichatbasecomponent, profilepublickey_a);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeGameProfile(packetplayoutplayerinfo_playerinfodata.getProfile());
                packetdataserializer.writeVarInt(packetplayoutplayerinfo_playerinfodata.getGameMode().getId());
                packetdataserializer.writeVarInt(packetplayoutplayerinfo_playerinfodata.getLatency());
                packetdataserializer.writeNullable(packetplayoutplayerinfo_playerinfodata.getDisplayName(), PacketDataSerializer::writeComponent);
                packetdataserializer.writeNullable(packetplayoutplayerinfo_playerinfodata.getProfilePublicKey(), (packetdataserializer1, profilepublickey_a) -> {
                    profilepublickey_a.write(packetdataserializer1);
                });
            }
        },
        UPDATE_GAME_MODE {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), (String) null);
                EnumGamemode enumgamemode = EnumGamemode.byId(packetdataserializer.readVarInt());

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, enumgamemode, (IChatBaseComponent) null, (ProfilePublicKey.a) null);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeUUID(packetplayoutplayerinfo_playerinfodata.getProfile().getId());
                packetdataserializer.writeVarInt(packetplayoutplayerinfo_playerinfodata.getGameMode().getId());
            }
        },
        UPDATE_LATENCY {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), (String) null);
                int i = packetdataserializer.readVarInt();

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, i, (EnumGamemode) null, (IChatBaseComponent) null, (ProfilePublicKey.a) null);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeUUID(packetplayoutplayerinfo_playerinfodata.getProfile().getId());
                packetdataserializer.writeVarInt(packetplayoutplayerinfo_playerinfodata.getLatency());
            }
        },
        UPDATE_DISPLAY_NAME {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), (String) null);
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, (EnumGamemode) null, ichatbasecomponent, (ProfilePublicKey.a) null);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeUUID(packetplayoutplayerinfo_playerinfodata.getProfile().getId());
                packetdataserializer.writeNullable(packetplayoutplayerinfo_playerinfodata.getDisplayName(), PacketDataSerializer::writeComponent);
            }
        },
        REMOVE_PLAYER {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), (String) null);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, (EnumGamemode) null, (IChatBaseComponent) null, (ProfilePublicKey.a) null);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeUUID(packetplayoutplayerinfo_playerinfodata.getProfile().getId());
            }
        };

        EnumPlayerInfoAction() {}

        protected abstract PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer);

        protected abstract void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata);
    }

    public static class PlayerInfoData {

        private final int latency;
        private final EnumGamemode gameMode;
        private final GameProfile profile;
        @Nullable
        private final IChatBaseComponent displayName;
        @Nullable
        private final ProfilePublicKey.a profilePublicKey;

        public PlayerInfoData(GameProfile gameprofile, int i, @Nullable EnumGamemode enumgamemode, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable ProfilePublicKey.a profilepublickey_a) {
            this.profile = gameprofile;
            this.latency = i;
            this.gameMode = enumgamemode;
            this.displayName = ichatbasecomponent;
            this.profilePublicKey = profilepublickey_a;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getLatency() {
            return this.latency;
        }

        public EnumGamemode getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public IChatBaseComponent getDisplayName() {
            return this.displayName;
        }

        @Nullable
        public ProfilePublicKey.a getProfilePublicKey() {
            return this.profilePublicKey;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : IChatBaseComponent.ChatSerializer.toJson(this.displayName)).add("profilePublicKey", this.profilePublicKey).toString();
        }
    }
}
