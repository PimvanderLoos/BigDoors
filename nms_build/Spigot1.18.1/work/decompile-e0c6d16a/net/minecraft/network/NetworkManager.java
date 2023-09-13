package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect;
import net.minecraft.network.protocol.login.PacketLoginOutDisconnect;
import net.minecraft.server.CancelledPacketHandleException;
import net.minecraft.server.network.LoginListener;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.LazyInitVar;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    private static final float AVERAGE_PACKETS_SMOOTHING = 0.75F;
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker ROOT_MARKER = MarkerManager.getMarker("NETWORK");
    public static final Marker PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.ROOT_MARKER);
    public static final AttributeKey<EnumProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyInitVar<>(() -> {
        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    });
    public static final LazyInitVar<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyInitVar<>(() -> {
        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
    });
    public static final LazyInitVar<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyInitVar<>(() -> {
        return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
    });
    private final EnumProtocolDirection receiving;
    private final Queue<NetworkManager.QueuedPacket> queue = Queues.newConcurrentLinkedQueue();
    public Channel channel;
    public SocketAddress address;
    private PacketListener packetListener;
    private IChatBaseComponent disconnectedReason;
    private boolean encrypted;
    private boolean disconnectionHandled;
    private int receivedPackets;
    private int sentPackets;
    private float averageReceivedPackets;
    private float averageSentPackets;
    private int tickCount;
    private boolean handlingFault;

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.receiving = enumprotocoldirection;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.address = this.channel.remoteAddress();

        try {
            this.setProtocol(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            NetworkManager.LOGGER.fatal(throwable);
        }

    }

    public void setProtocol(EnumProtocol enumprotocol) {
        this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).set(enumprotocol);
        this.channel.config().setAutoRead(true);
        NetworkManager.LOGGER.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) {
        this.disconnect(new ChatMessage("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
        if (throwable instanceof SkipEncodeException) {
            NetworkManager.LOGGER.debug("Skipping packet due to errors", throwable.getCause());
        } else {
            boolean flag = !this.handlingFault;

            this.handlingFault = true;
            if (this.channel.isOpen()) {
                if (throwable instanceof TimeoutException) {
                    NetworkManager.LOGGER.debug("Timeout", throwable);
                    this.disconnect(new ChatMessage("disconnect.timeout"));
                } else {
                    ChatMessage chatmessage = new ChatMessage("disconnect.genericReason", new Object[]{"Internal Exception: " + throwable});

                    if (flag) {
                        NetworkManager.LOGGER.debug("Failed to sent packet", throwable);
                        EnumProtocol enumprotocol = this.getCurrentProtocol();
                        Packet<?> packet = enumprotocol == EnumProtocol.LOGIN ? new PacketLoginOutDisconnect(chatmessage) : new PacketPlayOutKickDisconnect(chatmessage);

                        this.send((Packet) packet, (future) -> {
                            this.disconnect(chatmessage);
                        });
                        this.setReadOnly();
                    } else {
                        NetworkManager.LOGGER.debug("Double fault", throwable);
                        this.disconnect(chatmessage);
                    }
                }

            }
        }
    }

    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet<?> packet) {
        if (this.channel.isOpen()) {
            try {
                genericsFtw(packet, this.packetListener);
            } catch (CancelledPacketHandleException cancelledpackethandleexception) {
                ;
            } catch (ClassCastException classcastexception) {
                NetworkManager.LOGGER.error("Received {} that couldn't be processed", packet.getClass(), classcastexception);
                this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_packet"));
            }

            ++this.receivedPackets;
        }

    }

    private static <T extends PacketListener> void genericsFtw(Packet<T> packet, PacketListener packetlistener) {
        packet.handle(packetlistener);
    }

    public void setListener(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        this.packetListener = packetlistener;
    }

    public void send(Packet<?> packet) {
        this.send(packet, (GenericFutureListener) null);
    }

    public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        if (this.isConnected()) {
            this.flushQueue();
            this.sendPacket(packet, genericfuturelistener);
        } else {
            this.queue.add(new NetworkManager.QueuedPacket(packet, genericfuturelistener));
        }

    }

    private void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        EnumProtocol enumprotocol = EnumProtocol.getProtocolForPacket(packet);
        EnumProtocol enumprotocol1 = this.getCurrentProtocol();

        ++this.sentPackets;
        if (enumprotocol1 != enumprotocol) {
            NetworkManager.LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            this.doSendPacket(packet, genericfuturelistener, enumprotocol, enumprotocol1);
        } else {
            this.channel.eventLoop().execute(() -> {
                this.doSendPacket(packet, genericfuturelistener, enumprotocol, enumprotocol1);
            });
        }

    }

    private void doSendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, EnumProtocol enumprotocol, EnumProtocol enumprotocol1) {
        if (enumprotocol != enumprotocol1) {
            this.setProtocol(enumprotocol);
        }

        ChannelFuture channelfuture = this.channel.writeAndFlush(packet);

        if (genericfuturelistener != null) {
            channelfuture.addListener(genericfuturelistener);
        }

        channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private EnumProtocol getCurrentProtocol() {
        return (EnumProtocol) this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).get();
    }

    private void flushQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            Queue queue = this.queue;

            synchronized (this.queue) {
                NetworkManager.QueuedPacket networkmanager_queuedpacket;

                while ((networkmanager_queuedpacket = (NetworkManager.QueuedPacket) this.queue.poll()) != null) {
                    this.sendPacket(networkmanager_queuedpacket.packet, networkmanager_queuedpacket.listener);
                }

            }
        }
    }

    public void tick() {
        this.flushQueue();
        if (this.packetListener instanceof LoginListener) {
            ((LoginListener) this.packetListener).tick();
        }

        if (this.packetListener instanceof PlayerConnection) {
            ((PlayerConnection) this.packetListener).tick();
        }

        if (!this.isConnected() && !this.disconnectionHandled) {
            this.handleDisconnection();
        }

        if (this.channel != null) {
            this.channel.flush();
        }

        if (this.tickCount++ % 20 == 0) {
            this.tickSecond();
        }

    }

    protected void tickSecond() {
        this.averageSentPackets = MathHelper.lerp(0.75F, (float) this.sentPackets, this.averageSentPackets);
        this.averageReceivedPackets = MathHelper.lerp(0.75F, (float) this.receivedPackets, this.averageReceivedPackets);
        this.sentPackets = 0;
        this.receivedPackets = 0;
    }

    public SocketAddress getRemoteAddress() {
        return this.address;
    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectedReason = ichatbasecomponent;
        }

    }

    public boolean isMemoryConnection() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public EnumProtocolDirection getReceiving() {
        return this.receiving;
    }

    public EnumProtocolDirection getSending() {
        return this.receiving.getOpposite();
    }

    public static NetworkManager connectToServer(InetSocketAddress inetsocketaddress, boolean flag) {
        final NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.CLIENTBOUND);
        Class oclass;
        LazyInitVar lazyinitvar;

        if (Epoll.isAvailable() && flag) {
            oclass = EpollSocketChannel.class;
            lazyinitvar = NetworkManager.NETWORK_EPOLL_WORKER_GROUP;
        } else {
            oclass = NioSocketChannel.class;
            lazyinitvar = NetworkManager.NETWORK_WORKER_GROUP;
        }

        ((Bootstrap) ((Bootstrap) ((Bootstrap) (new Bootstrap()).group((EventLoopGroup) lazyinitvar.get())).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException channelexception) {
                    ;
                }

                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(EnumProtocolDirection.CLIENTBOUND)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(EnumProtocolDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);
            }
        })).channel(oclass)).connect(inetsocketaddress.getAddress(), inetsocketaddress.getPort()).syncUninterruptibly();
        return networkmanager;
    }

    public static NetworkManager connectToLocalServer(SocketAddress socketaddress) {
        final NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.CLIENTBOUND);

        ((Bootstrap) ((Bootstrap) ((Bootstrap) (new Bootstrap()).group((EventLoopGroup) NetworkManager.LOCAL_WORKER_GROUP.get())).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast("packet_handler", networkmanager);
            }
        })).channel(LocalChannel.class)).connect(socketaddress).syncUninterruptibly();
        return networkmanager;
    }

    public void setEncryptionKey(Cipher cipher, Cipher cipher1) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(cipher));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(cipher1));
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    @Nullable
    public IChatBaseComponent getDisconnectedReason() {
        return this.disconnectedReason;
    }

    public void setReadOnly() {
        this.channel.config().setAutoRead(false);
    }

    public void setupCompression(int i, boolean flag) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) this.channel.pipeline().get("decompress")).setThreshold(i, flag);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i, flag));
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) this.channel.pipeline().get("compress")).setThreshold(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }

    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.disconnectionHandled) {
                NetworkManager.LOGGER.warn("handleDisconnection() called twice");
            } else {
                this.disconnectionHandled = true;
                if (this.getDisconnectedReason() != null) {
                    this.getPacketListener().onDisconnect(this.getDisconnectedReason());
                } else if (this.getPacketListener() != null) {
                    this.getPacketListener().onDisconnect(new ChatMessage("multiplayer.disconnect.generic"));
                }
            }

        }
    }

    public float getAverageReceivedPackets() {
        return this.averageReceivedPackets;
    }

    public float getAverageSentPackets() {
        return this.averageSentPackets;
    }

    private static class QueuedPacket {

        final Packet<?> packet;
        @Nullable
        final GenericFutureListener<? extends Future<? super Void>> listener;

        public QueuedPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
            this.packet = packet;
            this.listener = genericfuturelistener;
        }
    }
}
