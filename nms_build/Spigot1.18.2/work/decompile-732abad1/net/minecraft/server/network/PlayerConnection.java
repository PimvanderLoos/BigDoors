package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.logging.LogUtils;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.EnumChatFormat;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PlayerConnectionUtils;
import net.minecraft.network.protocol.game.PacketListenerPlayIn;
import net.minecraft.network.protocol.game.PacketPlayInAbilities;
import net.minecraft.network.protocol.game.PacketPlayInAdvancements;
import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayInAutoRecipe;
import net.minecraft.network.protocol.game.PacketPlayInBEdit;
import net.minecraft.network.protocol.game.PacketPlayInBeacon;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayInBoatMove;
import net.minecraft.network.protocol.game.PacketPlayInChat;
import net.minecraft.network.protocol.game.PacketPlayInClientCommand;
import net.minecraft.network.protocol.game.PacketPlayInCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayInCustomPayload;
import net.minecraft.network.protocol.game.PacketPlayInDifficultyChange;
import net.minecraft.network.protocol.game.PacketPlayInDifficultyLock;
import net.minecraft.network.protocol.game.PacketPlayInEnchantItem;
import net.minecraft.network.protocol.game.PacketPlayInEntityAction;
import net.minecraft.network.protocol.game.PacketPlayInEntityNBTQuery;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayInItemName;
import net.minecraft.network.protocol.game.PacketPlayInJigsawGenerate;
import net.minecraft.network.protocol.game.PacketPlayInKeepAlive;
import net.minecraft.network.protocol.game.PacketPlayInPickItem;
import net.minecraft.network.protocol.game.PacketPlayInRecipeDisplayed;
import net.minecraft.network.protocol.game.PacketPlayInRecipeSettings;
import net.minecraft.network.protocol.game.PacketPlayInResourcePackStatus;
import net.minecraft.network.protocol.game.PacketPlayInSetCommandBlock;
import net.minecraft.network.protocol.game.PacketPlayInSetCommandMinecart;
import net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlot;
import net.minecraft.network.protocol.game.PacketPlayInSetJigsaw;
import net.minecraft.network.protocol.game.PacketPlayInSettings;
import net.minecraft.network.protocol.game.PacketPlayInSpectate;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.network.protocol.game.PacketPlayInStruct;
import net.minecraft.network.protocol.game.PacketPlayInTabComplete;
import net.minecraft.network.protocol.game.PacketPlayInTeleportAccept;
import net.minecraft.network.protocol.game.PacketPlayInTileNBTQuery;
import net.minecraft.network.protocol.game.PacketPlayInTrSel;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayInUseItem;
import net.minecraft.network.protocol.game.PacketPlayInVehicleMove;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayOutKeepAlive;
import net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect;
import net.minecraft.network.protocol.game.PacketPlayOutNBTQuery;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutTabComplete;
import net.minecraft.network.protocol.game.PacketPlayOutVehicleMove;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.RecipeBookServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.UtilColor;
import net.minecraft.util.thread.IAsyncTaskHandler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.IJumpable;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.ContainerBeacon;
import net.minecraft.world.inventory.ContainerMerchant;
import net.minecraft.world.inventory.ContainerRecipeBook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemBucket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.CommandBlockListenerAbstract;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockCommand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class PlayerConnection implements ServerPlayerConnection, PacketListenerPlayIn {

    static final Logger LOGGER = LogUtils.getLogger();
    private static final int LATENCY_CHECK_INTERVAL = 15000;
    public final NetworkManager connection;
    private final MinecraftServer server;
    public EntityPlayer player;
    private int tickCount;
    private long keepAliveTime;
    private boolean keepAlivePending;
    private long keepAliveChallenge;
    private int chatSpamTickCount;
    private int dropSpamTickCount;
    private double firstGoodX;
    private double firstGoodY;
    private double firstGoodZ;
    private double lastGoodX;
    private double lastGoodY;
    private double lastGoodZ;
    @Nullable
    private Entity lastVehicle;
    private double vehicleFirstGoodX;
    private double vehicleFirstGoodY;
    private double vehicleFirstGoodZ;
    private double vehicleLastGoodX;
    private double vehicleLastGoodY;
    private double vehicleLastGoodZ;
    @Nullable
    private Vec3D awaitingPositionFromClient;
    private int awaitingTeleport;
    private int awaitingTeleportTime;
    private boolean clientIsFloating;
    private int aboveGroundTickCount;
    private boolean clientVehicleIsFloating;
    private int aboveGroundVehicleTickCount;
    private int receivedMovePacketCount;
    private int knownMovePacketCount;

    public PlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        this.server = minecraftserver;
        this.connection = networkmanager;
        networkmanager.setListener(this);
        this.player = entityplayer;
        entityplayer.connection = this;
        this.keepAliveTime = SystemUtils.getMillis();
        entityplayer.getTextFilter().join();
    }

    public void tick() {
        this.resetPosition();
        this.player.xo = this.player.getX();
        this.player.yo = this.player.getY();
        this.player.zo = this.player.getZ();
        this.player.doTick();
        this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.getYRot(), this.player.getXRot());
        ++this.tickCount;
        this.knownMovePacketCount = this.receivedMovePacketCount;
        if (this.clientIsFloating && !this.player.isSleeping() && !this.player.isPassenger()) {
            if (++this.aboveGroundTickCount > 80) {
                PlayerConnection.LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
                this.disconnect(new ChatMessage("multiplayer.disconnect.flying"));
                return;
            }
        } else {
            this.clientIsFloating = false;
            this.aboveGroundTickCount = 0;
        }

        this.lastVehicle = this.player.getRootVehicle();
        if (this.lastVehicle != this.player && this.lastVehicle.getControllingPassenger() == this.player) {
            this.vehicleFirstGoodX = this.lastVehicle.getX();
            this.vehicleFirstGoodY = this.lastVehicle.getY();
            this.vehicleFirstGoodZ = this.lastVehicle.getZ();
            this.vehicleLastGoodX = this.lastVehicle.getX();
            this.vehicleLastGoodY = this.lastVehicle.getY();
            this.vehicleLastGoodZ = this.lastVehicle.getZ();
            if (this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
                if (++this.aboveGroundVehicleTickCount > 80) {
                    PlayerConnection.LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
                    this.disconnect(new ChatMessage("multiplayer.disconnect.flying"));
                    return;
                }
            } else {
                this.clientVehicleIsFloating = false;
                this.aboveGroundVehicleTickCount = 0;
            }
        } else {
            this.lastVehicle = null;
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
        }

        this.server.getProfiler().push("keepAlive");
        long i = SystemUtils.getMillis();

        if (i - this.keepAliveTime >= 15000L) {
            if (this.keepAlivePending) {
                this.disconnect(new ChatMessage("disconnect.timeout"));
            } else {
                this.keepAlivePending = true;
                this.keepAliveTime = i;
                this.keepAliveChallenge = i;
                this.send(new PacketPlayOutKeepAlive(this.keepAliveChallenge));
            }
        }

        this.server.getProfiler().pop();
        if (this.chatSpamTickCount > 0) {
            --this.chatSpamTickCount;
        }

        if (this.dropSpamTickCount > 0) {
            --this.dropSpamTickCount;
        }

        if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && SystemUtils.getMillis() - this.player.getLastActionTime() > (long) (this.server.getPlayerIdleTimeout() * 1000 * 60)) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.idling"));
        }

    }

    public void resetPosition() {
        this.firstGoodX = this.player.getX();
        this.firstGoodY = this.player.getY();
        this.firstGoodZ = this.player.getZ();
        this.lastGoodX = this.player.getX();
        this.lastGoodY = this.player.getY();
        this.lastGoodZ = this.player.getZ();
    }

    @Override
    public NetworkManager getConnection() {
        return this.connection;
    }

    private boolean isSingleplayerOwner() {
        return this.server.isSingleplayerOwner(this.player.getGameProfile());
    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        this.connection.send(new PacketPlayOutKickDisconnect(ichatbasecomponent), (future) -> {
            this.connection.disconnect(ichatbasecomponent);
        });
        this.connection.setReadOnly();
        MinecraftServer minecraftserver = this.server;
        NetworkManager networkmanager = this.connection;

        Objects.requireNonNull(this.connection);
        minecraftserver.executeBlocking(networkmanager::handleDisconnection);
    }

    private <T, R> void filterTextPacket(T t0, Consumer<R> consumer, BiFunction<ITextFilter, T, CompletableFuture<R>> bifunction) {
        IAsyncTaskHandler<?> iasynctaskhandler = this.player.getLevel().getServer();
        Consumer<R> consumer1 = (object) -> {
            if (this.getConnection().isConnected()) {
                try {
                    consumer.accept(object);
                } catch (Exception exception) {
                    PlayerConnection.LOGGER.error("Failed to handle chat packet {}, suppressing error", t0, exception);
                }
            } else {
                PlayerConnection.LOGGER.debug("Ignoring packet due to disconnection");
            }

        };

        ((CompletableFuture) bifunction.apply(this.player.getTextFilter(), t0)).thenAcceptAsync(consumer1, iasynctaskhandler);
    }

    private void filterTextPacket(String s, Consumer<ITextFilter.a> consumer) {
        this.filterTextPacket(s, consumer, ITextFilter::processStreamMessage);
    }

    private void filterTextPacket(List<String> list, Consumer<List<ITextFilter.a>> consumer) {
        this.filterTextPacket(list, consumer, ITextFilter::processMessageBundle);
    }

    @Override
    public void handlePlayerInput(PacketPlayInSteerVehicle packetplayinsteervehicle) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinsteervehicle, this, this.player.getLevel());
        this.player.setPlayerInput(packetplayinsteervehicle.getXxa(), packetplayinsteervehicle.getZza(), packetplayinsteervehicle.isJumping(), packetplayinsteervehicle.isShiftKeyDown());
    }

    private static boolean containsInvalidValues(double d0, double d1, double d2, float f, float f1) {
        return Double.isNaN(d0) || Double.isNaN(d1) || Double.isNaN(d2) || !Floats.isFinite(f1) || !Floats.isFinite(f);
    }

    private static double clampHorizontal(double d0) {
        return MathHelper.clamp(d0, -3.0E7D, 3.0E7D);
    }

    private static double clampVertical(double d0) {
        return MathHelper.clamp(d0, -2.0E7D, 2.0E7D);
    }

    @Override
    public void handleMoveVehicle(PacketPlayInVehicleMove packetplayinvehiclemove) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinvehiclemove, this, this.player.getLevel());
        if (containsInvalidValues(packetplayinvehiclemove.getX(), packetplayinvehiclemove.getY(), packetplayinvehiclemove.getZ(), packetplayinvehiclemove.getYRot(), packetplayinvehiclemove.getXRot())) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_vehicle_movement"));
        } else {
            Entity entity = this.player.getRootVehicle();

            if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lastVehicle) {
                WorldServer worldserver = this.player.getLevel();
                double d0 = entity.getX();
                double d1 = entity.getY();
                double d2 = entity.getZ();
                double d3 = clampHorizontal(packetplayinvehiclemove.getX());
                double d4 = clampVertical(packetplayinvehiclemove.getY());
                double d5 = clampHorizontal(packetplayinvehiclemove.getZ());
                float f = MathHelper.wrapDegrees(packetplayinvehiclemove.getYRot());
                float f1 = MathHelper.wrapDegrees(packetplayinvehiclemove.getXRot());
                double d6 = d3 - this.vehicleFirstGoodX;
                double d7 = d4 - this.vehicleFirstGoodY;
                double d8 = d5 - this.vehicleFirstGoodZ;
                double d9 = entity.getDeltaMovement().lengthSqr();
                double d10 = d6 * d6 + d7 * d7 + d8 * d8;

                if (d10 - d9 > 100.0D && !this.isSingleplayerOwner()) {
                    PlayerConnection.LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", new Object[]{entity.getName().getString(), this.player.getName().getString(), d6, d7, d8});
                    this.connection.send(new PacketPlayOutVehicleMove(entity));
                    return;
                }

                boolean flag = worldserver.noCollision(entity, entity.getBoundingBox().deflate(0.0625D));

                d6 = d3 - this.vehicleLastGoodX;
                d7 = d4 - this.vehicleLastGoodY - 1.0E-6D;
                d8 = d5 - this.vehicleLastGoodZ;
                boolean flag1 = entity.verticalCollisionBelow;

                entity.move(EnumMoveType.PLAYER, new Vec3D(d6, d7, d8));
                double d11 = d7;

                d6 = d3 - entity.getX();
                d7 = d4 - entity.getY();
                if (d7 > -0.5D || d7 < 0.5D) {
                    d7 = 0.0D;
                }

                d8 = d5 - entity.getZ();
                d10 = d6 * d6 + d7 * d7 + d8 * d8;
                boolean flag2 = false;

                if (d10 > 0.0625D) {
                    flag2 = true;
                    PlayerConnection.LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", new Object[]{entity.getName().getString(), this.player.getName().getString(), Math.sqrt(d10)});
                }

                entity.absMoveTo(d3, d4, d5, f, f1);
                boolean flag3 = worldserver.noCollision(entity, entity.getBoundingBox().deflate(0.0625D));

                if (flag && (flag2 || !flag3)) {
                    entity.absMoveTo(d0, d1, d2, f, f1);
                    this.connection.send(new PacketPlayOutVehicleMove(entity));
                    return;
                }

                this.player.getLevel().getChunkSource().move(this.player);
                this.player.checkMovementStatistics(this.player.getX() - d0, this.player.getY() - d1, this.player.getZ() - d2);
                this.clientVehicleIsFloating = d11 >= -0.03125D && !flag1 && !this.server.isFlightAllowed() && !entity.isNoGravity() && this.noBlocksAround(entity);
                this.vehicleLastGoodX = entity.getX();
                this.vehicleLastGoodY = entity.getY();
                this.vehicleLastGoodZ = entity.getZ();
            }

        }
    }

    private boolean noBlocksAround(Entity entity) {
        return entity.level.getBlockStates(entity.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D)).allMatch(BlockBase.BlockData::isAir);
    }

    @Override
    public void handleAcceptTeleportPacket(PacketPlayInTeleportAccept packetplayinteleportaccept) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinteleportaccept, this, this.player.getLevel());
        if (packetplayinteleportaccept.getId() == this.awaitingTeleport) {
            this.player.absMoveTo(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.getYRot(), this.player.getXRot());
            this.lastGoodX = this.awaitingPositionFromClient.x;
            this.lastGoodY = this.awaitingPositionFromClient.y;
            this.lastGoodZ = this.awaitingPositionFromClient.z;
            if (this.player.isChangingDimension()) {
                this.player.hasChangedDimension();
            }

            this.awaitingPositionFromClient = null;
        }

    }

    @Override
    public void handleRecipeBookSeenRecipePacket(PacketPlayInRecipeDisplayed packetplayinrecipedisplayed) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinrecipedisplayed, this, this.player.getLevel());
        Optional optional = this.server.getRecipeManager().byKey(packetplayinrecipedisplayed.getRecipe());
        RecipeBookServer recipebookserver = this.player.getRecipeBook();

        Objects.requireNonNull(recipebookserver);
        optional.ifPresent(recipebookserver::removeHighlight);
    }

    @Override
    public void handleRecipeBookChangeSettingsPacket(PacketPlayInRecipeSettings packetplayinrecipesettings) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinrecipesettings, this, this.player.getLevel());
        this.player.getRecipeBook().setBookSetting(packetplayinrecipesettings.getBookType(), packetplayinrecipesettings.isOpen(), packetplayinrecipesettings.isFiltering());
    }

    @Override
    public void handleSeenAdvancements(PacketPlayInAdvancements packetplayinadvancements) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinadvancements, this, this.player.getLevel());
        if (packetplayinadvancements.getAction() == PacketPlayInAdvancements.Status.OPENED_TAB) {
            MinecraftKey minecraftkey = packetplayinadvancements.getTab();
            Advancement advancement = this.server.getAdvancements().getAdvancement(minecraftkey);

            if (advancement != null) {
                this.player.getAdvancements().setSelectedTab(advancement);
            }
        }

    }

    @Override
    public void handleCustomCommandSuggestions(PacketPlayInTabComplete packetplayintabcomplete) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayintabcomplete, this, this.player.getLevel());
        StringReader stringreader = new StringReader(packetplayintabcomplete.getCommand());

        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }

        ParseResults<CommandListenerWrapper> parseresults = this.server.getCommands().getDispatcher().parse(stringreader, this.player.createCommandSourceStack());

        this.server.getCommands().getDispatcher().getCompletionSuggestions(parseresults).thenAccept((suggestions) -> {
            this.connection.send(new PacketPlayOutTabComplete(packetplayintabcomplete.getId(), suggestions));
        });
    }

    @Override
    public void handleSetCommandBlock(PacketPlayInSetCommandBlock packetplayinsetcommandblock) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinsetcommandblock, this, this.player.getLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendMessage(new ChatMessage("advMode.notEnabled"), SystemUtils.NIL_UUID);
        } else if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendMessage(new ChatMessage("advMode.notAllowed"), SystemUtils.NIL_UUID);
        } else {
            CommandBlockListenerAbstract commandblocklistenerabstract = null;
            TileEntityCommand tileentitycommand = null;
            BlockPosition blockposition = packetplayinsetcommandblock.getPos();
            TileEntity tileentity = this.player.level.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                tileentitycommand = (TileEntityCommand) tileentity;
                commandblocklistenerabstract = tileentitycommand.getCommandBlock();
            }

            String s = packetplayinsetcommandblock.getCommand();
            boolean flag = packetplayinsetcommandblock.isTrackOutput();

            if (commandblocklistenerabstract != null) {
                TileEntityCommand.Type tileentitycommand_type = tileentitycommand.getMode();
                IBlockData iblockdata = this.player.level.getBlockState(blockposition);
                EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockCommand.FACING);
                IBlockData iblockdata1;

                switch (packetplayinsetcommandblock.getMode()) {
                    case SEQUENCE:
                        iblockdata1 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
                        break;
                    case AUTO:
                        iblockdata1 = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
                        break;
                    case REDSTONE:
                    default:
                        iblockdata1 = Blocks.COMMAND_BLOCK.defaultBlockState();
                }

                IBlockData iblockdata2 = (IBlockData) ((IBlockData) iblockdata1.setValue(BlockCommand.FACING, enumdirection)).setValue(BlockCommand.CONDITIONAL, packetplayinsetcommandblock.isConditional());

                if (iblockdata2 != iblockdata) {
                    this.player.level.setBlock(blockposition, iblockdata2, 2);
                    tileentity.setBlockState(iblockdata2);
                    this.player.level.getChunkAt(blockposition).setBlockEntity(tileentity);
                }

                commandblocklistenerabstract.setCommand(s);
                commandblocklistenerabstract.setTrackOutput(flag);
                if (!flag) {
                    commandblocklistenerabstract.setLastOutput((IChatBaseComponent) null);
                }

                tileentitycommand.setAutomatic(packetplayinsetcommandblock.isAutomatic());
                if (tileentitycommand_type != packetplayinsetcommandblock.getMode()) {
                    tileentitycommand.onModeSwitch();
                }

                commandblocklistenerabstract.onUpdated();
                if (!UtilColor.isNullOrEmpty(s)) {
                    this.player.sendMessage(new ChatMessage("advMode.setCommand.success", new Object[]{s}), SystemUtils.NIL_UUID);
                }
            }

        }
    }

    @Override
    public void handleSetCommandMinecart(PacketPlayInSetCommandMinecart packetplayinsetcommandminecart) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinsetcommandminecart, this, this.player.getLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendMessage(new ChatMessage("advMode.notEnabled"), SystemUtils.NIL_UUID);
        } else if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendMessage(new ChatMessage("advMode.notAllowed"), SystemUtils.NIL_UUID);
        } else {
            CommandBlockListenerAbstract commandblocklistenerabstract = packetplayinsetcommandminecart.getCommandBlock(this.player.level);

            if (commandblocklistenerabstract != null) {
                commandblocklistenerabstract.setCommand(packetplayinsetcommandminecart.getCommand());
                commandblocklistenerabstract.setTrackOutput(packetplayinsetcommandminecart.isTrackOutput());
                if (!packetplayinsetcommandminecart.isTrackOutput()) {
                    commandblocklistenerabstract.setLastOutput((IChatBaseComponent) null);
                }

                commandblocklistenerabstract.onUpdated();
                this.player.sendMessage(new ChatMessage("advMode.setCommand.success", new Object[]{packetplayinsetcommandminecart.getCommand()}), SystemUtils.NIL_UUID);
            }

        }
    }

    @Override
    public void handlePickItem(PacketPlayInPickItem packetplayinpickitem) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinpickitem, this, this.player.getLevel());
        this.player.getInventory().pickSlot(packetplayinpickitem.getSlot());
        this.player.connection.send(new PacketPlayOutSetSlot(-2, 0, this.player.getInventory().selected, this.player.getInventory().getItem(this.player.getInventory().selected)));
        this.player.connection.send(new PacketPlayOutSetSlot(-2, 0, packetplayinpickitem.getSlot(), this.player.getInventory().getItem(packetplayinpickitem.getSlot())));
        this.player.connection.send(new PacketPlayOutHeldItemSlot(this.player.getInventory().selected));
    }

    @Override
    public void handleRenameItem(PacketPlayInItemName packetplayinitemname) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinitemname, this, this.player.getLevel());
        if (this.player.containerMenu instanceof ContainerAnvil) {
            ContainerAnvil containeranvil = (ContainerAnvil) this.player.containerMenu;
            String s = SharedConstants.filterText(packetplayinitemname.getName());

            if (s.length() <= 50) {
                containeranvil.setItemName(s);
            }
        }

    }

    @Override
    public void handleSetBeaconPacket(PacketPlayInBeacon packetplayinbeacon) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinbeacon, this, this.player.getLevel());
        if (this.player.containerMenu instanceof ContainerBeacon) {
            ((ContainerBeacon) this.player.containerMenu).updateEffects(packetplayinbeacon.getPrimary(), packetplayinbeacon.getSecondary());
        }

    }

    @Override
    public void handleSetStructureBlock(PacketPlayInStruct packetplayinstruct) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinstruct, this, this.player.getLevel());
        if (this.player.canUseGameMasterBlocks()) {
            BlockPosition blockposition = packetplayinstruct.getPos();
            IBlockData iblockdata = this.player.level.getBlockState(blockposition);
            TileEntity tileentity = this.player.level.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;

                tileentitystructure.setMode(packetplayinstruct.getMode());
                tileentitystructure.setStructureName(packetplayinstruct.getName());
                tileentitystructure.setStructurePos(packetplayinstruct.getOffset());
                tileentitystructure.setStructureSize(packetplayinstruct.getSize());
                tileentitystructure.setMirror(packetplayinstruct.getMirror());
                tileentitystructure.setRotation(packetplayinstruct.getRotation());
                tileentitystructure.setMetaData(packetplayinstruct.getData());
                tileentitystructure.setIgnoreEntities(packetplayinstruct.isIgnoreEntities());
                tileentitystructure.setShowAir(packetplayinstruct.isShowAir());
                tileentitystructure.setShowBoundingBox(packetplayinstruct.isShowBoundingBox());
                tileentitystructure.setIntegrity(packetplayinstruct.getIntegrity());
                tileentitystructure.setSeed(packetplayinstruct.getSeed());
                if (tileentitystructure.hasStructureName()) {
                    String s = tileentitystructure.getStructureName();

                    if (packetplayinstruct.getUpdateType() == TileEntityStructure.UpdateType.SAVE_AREA) {
                        if (tileentitystructure.saveStructure()) {
                            this.player.displayClientMessage(new ChatMessage("structure_block.save_success", new Object[]{s}), false);
                        } else {
                            this.player.displayClientMessage(new ChatMessage("structure_block.save_failure", new Object[]{s}), false);
                        }
                    } else if (packetplayinstruct.getUpdateType() == TileEntityStructure.UpdateType.LOAD_AREA) {
                        if (!tileentitystructure.isStructureLoadable()) {
                            this.player.displayClientMessage(new ChatMessage("structure_block.load_not_found", new Object[]{s}), false);
                        } else if (tileentitystructure.loadStructure(this.player.getLevel())) {
                            this.player.displayClientMessage(new ChatMessage("structure_block.load_success", new Object[]{s}), false);
                        } else {
                            this.player.displayClientMessage(new ChatMessage("structure_block.load_prepare", new Object[]{s}), false);
                        }
                    } else if (packetplayinstruct.getUpdateType() == TileEntityStructure.UpdateType.SCAN_AREA) {
                        if (tileentitystructure.detectSize()) {
                            this.player.displayClientMessage(new ChatMessage("structure_block.size_success", new Object[]{s}), false);
                        } else {
                            this.player.displayClientMessage(new ChatMessage("structure_block.size_failure"), false);
                        }
                    }
                } else {
                    this.player.displayClientMessage(new ChatMessage("structure_block.invalid_structure_name", new Object[]{packetplayinstruct.getName()}), false);
                }

                tileentitystructure.setChanged();
                this.player.level.sendBlockUpdated(blockposition, iblockdata, iblockdata, 3);
            }

        }
    }

    @Override
    public void handleSetJigsawBlock(PacketPlayInSetJigsaw packetplayinsetjigsaw) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinsetjigsaw, this, this.player.getLevel());
        if (this.player.canUseGameMasterBlocks()) {
            BlockPosition blockposition = packetplayinsetjigsaw.getPos();
            IBlockData iblockdata = this.player.level.getBlockState(blockposition);
            TileEntity tileentity = this.player.level.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityJigsaw) {
                TileEntityJigsaw tileentityjigsaw = (TileEntityJigsaw) tileentity;

                tileentityjigsaw.setName(packetplayinsetjigsaw.getName());
                tileentityjigsaw.setTarget(packetplayinsetjigsaw.getTarget());
                tileentityjigsaw.setPool(packetplayinsetjigsaw.getPool());
                tileentityjigsaw.setFinalState(packetplayinsetjigsaw.getFinalState());
                tileentityjigsaw.setJoint(packetplayinsetjigsaw.getJoint());
                tileentityjigsaw.setChanged();
                this.player.level.sendBlockUpdated(blockposition, iblockdata, iblockdata, 3);
            }

        }
    }

    @Override
    public void handleJigsawGenerate(PacketPlayInJigsawGenerate packetplayinjigsawgenerate) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinjigsawgenerate, this, this.player.getLevel());
        if (this.player.canUseGameMasterBlocks()) {
            BlockPosition blockposition = packetplayinjigsawgenerate.getPos();
            TileEntity tileentity = this.player.level.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityJigsaw) {
                TileEntityJigsaw tileentityjigsaw = (TileEntityJigsaw) tileentity;

                tileentityjigsaw.generate(this.player.getLevel(), packetplayinjigsawgenerate.levels(), packetplayinjigsawgenerate.keepJigsaws());
            }

        }
    }

    @Override
    public void handleSelectTrade(PacketPlayInTrSel packetplayintrsel) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayintrsel, this, this.player.getLevel());
        int i = packetplayintrsel.getItem();
        Container container = this.player.containerMenu;

        if (container instanceof ContainerMerchant) {
            ContainerMerchant containermerchant = (ContainerMerchant) container;

            containermerchant.setSelectionHint(i);
            containermerchant.tryMoveItems(i);
        }

    }

    @Override
    public void handleEditBook(PacketPlayInBEdit packetplayinbedit) {
        int i = packetplayinbedit.getSlot();

        if (PlayerInventory.isHotbarSlot(i) || i == 40) {
            List<String> list = Lists.newArrayList();
            Optional<String> optional = packetplayinbedit.getTitle();

            Objects.requireNonNull(list);
            optional.ifPresent(list::add);
            Stream stream = packetplayinbedit.getPages().stream().limit(100L);

            Objects.requireNonNull(list);
            stream.forEach(list::add);
            this.filterTextPacket((List) list, optional.isPresent() ? (list1) -> {
                this.signBook((ITextFilter.a) list1.get(0), list1.subList(1, list1.size()), i);
            } : (list1) -> {
                this.updateBookContents(list1, i);
            });
        }
    }

    private void updateBookContents(List<ITextFilter.a> list, int i) {
        ItemStack itemstack = this.player.getInventory().getItem(i);

        if (itemstack.is(Items.WRITABLE_BOOK)) {
            this.updateBookPages(list, UnaryOperator.identity(), itemstack);
        }
    }

    private void signBook(ITextFilter.a itextfilter_a, List<ITextFilter.a> list, int i) {
        ItemStack itemstack = this.player.getInventory().getItem(i);

        if (itemstack.is(Items.WRITABLE_BOOK)) {
            ItemStack itemstack1 = new ItemStack(Items.WRITTEN_BOOK);
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null) {
                itemstack1.setTag(nbttagcompound.copy());
            }

            itemstack1.addTagElement("author", NBTTagString.valueOf(this.player.getName().getString()));
            if (this.player.isTextFilteringEnabled()) {
                itemstack1.addTagElement("title", NBTTagString.valueOf(itextfilter_a.getFiltered()));
            } else {
                itemstack1.addTagElement("filtered_title", NBTTagString.valueOf(itextfilter_a.getFiltered()));
                itemstack1.addTagElement("title", NBTTagString.valueOf(itextfilter_a.getRaw()));
            }

            this.updateBookPages(list, (s) -> {
                return IChatBaseComponent.ChatSerializer.toJson(new ChatComponentText(s));
            }, itemstack1);
            this.player.getInventory().setItem(i, itemstack1);
        }
    }

    private void updateBookPages(List<ITextFilter.a> list, UnaryOperator<String> unaryoperator, ItemStack itemstack) {
        NBTTagList nbttaglist = new NBTTagList();

        if (this.player.isTextFilteringEnabled()) {
            Stream stream = list.stream().map((itextfilter_a) -> {
                return NBTTagString.valueOf((String) unaryoperator.apply(itextfilter_a.getFiltered()));
            });

            Objects.requireNonNull(nbttaglist);
            stream.forEach(nbttaglist::add);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            int i = 0;

            for (int j = list.size(); i < j; ++i) {
                ITextFilter.a itextfilter_a = (ITextFilter.a) list.get(i);
                String s = itextfilter_a.getRaw();

                nbttaglist.add(NBTTagString.valueOf((String) unaryoperator.apply(s)));
                String s1 = itextfilter_a.getFiltered();

                if (!s.equals(s1)) {
                    nbttagcompound.putString(String.valueOf(i), (String) unaryoperator.apply(s1));
                }
            }

            if (!nbttagcompound.isEmpty()) {
                itemstack.addTagElement("filtered_pages", nbttagcompound);
            }
        }

        itemstack.addTagElement("pages", nbttaglist);
    }

    @Override
    public void handleEntityTagQuery(PacketPlayInEntityNBTQuery packetplayinentitynbtquery) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinentitynbtquery, this, this.player.getLevel());
        if (this.player.hasPermissions(2)) {
            Entity entity = this.player.getLevel().getEntity(packetplayinentitynbtquery.getEntityId());

            if (entity != null) {
                NBTTagCompound nbttagcompound = entity.saveWithoutId(new NBTTagCompound());

                this.player.connection.send(new PacketPlayOutNBTQuery(packetplayinentitynbtquery.getTransactionId(), nbttagcompound));
            }

        }
    }

    @Override
    public void handleBlockEntityTagQuery(PacketPlayInTileNBTQuery packetplayintilenbtquery) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayintilenbtquery, this, this.player.getLevel());
        if (this.player.hasPermissions(2)) {
            TileEntity tileentity = this.player.getLevel().getBlockEntity(packetplayintilenbtquery.getPos());
            NBTTagCompound nbttagcompound = tileentity != null ? tileentity.saveWithoutMetadata() : null;

            this.player.connection.send(new PacketPlayOutNBTQuery(packetplayintilenbtquery.getTransactionId(), nbttagcompound));
        }
    }

    @Override
    public void handleMovePlayer(PacketPlayInFlying packetplayinflying) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinflying, this, this.player.getLevel());
        if (containsInvalidValues(packetplayinflying.getX(0.0D), packetplayinflying.getY(0.0D), packetplayinflying.getZ(0.0D), packetplayinflying.getYRot(0.0F), packetplayinflying.getXRot(0.0F))) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_player_movement"));
        } else {
            WorldServer worldserver = this.player.getLevel();

            if (!this.player.wonGame) {
                if (this.tickCount == 0) {
                    this.resetPosition();
                }

                if (this.awaitingPositionFromClient != null) {
                    if (this.tickCount - this.awaitingTeleportTime > 20) {
                        this.awaitingTeleportTime = this.tickCount;
                        this.teleport(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.getYRot(), this.player.getXRot());
                    }

                } else {
                    this.awaitingTeleportTime = this.tickCount;
                    double d0 = clampHorizontal(packetplayinflying.getX(this.player.getX()));
                    double d1 = clampVertical(packetplayinflying.getY(this.player.getY()));
                    double d2 = clampHorizontal(packetplayinflying.getZ(this.player.getZ()));
                    float f = MathHelper.wrapDegrees(packetplayinflying.getYRot(this.player.getYRot()));
                    float f1 = MathHelper.wrapDegrees(packetplayinflying.getXRot(this.player.getXRot()));

                    if (this.player.isPassenger()) {
                        this.player.absMoveTo(this.player.getX(), this.player.getY(), this.player.getZ(), f, f1);
                        this.player.getLevel().getChunkSource().move(this.player);
                    } else {
                        double d3 = this.player.getX();
                        double d4 = this.player.getY();
                        double d5 = this.player.getZ();
                        double d6 = this.player.getY();
                        double d7 = d0 - this.firstGoodX;
                        double d8 = d1 - this.firstGoodY;
                        double d9 = d2 - this.firstGoodZ;
                        double d10 = this.player.getDeltaMovement().lengthSqr();
                        double d11 = d7 * d7 + d8 * d8 + d9 * d9;

                        if (this.player.isSleeping()) {
                            if (d11 > 1.0D) {
                                this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), f, f1);
                            }

                        } else {
                            ++this.receivedMovePacketCount;
                            int i = this.receivedMovePacketCount - this.knownMovePacketCount;

                            if (i > 5) {
                                PlayerConnection.LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), i);
                                i = 1;
                            }

                            if (!this.player.isChangingDimension() && (!this.player.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                                float f2 = this.player.isFallFlying() ? 300.0F : 100.0F;

                                if (d11 - d10 > (double) (f2 * (float) i) && !this.isSingleplayerOwner()) {
                                    PlayerConnection.LOGGER.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName().getString(), d7, d8, d9});
                                    this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
                                    return;
                                }
                            }

                            AxisAlignedBB axisalignedbb = this.player.getBoundingBox();

                            d7 = d0 - this.lastGoodX;
                            d8 = d1 - this.lastGoodY;
                            d9 = d2 - this.lastGoodZ;
                            boolean flag = d8 > 0.0D;

                            if (this.player.isOnGround() && !packetplayinflying.isOnGround() && flag) {
                                this.player.jumpFromGround();
                            }

                            boolean flag1 = this.player.verticalCollisionBelow;

                            this.player.move(EnumMoveType.PLAYER, new Vec3D(d7, d8, d9));
                            double d12 = d8;

                            d7 = d0 - this.player.getX();
                            d8 = d1 - this.player.getY();
                            if (d8 > -0.5D || d8 < 0.5D) {
                                d8 = 0.0D;
                            }

                            d9 = d2 - this.player.getZ();
                            d11 = d7 * d7 + d8 * d8 + d9 * d9;
                            boolean flag2 = false;

                            if (!this.player.isChangingDimension() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != EnumGamemode.SPECTATOR) {
                                flag2 = true;
                                PlayerConnection.LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                            }

                            this.player.absMoveTo(d0, d1, d2, f, f1);
                            if (!this.player.noPhysics && !this.player.isSleeping() && (flag2 && worldserver.noCollision(this.player, axisalignedbb) || this.isPlayerCollidingWithAnythingNew(worldserver, axisalignedbb))) {
                                this.teleport(d3, d4, d5, f, f1);
                            } else {
                                this.clientIsFloating = d12 >= -0.03125D && !flag1 && this.player.gameMode.getGameModeForPlayer() != EnumGamemode.SPECTATOR && !this.server.isFlightAllowed() && !this.player.getAbilities().mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isFallFlying() && !this.player.isAutoSpinAttack() && this.noBlocksAround(this.player);
                                this.player.getLevel().getChunkSource().move(this.player);
                                this.player.doCheckFallDamage(this.player.getY() - d6, packetplayinflying.isOnGround());
                                this.player.setOnGround(packetplayinflying.isOnGround());
                                if (flag) {
                                    this.player.resetFallDistance();
                                }

                                this.player.checkMovementStatistics(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5);
                                this.lastGoodX = this.player.getX();
                                this.lastGoodY = this.player.getY();
                                this.lastGoodZ = this.player.getZ();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isPlayerCollidingWithAnythingNew(IWorldReader iworldreader, AxisAlignedBB axisalignedbb) {
        Iterable<VoxelShape> iterable = iworldreader.getCollisions(this.player, this.player.getBoundingBox().deflate(9.999999747378752E-6D));
        VoxelShape voxelshape = VoxelShapes.create(axisalignedbb.deflate(9.999999747378752E-6D));
        Iterator iterator = iterable.iterator();

        VoxelShape voxelshape1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            voxelshape1 = (VoxelShape) iterator.next();
        } while (VoxelShapes.joinIsNotEmpty(voxelshape1, voxelshape, OperatorBoolean.AND));

        return true;
    }

    public void dismount(double d0, double d1, double d2, float f, float f1) {
        this.teleport(d0, d1, d2, f, f1, Collections.emptySet(), true);
    }

    public void teleport(double d0, double d1, double d2, float f, float f1) {
        this.teleport(d0, d1, d2, f, f1, Collections.emptySet(), false);
    }

    public void teleport(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        this.teleport(d0, d1, d2, f, f1, set, false);
    }

    public void teleport(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, boolean flag) {
        double d3 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X) ? this.player.getX() : 0.0D;
        double d4 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y) ? this.player.getY() : 0.0D;
        double d5 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z) ? this.player.getZ() : 0.0D;
        float f2 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT) ? this.player.getYRot() : 0.0F;
        float f3 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT) ? this.player.getXRot() : 0.0F;

        this.awaitingPositionFromClient = new Vec3D(d0, d1, d2);
        if (++this.awaitingTeleport == Integer.MAX_VALUE) {
            this.awaitingTeleport = 0;
        }

        this.awaitingTeleportTime = this.tickCount;
        this.player.absMoveTo(d0, d1, d2, f, f1);
        this.player.connection.send(new PacketPlayOutPosition(d0 - d3, d1 - d4, d2 - d5, f - f2, f1 - f3, set, this.awaitingTeleport, flag));
    }

    @Override
    public void handlePlayerAction(PacketPlayInBlockDig packetplayinblockdig) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinblockdig, this, this.player.getLevel());
        BlockPosition blockposition = packetplayinblockdig.getPos();

        this.player.resetLastActionTime();
        PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype = packetplayinblockdig.getAction();

        switch (packetplayinblockdig_enumplayerdigtype) {
            case SWAP_ITEM_WITH_OFFHAND:
                if (!this.player.isSpectator()) {
                    ItemStack itemstack = this.player.getItemInHand(EnumHand.OFF_HAND);

                    this.player.setItemInHand(EnumHand.OFF_HAND, this.player.getItemInHand(EnumHand.MAIN_HAND));
                    this.player.setItemInHand(EnumHand.MAIN_HAND, itemstack);
                    this.player.stopUsingItem();
                }

                return;
            case DROP_ITEM:
                if (!this.player.isSpectator()) {
                    this.player.drop(false);
                }

                return;
            case DROP_ALL_ITEMS:
                if (!this.player.isSpectator()) {
                    this.player.drop(true);
                }

                return;
            case RELEASE_USE_ITEM:
                this.player.releaseUsingItem();
                return;
            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                this.player.gameMode.handleBlockBreakAction(blockposition, packetplayinblockdig_enumplayerdigtype, packetplayinblockdig.getDirection(), this.player.level.getMaxBuildHeight());
                return;
            default:
                throw new IllegalArgumentException("Invalid player action");
        }
    }

    private static boolean wasBlockPlacementAttempt(EntityPlayer entityplayer, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        } else {
            Item item = itemstack.getItem();

            return (item instanceof ItemBlock || item instanceof ItemBucket) && !entityplayer.getCooldowns().isOnCooldown(item);
        }
    }

    @Override
    public void handleUseItemOn(PacketPlayInUseItem packetplayinuseitem) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinuseitem, this, this.player.getLevel());
        WorldServer worldserver = this.player.getLevel();
        EnumHand enumhand = packetplayinuseitem.getHand();
        ItemStack itemstack = this.player.getItemInHand(enumhand);
        MovingObjectPositionBlock movingobjectpositionblock = packetplayinuseitem.getHitResult();
        Vec3D vec3d = movingobjectpositionblock.getLocation();
        BlockPosition blockposition = movingobjectpositionblock.getBlockPos();
        Vec3D vec3d1 = vec3d.subtract(Vec3D.atCenterOf(blockposition));

        if (this.player.level.getServer() != null && this.player.chunkPosition().getChessboardDistance(new ChunkCoordIntPair(blockposition)) < this.player.level.getServer().getPlayerList().getViewDistance()) {
            double d0 = 1.0000001D;

            if (Math.abs(vec3d1.x()) < 1.0000001D && Math.abs(vec3d1.y()) < 1.0000001D && Math.abs(vec3d1.z()) < 1.0000001D) {
                EnumDirection enumdirection = movingobjectpositionblock.getDirection();

                this.player.resetLastActionTime();
                int i = this.player.level.getMaxBuildHeight();

                if (blockposition.getY() < i) {
                    if (this.awaitingPositionFromClient == null && this.player.distanceToSqr((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) < 64.0D && worldserver.mayInteract(this.player, blockposition)) {
                        EnumInteractionResult enuminteractionresult = this.player.gameMode.useItemOn(this.player, worldserver, itemstack, enumhand, movingobjectpositionblock);

                        if (enumdirection == EnumDirection.UP && !enuminteractionresult.consumesAction() && blockposition.getY() >= i - 1 && wasBlockPlacementAttempt(this.player, itemstack)) {
                            IChatMutableComponent ichatmutablecomponent = (new ChatMessage("build.tooHigh", new Object[]{i - 1})).withStyle(EnumChatFormat.RED);

                            this.player.sendMessage(ichatmutablecomponent, ChatMessageType.GAME_INFO, SystemUtils.NIL_UUID);
                        } else if (enuminteractionresult.shouldSwing()) {
                            this.player.swing(enumhand, true);
                        }
                    }
                } else {
                    IChatMutableComponent ichatmutablecomponent1 = (new ChatMessage("build.tooHigh", new Object[]{i - 1})).withStyle(EnumChatFormat.RED);

                    this.player.sendMessage(ichatmutablecomponent1, ChatMessageType.GAME_INFO, SystemUtils.NIL_UUID);
                }

                this.player.connection.send(new PacketPlayOutBlockChange(worldserver, blockposition));
                this.player.connection.send(new PacketPlayOutBlockChange(worldserver, blockposition.relative(enumdirection)));
            } else {
                PlayerConnection.LOGGER.warn("Ignoring UseItemOnPacket from {}: Location {} too far away from hit block {}.", new Object[]{this.player.getGameProfile().getName(), vec3d, blockposition});
            }
        } else {
            PlayerConnection.LOGGER.warn("Ignoring UseItemOnPacket from {}: hit position {} too far away from player {}.", new Object[]{this.player.getGameProfile().getName(), blockposition, this.player.blockPosition()});
        }
    }

    @Override
    public void handleUseItem(PacketPlayInBlockPlace packetplayinblockplace) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinblockplace, this, this.player.getLevel());
        WorldServer worldserver = this.player.getLevel();
        EnumHand enumhand = packetplayinblockplace.getHand();
        ItemStack itemstack = this.player.getItemInHand(enumhand);

        this.player.resetLastActionTime();
        if (!itemstack.isEmpty()) {
            EnumInteractionResult enuminteractionresult = this.player.gameMode.useItem(this.player, worldserver, itemstack, enumhand);

            if (enuminteractionresult.shouldSwing()) {
                this.player.swing(enumhand, true);
            }

        }
    }

    @Override
    public void handleTeleportToEntityPacket(PacketPlayInSpectate packetplayinspectate) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinspectate, this, this.player.getLevel());
        if (this.player.isSpectator()) {
            Iterator iterator = this.server.getAllLevels().iterator();

            while (iterator.hasNext()) {
                WorldServer worldserver = (WorldServer) iterator.next();
                Entity entity = packetplayinspectate.getEntity(worldserver);

                if (entity != null) {
                    this.player.teleportTo(worldserver, entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                    return;
                }
            }
        }

    }

    @Override
    public void handleResourcePackResponse(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinresourcepackstatus, this, this.player.getLevel());
        if (packetplayinresourcepackstatus.getAction() == PacketPlayInResourcePackStatus.EnumResourcePackStatus.DECLINED && this.server.isResourcePackRequired()) {
            PlayerConnection.LOGGER.info("Disconnecting {} due to resource pack rejection", this.player.getName());
            this.disconnect(new ChatMessage("multiplayer.requiredTexturePrompt.disconnect"));
        }

    }

    @Override
    public void handlePaddleBoat(PacketPlayInBoatMove packetplayinboatmove) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinboatmove, this, this.player.getLevel());
        Entity entity = this.player.getVehicle();

        if (entity instanceof EntityBoat) {
            ((EntityBoat) entity).setPaddleState(packetplayinboatmove.getLeft(), packetplayinboatmove.getRight());
        }

    }

    @Override
    public void handlePong(ServerboundPongPacket serverboundpongpacket) {}

    @Override
    public void onDisconnect(IChatBaseComponent ichatbasecomponent) {
        PlayerConnection.LOGGER.info("{} lost connection: {}", this.player.getName().getString(), ichatbasecomponent.getString());
        this.server.invalidateStatus();
        this.server.getPlayerList().broadcastMessage((new ChatMessage("multiplayer.player.left", new Object[]{this.player.getDisplayName()})).withStyle(EnumChatFormat.YELLOW), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
        this.player.disconnect();
        this.server.getPlayerList().remove(this.player);
        this.player.getTextFilter().leave();
        if (this.isSingleplayerOwner()) {
            PlayerConnection.LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.halt(false);
        }

    }

    @Override
    public void send(Packet<?> packet) {
        this.send(packet, (GenericFutureListener) null);
    }

    public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        try {
            this.connection.send(packet, genericfuturelistener);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Sending packet");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Packet being sent");

            crashreportsystemdetails.setDetail("Packet class", () -> {
                return packet.getClass().getCanonicalName();
            });
            throw new ReportedException(crashreport);
        }
    }

    @Override
    public void handleSetCarriedItem(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinhelditemslot, this, this.player.getLevel());
        if (packetplayinhelditemslot.getSlot() >= 0 && packetplayinhelditemslot.getSlot() < PlayerInventory.getSelectionSize()) {
            if (this.player.getInventory().selected != packetplayinhelditemslot.getSlot() && this.player.getUsedItemHand() == EnumHand.MAIN_HAND) {
                this.player.stopUsingItem();
            }

            this.player.getInventory().selected = packetplayinhelditemslot.getSlot();
            this.player.resetLastActionTime();
        } else {
            PlayerConnection.LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
        }
    }

    @Override
    public void handleChat(PacketPlayInChat packetplayinchat) {
        String s = StringUtils.normalizeSpace(packetplayinchat.getMessage());

        for (int i = 0; i < s.length(); ++i) {
            if (!SharedConstants.isAllowedChatCharacter(s.charAt(i))) {
                this.disconnect(new ChatMessage("multiplayer.disconnect.illegal_characters"));
                return;
            }
        }

        if (s.startsWith("/")) {
            PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinchat, this, this.player.getLevel());
            this.handleChat(ITextFilter.a.passThrough(s));
        } else {
            this.filterTextPacket(s, this::handleChat);
        }

    }

    private void handleChat(ITextFilter.a itextfilter_a) {
        if (this.player.getChatVisibility() == EnumChatVisibility.HIDDEN) {
            this.send(new PacketPlayOutChat((new ChatMessage("chat.disabled.options")).withStyle(EnumChatFormat.RED), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID));
        } else {
            this.player.resetLastActionTime();
            String s = itextfilter_a.getRaw();

            if (s.startsWith("/")) {
                this.handleCommand(s);
            } else {
                String s1 = itextfilter_a.getFiltered();
                ChatMessage chatmessage = s1.isEmpty() ? null : new ChatMessage("chat.type.text", new Object[]{this.player.getDisplayName(), s1});
                ChatMessage chatmessage1 = new ChatMessage("chat.type.text", new Object[]{this.player.getDisplayName(), s});

                this.server.getPlayerList().broadcastMessage(chatmessage1, (entityplayer) -> {
                    return this.player.shouldFilterMessageTo(entityplayer) ? chatmessage : chatmessage1;
                }, ChatMessageType.CHAT, this.player.getUUID());
            }

            this.chatSpamTickCount += 20;
            if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
                this.disconnect(new ChatMessage("disconnect.spam"));
            }

        }
    }

    private void handleCommand(String s) {
        this.server.getCommands().performCommand(this.player.createCommandSourceStack(), s);
    }

    @Override
    public void handleAnimate(PacketPlayInArmAnimation packetplayinarmanimation) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinarmanimation, this, this.player.getLevel());
        this.player.resetLastActionTime();
        this.player.swing(packetplayinarmanimation.getHand());
    }

    @Override
    public void handlePlayerCommand(PacketPlayInEntityAction packetplayinentityaction) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinentityaction, this, this.player.getLevel());
        this.player.resetLastActionTime();
        IJumpable ijumpable;

        switch (packetplayinentityaction.getAction()) {
            case PRESS_SHIFT_KEY:
                this.player.setShiftKeyDown(true);
                break;
            case RELEASE_SHIFT_KEY:
                this.player.setShiftKeyDown(false);
                break;
            case START_SPRINTING:
                this.player.setSprinting(true);
                break;
            case STOP_SPRINTING:
                this.player.setSprinting(false);
                break;
            case STOP_SLEEPING:
                if (this.player.isSleeping()) {
                    this.player.stopSleepInBed(false, true);
                    this.awaitingPositionFromClient = this.player.position();
                }
                break;
            case START_RIDING_JUMP:
                if (this.player.getVehicle() instanceof IJumpable) {
                    ijumpable = (IJumpable) this.player.getVehicle();
                    int i = packetplayinentityaction.getData();

                    if (ijumpable.canJump() && i > 0) {
                        ijumpable.handleStartJump(i);
                    }
                }
                break;
            case STOP_RIDING_JUMP:
                if (this.player.getVehicle() instanceof IJumpable) {
                    ijumpable = (IJumpable) this.player.getVehicle();
                    ijumpable.handleStopJump();
                }
                break;
            case OPEN_INVENTORY:
                if (this.player.getVehicle() instanceof EntityHorseAbstract) {
                    ((EntityHorseAbstract) this.player.getVehicle()).openInventory(this.player);
                }
                break;
            case START_FALL_FLYING:
                if (!this.player.tryToStartFallFlying()) {
                    this.player.stopFallFlying();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid client command!");
        }

    }

    @Override
    public void handleInteract(PacketPlayInUseEntity packetplayinuseentity) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinuseentity, this, this.player.getLevel());
        WorldServer worldserver = this.player.getLevel();
        final Entity entity = packetplayinuseentity.getTarget(worldserver);

        this.player.resetLastActionTime();
        this.player.setShiftKeyDown(packetplayinuseentity.isUsingSecondaryAction());
        if (entity != null) {
            if (!worldserver.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                return;
            }

            double d0 = 36.0D;

            if (this.player.distanceToSqr(entity) < 36.0D) {
                packetplayinuseentity.dispatch(new PacketPlayInUseEntity.c() {
                    private void performInteraction(EnumHand enumhand, PlayerConnection.a playerconnection_a) {
                        ItemStack itemstack = PlayerConnection.this.player.getItemInHand(enumhand).copy();
                        EnumInteractionResult enuminteractionresult = playerconnection_a.run(PlayerConnection.this.player, entity, enumhand);

                        if (enuminteractionresult.consumesAction()) {
                            CriterionTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(PlayerConnection.this.player, itemstack, entity);
                            if (enuminteractionresult.shouldSwing()) {
                                PlayerConnection.this.player.swing(enumhand, true);
                            }
                        }

                    }

                    @Override
                    public void onInteraction(EnumHand enumhand) {
                        this.performInteraction(enumhand, EntityHuman::interactOn);
                    }

                    @Override
                    public void onInteraction(EnumHand enumhand, Vec3D vec3d) {
                        this.performInteraction(enumhand, (entityplayer, entity1, enumhand1) -> {
                            return entity1.interactAt(entityplayer, vec3d, enumhand1);
                        });
                    }

                    @Override
                    public void onAttack() {
                        if (!(entity instanceof EntityItem) && !(entity instanceof EntityExperienceOrb) && !(entity instanceof EntityArrow) && entity != PlayerConnection.this.player) {
                            PlayerConnection.this.player.attack(entity);
                        } else {
                            PlayerConnection.this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_entity_attacked"));
                            PlayerConnection.LOGGER.warn("Player {} tried to attack an invalid entity", PlayerConnection.this.player.getName().getString());
                        }
                    }
                });
            }
        }

    }

    @Override
    public void handleClientCommand(PacketPlayInClientCommand packetplayinclientcommand) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinclientcommand, this, this.player.getLevel());
        this.player.resetLastActionTime();
        PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand = packetplayinclientcommand.getAction();

        switch (packetplayinclientcommand_enumclientcommand) {
            case PERFORM_RESPAWN:
                if (this.player.wonGame) {
                    this.player.wonGame = false;
                    this.player = this.server.getPlayerList().respawn(this.player, true);
                    CriterionTriggers.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
                } else {
                    if (this.player.getHealth() > 0.0F) {
                        return;
                    }

                    this.player = this.server.getPlayerList().respawn(this.player, false);
                    if (this.server.isHardcore()) {
                        this.player.setGameMode(EnumGamemode.SPECTATOR);
                        ((GameRules.GameRuleBoolean) this.player.getLevel().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS)).set(false, this.server);
                    }
                }
                break;
            case REQUEST_STATS:
                this.player.getStats().sendStats(this.player);
        }

    }

    @Override
    public void handleContainerClose(PacketPlayInCloseWindow packetplayinclosewindow) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinclosewindow, this, this.player.getLevel());
        this.player.doCloseContainer();
    }

    @Override
    public void handleContainerClick(PacketPlayInWindowClick packetplayinwindowclick) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinwindowclick, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == packetplayinwindowclick.getContainerId()) {
            if (this.player.isSpectator()) {
                this.player.containerMenu.sendAllDataToRemote();
            } else {
                int i = packetplayinwindowclick.getSlotNum();

                if (!this.player.containerMenu.isValidSlotIndex(i)) {
                    PlayerConnection.LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", new Object[]{this.player.getName(), i, this.player.containerMenu.slots.size()});
                } else {
                    boolean flag = packetplayinwindowclick.getStateId() != this.player.containerMenu.getStateId();

                    this.player.containerMenu.suppressRemoteUpdates();
                    this.player.containerMenu.clicked(i, packetplayinwindowclick.getButtonNum(), packetplayinwindowclick.getClickType(), this.player);
                    ObjectIterator objectiterator = Int2ObjectMaps.fastIterable(packetplayinwindowclick.getChangedSlots()).iterator();

                    while (objectiterator.hasNext()) {
                        Entry<ItemStack> entry = (Entry) objectiterator.next();

                        this.player.containerMenu.setRemoteSlotNoCopy(entry.getIntKey(), (ItemStack) entry.getValue());
                    }

                    this.player.containerMenu.setRemoteCarried(packetplayinwindowclick.getCarriedItem());
                    this.player.containerMenu.resumeRemoteUpdates();
                    if (flag) {
                        this.player.containerMenu.broadcastFullState();
                    } else {
                        this.player.containerMenu.broadcastChanges();
                    }

                }
            }
        }
    }

    @Override
    public void handlePlaceRecipe(PacketPlayInAutoRecipe packetplayinautorecipe) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinautorecipe, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (!this.player.isSpectator() && this.player.containerMenu.containerId == packetplayinautorecipe.getContainerId() && this.player.containerMenu instanceof ContainerRecipeBook) {
            this.server.getRecipeManager().byKey(packetplayinautorecipe.getRecipe()).ifPresent((irecipe) -> {
                ((ContainerRecipeBook) this.player.containerMenu).handlePlacement(packetplayinautorecipe.isShiftDown(), irecipe, this.player);
            });
        }
    }

    @Override
    public void handleContainerButtonClick(PacketPlayInEnchantItem packetplayinenchantitem) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinenchantitem, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == packetplayinenchantitem.getContainerId() && !this.player.isSpectator()) {
            boolean flag = this.player.containerMenu.clickMenuButton(this.player, packetplayinenchantitem.getButtonId());

            if (flag) {
                this.player.containerMenu.broadcastChanges();
            }
        }

    }

    @Override
    public void handleSetCreativeModeSlot(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinsetcreativeslot, this, this.player.getLevel());
        if (this.player.gameMode.isCreative()) {
            boolean flag = packetplayinsetcreativeslot.getSlotNum() < 0;
            ItemStack itemstack = packetplayinsetcreativeslot.getItem();
            NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

            if (!itemstack.isEmpty() && nbttagcompound != null && nbttagcompound.contains("x") && nbttagcompound.contains("y") && nbttagcompound.contains("z")) {
                BlockPosition blockposition = TileEntity.getPosFromTag(nbttagcompound);
                TileEntity tileentity = this.player.level.getBlockEntity(blockposition);

                if (tileentity != null) {
                    tileentity.saveToItem(itemstack);
                }
            }

            boolean flag1 = packetplayinsetcreativeslot.getSlotNum() >= 1 && packetplayinsetcreativeslot.getSlotNum() <= 45;
            boolean flag2 = itemstack.isEmpty() || itemstack.getDamageValue() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();

            if (flag1 && flag2) {
                this.player.inventoryMenu.getSlot(packetplayinsetcreativeslot.getSlotNum()).set(itemstack);
                this.player.inventoryMenu.broadcastChanges();
            } else if (flag && flag2 && this.dropSpamTickCount < 200) {
                this.dropSpamTickCount += 20;
                this.player.drop(itemstack, true);
            }
        }

    }

    @Override
    public void handleSignUpdate(PacketPlayInUpdateSign packetplayinupdatesign) {
        List<String> list = (List) Stream.of(packetplayinupdatesign.getLines()).map(EnumChatFormat::stripFormatting).collect(Collectors.toList());

        this.filterTextPacket(list, (list1) -> {
            this.updateSignText(packetplayinupdatesign, list1);
        });
    }

    private void updateSignText(PacketPlayInUpdateSign packetplayinupdatesign, List<ITextFilter.a> list) {
        this.player.resetLastActionTime();
        WorldServer worldserver = this.player.getLevel();
        BlockPosition blockposition = packetplayinupdatesign.getPos();

        if (worldserver.hasChunkAt(blockposition)) {
            IBlockData iblockdata = worldserver.getBlockState(blockposition);
            TileEntity tileentity = worldserver.getBlockEntity(blockposition);

            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }

            TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.isEditable() || !this.player.getUUID().equals(tileentitysign.getPlayerWhoMayEdit())) {
                PlayerConnection.LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getName().getString());
                return;
            }

            for (int i = 0; i < list.size(); ++i) {
                ITextFilter.a itextfilter_a = (ITextFilter.a) list.get(i);

                if (this.player.isTextFilteringEnabled()) {
                    tileentitysign.setMessage(i, new ChatComponentText(itextfilter_a.getFiltered()));
                } else {
                    tileentitysign.setMessage(i, new ChatComponentText(itextfilter_a.getRaw()), new ChatComponentText(itextfilter_a.getFiltered()));
                }
            }

            tileentitysign.setChanged();
            worldserver.sendBlockUpdated(blockposition, iblockdata, iblockdata, 3);
        }

    }

    @Override
    public void handleKeepAlive(PacketPlayInKeepAlive packetplayinkeepalive) {
        if (this.keepAlivePending && packetplayinkeepalive.getId() == this.keepAliveChallenge) {
            int i = (int) (SystemUtils.getMillis() - this.keepAliveTime);

            this.player.latency = (this.player.latency * 3 + i) / 4;
            this.keepAlivePending = false;
        } else if (!this.isSingleplayerOwner()) {
            this.disconnect(new ChatMessage("disconnect.timeout"));
        }

    }

    @Override
    public void handlePlayerAbilities(PacketPlayInAbilities packetplayinabilities) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinabilities, this, this.player.getLevel());
        this.player.getAbilities().flying = packetplayinabilities.isFlying() && this.player.getAbilities().mayfly;
    }

    @Override
    public void handleClientInformation(PacketPlayInSettings packetplayinsettings) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayinsettings, this, this.player.getLevel());
        this.player.updateOptions(packetplayinsettings);
    }

    @Override
    public void handleCustomPayload(PacketPlayInCustomPayload packetplayincustompayload) {}

    @Override
    public void handleChangeDifficulty(PacketPlayInDifficultyChange packetplayindifficultychange) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayindifficultychange, this, this.player.getLevel());
        if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
            this.server.setDifficulty(packetplayindifficultychange.getDifficulty(), false);
        }
    }

    @Override
    public void handleLockDifficulty(PacketPlayInDifficultyLock packetplayindifficultylock) {
        PlayerConnectionUtils.ensureRunningOnSameThread(packetplayindifficultylock, this, this.player.getLevel());
        if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
            this.server.setDifficultyLocked(packetplayindifficultylock.isLocked());
        }
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @FunctionalInterface
    private interface a {

        EnumInteractionResult run(EntityPlayer entityplayer, Entity entity, EnumHand enumhand);
    }
}
