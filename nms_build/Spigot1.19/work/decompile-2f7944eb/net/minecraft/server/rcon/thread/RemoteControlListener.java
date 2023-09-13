package net.minecraft.server.rcon.thread;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.IMinecraftServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import org.slf4j.Logger;

public class RemoteControlListener extends RemoteConnectionThread {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final ServerSocket socket;
    private final String rconPassword;
    private final List<RemoteControlSession> clients = Lists.newArrayList();
    private final IMinecraftServer serverInterface;

    private RemoteControlListener(IMinecraftServer iminecraftserver, ServerSocket serversocket, String s) {
        super("RCON Listener");
        this.serverInterface = iminecraftserver;
        this.socket = serversocket;
        this.rconPassword = s;
    }

    private void clearClients() {
        this.clients.removeIf((remotecontrolsession) -> {
            return !remotecontrolsession.isRunning();
        });
    }

    public void run() {
        try {
            while (this.running) {
                try {
                    Socket socket = this.socket.accept();
                    RemoteControlSession remotecontrolsession = new RemoteControlSession(this.serverInterface, this.rconPassword, socket);

                    remotecontrolsession.start();
                    this.clients.add(remotecontrolsession);
                    this.clearClients();
                } catch (SocketTimeoutException sockettimeoutexception) {
                    this.clearClients();
                } catch (IOException ioexception) {
                    if (this.running) {
                        RemoteControlListener.LOGGER.info("IO exception: ", ioexception);
                    }
                }
            }
        } finally {
            this.closeSocket(this.socket);
        }

    }

    @Nullable
    public static RemoteControlListener create(IMinecraftServer iminecraftserver) {
        DedicatedServerProperties dedicatedserverproperties = iminecraftserver.getProperties();
        String s = iminecraftserver.getServerIp();

        if (s.isEmpty()) {
            s = "0.0.0.0";
        }

        int i = dedicatedserverproperties.rconPort;

        if (0 < i && 65535 >= i) {
            String s1 = dedicatedserverproperties.rconPassword;

            if (s1.isEmpty()) {
                RemoteControlListener.LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
                return null;
            } else {
                try {
                    ServerSocket serversocket = new ServerSocket(i, 0, InetAddress.getByName(s));

                    serversocket.setSoTimeout(500);
                    RemoteControlListener remotecontrollistener = new RemoteControlListener(iminecraftserver, serversocket, s1);

                    if (!remotecontrollistener.start()) {
                        return null;
                    } else {
                        RemoteControlListener.LOGGER.info("RCON running on {}:{}", s, i);
                        return remotecontrollistener;
                    }
                } catch (IOException ioexception) {
                    RemoteControlListener.LOGGER.warn("Unable to initialise RCON on {}:{}", new Object[]{s, i, ioexception});
                    return null;
                }
            }
        } else {
            RemoteControlListener.LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", i);
            return null;
        }
    }

    @Override
    public void stop() {
        this.running = false;
        this.closeSocket(this.socket);
        super.stop();
        Iterator iterator = this.clients.iterator();

        while (iterator.hasNext()) {
            RemoteControlSession remotecontrolsession = (RemoteControlSession) iterator.next();

            if (remotecontrolsession.isRunning()) {
                remotecontrolsession.stop();
            }
        }

        this.clients.clear();
    }

    private void closeSocket(ServerSocket serversocket) {
        RemoteControlListener.LOGGER.debug("closeSocket: {}", serversocket);

        try {
            serversocket.close();
        } catch (IOException ioexception) {
            RemoteControlListener.LOGGER.warn("Failed to close socket", ioexception);
        }

    }
}
