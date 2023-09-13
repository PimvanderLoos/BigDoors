package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
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

            this.entries.add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityplayer.getGameProfile(), entityplayer.latency, entityplayer.gameMode.getGameModeForPlayer(), entityplayer.getTabListDisplayName()));
        }

    }

    public PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction, Collection<EntityPlayer> collection) {
        this.action = packetplayoutplayerinfo_enumplayerinfoaction;
        this.entries = Lists.newArrayListWithCapacity(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            this.entries.add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityplayer.getGameProfile(), entityplayer.latency, entityplayer.gameMode.getGameModeForPlayer(), entityplayer.getTabListDisplayName()));
        }

    }

    public PacketPlayOutPlayerInfo(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) packetdataserializer.readEnum(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction = this.action;

        Objects.requireNonNull(this.action);
        this.entries = packetdataserializer.readList(packetplayoutplayerinfo_enumplayerinfoaction::read);
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

    @Nullable
    static IChatBaseComponent readDisplayName(PacketDataSerializer packetdataserializer) {
        return packetdataserializer.readBoolean() ? packetdataserializer.readComponent() : null;
    }

    static void writeDisplayName(PacketDataSerializer packetdataserializer, @Nullable IChatBaseComponent ichatbasecomponent) {
        if (ichatbasecomponent == null) {
            packetdataserializer.writeBoolean(false);
        } else {
            packetdataserializer.writeBoolean(true);
            packetdataserializer.writeComponent(ichatbasecomponent);
        }

    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
    }

    public static enum EnumPlayerInfoAction {

        ADD_PLAYER {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), packetdataserializer.readUtf(16));
                PropertyMap propertymap = gameprofile.getProperties();

                packetdataserializer.readWithCount((packetdataserializer1) -> {
                    String s = packetdataserializer1.readUtf();
                    String s1 = packetdataserializer1.readUtf();

                    if (packetdataserializer1.readBoolean()) {
                        String s2 = packetdataserializer1.readUtf();

                        propertymap.put(s, new Property(s, s1, s2));
                    } else {
                        propertymap.put(s, new Property(s, s1));
                    }

                });
                EnumGamemode enumgamemode = EnumGamemode.byId(packetdataserializer.readVarInt());
                int i = packetdataserializer.readVarInt();
                IChatBaseComponent ichatbasecomponent = PacketPlayOutPlayerInfo.readDisplayName(packetdataserializer);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, i, enumgamemode, ichatbasecomponent);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeUUID(packetplayoutplayerinfo_playerinfodata.getProfile().getId());
                packetdataserializer.writeUtf(packetplayoutplayerinfo_playerinfodata.getProfile().getName());
                packetdataserializer.writeCollection(packetplayoutplayerinfo_playerinfodata.getProfile().getProperties().values(), (packetdataserializer1, property) -> {
                    packetdataserializer1.writeUtf(property.getName());
                    packetdataserializer1.writeUtf(property.getValue());
                    if (property.hasSignature()) {
                        packetdataserializer1.writeBoolean(true);
                        packetdataserializer1.writeUtf(property.getSignature());
                    } else {
                        packetdataserializer1.writeBoolean(false);
                    }

                });
                packetdataserializer.writeVarInt(packetplayoutplayerinfo_playerinfodata.getGameMode().getId());
                packetdataserializer.writeVarInt(packetplayoutplayerinfo_playerinfodata.getLatency());
                PacketPlayOutPlayerInfo.writeDisplayName(packetdataserializer, packetplayoutplayerinfo_playerinfodata.getDisplayName());
            }
        },
        UPDATE_GAME_MODE {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), (String) null);
                EnumGamemode enumgamemode = EnumGamemode.byId(packetdataserializer.readVarInt());

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, enumgamemode, (IChatBaseComponent) null);
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

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, i, (EnumGamemode) null, (IChatBaseComponent) null);
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
                IChatBaseComponent ichatbasecomponent = PacketPlayOutPlayerInfo.readDisplayName(packetdataserializer);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, (EnumGamemode) null, ichatbasecomponent);
            }

            @Override
            protected void write(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.writeUUID(packetplayoutplayerinfo_playerinfodata.getProfile().getId());
                PacketPlayOutPlayerInfo.writeDisplayName(packetdataserializer, packetplayoutplayerinfo_playerinfodata.getDisplayName());
            }
        },
        REMOVE_PLAYER {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData read(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.readUUID(), (String) null);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, (EnumGamemode) null, (IChatBaseComponent) null);
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

        public PlayerInfoData(GameProfile gameprofile, int i, @Nullable EnumGamemode enumgamemode, @Nullable IChatBaseComponent ichatbasecomponent) {
            this.profile = gameprofile;
            this.latency = i;
            this.gameMode = enumgamemode;
            this.displayName = ichatbasecomponent;
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

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : IChatBaseComponent.ChatSerializer.toJson(this.displayName)).toString();
        }
    }
}
