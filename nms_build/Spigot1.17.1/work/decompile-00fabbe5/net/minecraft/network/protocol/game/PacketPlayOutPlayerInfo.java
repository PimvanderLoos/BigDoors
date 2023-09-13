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

            this.entries.add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityplayer.getProfile(), entityplayer.latency, entityplayer.gameMode.getGameMode(), entityplayer.getPlayerListName()));
        }

    }

    public PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction, Collection<EntityPlayer> collection) {
        this.action = packetplayoutplayerinfo_enumplayerinfoaction;
        this.entries = Lists.newArrayListWithCapacity(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            this.entries.add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityplayer.getProfile(), entityplayer.latency, entityplayer.gameMode.getGameMode(), entityplayer.getPlayerListName()));
        }

    }

    public PacketPlayOutPlayerInfo(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) packetdataserializer.a(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction = this.action;

        Objects.requireNonNull(this.action);
        this.entries = packetdataserializer.a(packetplayoutplayerinfo_enumplayerinfoaction::a);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.action);
        List list = this.entries;
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction packetplayoutplayerinfo_enumplayerinfoaction = this.action;

        Objects.requireNonNull(this.action);
        packetdataserializer.a((Collection) list, packetplayoutplayerinfo_enumplayerinfoaction::a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public List<PacketPlayOutPlayerInfo.PlayerInfoData> b() {
        return this.entries;
    }

    public PacketPlayOutPlayerInfo.EnumPlayerInfoAction c() {
        return this.action;
    }

    @Nullable
    static IChatBaseComponent b(PacketDataSerializer packetdataserializer) {
        return packetdataserializer.readBoolean() ? packetdataserializer.i() : null;
    }

    static void a(PacketDataSerializer packetdataserializer, @Nullable IChatBaseComponent ichatbasecomponent) {
        if (ichatbasecomponent == null) {
            packetdataserializer.writeBoolean(false);
        } else {
            packetdataserializer.writeBoolean(true);
            packetdataserializer.a(ichatbasecomponent);
        }

    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
    }

    public static enum EnumPlayerInfoAction {

        ADD_PLAYER {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData a(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.l(), packetdataserializer.e(16));
                PropertyMap propertymap = gameprofile.getProperties();

                packetdataserializer.a((packetdataserializer1) -> {
                    String s = packetdataserializer1.p();
                    String s1 = packetdataserializer1.p();

                    if (packetdataserializer1.readBoolean()) {
                        String s2 = packetdataserializer1.p();

                        propertymap.put(s, new Property(s, s1, s2));
                    } else {
                        propertymap.put(s, new Property(s, s1));
                    }

                });
                EnumGamemode enumgamemode = EnumGamemode.getById(packetdataserializer.j());
                int i = packetdataserializer.j();
                IChatBaseComponent ichatbasecomponent = PacketPlayOutPlayerInfo.b(packetdataserializer);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, i, enumgamemode, ichatbasecomponent);
            }

            @Override
            protected void a(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getId());
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getName());
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getProperties().values(), (packetdataserializer1, property) -> {
                    packetdataserializer1.a(property.getName());
                    packetdataserializer1.a(property.getValue());
                    if (property.hasSignature()) {
                        packetdataserializer1.writeBoolean(true);
                        packetdataserializer1.a(property.getSignature());
                    } else {
                        packetdataserializer1.writeBoolean(false);
                    }

                });
                packetdataserializer.d(packetplayoutplayerinfo_playerinfodata.c().getId());
                packetdataserializer.d(packetplayoutplayerinfo_playerinfodata.b());
                PacketPlayOutPlayerInfo.a(packetdataserializer, packetplayoutplayerinfo_playerinfodata.d());
            }
        },
        UPDATE_GAME_MODE {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData a(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.l(), (String) null);
                EnumGamemode enumgamemode = EnumGamemode.getById(packetdataserializer.j());

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, enumgamemode, (IChatBaseComponent) null);
            }

            @Override
            protected void a(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getId());
                packetdataserializer.d(packetplayoutplayerinfo_playerinfodata.c().getId());
            }
        },
        UPDATE_LATENCY {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData a(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.l(), (String) null);
                int i = packetdataserializer.j();

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, i, (EnumGamemode) null, (IChatBaseComponent) null);
            }

            @Override
            protected void a(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getId());
                packetdataserializer.d(packetplayoutplayerinfo_playerinfodata.b());
            }
        },
        UPDATE_DISPLAY_NAME {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData a(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.l(), (String) null);
                IChatBaseComponent ichatbasecomponent = PacketPlayOutPlayerInfo.b(packetdataserializer);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, (EnumGamemode) null, ichatbasecomponent);
            }

            @Override
            protected void a(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getId());
                PacketPlayOutPlayerInfo.a(packetdataserializer, packetplayoutplayerinfo_playerinfodata.d());
            }
        },
        REMOVE_PLAYER {
            @Override
            protected PacketPlayOutPlayerInfo.PlayerInfoData a(PacketDataSerializer packetdataserializer) {
                GameProfile gameprofile = new GameProfile(packetdataserializer.l(), (String) null);

                return new PacketPlayOutPlayerInfo.PlayerInfoData(gameprofile, 0, (EnumGamemode) null, (IChatBaseComponent) null);
            }

            @Override
            protected void a(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata) {
                packetdataserializer.a(packetplayoutplayerinfo_playerinfodata.a().getId());
            }
        };

        EnumPlayerInfoAction() {}

        protected abstract PacketPlayOutPlayerInfo.PlayerInfoData a(PacketDataSerializer packetdataserializer);

        protected abstract void a(PacketDataSerializer packetdataserializer, PacketPlayOutPlayerInfo.PlayerInfoData packetplayoutplayerinfo_playerinfodata);
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

        public GameProfile a() {
            return this.profile;
        }

        public int b() {
            return this.latency;
        }

        public EnumGamemode c() {
            return this.gameMode;
        }

        @Nullable
        public IChatBaseComponent d() {
            return this.displayName;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : IChatBaseComponent.ChatSerializer.a(this.displayName)).toString();
        }
    }
}
