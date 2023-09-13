package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.FileUtils;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutExperience;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayOutLogin;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutRecipeUpdate;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutServerDifficulty;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition;
import net.minecraft.network.protocol.game.PacketPlayOutTags;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateTime;
import net.minecraft.network.protocol.game.PacketPlayOutViewDistance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.IWorldBorderListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.storage.SavedFile;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.WorldNBTStorage;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {

    public static final File USERBANLIST_FILE = new File("banned-players.json");
    public static final File IPBANLIST_FILE = new File("banned-ips.json");
    public static final File OPLIST_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SEND_PLAYER_INFO_INTERVAL = 600;
    private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    public final List<EntityPlayer> players = Lists.newArrayList();
    private final Map<UUID, EntityPlayer> playersByUUID = Maps.newHashMap();
    private final GameProfileBanList bans;
    private final IpBanList ipBans;
    private final OpList ops;
    private final WhiteList whitelist;
    private final Map<UUID, ServerStatisticManager> stats;
    private final Map<UUID, AdvancementDataPlayer> advancements;
    public final WorldNBTStorage playerIo;
    private boolean doWhiteList;
    private final IRegistryCustom.Dimension registryHolder;
    protected final int maxPlayers;
    private int viewDistance;
    private boolean allowCheatsForAllPlayers;
    private static final boolean ALLOW_LOGOUTIVATOR = false;
    private int sendAllPlayerInfoIn;

    public PlayerList(MinecraftServer minecraftserver, IRegistryCustom.Dimension iregistrycustom_dimension, WorldNBTStorage worldnbtstorage, int i) {
        this.bans = new GameProfileBanList(PlayerList.USERBANLIST_FILE);
        this.ipBans = new IpBanList(PlayerList.IPBANLIST_FILE);
        this.ops = new OpList(PlayerList.OPLIST_FILE);
        this.whitelist = new WhiteList(PlayerList.WHITELIST_FILE);
        this.stats = Maps.newHashMap();
        this.advancements = Maps.newHashMap();
        this.server = minecraftserver;
        this.registryHolder = iregistrycustom_dimension;
        this.maxPlayers = i;
        this.playerIo = worldnbtstorage;
    }

    public void a(NetworkManager networkmanager, EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getProfile();
        UserCache usercache = this.server.getUserCache();
        Optional<GameProfile> optional = usercache.getProfile(gameprofile.getId());
        String s = (String) optional.map(GameProfile::getName).orElse(gameprofile.getName());

        usercache.a(gameprofile);
        NBTTagCompound nbttagcompound = this.a(entityplayer);
        ResourceKey resourcekey;

        if (nbttagcompound != null) {
            DataResult dataresult = DimensionManager.a(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Dimension")));
            Logger logger = PlayerList.LOGGER;

            Objects.requireNonNull(logger);
            resourcekey = (ResourceKey) dataresult.resultOrPartial(logger::error).orElse(World.OVERWORLD);
        } else {
            resourcekey = World.OVERWORLD;
        }

        ResourceKey<World> resourcekey1 = resourcekey;
        WorldServer worldserver = this.server.getWorldServer(resourcekey1);
        WorldServer worldserver1;

        if (worldserver == null) {
            PlayerList.LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourcekey1);
            worldserver1 = this.server.E();
        } else {
            worldserver1 = worldserver;
        }

        entityplayer.spawnIn(worldserver1);
        String s1 = "local";

        if (networkmanager.getSocketAddress() != null) {
            s1 = networkmanager.getSocketAddress().toString();
        }

        PlayerList.LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", entityplayer.getDisplayName().getString(), s1, entityplayer.getId(), entityplayer.locX(), entityplayer.locY(), entityplayer.locZ());
        WorldData worlddata = worldserver1.getWorldData();

        entityplayer.c(nbttagcompound);
        PlayerConnection playerconnection = new PlayerConnection(this.server, networkmanager, entityplayer);
        GameRules gamerules = worldserver1.getGameRules();
        boolean flag = gamerules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
        boolean flag1 = gamerules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);

        playerconnection.sendPacket(new PacketPlayOutLogin(entityplayer.getId(), entityplayer.gameMode.getGameMode(), entityplayer.gameMode.c(), BiomeManager.a(worldserver1.getSeed()), worlddata.isHardcore(), this.server.F(), this.registryHolder, worldserver1.getDimensionManager(), worldserver1.getDimensionKey(), this.getMaxPlayers(), this.viewDistance, flag1, !flag, worldserver1.isDebugWorld(), worldserver1.isFlatWorld()));
        playerconnection.sendPacket(new PacketPlayOutCustomPayload(PacketPlayOutCustomPayload.BRAND, (new PacketDataSerializer(Unpooled.buffer())).a(this.getServer().getServerModName())));
        playerconnection.sendPacket(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        playerconnection.sendPacket(new PacketPlayOutAbilities(entityplayer.getAbilities()));
        playerconnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.getInventory().selected));
        playerconnection.sendPacket(new PacketPlayOutRecipeUpdate(this.server.getCraftingManager().b()));
        playerconnection.sendPacket(new PacketPlayOutTags(this.server.getTagRegistry().a((IRegistryCustom) this.registryHolder)));
        this.d(entityplayer);
        entityplayer.getStatisticManager().c();
        entityplayer.getRecipeBook().a(entityplayer);
        this.sendScoreboard(worldserver1.getScoreboard(), entityplayer);
        this.server.invalidatePingSample();
        ChatMessage chatmessage;

        if (entityplayer.getProfile().getName().equalsIgnoreCase(s)) {
            chatmessage = new ChatMessage("multiplayer.player.joined", new Object[]{entityplayer.getScoreboardDisplayName()});
        } else {
            chatmessage = new ChatMessage("multiplayer.player.joined.renamed", new Object[]{entityplayer.getScoreboardDisplayName(), s});
        }

        this.sendMessage(chatmessage.a(EnumChatFormat.YELLOW), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
        playerconnection.b(entityplayer.locX(), entityplayer.locY(), entityplayer.locZ(), entityplayer.getYRot(), entityplayer.getXRot());
        this.players.add(entityplayer);
        this.playersByUUID.put(entityplayer.getUniqueID(), entityplayer);
        this.sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{entityplayer}));

        for (int i = 0; i < this.players.size(); ++i) {
            entityplayer.connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{(EntityPlayer) this.players.get(i)}));
        }

        worldserver1.addPlayerJoin(entityplayer);
        this.server.getBossBattleCustomData().a(entityplayer);
        this.a(entityplayer, worldserver1);
        if (!this.server.getResourcePack().isEmpty()) {
            entityplayer.setResourcePack(this.server.getResourcePack(), this.server.getResourcePackHash(), this.server.aX(), this.server.ba());
        }

        Iterator iterator = entityplayer.getEffects().iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            playerconnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), mobeffect));
        }

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("RootVehicle", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("RootVehicle");
            Entity entity = EntityTypes.a(nbttagcompound1.getCompound("Entity"), worldserver1, (entity1) -> {
                return !worldserver1.addEntitySerialized(entity1) ? null : entity1;
            });

            if (entity != null) {
                UUID uuid;

                if (nbttagcompound1.b("Attach")) {
                    uuid = nbttagcompound1.a("Attach");
                } else {
                    uuid = null;
                }

                Iterator iterator1;
                Entity entity1;

                if (entity.getUniqueID().equals(uuid)) {
                    entityplayer.a(entity, true);
                } else {
                    iterator1 = entity.getAllPassengers().iterator();

                    while (iterator1.hasNext()) {
                        entity1 = (Entity) iterator1.next();
                        if (entity1.getUniqueID().equals(uuid)) {
                            entityplayer.a(entity1, true);
                            break;
                        }
                    }
                }

                if (!entityplayer.isPassenger()) {
                    PlayerList.LOGGER.warn("Couldn't reattach entity to player");
                    entity.die();
                    iterator1 = entity.getAllPassengers().iterator();

                    while (iterator1.hasNext()) {
                        entity1 = (Entity) iterator1.next();
                        entity1.die();
                    }
                }
            }
        }

        entityplayer.syncInventory();
    }

    public void sendScoreboard(ScoreboardServer scoreboardserver, EntityPlayer entityplayer) {
        Set<ScoreboardObjective> set = Sets.newHashSet();
        Iterator iterator = scoreboardserver.getTeams().iterator();

        while (iterator.hasNext()) {
            ScoreboardTeam scoreboardteam = (ScoreboardTeam) iterator.next();

            entityplayer.connection.sendPacket(PacketPlayOutScoreboardTeam.a(scoreboardteam, true));
        }

        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjectiveForSlot(i);

            if (scoreboardobjective != null && !set.contains(scoreboardobjective)) {
                List<Packet<?>> list = scoreboardserver.getScoreboardScorePacketsForObjective(scoreboardobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    Packet<?> packet = (Packet) iterator1.next();

                    entityplayer.connection.sendPacket(packet);
                }

                set.add(scoreboardobjective);
            }
        }

    }

    public void setPlayerFileData(WorldServer worldserver) {
        worldserver.getWorldBorder().a(new IWorldBorderListener() {
            @Override
            public void a(WorldBorder worldborder, double d0) {
                PlayerList.this.sendAll(new ClientboundSetBorderSizePacket(worldborder));
            }

            @Override
            public void a(WorldBorder worldborder, double d0, double d1, long i) {
                PlayerList.this.sendAll(new ClientboundSetBorderLerpSizePacket(worldborder));
            }

            @Override
            public void a(WorldBorder worldborder, double d0, double d1) {
                PlayerList.this.sendAll(new ClientboundSetBorderCenterPacket(worldborder));
            }

            @Override
            public void a(WorldBorder worldborder, int i) {
                PlayerList.this.sendAll(new ClientboundSetBorderWarningDelayPacket(worldborder));
            }

            @Override
            public void b(WorldBorder worldborder, int i) {
                PlayerList.this.sendAll(new ClientboundSetBorderWarningDistancePacket(worldborder));
            }

            @Override
            public void b(WorldBorder worldborder, double d0) {}

            @Override
            public void c(WorldBorder worldborder, double d0) {}
        });
    }

    @Nullable
    public NBTTagCompound a(EntityPlayer entityplayer) {
        NBTTagCompound nbttagcompound = this.server.getSaveData().y();
        NBTTagCompound nbttagcompound1;

        if (entityplayer.getDisplayName().getString().equals(this.server.getSinglePlayerName()) && nbttagcompound != null) {
            nbttagcompound1 = nbttagcompound;
            entityplayer.load(nbttagcompound);
            PlayerList.LOGGER.debug("loading single player");
        } else {
            nbttagcompound1 = this.playerIo.load(entityplayer);
        }

        return nbttagcompound1;
    }

    protected void savePlayerFile(EntityPlayer entityplayer) {
        this.playerIo.save(entityplayer);
        ServerStatisticManager serverstatisticmanager = (ServerStatisticManager) this.stats.get(entityplayer.getUniqueID());

        if (serverstatisticmanager != null) {
            serverstatisticmanager.save();
        }

        AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) this.advancements.get(entityplayer.getUniqueID());

        if (advancementdataplayer != null) {
            advancementdataplayer.b();
        }

    }

    public void disconnect(EntityPlayer entityplayer) {
        WorldServer worldserver = entityplayer.getWorldServer();

        entityplayer.a(StatisticList.LEAVE_GAME);
        this.savePlayerFile(entityplayer);
        if (entityplayer.isPassenger()) {
            Entity entity = entityplayer.getRootVehicle();

            if (entity.hasSinglePlayerPassenger()) {
                PlayerList.LOGGER.debug("Removing player mount");
                entityplayer.stopRiding();
                entity.cD().forEach((entity1) -> {
                    entity1.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
                });
            }
        }

        entityplayer.decouple();
        worldserver.a(entityplayer, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        entityplayer.getAdvancementData().a();
        this.players.remove(entityplayer);
        this.server.getBossBattleCustomData().b(entityplayer);
        UUID uuid = entityplayer.getUniqueID();
        EntityPlayer entityplayer1 = (EntityPlayer) this.playersByUUID.get(uuid);

        if (entityplayer1 == entityplayer) {
            this.playersByUUID.remove(uuid);
            this.stats.remove(uuid);
            this.advancements.remove(uuid);
        }

        this.sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{entityplayer}));
    }

    @Nullable
    public IChatBaseComponent attemptLogin(SocketAddress socketaddress, GameProfile gameprofile) {
        ChatMessage chatmessage;

        if (this.bans.isBanned(gameprofile)) {
            GameProfileBanEntry gameprofilebanentry = (GameProfileBanEntry) this.bans.get(gameprofile);

            chatmessage = new ChatMessage("multiplayer.disconnect.banned.reason", new Object[]{gameprofilebanentry.getReason()});
            if (gameprofilebanentry.getExpires() != null) {
                chatmessage.addSibling(new ChatMessage("multiplayer.disconnect.banned.expiration", new Object[]{PlayerList.BAN_DATE_FORMAT.format(gameprofilebanentry.getExpires())}));
            }

            return chatmessage;
        } else if (!this.isWhitelisted(gameprofile)) {
            return new ChatMessage("multiplayer.disconnect.not_whitelisted");
        } else if (this.ipBans.isBanned(socketaddress)) {
            IpBanEntry ipbanentry = this.ipBans.get(socketaddress);

            chatmessage = new ChatMessage("multiplayer.disconnect.banned_ip.reason", new Object[]{ipbanentry.getReason()});
            if (ipbanentry.getExpires() != null) {
                chatmessage.addSibling(new ChatMessage("multiplayer.disconnect.banned_ip.expiration", new Object[]{PlayerList.BAN_DATE_FORMAT.format(ipbanentry.getExpires())}));
            }

            return chatmessage;
        } else {
            return this.players.size() >= this.maxPlayers && !this.d(gameprofile) ? new ChatMessage("multiplayer.disconnect.server_full") : null;
        }
    }

    public EntityPlayer processLogin(GameProfile gameprofile) {
        UUID uuid = EntityHuman.a(gameprofile);
        List<EntityPlayer> list = Lists.newArrayList();

        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

            if (entityplayer.getUniqueID().equals(uuid)) {
                list.add(entityplayer);
            }
        }

        EntityPlayer entityplayer1 = (EntityPlayer) this.playersByUUID.get(gameprofile.getId());

        if (entityplayer1 != null && !list.contains(entityplayer1)) {
            list.add(entityplayer1);
        }

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();

            entityplayer2.connection.disconnect(new ChatMessage("multiplayer.disconnect.duplicate_login"));
        }

        return new EntityPlayer(this.server, this.server.E(), gameprofile);
    }

    public EntityPlayer moveToWorld(EntityPlayer entityplayer, boolean flag) {
        this.players.remove(entityplayer);
        entityplayer.getWorldServer().a(entityplayer, Entity.RemovalReason.DISCARDED);
        BlockPosition blockposition = entityplayer.getSpawn();
        float f = entityplayer.getSpawnAngle();
        boolean flag1 = entityplayer.isSpawnForced();
        WorldServer worldserver = this.server.getWorldServer(entityplayer.getSpawnDimension());
        Optional optional;

        if (worldserver != null && blockposition != null) {
            optional = EntityHuman.getBed(worldserver, blockposition, f, flag1, flag);
        } else {
            optional = Optional.empty();
        }

        WorldServer worldserver1 = worldserver != null && optional.isPresent() ? worldserver : this.server.E();
        EntityPlayer entityplayer1 = new EntityPlayer(this.server, worldserver1, entityplayer.getProfile());

        entityplayer1.connection = entityplayer.connection;
        entityplayer1.copyFrom(entityplayer, flag);
        entityplayer1.e(entityplayer.getId());
        entityplayer1.a(entityplayer.getMainHand());
        Iterator iterator = entityplayer.getScoreboardTags().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            entityplayer1.addScoreboardTag(s);
        }

        boolean flag2 = false;

        if (optional.isPresent()) {
            IBlockData iblockdata = worldserver1.getType(blockposition);
            boolean flag3 = iblockdata.a(Blocks.RESPAWN_ANCHOR);
            Vec3D vec3d = (Vec3D) optional.get();
            float f1;

            if (!iblockdata.a((Tag) TagsBlock.BEDS) && !flag3) {
                f1 = f;
            } else {
                Vec3D vec3d1 = Vec3D.c((BaseBlockPosition) blockposition).d(vec3d).d();

                f1 = (float) MathHelper.f(MathHelper.d(vec3d1.z, vec3d1.x) * 57.2957763671875D - 90.0D);
            }

            entityplayer1.setPositionRotation(vec3d.x, vec3d.y, vec3d.z, f1, 0.0F);
            entityplayer1.setRespawnPosition(worldserver1.getDimensionKey(), blockposition, f, flag1, false);
            flag2 = !flag && flag3;
        } else if (blockposition != null) {
            entityplayer1.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
        }

        while (!worldserver1.getCubes(entityplayer1) && entityplayer1.locY() < (double) worldserver1.getMaxBuildHeight()) {
            entityplayer1.setPosition(entityplayer1.locX(), entityplayer1.locY() + 1.0D, entityplayer1.locZ());
        }

        WorldData worlddata = entityplayer1.level.getWorldData();

        entityplayer1.connection.sendPacket(new PacketPlayOutRespawn(entityplayer1.level.getDimensionManager(), entityplayer1.level.getDimensionKey(), BiomeManager.a(entityplayer1.getWorldServer().getSeed()), entityplayer1.gameMode.getGameMode(), entityplayer1.gameMode.c(), entityplayer1.getWorldServer().isDebugWorld(), entityplayer1.getWorldServer().isFlatWorld(), flag));
        entityplayer1.connection.b(entityplayer1.locX(), entityplayer1.locY(), entityplayer1.locZ(), entityplayer1.getYRot(), entityplayer1.getXRot());
        entityplayer1.connection.sendPacket(new PacketPlayOutSpawnPosition(worldserver1.getSpawn(), worldserver1.x()));
        entityplayer1.connection.sendPacket(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        entityplayer1.connection.sendPacket(new PacketPlayOutExperience(entityplayer1.experienceProgress, entityplayer1.totalExperience, entityplayer1.experienceLevel));
        this.a(entityplayer1, worldserver1);
        this.d(entityplayer1);
        worldserver1.addPlayerRespawn(entityplayer1);
        this.players.add(entityplayer1);
        this.playersByUUID.put(entityplayer1.getUniqueID(), entityplayer1);
        entityplayer1.syncInventory();
        entityplayer1.setHealth(entityplayer1.getHealth());
        if (flag2) {
            entityplayer1.connection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0F, 1.0F));
        }

        return entityplayer1;
    }

    public void d(EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getProfile();
        int i = this.server.b(gameprofile);

        this.a(entityplayer, i);
    }

    public void tick() {
        if (++this.sendAllPlayerInfoIn > 600) {
            this.sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, this.players));
            this.sendAllPlayerInfoIn = 0;
        }

    }

    public void sendAll(Packet<?> packet) {
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.sendPacket(packet);
        }

    }

    public void a(Packet<?> packet, ResourceKey<World> resourcekey) {
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.level.getDimensionKey() == resourcekey) {
                entityplayer.connection.sendPacket(packet);
            }
        }

    }

    public void a(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.getScoreboardTeam();

        if (scoreboardteambase != null) {
            Collection<String> collection = scoreboardteambase.getPlayerNameSet();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                EntityPlayer entityplayer = this.getPlayer(s);

                if (entityplayer != null && entityplayer != entityhuman) {
                    entityplayer.sendMessage(ichatbasecomponent, entityhuman.getUniqueID());
                }
            }

        }
    }

    public void b(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.getScoreboardTeam();

        if (scoreboardteambase == null) {
            this.sendMessage(ichatbasecomponent, ChatMessageType.SYSTEM, entityhuman.getUniqueID());
        } else {
            for (int i = 0; i < this.players.size(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

                if (entityplayer.getScoreboardTeam() != scoreboardteambase) {
                    entityplayer.sendMessage(ichatbasecomponent, entityhuman.getUniqueID());
                }
            }

        }
    }

    public String[] e() {
        String[] astring = new String[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i) {
            astring[i] = ((EntityPlayer) this.players.get(i)).getProfile().getName();
        }

        return astring;
    }

    public GameProfileBanList getProfileBans() {
        return this.bans;
    }

    public IpBanList getIPBans() {
        return this.ipBans;
    }

    public void addOp(GameProfile gameprofile) {
        this.ops.add(new OpListEntry(gameprofile, this.server.h(), this.ops.canBypassPlayerLimit(gameprofile)));
        EntityPlayer entityplayer = this.getPlayer(gameprofile.getId());

        if (entityplayer != null) {
            this.d(entityplayer);
        }

    }

    public void removeOp(GameProfile gameprofile) {
        this.ops.remove(gameprofile);
        EntityPlayer entityplayer = this.getPlayer(gameprofile.getId());

        if (entityplayer != null) {
            this.d(entityplayer);
        }

    }

    private void a(EntityPlayer entityplayer, int i) {
        if (entityplayer.connection != null) {
            byte b0;

            if (i <= 0) {
                b0 = 24;
            } else if (i >= 4) {
                b0 = 28;
            } else {
                b0 = (byte) (24 + i);
            }

            entityplayer.connection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, b0));
        }

        this.server.getCommandDispatcher().a(entityplayer);
    }

    public boolean isWhitelisted(GameProfile gameprofile) {
        return !this.doWhiteList || this.ops.d(gameprofile) || this.whitelist.d(gameprofile);
    }

    public boolean isOp(GameProfile gameprofile) {
        return this.ops.d(gameprofile) || this.server.a(gameprofile) && this.server.getSaveData().o() || this.allowCheatsForAllPlayers;
    }

    @Nullable
    public EntityPlayer getPlayer(String s) {
        Iterator iterator = this.players.iterator();

        EntityPlayer entityplayer;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityplayer = (EntityPlayer) iterator.next();
        } while (!entityplayer.getProfile().getName().equalsIgnoreCase(s));

        return entityplayer;
    }

    public void sendPacketNearby(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, double d3, ResourceKey<World> resourcekey, Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

            if (entityplayer != entityhuman && entityplayer.level.getDimensionKey() == resourcekey) {
                double d4 = d0 - entityplayer.locX();
                double d5 = d1 - entityplayer.locY();
                double d6 = d2 - entityplayer.locZ();

                if (d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3) {
                    entityplayer.connection.sendPacket(packet);
                }
            }
        }

    }

    public void savePlayers() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.savePlayerFile((EntityPlayer) this.players.get(i));
        }

    }

    public WhiteList getWhitelist() {
        return this.whitelist;
    }

    public String[] getWhitelisted() {
        return this.whitelist.getEntries();
    }

    public OpList getOPs() {
        return this.ops;
    }

    public String[] l() {
        return this.ops.getEntries();
    }

    public void reloadWhitelist() {}

    public void a(EntityPlayer entityplayer, WorldServer worldserver) {
        WorldBorder worldborder = this.server.E().getWorldBorder();

        entityplayer.connection.sendPacket(new ClientboundInitializeBorderPacket(worldborder));
        entityplayer.connection.sendPacket(new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        entityplayer.connection.sendPacket(new PacketPlayOutSpawnPosition(worldserver.getSpawn(), worldserver.x()));
        if (worldserver.isRaining()) {
            entityplayer.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.START_RAINING, 0.0F));
            entityplayer.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.RAIN_LEVEL_CHANGE, worldserver.d(1.0F)));
            entityplayer.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.THUNDER_LEVEL_CHANGE, worldserver.b(1.0F)));
        }

    }

    public void updateClient(EntityPlayer entityplayer) {
        entityplayer.inventoryMenu.updateInventory();
        entityplayer.triggerHealthUpdate();
        entityplayer.connection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.getInventory().selected));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean getHasWhitelist() {
        return this.doWhiteList;
    }

    public void setHasWhitelist(boolean flag) {
        this.doWhiteList = flag;
    }

    public List<EntityPlayer> b(String s) {
        List<EntityPlayer> list = Lists.newArrayList();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.v().equals(s)) {
                list.add(entityplayer);
            }
        }

        return list;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public NBTTagCompound save() {
        return null;
    }

    public void b(boolean flag) {
        this.allowCheatsForAllPlayers = flag;
    }

    public void shutdown() {
        for (int i = 0; i < this.players.size(); ++i) {
            ((EntityPlayer) this.players.get(i)).connection.disconnect(new ChatMessage("multiplayer.disconnect.server_shutdown"));
        }

    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent, ChatMessageType chatmessagetype, UUID uuid) {
        this.server.sendMessage(ichatbasecomponent, uuid);
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.a(ichatbasecomponent, chatmessagetype, uuid);
        }

    }

    public void a(IChatBaseComponent ichatbasecomponent, Function<EntityPlayer, IChatBaseComponent> function, ChatMessageType chatmessagetype, UUID uuid) {
        this.server.sendMessage(ichatbasecomponent, uuid);
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) function.apply(entityplayer);

            if (ichatbasecomponent1 != null) {
                entityplayer.a(ichatbasecomponent1, chatmessagetype, uuid);
            }
        }

    }

    public ServerStatisticManager getStatisticManager(EntityHuman entityhuman) {
        UUID uuid = entityhuman.getUniqueID();
        ServerStatisticManager serverstatisticmanager = uuid == null ? null : (ServerStatisticManager) this.stats.get(uuid);

        if (serverstatisticmanager == null) {
            File file = this.server.a(SavedFile.PLAYER_STATS_DIR).toFile();
            File file1 = new File(file, uuid + ".json");

            if (!file1.exists()) {
                File file2 = new File(file, entityhuman.getDisplayName().getString() + ".json");
                Path path = file2.toPath();

                if (FileUtils.a(path) && FileUtils.b(path) && path.startsWith(file.getPath()) && file2.isFile()) {
                    file2.renameTo(file1);
                }
            }

            serverstatisticmanager = new ServerStatisticManager(this.server, file1);
            this.stats.put(uuid, serverstatisticmanager);
        }

        return serverstatisticmanager;
    }

    public AdvancementDataPlayer f(EntityPlayer entityplayer) {
        UUID uuid = entityplayer.getUniqueID();
        AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) this.advancements.get(uuid);

        if (advancementdataplayer == null) {
            File file = this.server.a(SavedFile.PLAYER_ADVANCEMENTS_DIR).toFile();
            File file1 = new File(file, uuid + ".json");

            advancementdataplayer = new AdvancementDataPlayer(this.server.getDataFixer(), this, this.server.getAdvancementData(), file1, entityplayer);
            this.advancements.put(uuid, advancementdataplayer);
        }

        advancementdataplayer.a(entityplayer);
        return advancementdataplayer;
    }

    public void a(int i) {
        this.viewDistance = i;
        this.sendAll(new PacketPlayOutViewDistance(i));
        Iterator iterator = this.server.getWorlds().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver != null) {
                worldserver.getChunkProvider().setViewDistance(i);
            }
        }

    }

    public List<EntityPlayer> getPlayers() {
        return this.players;
    }

    @Nullable
    public EntityPlayer getPlayer(UUID uuid) {
        return (EntityPlayer) this.playersByUUID.get(uuid);
    }

    public boolean d(GameProfile gameprofile) {
        return false;
    }

    public void reload() {
        Iterator iterator = this.advancements.values().iterator();

        while (iterator.hasNext()) {
            AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) iterator.next();

            advancementdataplayer.a(this.server.getAdvancementData());
        }

        this.sendAll(new PacketPlayOutTags(this.server.getTagRegistry().a((IRegistryCustom) this.registryHolder)));
        PacketPlayOutRecipeUpdate packetplayoutrecipeupdate = new PacketPlayOutRecipeUpdate(this.server.getCraftingManager().b());
        Iterator iterator1 = this.players.iterator();

        while (iterator1.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator1.next();

            entityplayer.connection.sendPacket(packetplayoutrecipeupdate);
            entityplayer.getRecipeBook().a(entityplayer);
        }

    }

    public boolean u() {
        return this.allowCheatsForAllPlayers;
    }
}
