package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
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
import net.minecraft.nbt.NBTBase;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerConnection implements ServerPlayerConnection, PacketListenerPlayIn {

    static final Logger LOGGER = LogManager.getLogger();
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
        networkmanager.setPacketListener(this);
        this.player = entityplayer;
        entityplayer.connection = this;
        entityplayer.Q().a();
    }

    public void tick() {
        this.syncPosition();
        this.player.xo = this.player.locX();
        this.player.yo = this.player.locY();
        this.player.zo = this.player.locZ();
        this.player.playerTick();
        this.player.setLocation(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.getYRot(), this.player.getXRot());
        ++this.tickCount;
        this.knownMovePacketCount = this.receivedMovePacketCount;
        if (this.clientIsFloating && !this.player.isSleeping()) {
            if (++this.aboveGroundTickCount > 80) {
                PlayerConnection.LOGGER.warn("{} was kicked for floating too long!", this.player.getDisplayName().getString());
                this.disconnect(new ChatMessage("multiplayer.disconnect.flying"));
                return;
            }
        } else {
            this.clientIsFloating = false;
            this.aboveGroundTickCount = 0;
        }

        this.lastVehicle = this.player.getRootVehicle();
        if (this.lastVehicle != this.player && this.lastVehicle.getRidingPassenger() == this.player) {
            this.vehicleFirstGoodX = this.lastVehicle.locX();
            this.vehicleFirstGoodY = this.lastVehicle.locY();
            this.vehicleFirstGoodZ = this.lastVehicle.locZ();
            this.vehicleLastGoodX = this.lastVehicle.locX();
            this.vehicleLastGoodY = this.lastVehicle.locY();
            this.vehicleLastGoodZ = this.lastVehicle.locZ();
            if (this.clientVehicleIsFloating && this.player.getRootVehicle().getRidingPassenger() == this.player) {
                if (++this.aboveGroundVehicleTickCount > 80) {
                    PlayerConnection.LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getDisplayName().getString());
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

        this.server.getMethodProfiler().enter("keepAlive");
        long i = SystemUtils.getMonotonicMillis();

        if (i - this.keepAliveTime >= 15000L) {
            if (this.keepAlivePending) {
                this.disconnect(new ChatMessage("disconnect.timeout"));
            } else {
                this.keepAlivePending = true;
                this.keepAliveTime = i;
                this.keepAliveChallenge = i;
                this.sendPacket(new PacketPlayOutKeepAlive(this.keepAliveChallenge));
            }
        }

        this.server.getMethodProfiler().exit();
        if (this.chatSpamTickCount > 0) {
            --this.chatSpamTickCount;
        }

        if (this.dropSpamTickCount > 0) {
            --this.dropSpamTickCount;
        }

        if (this.player.F() > 0L && this.server.getIdleTimeout() > 0 && SystemUtils.getMonotonicMillis() - this.player.F() > (long) (this.server.getIdleTimeout() * 1000 * 60)) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.idling"));
        }

    }

    public void syncPosition() {
        this.firstGoodX = this.player.locX();
        this.firstGoodY = this.player.locY();
        this.firstGoodZ = this.player.locZ();
        this.lastGoodX = this.player.locX();
        this.lastGoodY = this.player.locY();
        this.lastGoodZ = this.player.locZ();
    }

    @Override
    public NetworkManager a() {
        return this.connection;
    }

    private boolean isExemptPlayer() {
        return this.server.a(this.player.getProfile());
    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        this.connection.sendPacket(new PacketPlayOutKickDisconnect(ichatbasecomponent), (future) -> {
            this.connection.close(ichatbasecomponent);
        });
        this.connection.stopReading();
        MinecraftServer minecraftserver = this.server;
        NetworkManager networkmanager = this.connection;

        Objects.requireNonNull(this.connection);
        minecraftserver.executeSync(networkmanager::handleDisconnection);
    }

    private <T, R> void a(T t0, Consumer<R> consumer, BiFunction<ITextFilter, T, CompletableFuture<R>> bifunction) {
        IAsyncTaskHandler<?> iasynctaskhandler = this.player.getWorldServer().getMinecraftServer();
        Consumer<R> consumer1 = (object) -> {
            if (this.a().isConnected()) {
                consumer.accept(object);
            } else {
                PlayerConnection.LOGGER.debug("Ignoring packet due to disconnection");
            }

        };

        ((CompletableFuture) bifunction.apply(this.player.Q(), t0)).thenAcceptAsync(consumer1, iasynctaskhandler);
    }

    private void a(String s, Consumer<ITextFilter.a> consumer) {
        this.a((Object) s, consumer, ITextFilter::a);
    }

    private void a(List<String> list, Consumer<List<ITextFilter.a>> consumer) {
        this.a((Object) list, consumer, ITextFilter::a);
    }

    @Override
    public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsteervehicle, this, this.player.getWorldServer());
        this.player.a(packetplayinsteervehicle.b(), packetplayinsteervehicle.c(), packetplayinsteervehicle.d(), packetplayinsteervehicle.e());
    }

    private static boolean c(double d0, double d1, double d2, float f, float f1) {
        return Double.isNaN(d0) || Double.isNaN(d1) || Double.isNaN(d2) || !Floats.isFinite(f1) || !Floats.isFinite(f);
    }

    private static double a(double d0) {
        return MathHelper.a(d0, -3.0E7D, 3.0E7D);
    }

    private static double b(double d0) {
        return MathHelper.a(d0, -2.0E7D, 2.0E7D);
    }

    @Override
    public void a(PacketPlayInVehicleMove packetplayinvehiclemove) {
        PlayerConnectionUtils.ensureMainThread(packetplayinvehiclemove, this, this.player.getWorldServer());
        if (c(packetplayinvehiclemove.getX(), packetplayinvehiclemove.getY(), packetplayinvehiclemove.getZ(), packetplayinvehiclemove.getYaw(), packetplayinvehiclemove.getPitch())) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_vehicle_movement"));
        } else {
            Entity entity = this.player.getRootVehicle();

            if (entity != this.player && entity.getRidingPassenger() == this.player && entity == this.lastVehicle) {
                WorldServer worldserver = this.player.getWorldServer();
                double d0 = entity.locX();
                double d1 = entity.locY();
                double d2 = entity.locZ();
                double d3 = a(packetplayinvehiclemove.getX());
                double d4 = b(packetplayinvehiclemove.getY());
                double d5 = a(packetplayinvehiclemove.getZ());
                float f = MathHelper.g(packetplayinvehiclemove.getYaw());
                float f1 = MathHelper.g(packetplayinvehiclemove.getPitch());
                double d6 = d3 - this.vehicleFirstGoodX;
                double d7 = d4 - this.vehicleFirstGoodY;
                double d8 = d5 - this.vehicleFirstGoodZ;
                double d9 = entity.getMot().g();
                double d10 = d6 * d6 + d7 * d7 + d8 * d8;

                if (d10 - d9 > 100.0D && !this.isExemptPlayer()) {
                    PlayerConnection.LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getDisplayName().getString(), this.player.getDisplayName().getString(), d6, d7, d8);
                    this.connection.sendPacket(new PacketPlayOutVehicleMove(entity));
                    return;
                }

                boolean flag = worldserver.getCubes(entity, entity.getBoundingBox().shrink(0.0625D));

                d6 = d3 - this.vehicleLastGoodX;
                d7 = d4 - this.vehicleLastGoodY - 1.0E-6D;
                d8 = d5 - this.vehicleLastGoodZ;
                entity.move(EnumMoveType.PLAYER, new Vec3D(d6, d7, d8));
                double d11 = d7;

                d6 = d3 - entity.locX();
                d7 = d4 - entity.locY();
                if (d7 > -0.5D || d7 < 0.5D) {
                    d7 = 0.0D;
                }

                d8 = d5 - entity.locZ();
                d10 = d6 * d6 + d7 * d7 + d8 * d8;
                boolean flag1 = false;

                if (d10 > 0.0625D) {
                    flag1 = true;
                    PlayerConnection.LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getDisplayName().getString(), this.player.getDisplayName().getString(), Math.sqrt(d10));
                }

                entity.setLocation(d3, d4, d5, f, f1);
                boolean flag2 = worldserver.getCubes(entity, entity.getBoundingBox().shrink(0.0625D));

                if (flag && (flag1 || !flag2)) {
                    entity.setLocation(d0, d1, d2, f, f1);
                    this.connection.sendPacket(new PacketPlayOutVehicleMove(entity));
                    return;
                }

                this.player.getWorldServer().getChunkProvider().movePlayer(this.player);
                this.player.checkMovement(this.player.locX() - d0, this.player.locY() - d1, this.player.locZ() - d2);
                this.clientVehicleIsFloating = d11 >= -0.03125D && !this.server.getAllowFlight() && this.a(entity);
                this.vehicleLastGoodX = entity.locX();
                this.vehicleLastGoodY = entity.locY();
                this.vehicleLastGoodZ = entity.locZ();
            }

        }
    }

    private boolean a(Entity entity) {
        return entity.level.a(entity.getBoundingBox().g(0.0625D).b(0.0D, -0.55D, 0.0D)).allMatch(BlockBase.BlockData::isAir);
    }

    @Override
    public void a(PacketPlayInTeleportAccept packetplayinteleportaccept) {
        PlayerConnectionUtils.ensureMainThread(packetplayinteleportaccept, this, this.player.getWorldServer());
        if (packetplayinteleportaccept.b() == this.awaitingTeleport) {
            this.player.setLocation(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.getYRot(), this.player.getXRot());
            this.lastGoodX = this.awaitingPositionFromClient.x;
            this.lastGoodY = this.awaitingPositionFromClient.y;
            this.lastGoodZ = this.awaitingPositionFromClient.z;
            if (this.player.H()) {
                this.player.I();
            }

            this.awaitingPositionFromClient = null;
        }

    }

    @Override
    public void a(PacketPlayInRecipeDisplayed packetplayinrecipedisplayed) {
        PlayerConnectionUtils.ensureMainThread(packetplayinrecipedisplayed, this, this.player.getWorldServer());
        Optional optional = this.server.getCraftingManager().getRecipe(packetplayinrecipedisplayed.b());
        RecipeBookServer recipebookserver = this.player.getRecipeBook();

        Objects.requireNonNull(recipebookserver);
        optional.ifPresent(recipebookserver::e);
    }

    @Override
    public void a(PacketPlayInRecipeSettings packetplayinrecipesettings) {
        PlayerConnectionUtils.ensureMainThread(packetplayinrecipesettings, this, this.player.getWorldServer());
        this.player.getRecipeBook().a(packetplayinrecipesettings.b(), packetplayinrecipesettings.c(), packetplayinrecipesettings.d());
    }

    @Override
    public void a(PacketPlayInAdvancements packetplayinadvancements) {
        PlayerConnectionUtils.ensureMainThread(packetplayinadvancements, this, this.player.getWorldServer());
        if (packetplayinadvancements.c() == PacketPlayInAdvancements.Status.OPENED_TAB) {
            MinecraftKey minecraftkey = packetplayinadvancements.d();
            Advancement advancement = this.server.getAdvancementData().a(minecraftkey);

            if (advancement != null) {
                this.player.getAdvancementData().a(advancement);
            }
        }

    }

    @Override
    public void a(PacketPlayInTabComplete packetplayintabcomplete) {
        PlayerConnectionUtils.ensureMainThread(packetplayintabcomplete, this, this.player.getWorldServer());
        StringReader stringreader = new StringReader(packetplayintabcomplete.c());

        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }

        ParseResults<CommandListenerWrapper> parseresults = this.server.getCommandDispatcher().a().parse(stringreader, this.player.getCommandListener());

        this.server.getCommandDispatcher().a().getCompletionSuggestions(parseresults).thenAccept((suggestions) -> {
            this.connection.sendPacket(new PacketPlayOutTabComplete(packetplayintabcomplete.b(), suggestions));
        });
    }

    @Override
    public void a(PacketPlayInSetCommandBlock packetplayinsetcommandblock) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetcommandblock, this, this.player.getWorldServer());
        if (!this.server.getEnableCommandBlock()) {
            this.player.sendMessage(new ChatMessage("advMode.notEnabled"), SystemUtils.NIL_UUID);
        } else if (!this.player.isCreativeAndOp()) {
            this.player.sendMessage(new ChatMessage("advMode.notAllowed"), SystemUtils.NIL_UUID);
        } else {
            CommandBlockListenerAbstract commandblocklistenerabstract = null;
            TileEntityCommand tileentitycommand = null;
            BlockPosition blockposition = packetplayinsetcommandblock.b();
            TileEntity tileentity = this.player.level.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                tileentitycommand = (TileEntityCommand) tileentity;
                commandblocklistenerabstract = tileentitycommand.getCommandBlock();
            }

            String s = packetplayinsetcommandblock.c();
            boolean flag = packetplayinsetcommandblock.d();

            if (commandblocklistenerabstract != null) {
                TileEntityCommand.Type tileentitycommand_type = tileentitycommand.t();
                IBlockData iblockdata = this.player.level.getType(blockposition);
                EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockCommand.FACING);
                IBlockData iblockdata1;

                switch (packetplayinsetcommandblock.g()) {
                    case SEQUENCE:
                        iblockdata1 = Blocks.CHAIN_COMMAND_BLOCK.getBlockData();
                        break;
                    case AUTO:
                        iblockdata1 = Blocks.REPEATING_COMMAND_BLOCK.getBlockData();
                        break;
                    case REDSTONE:
                    default:
                        iblockdata1 = Blocks.COMMAND_BLOCK.getBlockData();
                }

                IBlockData iblockdata2 = (IBlockData) ((IBlockData) iblockdata1.set(BlockCommand.FACING, enumdirection)).set(BlockCommand.CONDITIONAL, packetplayinsetcommandblock.e());

                if (iblockdata2 != iblockdata) {
                    this.player.level.setTypeAndData(blockposition, iblockdata2, 2);
                    tileentity.b(iblockdata2);
                    this.player.level.getChunkAtWorldCoords(blockposition).setTileEntity(tileentity);
                }

                commandblocklistenerabstract.setCommand(s);
                commandblocklistenerabstract.a(flag);
                if (!flag) {
                    commandblocklistenerabstract.b((IChatBaseComponent) null);
                }

                tileentitycommand.b(packetplayinsetcommandblock.f());
                if (tileentitycommand_type != packetplayinsetcommandblock.g()) {
                    tileentitycommand.h();
                }

                commandblocklistenerabstract.f();
                if (!UtilColor.b(s)) {
                    this.player.sendMessage(new ChatMessage("advMode.setCommand.success", new Object[]{s}), SystemUtils.NIL_UUID);
                }
            }

        }
    }

    @Override
    public void a(PacketPlayInSetCommandMinecart packetplayinsetcommandminecart) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetcommandminecart, this, this.player.getWorldServer());
        if (!this.server.getEnableCommandBlock()) {
            this.player.sendMessage(new ChatMessage("advMode.notEnabled"), SystemUtils.NIL_UUID);
        } else if (!this.player.isCreativeAndOp()) {
            this.player.sendMessage(new ChatMessage("advMode.notAllowed"), SystemUtils.NIL_UUID);
        } else {
            CommandBlockListenerAbstract commandblocklistenerabstract = packetplayinsetcommandminecart.a(this.player.level);

            if (commandblocklistenerabstract != null) {
                commandblocklistenerabstract.setCommand(packetplayinsetcommandminecart.b());
                commandblocklistenerabstract.a(packetplayinsetcommandminecart.c());
                if (!packetplayinsetcommandminecart.c()) {
                    commandblocklistenerabstract.b((IChatBaseComponent) null);
                }

                commandblocklistenerabstract.f();
                this.player.sendMessage(new ChatMessage("advMode.setCommand.success", new Object[]{packetplayinsetcommandminecart.b()}), SystemUtils.NIL_UUID);
            }

        }
    }

    @Override
    public void a(PacketPlayInPickItem packetplayinpickitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinpickitem, this, this.player.getWorldServer());
        this.player.getInventory().c(packetplayinpickitem.b());
        this.player.connection.sendPacket(new PacketPlayOutSetSlot(-2, 0, this.player.getInventory().selected, this.player.getInventory().getItem(this.player.getInventory().selected)));
        this.player.connection.sendPacket(new PacketPlayOutSetSlot(-2, 0, packetplayinpickitem.b(), this.player.getInventory().getItem(packetplayinpickitem.b())));
        this.player.connection.sendPacket(new PacketPlayOutHeldItemSlot(this.player.getInventory().selected));
    }

    @Override
    public void a(PacketPlayInItemName packetplayinitemname) {
        PlayerConnectionUtils.ensureMainThread(packetplayinitemname, this, this.player.getWorldServer());
        if (this.player.containerMenu instanceof ContainerAnvil) {
            ContainerAnvil containeranvil = (ContainerAnvil) this.player.containerMenu;
            String s = SharedConstants.a(packetplayinitemname.b());

            if (s.length() <= 50) {
                containeranvil.a(s);
            }
        }

    }

    @Override
    public void a(PacketPlayInBeacon packetplayinbeacon) {
        PlayerConnectionUtils.ensureMainThread(packetplayinbeacon, this, this.player.getWorldServer());
        if (this.player.containerMenu instanceof ContainerBeacon) {
            ((ContainerBeacon) this.player.containerMenu).c(packetplayinbeacon.b(), packetplayinbeacon.c());
        }

    }

    @Override
    public void a(PacketPlayInStruct packetplayinstruct) {
        PlayerConnectionUtils.ensureMainThread(packetplayinstruct, this, this.player.getWorldServer());
        if (this.player.isCreativeAndOp()) {
            BlockPosition blockposition = packetplayinstruct.b();
            IBlockData iblockdata = this.player.level.getType(blockposition);
            TileEntity tileentity = this.player.level.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;

                tileentitystructure.setUsageMode(packetplayinstruct.d());
                tileentitystructure.setStructureName(packetplayinstruct.e());
                tileentitystructure.a(packetplayinstruct.f());
                tileentitystructure.a(packetplayinstruct.g());
                tileentitystructure.a(packetplayinstruct.h());
                tileentitystructure.a(packetplayinstruct.i());
                tileentitystructure.b(packetplayinstruct.j());
                tileentitystructure.a(packetplayinstruct.k());
                tileentitystructure.d(packetplayinstruct.l());
                tileentitystructure.e(packetplayinstruct.m());
                tileentitystructure.a(packetplayinstruct.n());
                tileentitystructure.a(packetplayinstruct.o());
                if (tileentitystructure.g()) {
                    String s = tileentitystructure.getStructureName();

                    if (packetplayinstruct.c() == TileEntityStructure.UpdateType.SAVE_AREA) {
                        if (tileentitystructure.z()) {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.save_success", new Object[]{s})), false);
                        } else {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.save_failure", new Object[]{s})), false);
                        }
                    } else if (packetplayinstruct.c() == TileEntityStructure.UpdateType.LOAD_AREA) {
                        if (!tileentitystructure.B()) {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.load_not_found", new Object[]{s})), false);
                        } else if (tileentitystructure.a(this.player.getWorldServer())) {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.load_success", new Object[]{s})), false);
                        } else {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.load_prepare", new Object[]{s})), false);
                        }
                    } else if (packetplayinstruct.c() == TileEntityStructure.UpdateType.SCAN_AREA) {
                        if (tileentitystructure.y()) {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.size_success", new Object[]{s})), false);
                        } else {
                            this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.size_failure")), false);
                        }
                    }
                } else {
                    this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.invalid_structure_name", new Object[]{packetplayinstruct.e()})), false);
                }

                tileentitystructure.update();
                this.player.level.notify(blockposition, iblockdata, iblockdata, 3);
            }

        }
    }

    @Override
    public void a(PacketPlayInSetJigsaw packetplayinsetjigsaw) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetjigsaw, this, this.player.getWorldServer());
        if (this.player.isCreativeAndOp()) {
            BlockPosition blockposition = packetplayinsetjigsaw.b();
            IBlockData iblockdata = this.player.level.getType(blockposition);
            TileEntity tileentity = this.player.level.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityJigsaw) {
                TileEntityJigsaw tileentityjigsaw = (TileEntityJigsaw) tileentity;

                tileentityjigsaw.a(packetplayinsetjigsaw.c());
                tileentityjigsaw.b(packetplayinsetjigsaw.d());
                tileentityjigsaw.c(packetplayinsetjigsaw.e());
                tileentityjigsaw.a(packetplayinsetjigsaw.f());
                tileentityjigsaw.a(packetplayinsetjigsaw.g());
                tileentityjigsaw.update();
                this.player.level.notify(blockposition, iblockdata, iblockdata, 3);
            }

        }
    }

    @Override
    public void a(PacketPlayInJigsawGenerate packetplayinjigsawgenerate) {
        PlayerConnectionUtils.ensureMainThread(packetplayinjigsawgenerate, this, this.player.getWorldServer());
        if (this.player.isCreativeAndOp()) {
            BlockPosition blockposition = packetplayinjigsawgenerate.b();
            TileEntity tileentity = this.player.level.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityJigsaw) {
                TileEntityJigsaw tileentityjigsaw = (TileEntityJigsaw) tileentity;

                tileentityjigsaw.a(this.player.getWorldServer(), packetplayinjigsawgenerate.c(), packetplayinjigsawgenerate.d());
            }

        }
    }

    @Override
    public void a(PacketPlayInTrSel packetplayintrsel) {
        PlayerConnectionUtils.ensureMainThread(packetplayintrsel, this, this.player.getWorldServer());
        int i = packetplayintrsel.b();
        Container container = this.player.containerMenu;

        if (container instanceof ContainerMerchant) {
            ContainerMerchant containermerchant = (ContainerMerchant) container;

            containermerchant.d(i);
            containermerchant.g(i);
        }

    }

    @Override
    public void a(PacketPlayInBEdit packetplayinbedit) {
        int i = packetplayinbedit.d();

        if (PlayerInventory.d(i) || i == 40) {
            List<String> list = Lists.newArrayList();
            Optional<String> optional = packetplayinbedit.c();

            Objects.requireNonNull(list);
            optional.ifPresent(list::add);
            Stream stream = packetplayinbedit.b().stream().limit(100L);

            Objects.requireNonNull(list);
            stream.forEach(list::add);
            this.a((List) list, optional.isPresent() ? (list1) -> {
                this.a((ITextFilter.a) list1.get(0), list1.subList(1, list1.size()), i);
            } : (list1) -> {
                this.a(list1, i);
            });
        }
    }

    private void a(List<ITextFilter.a> list, int i) {
        ItemStack itemstack = this.player.getInventory().getItem(i);

        if (itemstack.a(Items.WRITABLE_BOOK)) {
            this.a(list, UnaryOperator.identity(), itemstack);
        }
    }

    private void a(ITextFilter.a itextfilter_a, List<ITextFilter.a> list, int i) {
        ItemStack itemstack = this.player.getInventory().getItem(i);

        if (itemstack.a(Items.WRITABLE_BOOK)) {
            ItemStack itemstack1 = new ItemStack(Items.WRITTEN_BOOK);
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null) {
                itemstack1.setTag(nbttagcompound.clone());
            }

            itemstack1.a("author", (NBTBase) NBTTagString.a(this.player.getDisplayName().getString()));
            if (this.player.R()) {
                itemstack1.a("title", (NBTBase) NBTTagString.a(itextfilter_a.b()));
            } else {
                itemstack1.a("filtered_title", (NBTBase) NBTTagString.a(itextfilter_a.b()));
                itemstack1.a("title", (NBTBase) NBTTagString.a(itextfilter_a.a()));
            }

            this.a(list, (s) -> {
                return IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) (new ChatComponentText(s)));
            }, itemstack1);
            this.player.getInventory().setItem(i, itemstack1);
        }
    }

    private void a(List<ITextFilter.a> list, UnaryOperator<String> unaryoperator, ItemStack itemstack) {
        NBTTagList nbttaglist = new NBTTagList();

        if (this.player.R()) {
            Stream stream = list.stream().map((itextfilter_a) -> {
                return NBTTagString.a((String) unaryoperator.apply(itextfilter_a.b()));
            });

            Objects.requireNonNull(nbttaglist);
            stream.forEach(nbttaglist::add);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            int i = 0;

            for (int j = list.size(); i < j; ++i) {
                ITextFilter.a itextfilter_a = (ITextFilter.a) list.get(i);
                String s = itextfilter_a.a();

                nbttaglist.add(NBTTagString.a((String) unaryoperator.apply(s)));
                String s1 = itextfilter_a.b();

                if (!s.equals(s1)) {
                    nbttagcompound.setString(String.valueOf(i), (String) unaryoperator.apply(s1));
                }
            }

            if (!nbttagcompound.isEmpty()) {
                itemstack.a("filtered_pages", (NBTBase) nbttagcompound);
            }
        }

        itemstack.a("pages", (NBTBase) nbttaglist);
    }

    @Override
    public void a(PacketPlayInEntityNBTQuery packetplayinentitynbtquery) {
        PlayerConnectionUtils.ensureMainThread(packetplayinentitynbtquery, this, this.player.getWorldServer());
        if (this.player.l(2)) {
            Entity entity = this.player.getWorldServer().getEntity(packetplayinentitynbtquery.c());

            if (entity != null) {
                NBTTagCompound nbttagcompound = entity.save(new NBTTagCompound());

                this.player.connection.sendPacket(new PacketPlayOutNBTQuery(packetplayinentitynbtquery.b(), nbttagcompound));
            }

        }
    }

    @Override
    public void a(PacketPlayInTileNBTQuery packetplayintilenbtquery) {
        PlayerConnectionUtils.ensureMainThread(packetplayintilenbtquery, this, this.player.getWorldServer());
        if (this.player.l(2)) {
            TileEntity tileentity = this.player.getWorldServer().getTileEntity(packetplayintilenbtquery.c());
            NBTTagCompound nbttagcompound = tileentity != null ? tileentity.save(new NBTTagCompound()) : null;

            this.player.connection.sendPacket(new PacketPlayOutNBTQuery(packetplayintilenbtquery.b(), nbttagcompound));
        }
    }

    @Override
    public void a(PacketPlayInFlying packetplayinflying) {
        PlayerConnectionUtils.ensureMainThread(packetplayinflying, this, this.player.getWorldServer());
        if (c(packetplayinflying.a(0.0D), packetplayinflying.b(0.0D), packetplayinflying.c(0.0D), packetplayinflying.a(0.0F), packetplayinflying.b(0.0F))) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_player_movement"));
        } else {
            WorldServer worldserver = this.player.getWorldServer();

            if (!this.player.wonGame) {
                if (this.tickCount == 0) {
                    this.syncPosition();
                }

                if (this.awaitingPositionFromClient != null) {
                    if (this.tickCount - this.awaitingTeleportTime > 20) {
                        this.awaitingTeleportTime = this.tickCount;
                        this.b(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.getYRot(), this.player.getXRot());
                    }

                } else {
                    this.awaitingTeleportTime = this.tickCount;
                    double d0 = a(packetplayinflying.a(this.player.locX()));
                    double d1 = b(packetplayinflying.b(this.player.locY()));
                    double d2 = a(packetplayinflying.c(this.player.locZ()));
                    float f = MathHelper.g(packetplayinflying.a(this.player.getYRot()));
                    float f1 = MathHelper.g(packetplayinflying.b(this.player.getXRot()));

                    if (this.player.isPassenger()) {
                        this.player.setLocation(this.player.locX(), this.player.locY(), this.player.locZ(), f, f1);
                        this.player.getWorldServer().getChunkProvider().movePlayer(this.player);
                    } else {
                        double d3 = this.player.locX();
                        double d4 = this.player.locY();
                        double d5 = this.player.locZ();
                        double d6 = this.player.locY();
                        double d7 = d0 - this.firstGoodX;
                        double d8 = d1 - this.firstGoodY;
                        double d9 = d2 - this.firstGoodZ;
                        double d10 = this.player.getMot().g();
                        double d11 = d7 * d7 + d8 * d8 + d9 * d9;

                        if (this.player.isSleeping()) {
                            if (d11 > 1.0D) {
                                this.b(this.player.locX(), this.player.locY(), this.player.locZ(), f, f1);
                            }

                        } else {
                            ++this.receivedMovePacketCount;
                            int i = this.receivedMovePacketCount - this.knownMovePacketCount;

                            if (i > 5) {
                                PlayerConnection.LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getDisplayName().getString(), i);
                                i = 1;
                            }

                            if (!this.player.H() && (!this.player.getWorldServer().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isGliding())) {
                                float f2 = this.player.isGliding() ? 300.0F : 100.0F;

                                if (d11 - d10 > (double) (f2 * (float) i) && !this.isExemptPlayer()) {
                                    PlayerConnection.LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getDisplayName().getString(), d7, d8, d9);
                                    this.b(this.player.locX(), this.player.locY(), this.player.locZ(), this.player.getYRot(), this.player.getXRot());
                                    return;
                                }
                            }

                            AxisAlignedBB axisalignedbb = this.player.getBoundingBox();

                            d7 = d0 - this.lastGoodX;
                            d8 = d1 - this.lastGoodY;
                            d9 = d2 - this.lastGoodZ;
                            boolean flag = d8 > 0.0D;

                            if (this.player.isOnGround() && !packetplayinflying.b() && flag) {
                                this.player.jump();
                            }

                            this.player.move(EnumMoveType.PLAYER, new Vec3D(d7, d8, d9));
                            double d12 = d8;

                            d7 = d0 - this.player.locX();
                            d8 = d1 - this.player.locY();
                            if (d8 > -0.5D || d8 < 0.5D) {
                                d8 = 0.0D;
                            }

                            d9 = d2 - this.player.locZ();
                            d11 = d7 * d7 + d8 * d8 + d9 * d9;
                            boolean flag1 = false;

                            if (!this.player.H() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameMode() != EnumGamemode.SPECTATOR) {
                                flag1 = true;
                                PlayerConnection.LOGGER.warn("{} moved wrongly!", this.player.getDisplayName().getString());
                            }

                            this.player.setLocation(d0, d1, d2, f, f1);
                            if (!this.player.noPhysics && !this.player.isSleeping() && (flag1 && worldserver.getCubes(this.player, axisalignedbb) || this.a((IWorldReader) worldserver, axisalignedbb))) {
                                this.b(d3, d4, d5, f, f1);
                            } else {
                                this.clientIsFloating = d12 >= -0.03125D && this.player.gameMode.getGameMode() != EnumGamemode.SPECTATOR && !this.server.getAllowFlight() && !this.player.getAbilities().mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isGliding() && this.a((Entity) this.player);
                                this.player.getWorldServer().getChunkProvider().movePlayer(this.player);
                                this.player.a(this.player.locY() - d6, packetplayinflying.b());
                                this.player.setOnGround(packetplayinflying.b());
                                if (flag) {
                                    this.player.fallDistance = 0.0F;
                                }

                                this.player.checkMovement(this.player.locX() - d3, this.player.locY() - d4, this.player.locZ() - d5);
                                this.lastGoodX = this.player.locX();
                                this.lastGoodY = this.player.locY();
                                this.lastGoodZ = this.player.locZ();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean a(IWorldReader iworldreader, AxisAlignedBB axisalignedbb) {
        Stream<VoxelShape> stream = iworldreader.d(this.player, this.player.getBoundingBox().shrink(9.999999747378752E-6D), (entity) -> {
            return true;
        });
        VoxelShape voxelshape = VoxelShapes.a(axisalignedbb.shrink(9.999999747378752E-6D));

        return stream.anyMatch((voxelshape1) -> {
            return !VoxelShapes.c(voxelshape1, voxelshape, OperatorBoolean.AND);
        });
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        this.a(d0, d1, d2, f, f1, Collections.emptySet(), true);
    }

    public void b(double d0, double d1, double d2, float f, float f1) {
        this.a(d0, d1, d2, f, f1, Collections.emptySet(), false);
    }

    public void a(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        this.a(d0, d1, d2, f, f1, set, false);
    }

    public void a(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, boolean flag) {
        double d3 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X) ? this.player.locX() : 0.0D;
        double d4 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y) ? this.player.locY() : 0.0D;
        double d5 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z) ? this.player.locZ() : 0.0D;
        float f2 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT) ? this.player.getYRot() : 0.0F;
        float f3 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT) ? this.player.getXRot() : 0.0F;

        this.awaitingPositionFromClient = new Vec3D(d0, d1, d2);
        if (++this.awaitingTeleport == Integer.MAX_VALUE) {
            this.awaitingTeleport = 0;
        }

        this.awaitingTeleportTime = this.tickCount;
        this.player.setLocation(d0, d1, d2, f, f1);
        this.player.connection.sendPacket(new PacketPlayOutPosition(d0 - d3, d1 - d4, d2 - d5, f - f2, f1 - f3, set, this.awaitingTeleport, flag));
    }

    @Override
    public void a(PacketPlayInBlockDig packetplayinblockdig) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockdig, this, this.player.getWorldServer());
        BlockPosition blockposition = packetplayinblockdig.b();

        this.player.resetIdleTimer();
        PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype = packetplayinblockdig.d();

        switch (packetplayinblockdig_enumplayerdigtype) {
            case SWAP_ITEM_WITH_OFFHAND:
                if (!this.player.isSpectator()) {
                    ItemStack itemstack = this.player.b(EnumHand.OFF_HAND);

                    this.player.a(EnumHand.OFF_HAND, this.player.b(EnumHand.MAIN_HAND));
                    this.player.a(EnumHand.MAIN_HAND, itemstack);
                    this.player.clearActiveItem();
                }

                return;
            case DROP_ITEM:
                if (!this.player.isSpectator()) {
                    this.player.dropItem(false);
                }

                return;
            case DROP_ALL_ITEMS:
                if (!this.player.isSpectator()) {
                    this.player.dropItem(true);
                }

                return;
            case RELEASE_USE_ITEM:
                this.player.releaseActiveItem();
                return;
            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                this.player.gameMode.a(blockposition, packetplayinblockdig_enumplayerdigtype, packetplayinblockdig.c(), this.player.level.getMaxBuildHeight());
                return;
            default:
                throw new IllegalArgumentException("Invalid player action");
        }
    }

    private static boolean a(EntityPlayer entityplayer, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        } else {
            Item item = itemstack.getItem();

            return (item instanceof ItemBlock || item instanceof ItemBucket) && !entityplayer.getCooldownTracker().hasCooldown(item);
        }
    }

    @Override
    public void a(PacketPlayInUseItem packetplayinuseitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinuseitem, this, this.player.getWorldServer());
        WorldServer worldserver = this.player.getWorldServer();
        EnumHand enumhand = packetplayinuseitem.b();
        ItemStack itemstack = this.player.b(enumhand);
        MovingObjectPositionBlock movingobjectpositionblock = packetplayinuseitem.c();
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
        EnumDirection enumdirection = movingobjectpositionblock.getDirection();

        this.player.resetIdleTimer();
        int i = this.player.level.getMaxBuildHeight();

        if (blockposition.getY() < i) {
            if (this.awaitingPositionFromClient == null && this.player.h((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) < 64.0D && worldserver.a((EntityHuman) this.player, blockposition)) {
                EnumInteractionResult enuminteractionresult = this.player.gameMode.a(this.player, worldserver, itemstack, enumhand, movingobjectpositionblock);

                if (enumdirection == EnumDirection.UP && !enuminteractionresult.a() && blockposition.getY() >= i - 1 && a(this.player, itemstack)) {
                    IChatMutableComponent ichatmutablecomponent = (new ChatMessage("build.tooHigh", new Object[]{i - 1})).a(EnumChatFormat.RED);

                    this.player.a((IChatBaseComponent) ichatmutablecomponent, ChatMessageType.GAME_INFO, SystemUtils.NIL_UUID);
                } else if (enuminteractionresult.b()) {
                    this.player.swingHand(enumhand, true);
                }
            }
        } else {
            IChatMutableComponent ichatmutablecomponent1 = (new ChatMessage("build.tooHigh", new Object[]{i - 1})).a(EnumChatFormat.RED);

            this.player.a((IChatBaseComponent) ichatmutablecomponent1, ChatMessageType.GAME_INFO, SystemUtils.NIL_UUID);
        }

        this.player.connection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
        this.player.connection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition.shift(enumdirection)));
    }

    @Override
    public void a(PacketPlayInBlockPlace packetplayinblockplace) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockplace, this, this.player.getWorldServer());
        WorldServer worldserver = this.player.getWorldServer();
        EnumHand enumhand = packetplayinblockplace.b();
        ItemStack itemstack = this.player.b(enumhand);

        this.player.resetIdleTimer();
        if (!itemstack.isEmpty()) {
            EnumInteractionResult enuminteractionresult = this.player.gameMode.a(this.player, worldserver, itemstack, enumhand);

            if (enuminteractionresult.b()) {
                this.player.swingHand(enumhand, true);
            }

        }
    }

    @Override
    public void a(PacketPlayInSpectate packetplayinspectate) {
        PlayerConnectionUtils.ensureMainThread(packetplayinspectate, this, this.player.getWorldServer());
        if (this.player.isSpectator()) {
            Iterator iterator = this.server.getWorlds().iterator();

            while (iterator.hasNext()) {
                WorldServer worldserver = (WorldServer) iterator.next();
                Entity entity = packetplayinspectate.a(worldserver);

                if (entity != null) {
                    this.player.a(worldserver, entity.locX(), entity.locY(), entity.locZ(), entity.getYRot(), entity.getXRot());
                    return;
                }
            }
        }

    }

    @Override
    public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {
        PlayerConnectionUtils.ensureMainThread(packetplayinresourcepackstatus, this, this.player.getWorldServer());
        if (packetplayinresourcepackstatus.b() == PacketPlayInResourcePackStatus.EnumResourcePackStatus.DECLINED && this.server.aX()) {
            PlayerConnection.LOGGER.info("Disconnecting {} due to resource pack rejection", this.player.getDisplayName());
            this.disconnect(new ChatMessage("multiplayer.requiredTexturePrompt.disconnect"));
        }

    }

    @Override
    public void a(PacketPlayInBoatMove packetplayinboatmove) {
        PlayerConnectionUtils.ensureMainThread(packetplayinboatmove, this, this.player.getWorldServer());
        Entity entity = this.player.getVehicle();

        if (entity instanceof EntityBoat) {
            ((EntityBoat) entity).a(packetplayinboatmove.b(), packetplayinboatmove.c());
        }

    }

    @Override
    public void a(ServerboundPongPacket serverboundpongpacket) {}

    @Override
    public void a(IChatBaseComponent ichatbasecomponent) {
        PlayerConnection.LOGGER.info("{} lost connection: {}", this.player.getDisplayName().getString(), ichatbasecomponent.getString());
        this.server.invalidatePingSample();
        this.server.getPlayerList().sendMessage((new ChatMessage("multiplayer.player.left", new Object[]{this.player.getScoreboardDisplayName()})).a(EnumChatFormat.YELLOW), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
        this.player.p();
        this.server.getPlayerList().disconnect(this.player);
        this.player.Q().b();
        if (this.isExemptPlayer()) {
            PlayerConnection.LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.safeShutdown(false);
        }

    }

    @Override
    public void sendPacket(Packet<?> packet) {
        this.a(packet, (GenericFutureListener) null);
    }

    public void a(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        try {
            this.connection.sendPacket(packet, genericfuturelistener);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Sending packet");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Packet being sent");

            crashreportsystemdetails.a("Packet class", () -> {
                return packet.getClass().getCanonicalName();
            });
            throw new ReportedException(crashreport);
        }
    }

    @Override
    public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
        PlayerConnectionUtils.ensureMainThread(packetplayinhelditemslot, this, this.player.getWorldServer());
        if (packetplayinhelditemslot.b() >= 0 && packetplayinhelditemslot.b() < PlayerInventory.getHotbarSize()) {
            if (this.player.getInventory().selected != packetplayinhelditemslot.b() && this.player.getRaisedHand() == EnumHand.MAIN_HAND) {
                this.player.clearActiveItem();
            }

            this.player.getInventory().selected = packetplayinhelditemslot.b();
            this.player.resetIdleTimer();
        } else {
            PlayerConnection.LOGGER.warn("{} tried to set an invalid carried item", this.player.getDisplayName().getString());
        }
    }

    @Override
    public void a(PacketPlayInChat packetplayinchat) {
        String s = StringUtils.normalizeSpace(packetplayinchat.b());

        for (int i = 0; i < s.length(); ++i) {
            if (!SharedConstants.isAllowedChatCharacter(s.charAt(i))) {
                this.disconnect(new ChatMessage("multiplayer.disconnect.illegal_characters"));
                return;
            }
        }

        if (s.startsWith("/")) {
            PlayerConnectionUtils.ensureMainThread(packetplayinchat, this, this.player.getWorldServer());
            this.a(ITextFilter.a.a(s));
        } else {
            this.a(s, this::a);
        }

    }

    private void a(ITextFilter.a itextfilter_a) {
        if (this.player.getChatFlags() == EnumChatVisibility.HIDDEN) {
            this.sendPacket(new PacketPlayOutChat((new ChatMessage("chat.disabled.options")).a(EnumChatFormat.RED), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID));
        } else {
            this.player.resetIdleTimer();
            String s = itextfilter_a.a();

            if (s.startsWith("/")) {
                this.handleCommand(s);
            } else {
                String s1 = itextfilter_a.b();
                ChatMessage chatmessage = s1.isEmpty() ? null : new ChatMessage("chat.type.text", new Object[]{this.player.getScoreboardDisplayName(), s1});
                ChatMessage chatmessage1 = new ChatMessage("chat.type.text", new Object[]{this.player.getScoreboardDisplayName(), s});

                this.server.getPlayerList().a(chatmessage1, (entityplayer) -> {
                    return this.player.b(entityplayer) ? chatmessage : chatmessage1;
                }, ChatMessageType.CHAT, this.player.getUniqueID());
            }

            this.chatSpamTickCount += 20;
            if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getProfile())) {
                this.disconnect(new ChatMessage("disconnect.spam"));
            }

        }
    }

    private void handleCommand(String s) {
        this.server.getCommandDispatcher().a(this.player.getCommandListener(), s);
    }

    @Override
    public void a(PacketPlayInArmAnimation packetplayinarmanimation) {
        PlayerConnectionUtils.ensureMainThread(packetplayinarmanimation, this, this.player.getWorldServer());
        this.player.resetIdleTimer();
        this.player.swingHand(packetplayinarmanimation.b());
    }

    @Override
    public void a(PacketPlayInEntityAction packetplayinentityaction) {
        PlayerConnectionUtils.ensureMainThread(packetplayinentityaction, this, this.player.getWorldServer());
        this.player.resetIdleTimer();
        IJumpable ijumpable;

        switch (packetplayinentityaction.c()) {
            case PRESS_SHIFT_KEY:
                this.player.setSneaking(true);
                break;
            case RELEASE_SHIFT_KEY:
                this.player.setSneaking(false);
                break;
            case START_SPRINTING:
                this.player.setSprinting(true);
                break;
            case STOP_SPRINTING:
                this.player.setSprinting(false);
                break;
            case STOP_SLEEPING:
                if (this.player.isSleeping()) {
                    this.player.wakeup(false, true);
                    this.awaitingPositionFromClient = this.player.getPositionVector();
                }
                break;
            case START_RIDING_JUMP:
                if (this.player.getVehicle() instanceof IJumpable) {
                    ijumpable = (IJumpable) this.player.getVehicle();
                    int i = packetplayinentityaction.d();

                    if (ijumpable.a() && i > 0) {
                        ijumpable.b(i);
                    }
                }
                break;
            case STOP_RIDING_JUMP:
                if (this.player.getVehicle() instanceof IJumpable) {
                    ijumpable = (IJumpable) this.player.getVehicle();
                    ijumpable.b();
                }
                break;
            case OPEN_INVENTORY:
                if (this.player.getVehicle() instanceof EntityHorseAbstract) {
                    ((EntityHorseAbstract) this.player.getVehicle()).f((EntityHuman) this.player);
                }
                break;
            case START_FALL_FLYING:
                if (!this.player.fo()) {
                    this.player.stopGliding();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid client command!");
        }

    }

    @Override
    public void a(PacketPlayInUseEntity packetplayinuseentity) {
        PlayerConnectionUtils.ensureMainThread(packetplayinuseentity, this, this.player.getWorldServer());
        WorldServer worldserver = this.player.getWorldServer();
        final Entity entity = packetplayinuseentity.a(worldserver);

        this.player.resetIdleTimer();
        this.player.setSneaking(packetplayinuseentity.b());
        if (entity != null) {
            double d0 = 36.0D;

            if (this.player.f(entity) < 36.0D) {
                packetplayinuseentity.a(new PacketPlayInUseEntity.c() {
                    private void a(EnumHand enumhand, PlayerConnection.a playerconnection_a) {
                        ItemStack itemstack = PlayerConnection.this.player.b(enumhand).cloneItemStack();
                        EnumInteractionResult enuminteractionresult = playerconnection_a.run(PlayerConnection.this.player, entity, enumhand);

                        if (enuminteractionresult.a()) {
                            CriterionTriggers.PLAYER_INTERACTED_WITH_ENTITY.a(PlayerConnection.this.player, itemstack, entity);
                            if (enuminteractionresult.b()) {
                                PlayerConnection.this.player.swingHand(enumhand, true);
                            }
                        }

                    }

                    @Override
                    public void a(EnumHand enumhand) {
                        this.a(enumhand, EntityHuman::a);
                    }

                    @Override
                    public void a(EnumHand enumhand, Vec3D vec3d) {
                        this.a(enumhand, (entityplayer, entity1, enumhand1) -> {
                            return entity1.a((EntityHuman) entityplayer, vec3d, enumhand1);
                        });
                    }

                    @Override
                    public void a() {
                        if (!(entity instanceof EntityItem) && !(entity instanceof EntityExperienceOrb) && !(entity instanceof EntityArrow) && entity != PlayerConnection.this.player) {
                            PlayerConnection.this.player.attack(entity);
                        } else {
                            PlayerConnection.this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_entity_attacked"));
                            PlayerConnection.LOGGER.warn("Player {} tried to attack an invalid entity", PlayerConnection.this.player.getDisplayName().getString());
                        }
                    }
                });
            }
        }

    }

    @Override
    public void a(PacketPlayInClientCommand packetplayinclientcommand) {
        PlayerConnectionUtils.ensureMainThread(packetplayinclientcommand, this, this.player.getWorldServer());
        this.player.resetIdleTimer();
        PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand = packetplayinclientcommand.b();

        switch (packetplayinclientcommand_enumclientcommand) {
            case PERFORM_RESPAWN:
                if (this.player.wonGame) {
                    this.player.wonGame = false;
                    this.player = this.server.getPlayerList().moveToWorld(this.player, true);
                    CriterionTriggers.CHANGED_DIMENSION.a(this.player, World.END, World.OVERWORLD);
                } else {
                    if (this.player.getHealth() > 0.0F) {
                        return;
                    }

                    this.player = this.server.getPlayerList().moveToWorld(this.player, false);
                    if (this.server.isHardcore()) {
                        this.player.a(EnumGamemode.SPECTATOR);
                        ((GameRules.GameRuleBoolean) this.player.getWorldServer().getGameRules().get(GameRules.RULE_SPECTATORSGENERATECHUNKS)).a(false, this.server);
                    }
                }
                break;
            case REQUEST_STATS:
                this.player.getStatisticManager().a(this.player);
        }

    }

    @Override
    public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
        PlayerConnectionUtils.ensureMainThread(packetplayinclosewindow, this, this.player.getWorldServer());
        this.player.o();
    }

    @Override
    public void a(PacketPlayInWindowClick packetplayinwindowclick) {
        PlayerConnectionUtils.ensureMainThread(packetplayinwindowclick, this, this.player.getWorldServer());
        this.player.resetIdleTimer();
        if (this.player.containerMenu.containerId == packetplayinwindowclick.b()) {
            if (this.player.isSpectator()) {
                this.player.containerMenu.updateInventory();
            } else {
                boolean flag = packetplayinwindowclick.h() != this.player.containerMenu.getStateId();

                this.player.containerMenu.h();
                this.player.containerMenu.a(packetplayinwindowclick.c(), packetplayinwindowclick.d(), packetplayinwindowclick.g(), this.player);
                ObjectIterator objectiterator = Int2ObjectMaps.fastIterable(packetplayinwindowclick.f()).iterator();

                while (objectiterator.hasNext()) {
                    Entry<ItemStack> entry = (Entry) objectiterator.next();

                    this.player.containerMenu.b(entry.getIntKey(), (ItemStack) entry.getValue());
                }

                this.player.containerMenu.a(packetplayinwindowclick.e());
                this.player.containerMenu.i();
                if (flag) {
                    this.player.containerMenu.e();
                } else {
                    this.player.containerMenu.d();
                }
            }
        }

    }

    @Override
    public void a(PacketPlayInAutoRecipe packetplayinautorecipe) {
        PlayerConnectionUtils.ensureMainThread(packetplayinautorecipe, this, this.player.getWorldServer());
        this.player.resetIdleTimer();
        if (!this.player.isSpectator() && this.player.containerMenu.containerId == packetplayinautorecipe.b() && this.player.containerMenu instanceof ContainerRecipeBook) {
            this.server.getCraftingManager().getRecipe(packetplayinautorecipe.c()).ifPresent((irecipe) -> {
                ((ContainerRecipeBook) this.player.containerMenu).a(packetplayinautorecipe.d(), irecipe, this.player);
            });
        }
    }

    @Override
    public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinenchantitem, this, this.player.getWorldServer());
        this.player.resetIdleTimer();
        if (this.player.containerMenu.containerId == packetplayinenchantitem.b() && !this.player.isSpectator()) {
            this.player.containerMenu.a((EntityHuman) this.player, packetplayinenchantitem.c());
            this.player.containerMenu.d();
        }

    }

    @Override
    public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetcreativeslot, this, this.player.getWorldServer());
        if (this.player.gameMode.isCreative()) {
            boolean flag = packetplayinsetcreativeslot.b() < 0;
            ItemStack itemstack = packetplayinsetcreativeslot.getItemStack();
            NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

            if (!itemstack.isEmpty() && nbttagcompound != null && nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
                BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
                TileEntity tileentity = this.player.level.getTileEntity(blockposition);

                if (tileentity != null) {
                    NBTTagCompound nbttagcompound1 = tileentity.save(new NBTTagCompound());

                    nbttagcompound1.remove("x");
                    nbttagcompound1.remove("y");
                    nbttagcompound1.remove("z");
                    itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound1);
                }
            }

            boolean flag1 = packetplayinsetcreativeslot.b() >= 1 && packetplayinsetcreativeslot.b() <= 45;
            boolean flag2 = itemstack.isEmpty() || itemstack.getDamage() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();

            if (flag1 && flag2) {
                this.player.inventoryMenu.getSlot(packetplayinsetcreativeslot.b()).set(itemstack);
                this.player.inventoryMenu.d();
            } else if (flag && flag2 && this.dropSpamTickCount < 200) {
                this.dropSpamTickCount += 20;
                this.player.drop(itemstack, true);
            }
        }

    }

    @Override
    public void a(PacketPlayInUpdateSign packetplayinupdatesign) {
        List<String> list = (List) Stream.of(packetplayinupdatesign.c()).map(EnumChatFormat::a).collect(Collectors.toList());

        this.a(list, (list1) -> {
            this.a(packetplayinupdatesign, list1);
        });
    }

    private void a(PacketPlayInUpdateSign packetplayinupdatesign, List<ITextFilter.a> list) {
        this.player.resetIdleTimer();
        WorldServer worldserver = this.player.getWorldServer();
        BlockPosition blockposition = packetplayinupdatesign.b();

        if (worldserver.isLoaded(blockposition)) {
            IBlockData iblockdata = worldserver.getType(blockposition);
            TileEntity tileentity = worldserver.getTileEntity(blockposition);

            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }

            TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.d() || !this.player.getUniqueID().equals(tileentitysign.f())) {
                PlayerConnection.LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getDisplayName().getString());
                return;
            }

            for (int i = 0; i < list.size(); ++i) {
                ITextFilter.a itextfilter_a = (ITextFilter.a) list.get(i);

                if (this.player.R()) {
                    tileentitysign.a(i, new ChatComponentText(itextfilter_a.b()));
                } else {
                    tileentitysign.a(i, new ChatComponentText(itextfilter_a.a()), new ChatComponentText(itextfilter_a.b()));
                }
            }

            tileentitysign.update();
            worldserver.notify(blockposition, iblockdata, iblockdata, 3);
        }

    }

    @Override
    public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
        if (this.keepAlivePending && packetplayinkeepalive.b() == this.keepAliveChallenge) {
            int i = (int) (SystemUtils.getMonotonicMillis() - this.keepAliveTime);

            this.player.latency = (this.player.latency * 3 + i) / 4;
            this.keepAlivePending = false;
        } else if (!this.isExemptPlayer()) {
            this.disconnect(new ChatMessage("disconnect.timeout"));
        }

    }

    @Override
    public void a(PacketPlayInAbilities packetplayinabilities) {
        PlayerConnectionUtils.ensureMainThread(packetplayinabilities, this, this.player.getWorldServer());
        this.player.getAbilities().flying = packetplayinabilities.isFlying() && this.player.getAbilities().mayfly;
    }

    @Override
    public void a(PacketPlayInSettings packetplayinsettings) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsettings, this, this.player.getWorldServer());
        this.player.a(packetplayinsettings);
    }

    @Override
    public void a(PacketPlayInCustomPayload packetplayincustompayload) {}

    @Override
    public void a(PacketPlayInDifficultyChange packetplayindifficultychange) {
        PlayerConnectionUtils.ensureMainThread(packetplayindifficultychange, this, this.player.getWorldServer());
        if (this.player.l(2) || this.isExemptPlayer()) {
            this.server.a(packetplayindifficultychange.b(), false);
        }
    }

    @Override
    public void a(PacketPlayInDifficultyLock packetplayindifficultylock) {
        PlayerConnectionUtils.ensureMainThread(packetplayindifficultylock, this, this.player.getWorldServer());
        if (this.player.l(2) || this.isExemptPlayer()) {
            this.server.b(packetplayindifficultylock.b());
        }
    }

    @Override
    public EntityPlayer d() {
        return this.player;
    }

    @FunctionalInterface
    private interface a {

        EnumInteractionResult run(EntityPlayer entityplayer, Entity entity, EnumHand enumhand);
    }
}
