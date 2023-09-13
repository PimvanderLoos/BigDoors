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
    private static final String GAME_TYPE = "SMP";
    private static final String GAME_ID = "MINECRAFT";
    private static final long CHALLENGE_CHECK_INTERVAL = 30000L;
    private static final long RESPONSE_CACHE_TIME = 5000L;
    private long lastChallengeCheck;
    private final int port;
    private final int serverPort;
    private final int maxPlayers;
    private final String serverName;
    private final String worldName;
    private DatagramSocket socket;
    private final byte[] buffer = new byte[1460];
    private String hostIp;
    private String serverIp;
    private final Map<SocketAddress, RemoteStatusListener.RemoteStatusChallenge> validChallenges;
    private final RemoteStatusReply rulesResponse;
    private long lastRulesResponse;
    private final IMinecraftServer serverInterface;

    private RemoteStatusListener(IMinecraftServer iminecraftserver, int i) {
        super("Query Listener");
        this.serverInterface = iminecraftserver;
        this.port = i;
        this.serverIp = iminecraftserver.b();
        this.serverPort = iminecraftserver.d();
        this.serverName = iminecraftserver.q();
        this.maxPlayers = iminecraftserver.getMaxPlayers();
        this.worldName = iminecraftserver.getWorld();
        this.lastRulesResponse = 0L;
        this.hostIp = "0.0.0.0";
        if (!this.serverIp.isEmpty() && !this.hostIp.equals(this.serverIp)) {
            this.hostIp = this.serverIp;
        } else {
            this.serverIp = "0.0.0.0";

            try {
                InetAddress inetaddress = InetAddress.getLocalHost();

                this.hostIp = inetaddress.getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                RemoteStatusListener.LOGGER.warn("Unable to determine local host IP, please set server-ip in server.properties", unknownhostexception);
            }
        }

        this.rulesResponse = new RemoteStatusReply(1460);
        this.validChallenges = Maps.newHashMap();
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
        this.socket.send(new DatagramPacket(abyte, abyte.length, datagrampacket.getSocketAddress()));
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
                        remotestatusreply.a(this.serverName);
                        remotestatusreply.a("SMP");
                        remotestatusreply.a(this.worldName);
                        remotestatusreply.a(Integer.toString(this.serverInterface.getPlayerCount()));
                        remotestatusreply.a(Integer.toString(this.maxPlayers));
                        remotestatusreply.a((short) this.serverPort);
                        remotestatusreply.a(this.hostIp);
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

        if (i < this.lastRulesResponse + 5000L) {
            byte[] abyte = this.rulesResponse.a();
            byte[] abyte1 = this.a(datagrampacket.getSocketAddress());

            abyte[1] = abyte1[0];
            abyte[2] = abyte1[1];
            abyte[3] = abyte1[2];
            abyte[4] = abyte1[3];
            return abyte;
        } else {
            this.lastRulesResponse = i;
            this.rulesResponse.b();
            this.rulesResponse.a((int) 0);
            this.rulesResponse.a(this.a(datagrampacket.getSocketAddress()));
            this.rulesResponse.a("splitnum");
            this.rulesResponse.a((int) 128);
            this.rulesResponse.a((int) 0);
            this.rulesResponse.a("hostname");
            this.rulesResponse.a(this.serverName);
            this.rulesResponse.a("gametype");
            this.rulesResponse.a("SMP");
            this.rulesResponse.a("game_id");
            this.rulesResponse.a("MINECRAFT");
            this.rulesResponse.a("version");
            this.rulesResponse.a(this.serverInterface.getVersion());
            this.rulesResponse.a("plugins");
            this.rulesResponse.a(this.serverInterface.getPlugins());
            this.rulesResponse.a("map");
            this.rulesResponse.a(this.worldName);
            this.rulesResponse.a("numplayers");
            this.rulesResponse.a(this.serverInterface.getPlayerCount().makeConcatWithConstants < invokedynamic > (this.serverInterface.getPlayerCount()));
            this.rulesResponse.a("maxplayers");
            this.rulesResponse.a(this.maxPlayers.makeConcatWithConstants < invokedynamic > (this.maxPlayers));
            this.rulesResponse.a("hostport");
            this.rulesResponse.a(this.serverPort.makeConcatWithConstants < invokedynamic > (this.serverPort));
            this.rulesResponse.a("hostip");
            this.rulesResponse.a(this.hostIp);
            this.rulesResponse.a((int) 0);
            this.rulesResponse.a((int) 1);
            this.rulesResponse.a("player_");
            this.rulesResponse.a((int) 0);
            String[] astring = this.serverInterface.getPlayers();
            String[] astring1 = astring;
            int j = astring.length;

            for (int k = 0; k < j; ++k) {
                String s = astring1[k];

                this.rulesResponse.a(s);
            }

            this.rulesResponse.a((int) 0);
            return this.rulesResponse.a();
        }
    }

    private byte[] a(SocketAddress socketaddress) {
        return ((RemoteStatusListener.RemoteStatusChallenge) this.validChallenges.get(socketaddress)).c();
    }

    private Boolean c(DatagramPacket datagrampacket) {
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        if (!this.validChallenges.containsKey(socketaddress)) {
            return false;
        } else {
            byte[] abyte = datagrampacket.getData();

            return ((RemoteStatusListener.RemoteStatusChallenge) this.validChallenges.get(socketaddress)).a() == StatusChallengeUtils.c(abyte, 7, datagrampacket.getLength());
        }
    }

    private void d(DatagramPacket datagrampacket) throws IOException {
        RemoteStatusListener.RemoteStatusChallenge remotestatuslistener_remotestatuschallenge = new RemoteStatusListener.RemoteStatusChallenge(datagrampacket);

        this.validChallenges.put(datagrampacket.getSocketAddress(), remotestatuslistener_remotestatuschallenge);
        this.a(remotestatuslistener_remotestatuschallenge.b(), datagrampacket);
    }

    private void d() {
        if (this.running) {
            long i = SystemUtils.getMonotonicMillis();

            if (i >= this.lastChallengeCheck + 30000L) {
                this.lastChallengeCheck = i;
                this.validChallenges.values().removeIf((remotestatuslistener_remotestatuschallenge) -> {
                    return remotestatuslistener_remotestatuschallenge.a(i);
                });
            }
        }
    }

    public void run() {
        RemoteStatusListener.LOGGER.info("Query running on {}:{}", this.serverIp, this.port);
        this.lastChallengeCheck = SystemUtils.getMonotonicMillis();
        DatagramPacket datagrampacket = new DatagramPacket(this.buffer, this.buffer.length);

        try {
            while (this.running) {
                try {
                    this.socket.receive(datagrampacket);
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
            RemoteStatusListener.LOGGER.debug("closeSocket: {}:{}", this.serverIp, this.port);
            this.socket.close();
        }

    }

    @Override
    public boolean a() {
        return this.running ? true : (!this.e() ? false : super.a());
    }

    private void a(Exception exception) {
        if (this.running) {
            RemoteStatusListener.LOGGER.warn("Unexpected exception", exception);
            if (!this.e()) {
                RemoteStatusListener.LOGGER.error("Failed to recover from exception, shutting down!");
                this.running = false;
            }

        }
    }

    private boolean e() {
        try {
            this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp));
            this.socket.setSoTimeout(500);
            return true;
        } catch (Exception exception) {
            RemoteStatusListener.LOGGER.warn("Unable to initialise query system on {}:{}", this.serverIp, this.port, exception);
            return false;
        }
    }

    private static class RemoteStatusChallenge {

        private final long time = (new Date()).getTime();
        private final int challenge;
        private final byte[] identBytes;
        private final byte[] challengeBytes;
        private final String ident;

        public RemoteStatusChallenge(DatagramPacket datagrampacket) {
            byte[] abyte = datagrampacket.getData();

            this.identBytes = new byte[4];
            this.identBytes[0] = abyte[3];
            this.identBytes[1] = abyte[4];
            this.identBytes[2] = abyte[5];
            this.identBytes[3] = abyte[6];
            this.ident = new String(this.identBytes, StandardCharsets.UTF_8);
            this.challenge = (new Random()).nextInt(16777216);
            this.challengeBytes = String.format("\t%s%d\u0000", this.ident, this.challenge).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean a(long i) {
            return this.time < i;
        }

        public int a() {
            return this.challenge;
        }

        public byte[] b() {
            return this.challengeBytes;
        }

        public byte[] c() {
            return this.identBytes;
        }

        public String d() {
            return this.ident;
        }
    }
}
