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
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
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
    private int simulationDistance;
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

    public void placeNewPlayer(NetworkManager networkmanager, EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getGameProfile();
        UserCache usercache = this.server.getProfileCache();
        Optional<GameProfile> optional = usercache.get(gameprofile.getId());
        String s = (String) optional.map(GameProfile::getName).orElse(gameprofile.getName());

        usercache.add(gameprofile);
        NBTTagCompound nbttagcompound = this.load(entityplayer);
        ResourceKey resourcekey;

        if (nbttagcompound != null) {
            DataResult dataresult = DimensionManager.parseLegacy(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Dimension")));
            Logger logger = PlayerList.LOGGER;

            Objects.requireNonNull(logger);
            resourcekey = (ResourceKey) dataresult.resultOrPartial(logger::error).orElse(World.OVERWORLD);
        } else {
            resourcekey = World.OVERWORLD;
        }

        ResourceKey<World> resourcekey1 = resourcekey;
        WorldServer worldserver = this.server.getLevel(resourcekey1);
        WorldServer worldserver1;

        if (worldserver == null) {
            PlayerList.LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourcekey1);
            worldserver1 = this.server.overworld();
        } else {
            worldserver1 = worldserver;
        }

        entityplayer.setLevel(worldserver1);
        String s1 = "local";

        if (networkmanager.getRemoteAddress() != null) {
            s1 = networkmanager.getRemoteAddress().toString();
        }

        PlayerList.LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", entityplayer.getName().getString(), s1, entityplayer.getId(), entityplayer.getX(), entityplayer.getY(), entityplayer.getZ());
        WorldData worlddata = worldserver1.getLevelData();

        entityplayer.loadGameTypes(nbttagcompound);
        PlayerConnection playerconnection = new PlayerConnection(this.server, networkmanager, entityplayer);
        GameRules gamerules = worldserver1.getGameRules();
        boolean flag = gamerules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
        boolean flag1 = gamerules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);

        playerconnection.send(new PacketPlayOutLogin(entityplayer.getId(), worlddata.isHardcore(), entityplayer.gameMode.getGameModeForPlayer(), entityplayer.gameMode.getPreviousGameModeForPlayer(), this.server.levelKeys(), this.registryHolder, worldserver1.dimensionType(), worldserver1.dimension(), BiomeManager.obfuscateSeed(worldserver1.getSeed()), this.getMaxPlayers(), this.viewDistance, this.simulationDistance, flag1, !flag, worldserver1.isDebug(), worldserver1.isFlat()));
        playerconnection.send(new PacketPlayOutCustomPayload(PacketPlayOutCustomPayload.BRAND, (new PacketDataSerializer(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
        playerconnection.send(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        playerconnection.send(new PacketPlayOutAbilities(entityplayer.getAbilities()));
        playerconnection.send(new PacketPlayOutHeldItemSlot(entityplayer.getInventory().selected));
        playerconnection.send(new PacketPlayOutRecipeUpdate(this.server.getRecipeManager().getRecipes()));
        playerconnection.send(new PacketPlayOutTags(this.server.getTags().serializeToNetwork(this.registryHolder)));
        this.sendPlayerPermissionLevel(entityplayer);
        entityplayer.getStats().markAllDirty();
        entityplayer.getRecipeBook().sendInitialRecipeBook(entityplayer);
        this.updateEntireScoreboard(worldserver1.getScoreboard(), entityplayer);
        this.server.invalidateStatus();
        ChatMessage chatmessage;

        if (entityplayer.getGameProfile().getName().equalsIgnoreCase(s)) {
            chatmessage = new ChatMessage("multiplayer.player.joined", new Object[]{entityplayer.getDisplayName()});
        } else {
            chatmessage = new ChatMessage("multiplayer.player.joined.renamed", new Object[]{entityplayer.getDisplayName(), s});
        }

        this.broadcastMessage(chatmessage.withStyle(EnumChatFormat.YELLOW), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
        playerconnection.teleport(entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), entityplayer.getYRot(), entityplayer.getXRot());
        this.players.add(entityplayer);
        this.playersByUUID.put(entityplayer.getUUID(), entityplayer);
        this.broadcastAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{entityplayer}));

        for (int i = 0; i < this.players.size(); ++i) {
            entityplayer.connection.send(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{(EntityPlayer) this.players.get(i)}));
        }

        worldserver1.addNewPlayer(entityplayer);
        this.server.getCustomBossEvents().onPlayerConnect(entityplayer);
        this.sendLevelInfo(entityplayer, worldserver1);
        if (!this.server.getResourcePack().isEmpty()) {
            entityplayer.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash(), this.server.isResourcePackRequired(), this.server.getResourcePackPrompt());
        }

        Iterator iterator = entityplayer.getActiveEffects().iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            playerconnection.send(new PacketPlayOutEntityEffect(entityplayer.getId(), mobeffect));
        }

        if (nbttagcompound != null && nbttagcompound.contains("RootVehicle", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("RootVehicle");
            Entity entity = EntityTypes.loadEntityRecursive(nbttagcompound1.getCompound("Entity"), worldserver1, (entity1) -> {
                return !worldserver1.addWithUUID(entity1) ? null : entity1;
            });

            if (entity != null) {
                UUID uuid;

                if (nbttagcompound1.hasUUID("Attach")) {
                    uuid = nbttagcompound1.getUUID("Attach");
                } else {
                    uuid = null;
                }

                Iterator iterator1;
                Entity entity1;

                if (entity.getUUID().equals(uuid)) {
                    entityplayer.startRiding(entity, true);
                } else {
                    iterator1 = entity.getIndirectPassengers().iterator();

                    while (iterator1.hasNext()) {
                        entity1 = (Entity) iterator1.next();
                        if (entity1.getUUID().equals(uuid)) {
                            entityplayer.startRiding(entity1, true);
                            break;
                        }
                    }
                }

                if (!entityplayer.isPassenger()) {
                    PlayerList.LOGGER.warn("Couldn't reattach entity to player");
                    entity.discard();
                    iterator1 = entity.getIndirectPassengers().iterator();

                    while (iterator1.hasNext()) {
                        entity1 = (Entity) iterator1.next();
                        entity1.discard();
                    }
                }
            }
        }

        entityplayer.initInventoryMenu();
    }

    public void updateEntireScoreboard(ScoreboardServer scoreboardserver, EntityPlayer entityplayer) {
        Set<ScoreboardObjective> set = Sets.newHashSet();
        Iterator iterator = scoreboardserver.getPlayerTeams().iterator();

        while (iterator.hasNext()) {
            ScoreboardTeam scoreboardteam = (ScoreboardTeam) iterator.next();

            entityplayer.connection.send(PacketPlayOutScoreboardTeam.createAddOrModifyPacket(scoreboardteam, true));
        }

        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardobjective = scoreboardserver.getDisplayObjective(i);

            if (scoreboardobjective != null && !set.contains(scoreboardobjective)) {
                List<Packet<?>> list = scoreboardserver.getStartTrackingPackets(scoreboardobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    Packet<?> packet = (Packet) iterator1.next();

                    entityplayer.connection.send(packet);
                }

                set.add(scoreboardobjective);
            }
        }

    }

    public void addWorldborderListener(WorldServer worldserver) {
        worldserver.getWorldBorder().addListener(new IWorldBorderListener() {
            @Override
            public void onBorderSizeSet(WorldBorder worldborder, double d0) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderSizePacket(worldborder));
            }

            @Override
            public void onBorderSizeLerping(WorldBorder worldborder, double d0, double d1, long i) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderLerpSizePacket(worldborder));
            }

            @Override
            public void onBorderCenterSet(WorldBorder worldborder, double d0, double d1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderCenterPacket(worldborder));
            }

            @Override
            public void onBorderSetWarningTime(WorldBorder worldborder, int i) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDelayPacket(worldborder));
            }

            @Override
            public void onBorderSetWarningBlocks(WorldBorder worldborder, int i) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDistancePacket(worldborder));
            }

            @Override
            public void onBorderSetDamagePerBlock(WorldBorder worldborder, double d0) {}

            @Override
            public void onBorderSetDamageSafeZOne(WorldBorder worldborder, double d0) {}
        });
    }

    @Nullable
    public NBTTagCompound load(EntityPlayer entityplayer) {
        NBTTagCompound nbttagcompound = this.server.getWorldData().getLoadedPlayerTag();
        NBTTagCompound nbttagcompound1;

        if (entityplayer.getName().getString().equals(this.server.getSingleplayerName()) && nbttagcompound != null) {
            nbttagcompound1 = nbttagcompound;
            entityplayer.load(nbttagcompound);
            PlayerList.LOGGER.debug("loading single player");
        } else {
            nbttagcompound1 = this.playerIo.load(entityplayer);
        }

        return nbttagcompound1;
    }

    protected void save(EntityPlayer entityplayer) {
        this.playerIo.save(entityplayer);
        ServerStatisticManager serverstatisticmanager = (ServerStatisticManager) this.stats.get(entityplayer.getUUID());

        if (serverstatisticmanager != null) {
            serverstatisticmanager.save();
        }

        AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) this.advancements.get(entityplayer.getUUID());

        if (advancementdataplayer != null) {
            advancementdataplayer.save();
        }

    }

    public void remove(EntityPlayer entityplayer) {
        WorldServer worldserver = entityplayer.getLevel();

        entityplayer.awardStat(StatisticList.LEAVE_GAME);
        this.save(entityplayer);
        if (entityplayer.isPassenger()) {
            Entity entity = entityplayer.getRootVehicle();

            if (entity.hasExactlyOnePlayerPassenger()) {
                PlayerList.LOGGER.debug("Removing player mount");
                entityplayer.stopRiding();
                entity.getPassengersAndSelf().forEach((entity1) -> {
                    entity1.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
                });
            }
        }

        entityplayer.unRide();
        worldserver.removePlayerImmediately(entityplayer, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        entityplayer.getAdvancements().stopListening();
        this.players.remove(entityplayer);
        this.server.getCustomBossEvents().onPlayerDisconnect(entityplayer);
        UUID uuid = entityplayer.getUUID();
        EntityPlayer entityplayer1 = (EntityPlayer) this.playersByUUID.get(uuid);

        if (entityplayer1 == entityplayer) {
            this.playersByUUID.remove(uuid);
            this.stats.remove(uuid);
            this.advancements.remove(uuid);
        }

        this.broadcastAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{entityplayer}));
    }

    @Nullable
    public IChatBaseComponent canPlayerLogin(SocketAddress socketaddress, GameProfile gameprofile) {
        ChatMessage chatmessage;

        if (this.bans.isBanned(gameprofile)) {
            GameProfileBanEntry gameprofilebanentry = (GameProfileBanEntry) this.bans.get(gameprofile);

            chatmessage = new ChatMessage("multiplayer.disconnect.banned.reason", new Object[]{gameprofilebanentry.getReason()});
            if (gameprofilebanentry.getExpires() != null) {
                chatmessage.append((IChatBaseComponent) (new ChatMessage("multiplayer.disconnect.banned.expiration", new Object[]{PlayerList.BAN_DATE_FORMAT.format(gameprofilebanentry.getExpires())})));
            }

            return chatmessage;
        } else if (!this.isWhiteListed(gameprofile)) {
            return new ChatMessage("multiplayer.disconnect.not_whitelisted");
        } else if (this.ipBans.isBanned(socketaddress)) {
            IpBanEntry ipbanentry = this.ipBans.get(socketaddress);

            chatmessage = new ChatMessage("multiplayer.disconnect.banned_ip.reason", new Object[]{ipbanentry.getReason()});
            if (ipbanentry.getExpires() != null) {
                chatmessage.append((IChatBaseComponent) (new ChatMessage("multiplayer.disconnect.banned_ip.expiration", new Object[]{PlayerList.BAN_DATE_FORMAT.format(ipbanentry.getExpires())})));
            }

            return chatmessage;
        } else {
            return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameprofile) ? new ChatMessage("multiplayer.disconnect.server_full") : null;
        }
    }

    public EntityPlayer getPlayerForLogin(GameProfile gameprofile) {
        UUID uuid = EntityHuman.createPlayerUUID(gameprofile);
        List<EntityPlayer> list = Lists.newArrayList();

        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

            if (entityplayer.getUUID().equals(uuid)) {
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

        return new EntityPlayer(this.server, this.server.overworld(), gameprofile);
    }

    public EntityPlayer respawn(EntityPlayer entityplayer, boolean flag) {
        this.players.remove(entityplayer);
        entityplayer.getLevel().removePlayerImmediately(entityplayer, Entity.RemovalReason.DISCARDED);
        BlockPosition blockposition = entityplayer.getRespawnPosition();
        float f = entityplayer.getRespawnAngle();
        boolean flag1 = entityplayer.isRespawnForced();
        WorldServer worldserver = this.server.getLevel(entityplayer.getRespawnDimension());
        Optional optional;

        if (worldserver != null && blockposition != null) {
            optional = EntityHuman.findRespawnPositionAndUseSpawnBlock(worldserver, blockposition, f, flag1, flag);
        } else {
            optional = Optional.empty();
        }

        WorldServer worldserver1 = worldserver != null && optional.isPresent() ? worldserver : this.server.overworld();
        EntityPlayer entityplayer1 = new EntityPlayer(this.server, worldserver1, entityplayer.getGameProfile());

        entityplayer1.connection = entityplayer.connection;
        entityplayer1.restoreFrom(entityplayer, flag);
        entityplayer1.setId(entityplayer.getId());
        entityplayer1.setMainArm(entityplayer.getMainArm());
        Iterator iterator = entityplayer.getTags().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            entityplayer1.addTag(s);
        }

        boolean flag2 = false;

        if (optional.isPresent()) {
            IBlockData iblockdata = worldserver1.getBlockState(blockposition);
            boolean flag3 = iblockdata.is(Blocks.RESPAWN_ANCHOR);
            Vec3D vec3d = (Vec3D) optional.get();
            float f1;

            if (!iblockdata.is((Tag) TagsBlock.BEDS) && !flag3) {
                f1 = f;
            } else {
                Vec3D vec3d1 = Vec3D.atBottomCenterOf(blockposition).subtract(vec3d).normalize();

                f1 = (float) MathHelper.wrapDegrees(MathHelper.atan2(vec3d1.z, vec3d1.x) * 57.2957763671875D - 90.0D);
            }

            entityplayer1.moveTo(vec3d.x, vec3d.y, vec3d.z, f1, 0.0F);
            entityplayer1.setRespawnPosition(worldserver1.dimension(), blockposition, f, flag1, false);
            flag2 = !flag && flag3;
        } else if (blockposition != null) {
            entityplayer1.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
        }

        while (!worldserver1.noCollision((Entity) entityplayer1) && entityplayer1.getY() < (double) worldserver1.getMaxBuildHeight()) {
            entityplayer1.setPos(entityplayer1.getX(), entityplayer1.getY() + 1.0D, entityplayer1.getZ());
        }

        WorldData worlddata = entityplayer1.level.getLevelData();

        entityplayer1.connection.send(new PacketPlayOutRespawn(entityplayer1.level.dimensionType(), entityplayer1.level.dimension(), BiomeManager.obfuscateSeed(entityplayer1.getLevel().getSeed()), entityplayer1.gameMode.getGameModeForPlayer(), entityplayer1.gameMode.getPreviousGameModeForPlayer(), entityplayer1.getLevel().isDebug(), entityplayer1.getLevel().isFlat(), flag));
        entityplayer1.connection.teleport(entityplayer1.getX(), entityplayer1.getY(), entityplayer1.getZ(), entityplayer1.getYRot(), entityplayer1.getXRot());
        entityplayer1.connection.send(new PacketPlayOutSpawnPosition(worldserver1.getSharedSpawnPos(), worldserver1.getSharedSpawnAngle()));
        entityplayer1.connection.send(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        entityplayer1.connection.send(new PacketPlayOutExperience(entityplayer1.experienceProgress, entityplayer1.totalExperience, entityplayer1.experienceLevel));
        this.sendLevelInfo(entityplayer1, worldserver1);
        this.sendPlayerPermissionLevel(entityplayer1);
        worldserver1.addRespawnedPlayer(entityplayer1);
        this.players.add(entityplayer1);
        this.playersByUUID.put(entityplayer1.getUUID(), entityplayer1);
        entityplayer1.initInventoryMenu();
        entityplayer1.setHealth(entityplayer1.getHealth());
        if (flag2) {
            entityplayer1.connection.send(new PacketPlayOutNamedSoundEffect(SoundEffects.RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0F, 1.0F));
        }

        return entityplayer1;
    }

    public void sendPlayerPermissionLevel(EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getGameProfile();
        int i = this.server.getProfilePermissions(gameprofile);

        this.sendPlayerPermissionLevel(entityplayer, i);
    }

    public void tick() {
        if (++this.sendAllPlayerInfoIn > 600) {
            this.broadcastAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, this.players));
            this.sendAllPlayerInfoIn = 0;
        }

    }

    public void broadcastAll(Packet<?> packet) {
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(packet);
        }

    }

    public void broadcastAll(Packet<?> packet, ResourceKey<World> resourcekey) {
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.level.dimension() == resourcekey) {
                entityplayer.connection.send(packet);
            }
        }

    }

    public void broadcastToTeam(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.getTeam();

        if (scoreboardteambase != null) {
            Collection<String> collection = scoreboardteambase.getPlayers();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                EntityPlayer entityplayer = this.getPlayerByName(s);

                if (entityplayer != null && entityplayer != entityhuman) {
                    entityplayer.sendMessage(ichatbasecomponent, entityhuman.getUUID());
                }
            }

        }
    }

    public void broadcastToAllExceptTeam(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.getTeam();

        if (scoreboardteambase == null) {
            this.broadcastMessage(ichatbasecomponent, ChatMessageType.SYSTEM, entityhuman.getUUID());
        } else {
            for (int i = 0; i < this.players.size(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

                if (entityplayer.getTeam() != scoreboardteambase) {
                    entityplayer.sendMessage(ichatbasecomponent, entityhuman.getUUID());
                }
            }

        }
    }

    public String[] getPlayerNamesArray() {
        String[] astring = new String[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i) {
            astring[i] = ((EntityPlayer) this.players.get(i)).getGameProfile().getName();
        }

        return astring;
    }

    public GameProfileBanList getBans() {
        return this.bans;
    }

    public IpBanList getIpBans() {
        return this.ipBans;
    }

    public void op(GameProfile gameprofile) {
        this.ops.add(new OpListEntry(gameprofile, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(gameprofile)));
        EntityPlayer entityplayer = this.getPlayer(gameprofile.getId());

        if (entityplayer != null) {
            this.sendPlayerPermissionLevel(entityplayer);
        }

    }

    public void deop(GameProfile gameprofile) {
        this.ops.remove((Object) gameprofile);
        EntityPlayer entityplayer = this.getPlayer(gameprofile.getId());

        if (entityplayer != null) {
            this.sendPlayerPermissionLevel(entityplayer);
        }

    }

    private void sendPlayerPermissionLevel(EntityPlayer entityplayer, int i) {
        if (entityplayer.connection != null) {
            byte b0;

            if (i <= 0) {
                b0 = 24;
            } else if (i >= 4) {
                b0 = 28;
            } else {
                b0 = (byte) (24 + i);
            }

            entityplayer.connection.send(new PacketPlayOutEntityStatus(entityplayer, b0));
        }

        this.server.getCommands().sendCommands(entityplayer);
    }

    public boolean isWhiteListed(GameProfile gameprofile) {
        return !this.doWhiteList || this.ops.contains(gameprofile) || this.whitelist.contains(gameprofile);
    }

    public boolean isOp(GameProfile gameprofile) {
        return this.ops.contains(gameprofile) || this.server.isSingleplayerOwner(gameprofile) && this.server.getWorldData().getAllowCommands() || this.allowCheatsForAllPlayers;
    }

    @Nullable
    public EntityPlayer getPlayerByName(String s) {
        Iterator iterator = this.players.iterator();

        EntityPlayer entityplayer;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityplayer = (EntityPlayer) iterator.next();
        } while (!entityplayer.getGameProfile().getName().equalsIgnoreCase(s));

        return entityplayer;
    }

    public void broadcast(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, double d3, ResourceKey<World> resourcekey, Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

            if (entityplayer != entityhuman && entityplayer.level.dimension() == resourcekey) {
                double d4 = d0 - entityplayer.getX();
                double d5 = d1 - entityplayer.getY();
                double d6 = d2 - entityplayer.getZ();

                if (d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3) {
                    entityplayer.connection.send(packet);
                }
            }
        }

    }

    public void saveAll() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.save((EntityPlayer) this.players.get(i));
        }

    }

    public WhiteList getWhiteList() {
        return this.whitelist;
    }

    public String[] getWhiteListNames() {
        return this.whitelist.getUserList();
    }

    public OpList getOps() {
        return this.ops;
    }

    public String[] getOpNames() {
        return this.ops.getUserList();
    }

    public void reloadWhiteList() {}

    public void sendLevelInfo(EntityPlayer entityplayer, WorldServer worldserver) {
        WorldBorder worldborder = this.server.overworld().getWorldBorder();

        entityplayer.connection.send(new ClientboundInitializeBorderPacket(worldborder));
        entityplayer.connection.send(new PacketPlayOutUpdateTime(worldserver.getGameTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        entityplayer.connection.send(new PacketPlayOutSpawnPosition(worldserver.getSharedSpawnPos(), worldserver.getSharedSpawnAngle()));
        if (worldserver.isRaining()) {
            entityplayer.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.START_RAINING, 0.0F));
            entityplayer.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.RAIN_LEVEL_CHANGE, worldserver.getRainLevel(1.0F)));
            entityplayer.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.THUNDER_LEVEL_CHANGE, worldserver.getThunderLevel(1.0F)));
        }

    }

    public void sendAllPlayerInfo(EntityPlayer entityplayer) {
        entityplayer.inventoryMenu.sendAllDataToRemote();
        entityplayer.resetSentInfo();
        entityplayer.connection.send(new PacketPlayOutHeldItemSlot(entityplayer.getInventory().selected));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean isUsingWhitelist() {
        return this.doWhiteList;
    }

    public void setUsingWhiteList(boolean flag) {
        this.doWhiteList = flag;
    }

    public List<EntityPlayer> getPlayersWithAddress(String s) {
        List<EntityPlayer> list = Lists.newArrayList();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.getIpAddress().equals(s)) {
                list.add(entityplayer);
            }
        }

        return list;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getSimulationDistance() {
        return this.simulationDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Nullable
    public NBTTagCompound getSingleplayerData() {
        return null;
    }

    public void setAllowCheatsForAllPlayers(boolean flag) {
        this.allowCheatsForAllPlayers = flag;
    }

    public void removeAll() {
        for (int i = 0; i < this.players.size(); ++i) {
            ((EntityPlayer) this.players.get(i)).connection.disconnect(new ChatMessage("multiplayer.disconnect.server_shutdown"));
        }

    }

    public void broadcastMessage(IChatBaseComponent ichatbasecomponent, ChatMessageType chatmessagetype, UUID uuid) {
        this.server.sendMessage(ichatbasecomponent, uuid);
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.sendMessage(ichatbasecomponent, chatmessagetype, uuid);
        }

    }

    public void broadcastMessage(IChatBaseComponent ichatbasecomponent, Function<EntityPlayer, IChatBaseComponent> function, ChatMessageType chatmessagetype, UUID uuid) {
        this.server.sendMessage(ichatbasecomponent, uuid);
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) function.apply(entityplayer);

            if (ichatbasecomponent1 != null) {
                entityplayer.sendMessage(ichatbasecomponent1, chatmessagetype, uuid);
            }
        }

    }

    public ServerStatisticManager getPlayerStats(EntityHuman entityhuman) {
        UUID uuid = entityhuman.getUUID();
        ServerStatisticManager serverstatisticmanager = (ServerStatisticManager) this.stats.get(uuid);

        if (serverstatisticmanager == null) {
            File file = this.server.getWorldPath(SavedFile.PLAYER_STATS_DIR).toFile();
            File file1 = new File(file, uuid + ".json");

            if (!file1.exists()) {
                File file2 = new File(file, entityhuman.getName().getString() + ".json");
                Path path = file2.toPath();

                if (FileUtils.isPathNormalized(path) && FileUtils.isPathPortable(path) && path.startsWith(file.getPath()) && file2.isFile()) {
                    file2.renameTo(file1);
                }
            }

            serverstatisticmanager = new ServerStatisticManager(this.server, file1);
            this.stats.put(uuid, serverstatisticmanager);
        }

        return serverstatisticmanager;
    }

    public AdvancementDataPlayer getPlayerAdvancements(EntityPlayer entityplayer) {
        UUID uuid = entityplayer.getUUID();
        AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) this.advancements.get(uuid);

        if (advancementdataplayer == null) {
            File file = this.server.getWorldPath(SavedFile.PLAYER_ADVANCEMENTS_DIR).toFile();
            File file1 = new File(file, uuid + ".json");

            advancementdataplayer = new AdvancementDataPlayer(this.server.getFixerUpper(), this, this.server.getAdvancements(), file1, entityplayer);
            this.advancements.put(uuid, advancementdataplayer);
        }

        advancementdataplayer.setPlayer(entityplayer);
        return advancementdataplayer;
    }

    public void setViewDistance(int i) {
        this.viewDistance = i;
        this.broadcastAll(new PacketPlayOutViewDistance(i));
        Iterator iterator = this.server.getAllLevels().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver != null) {
                worldserver.getChunkSource().setViewDistance(i);
            }
        }

    }

    public void setSimulationDistance(int i) {
        this.simulationDistance = i;
        this.broadcastAll(new ClientboundSetSimulationDistancePacket(i));
        Iterator iterator = this.server.getAllLevels().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver != null) {
                worldserver.getChunkSource().setSimulationDistance(i);
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

    public boolean canBypassPlayerLimit(GameProfile gameprofile) {
        return false;
    }

    public void reloadResources() {
        Iterator iterator = this.advancements.values().iterator();

        while (iterator.hasNext()) {
            AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) iterator.next();

            advancementdataplayer.reload(this.server.getAdvancements());
        }

        this.broadcastAll(new PacketPlayOutTags(this.server.getTags().serializeToNetwork(this.registryHolder)));
        PacketPlayOutRecipeUpdate packetplayoutrecipeupdate = new PacketPlayOutRecipeUpdate(this.server.getRecipeManager().getRecipes());
        Iterator iterator1 = this.players.iterator();

        while (iterator1.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator1.next();

            entityplayer.connection.send(packetplayoutrecipeupdate);
            entityplayer.getRecipeBook().sendInitialRecipeBook(entityplayer);
        }

    }

    public boolean isAllowCheatsForAllPlayers() {
        return this.allowCheatsForAllPlayers;
    }
}
