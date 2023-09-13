package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoteControlListener extends RemoteConnectionThread {

    private static final Logger LOGGER = LogManager.getLogger();
    private final int e;
    private String f;
    private ServerSocket g;
    private final String h;
    private final List<RemoteControlSession> i = Lists.newArrayList();
    private final IMinecraftServer j;

    public RemoteControlListener(IMinecraftServer iminecraftserver) {
        super("RCON Listener");
        this.j = iminecraftserver;
        DedicatedServerProperties dedicatedserverproperties = iminecraftserver.getDedicatedServerProperties();

        this.e = dedicatedserverproperties.rconPort;
        this.h = dedicatedserverproperties.rconPassword;
        this.f = iminecraftserver.h_();
        if (this.f.isEmpty()) {
            this.f = "0.0.0.0";
        }

    }

    private void d() {
        this.i.removeIf((remotecontrolsession) -> {
            return !remotecontrolsession.c();
        });
    }

    public void run() {
        RemoteControlListener.LOGGER.info("RCON running on {}:{}", this.f, this.e);

        try {
            while (this.a) {
                try {
                    Socket socket = this.g.accept();
                    RemoteControlSession remotecontrolsession = new RemoteControlSession(this.j, this.h, socket);

                    remotecontrolsession.a();
                    this.i.add(remotecontrolsession);
                    this.d();
                } catch (SocketTimeoutException sockettimeoutexception) {
                    this.d();
                } catch (IOException ioexception) {
                    if (this.a) {
                        RemoteControlListener.LOGGER.info("IO exception: ", ioexception);
                    }
                }
            }
        } finally {
            this.a(this.g);
        }

    }

    @Override
    public void a() {
        if (this.h.isEmpty()) {
            RemoteControlListener.LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
        } else if (0 < this.e && 65535 >= this.e) {
            if (!this.a) {
                try {
                    this.g = new ServerSocket(this.e, 0, InetAddress.getByName(this.f));
                    this.g.setSoTimeout(500);
                    super.a();
                } catch (IOException ioexception) {
                    RemoteControlListener.LOGGER.warn("Unable to initialise rcon on {}:{}", this.f, this.e, ioexception);
                }

            }
        } else {
            RemoteControlListener.LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", this.e);
        }
    }

    @Override
    public void b() {
        this.a = false;
        this.a(this.g);
        super.b();
        Iterator iterator = this.i.iterator();

        while (iterator.hasNext()) {
            RemoteControlSession remotecontrolsession = (RemoteControlSession) iterator.next();

            if (remotecontrolsession.c()) {
                remotecontrolsession.b();
            }
        }

        this.i.clear();
    }

    private void a(ServerSocket serversocket) {
        RemoteControlListener.LOGGER.debug("closeSocket: {}", serversocket);

        try {
            serversocket.close();
        } catch (IOException ioexception) {
            RemoteControlListener.LOGGER.warn("Failed to close socket", ioexception);
        }

    }
}
