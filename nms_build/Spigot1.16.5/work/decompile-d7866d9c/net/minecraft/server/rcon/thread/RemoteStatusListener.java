package net.minecraft.server.rcon.thread;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.server.IMinecraftServer;
import net.minecraft.server.rcon.RemoteStatusReply;
import net.minecraft.server.rcon.StatusChallengeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoteStatusListener extends RemoteConnectionThread {

    private static final Logger LOGGER = LogManager.getLogger();
    private long e;
    private final int f;
    private final int g;
    private final int h;
    private final String i;
    private final String j;
    private DatagramSocket k;
    private final byte[] l = new byte[1460];
    private String m;
    private String n;
    private final Map<SocketAddress, RemoteStatusListener.RemoteStatusChallenge> o;
    private final RemoteStatusReply p;
    private long q;
    private final IMinecraftServer r;

    private RemoteStatusListener(IMinecraftServer iminecraftserver, int i) {
        super("Query Listener");
        this.r = iminecraftserver;
        this.f = i;
        this.n = iminecraftserver.h_();
        this.g = iminecraftserver.p();
        this.i = iminecraftserver.i_();
        this.h = iminecraftserver.getMaxPlayers();
        this.j = iminecraftserver.getWorld();
        this.q = 0L;
        this.m = "0.0.0.0";
        if (!this.n.isEmpty() && !this.m.equals(this.n)) {
            this.m = this.n;
        } else {
            this.n = "0.0.0.0";

            try {
                InetAddress inetaddress = InetAddress.getLocalHost();

                this.m = inetaddress.getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                RemoteStatusListener.LOGGER.warn("Unable to determine local host IP, please set server-ip in server.properties", unknownhostexception);
            }
        }

        this.p = new RemoteStatusReply(1460);
        this.o = Maps.newHashMap();
    }

    @Nullable
    public static RemoteStatusListener a(IMinecraftServer iminecraftserver) {
        int i = iminecraftserver.getDedicatedServerProperties().queryPort;

        if (0 < i && 65535 >= i) {
            RemoteStatusListener remotestatuslistener = new RemoteStatusListener(iminecraftserver, i);

            return !remotestatuslistener.a() ? null : remotestatuslistener;
        } else {
            RemoteStatusListener.LOGGER.warn("Invalid query port {} found in server.properties (queries disabled)", i);
            return null;
        }
    }

    private void a(byte[] abyte, DatagramPacket datagrampacket) throws IOException {
        this.k.send(new DatagramPacket(abyte, abyte.length, datagrampacket.getSocketAddress()));
    }

    private boolean a(DatagramPacket datagrampacket) throws IOException {
        byte[] abyte = datagrampacket.getData();
        int i = datagrampacket.getLength();
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        RemoteStatusListener.LOGGER.debug("Packet len {} [{}]", i, socketaddress);
        if (3 <= i && -2 == abyte[0] && -3 == abyte[1]) {
            RemoteStatusListener.LOGGER.debug("Packet '{}' [{}]", StatusChallengeUtils.a(abyte[2]), socketaddress);
            switch (abyte[2]) {
                case 0:
                    if (!this.c(datagrampacket)) {
                        RemoteStatusListener.LOGGER.debug("Invalid challenge [{}]", socketaddress);
                        return false;
                    } else if (15 == i) {
                        this.a(this.b(datagrampacket), datagrampacket);
                        RemoteStatusListener.LOGGER.debug("Rules [{}]", socketaddress);
                    } else {
                        RemoteStatusReply remotestatusreply = new RemoteStatusReply(1460);

                        remotestatusreply.a((int) 0);
                        remotestatusreply.a(this.a(datagrampacket.getSocketAddress()));
                        remotestatusreply.a(this.i);
                        remotestatusreply.a("SMP");
                        remotestatusreply.a(this.j);
                        remotestatusreply.a(Integer.toString(this.r.getPlayerCount()));
                        remotestatusreply.a(Integer.toString(this.h));
                        remotestatusreply.a((short) this.g);
                        remotestatusreply.a(this.m);
                        this.a(remotestatusreply.a(), datagrampacket);
                        RemoteStatusListener.LOGGER.debug("Status [{}]", socketaddress);
                    }
                default:
                    return true;
                case 9:
                    this.d(datagrampacket);
                    RemoteStatusListener.LOGGER.debug("Challenge [{}]", socketaddress);
                    return true;
            }
        } else {
            RemoteStatusListener.LOGGER.debug("Invalid packet [{}]", socketaddress);
            return false;
        }
    }

    private byte[] b(DatagramPacket datagrampacket) throws IOException {
        long i = SystemUtils.getMonotonicMillis();

        if (i < this.q + 5000L) {
            byte[] abyte = this.p.a();
            byte[] abyte1 = this.a(datagrampacket.getSocketAddress());

            abyte[1] = abyte1[0];
            abyte[2] = abyte1[1];
            abyte[3] = abyte1[2];
            abyte[4] = abyte1[3];
            return abyte;
        } else {
            this.q = i;
            this.p.b();
            this.p.a((int) 0);
            this.p.a(this.a(datagrampacket.getSocketAddress()));
            this.p.a("splitnum");
            this.p.a((int) 128);
            this.p.a((int) 0);
            this.p.a("hostname");
            this.p.a(this.i);
            this.p.a("gametype");
            this.p.a("SMP");
            this.p.a("game_id");
            this.p.a("MINECRAFT");
            this.p.a("version");
            this.p.a(this.r.getVersion());
            this.p.a("plugins");
            this.p.a(this.r.getPlugins());
            this.p.a("map");
            this.p.a(this.j);
            this.p.a("numplayers");
            this.p.a("" + this.r.getPlayerCount());
            this.p.a("maxplayers");
            this.p.a("" + this.h);
            this.p.a("hostport");
            this.p.a("" + this.g);
            this.p.a("hostip");
            this.p.a(this.m);
            this.p.a((int) 0);
            this.p.a((int) 1);
            this.p.a("player_");
            this.p.a((int) 0);
            String[] astring = this.r.getPlayers();
            String[] astring1 = astring;
            int j = astring.length;

            for (int k = 0; k < j; ++k) {
                String s = astring1[k];

                this.p.a(s);
            }

            this.p.a((int) 0);
            return this.p.a();
        }
    }

    private byte[] a(SocketAddress socketaddress) {
        return ((RemoteStatusListener.RemoteStatusChallenge) this.o.get(socketaddress)).c();
    }

    private Boolean c(DatagramPacket datagrampacket) {
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        if (!this.o.containsKey(socketaddress)) {
            return false;
        } else {
            byte[] abyte = datagrampacket.getData();

            return ((RemoteStatusListener.RemoteStatusChallenge) this.o.get(socketaddress)).a() == StatusChallengeUtils.c(abyte, 7, datagrampacket.getLength());
        }
    }

    private void d(DatagramPacket datagrampacket) throws IOException {
        RemoteStatusListener.RemoteStatusChallenge remotestatuslistener_remotestatuschallenge = new RemoteStatusListener.RemoteStatusChallenge(datagrampacket);

        this.o.put(datagrampacket.getSocketAddress(), remotestatuslistener_remotestatuschallenge);
        this.a(remotestatuslistener_remotestatuschallenge.b(), datagrampacket);
    }

    private void d() {
        if (this.a) {
            long i = SystemUtils.getMonotonicMillis();

            if (i >= this.e + 30000L) {
                this.e = i;
                this.o.values().removeIf((remotestatuslistener_remotestatuschallenge) -> {
                    return remotestatuslistener_remotestatuschallenge.a(i);
                });
            }
        }
    }

    public void run() {
        RemoteStatusListener.LOGGER.info("Query running on {}:{}", this.n, this.f);
        this.e = SystemUtils.getMonotonicMillis();
        DatagramPacket datagrampacket = new DatagramPacket(this.l, this.l.length);

        try {
            while (this.a) {
                try {
                    this.k.receive(datagrampacket);
                    this.d();
                    this.a(datagrampacket);
                } catch (SocketTimeoutException sockettimeoutexception) {
                    this.d();
                } catch (PortUnreachableException portunreachableexception) {
                    ;
                } catch (IOException ioexception) {
                    this.a((Exception) ioexception);
                }
            }
        } finally {
            RemoteStatusListener.LOGGER.debug("closeSocket: {}:{}", this.n, this.f);
            this.k.close();
        }

    }

    @Override
    public boolean a() {
        return this.a ? true : (!this.e() ? false : super.a());
    }

    private void a(Exception exception) {
        if (this.a) {
            RemoteStatusListener.LOGGER.warn("Unexpected exception", exception);
            if (!this.e()) {
                RemoteStatusListener.LOGGER.error("Failed to recover from exception, shutting down!");
                this.a = false;
            }

        }
    }

    private boolean e() {
        try {
            this.k = new DatagramSocket(this.f, InetAddress.getByName(this.n));
            this.k.setSoTimeout(500);
            return true;
        } catch (Exception exception) {
            RemoteStatusListener.LOGGER.warn("Unable to initialise query system on {}:{}", this.n, this.f, exception);
            return false;
        }
    }

    static class RemoteStatusChallenge {

        private final long time = (new Date()).getTime();
        private final int token;
        private final byte[] identity;
        private final byte[] d;
        private final String e;

        public RemoteStatusChallenge(DatagramPacket datagrampacket) {
            byte[] abyte = datagrampacket.getData();

            this.identity = new byte[4];
            this.identity[0] = abyte[3];
            this.identity[1] = abyte[4];
            this.identity[2] = abyte[5];
            this.identity[3] = abyte[6];
            this.e = new String(this.identity, StandardCharsets.UTF_8);
            this.token = (new Random()).nextInt(16777216);
            this.d = String.format("\t%s%d\u0000", this.e, this.token).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean a(long i) {
            return this.time < i;
        }

        public int a() {
            return this.token;
        }

        public byte[] b() {
            return this.d;
        }

        public byte[] c() {
            return this.identity;
        }
    }
}
