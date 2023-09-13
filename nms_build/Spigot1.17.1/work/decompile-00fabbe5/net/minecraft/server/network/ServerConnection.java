package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkManagerServer;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketPrepender;
import net.minecraft.network.PacketSplitter;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyInitVar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnection {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final LazyInitVar<NioEventLoopGroup> SERVER_EVENT_GROUP = new LazyInitVar<>(() -> {
        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
    });
    public static final LazyInitVar<EpollEventLoopGroup> SERVER_EPOLL_EVENT_GROUP = new LazyInitVar<>(() -> {
        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
    });
    final MinecraftServer server;
    public volatile boolean running;
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
    final List<NetworkManager> connections = Collections.synchronizedList(Lists.newArrayList());

    public ServerConnection(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
        this.running = true;
    }

    public void a(@Nullable InetAddress inetaddress, int i) throws IOException {
        List list = this.channels;

        synchronized (this.channels) {
            Class oclass;
            LazyInitVar lazyinitvar;

            if (Epoll.isAvailable() && this.server.m()) {
                oclass = EpollServerSocketChannel.class;
                lazyinitvar = ServerConnection.SERVER_EPOLL_EVENT_GROUP;
                ServerConnection.LOGGER.info("Using epoll channel type");
            } else {
                oclass = NioServerSocketChannel.class;
                lazyinitvar = ServerConnection.SERVER_EVENT_GROUP;
                ServerConnection.LOGGER.info("Using default channel type");
            }

            this.channels.add(((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(oclass)).childHandler(new ChannelInitializer<Channel>() {
                protected void initChannel(Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException channelexception) {
                        ;
                    }

                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyPingHandler(ServerConnection.this)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
                    int j = ServerConnection.this.server.l();
                    Object object = j > 0 ? new NetworkManagerServer(j) : new NetworkManager(EnumProtocolDirection.SERVERBOUND);

                    ServerConnection.this.connections.add(object);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler) object);
                    ((NetworkManager) object).setPacketListener(new HandshakeListener(ServerConnection.this.server, (NetworkManager) object));
                }
            }).group((EventLoopGroup) lazyinitvar.a()).localAddress(inetaddress, i)).bind().syncUninterruptibly());
        }
    }

    public SocketAddress a() {
        List list = this.channels;
        ChannelFuture channelfuture;

        synchronized (this.channels) {
            channelfuture = ((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>() {
                protected void initChannel(Channel channel) {
                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);

                    networkmanager.setPacketListener(new MemoryServerHandshakePacketListenerImpl(ServerConnection.this.server, networkmanager));
                    ServerConnection.this.connections.add(networkmanager);
                    channel.pipeline().addLast("packet_handler", networkmanager);
                }
            }).group((EventLoopGroup) ServerConnection.SERVER_EVENT_GROUP.a()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
            this.channels.add(channelfuture);
        }

        return channelfuture.channel().localAddress();
    }

    public void b() {
        this.running = false;
        Iterator iterator = this.channels.iterator();

        while (iterator.hasNext()) {
            ChannelFuture channelfuture = (ChannelFuture) iterator.next();

            try {
                channelfuture.channel().close().sync();
            } catch (InterruptedException interruptedexception) {
                ServerConnection.LOGGER.error("Interrupted whilst closing channel");
            }
        }

    }

    public void c() {
        List list = this.connections;

        synchronized (this.connections) {
            Iterator iterator = this.connections.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkmanager = (NetworkManager) iterator.next();

                if (!networkmanager.i()) {
                    if (networkmanager.isConnected()) {
                        try {
                            networkmanager.a();
                        } catch (Exception exception) {
                            if (networkmanager.isLocal()) {
                                throw new ReportedException(CrashReport.a(exception, "Ticking memory connection"));
                            }

                            ServerConnection.LOGGER.warn("Failed to handle packet for {}", networkmanager.getSocketAddress(), exception);
                            ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.sendPacket(new PacketPlayOutKickDisconnect(chatcomponenttext), (future) -> {
                                networkmanager.close(chatcomponenttext);
                            });
                            networkmanager.stopReading();
                        }
                    } else {
                        iterator.remove();
                        networkmanager.handleDisconnection();
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.server;
    }

    private static class LatencySimulator extends ChannelInboundHandlerAdapter {

        private static final Timer TIMER = new HashedWheelTimer();
        private final int delay;
        private final int jitter;
        private final List<ServerConnection.LatencySimulator.DelayedMessage> queuedMessages = Lists.newArrayList();

        public LatencySimulator(int i, int j) {
            this.delay = i;
            this.jitter = j;
        }

        public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) {
            this.a(channelhandlercontext, object);
        }

        private void a(ChannelHandlerContext channelhandlercontext, Object object) {
            int i = this.delay + (int) (Math.random() * (double) this.jitter);

            this.queuedMessages.add(new ServerConnection.LatencySimulator.DelayedMessage(channelhandlercontext, object));
            ServerConnection.LatencySimulator.TIMER.newTimeout(this::a, (long) i, TimeUnit.MILLISECONDS);
        }

        private void a(Timeout timeout) {
            ServerConnection.LatencySimulator.DelayedMessage serverconnection_latencysimulator_delayedmessage = (ServerConnection.LatencySimulator.DelayedMessage) this.queuedMessages.remove(0);

            serverconnection_latencysimulator_delayedmessage.ctx.fireChannelRead(serverconnection_latencysimulator_delayedmessage.msg);
        }

        private static class DelayedMessage {

            public final ChannelHandlerContext ctx;
            public final Object msg;

            public DelayedMessage(ChannelHandlerContext channelhandlercontext, Object object) {
                this.ctx = channelhandlercontext;
                this.msg = object;
            }
        }
    }
}
