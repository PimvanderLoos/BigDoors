package net.minecraft.server.rcon.thread;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
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
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.server.IMinecraftServer;
import net.minecraft.server.rcon.RemoteStatusReply;
import net.minecraft.server.rcon.StatusChallengeUtils;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class RemoteStatusListener extends RemoteConnectionThread {

    private static final Logger LOGGER = LogUtils.getLogger();
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
        this.serverIp = iminecraftserver.getServerIp();
        this.serverPort = iminecraftserver.getServerPort();
        this.serverName = iminecraftserver.getServerName();
        this.maxPlayers = iminecraftserver.getMaxPlayers();
        this.worldName = iminecraftserver.getLevelIdName();
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
    public static RemoteStatusListener create(IMinecraftServer iminecraftserver) {
        int i = iminecraftserver.getProperties().queryPort;

        if (0 < i && 65535 >= i) {
            RemoteStatusListener remotestatuslistener = new RemoteStatusListener(iminecraftserver, i);

            return !remotestatuslistener.start() ? null : remotestatuslistener;
        } else {
            RemoteStatusListener.LOGGER.warn("Invalid query port {} found in server.properties (queries disabled)", i);
            return null;
        }
    }

    private void sendTo(byte[] abyte, DatagramPacket datagrampacket) throws IOException {
        this.socket.send(new DatagramPacket(abyte, abyte.length, datagrampacket.getSocketAddress()));
    }

    private boolean processPacket(DatagramPacket datagrampacket) throws IOException {
        byte[] abyte = datagrampacket.getData();
        int i = datagrampacket.getLength();
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        RemoteStatusListener.LOGGER.debug("Packet len {} [{}]", i, socketaddress);
        if (3 <= i && -2 == abyte[0] && -3 == abyte[1]) {
            RemoteStatusListener.LOGGER.debug("Packet '{}' [{}]", StatusChallengeUtils.toHexString(abyte[2]), socketaddress);
            switch (abyte[2]) {
                case 0:
                    if (!this.validChallenge(datagrampacket)) {
                        RemoteStatusListener.LOGGER.debug("Invalid challenge [{}]", socketaddress);
                        return false;
                    } else if (15 == i) {
                        this.sendTo(this.buildRuleResponse(datagrampacket), datagrampacket);
                        RemoteStatusListener.LOGGER.debug("Rules [{}]", socketaddress);
                    } else {
                        RemoteStatusReply remotestatusreply = new RemoteStatusReply(1460);

                        remotestatusreply.write(0);
                        remotestatusreply.writeBytes(this.getIdentBytes(datagrampacket.getSocketAddress()));
                        remotestatusreply.writeString(this.serverName);
                        remotestatusreply.writeString("SMP");
                        remotestatusreply.writeString(this.worldName);
                        remotestatusreply.writeString(Integer.toString(this.serverInterface.getPlayerCount()));
                        remotestatusreply.writeString(Integer.toString(this.maxPlayers));
                        remotestatusreply.writeShort((short) this.serverPort);
                        remotestatusreply.writeString(this.hostIp);
                        this.sendTo(remotestatusreply.toByteArray(), datagrampacket);
                        RemoteStatusListener.LOGGER.debug("Status [{}]", socketaddress);
                    }
                default:
                    return true;
                case 9:
                    this.sendChallenge(datagrampacket);
                    RemoteStatusListener.LOGGER.debug("Challenge [{}]", socketaddress);
                    return true;
            }
        } else {
            RemoteStatusListener.LOGGER.debug("Invalid packet [{}]", socketaddress);
            return false;
        }
    }

    private byte[] buildRuleResponse(DatagramPacket datagrampacket) throws IOException {
        long i = SystemUtils.getMillis();

        if (i < this.lastRulesResponse + 5000L) {
            byte[] abyte = this.rulesResponse.toByteArray();
            byte[] abyte1 = this.getIdentBytes(datagrampacket.getSocketAddress());

            abyte[1] = abyte1[0];
            abyte[2] = abyte1[1];
            abyte[3] = abyte1[2];
            abyte[4] = abyte1[3];
            return abyte;
        } else {
            this.lastRulesResponse = i;
            this.rulesResponse.reset();
            this.rulesResponse.write(0);
            this.rulesResponse.writeBytes(this.getIdentBytes(datagrampacket.getSocketAddress()));
            this.rulesResponse.writeString("splitnum");
            this.rulesResponse.write(128);
            this.rulesResponse.write(0);
            this.rulesResponse.writeString("hostname");
            this.rulesResponse.writeString(this.serverName);
            this.rulesResponse.writeString("gametype");
            this.rulesResponse.writeString("SMP");
            this.rulesResponse.writeString("game_id");
            this.rulesResponse.writeString("MINECRAFT");
            this.rulesResponse.writeString("version");
            this.rulesResponse.writeString(this.serverInterface.getServerVersion());
            this.rulesResponse.writeString("plugins");
            this.rulesResponse.writeString(this.serverInterface.getPluginNames());
            this.rulesResponse.writeString("map");
            this.rulesResponse.writeString(this.worldName);
            this.rulesResponse.writeString("numplayers");
            this.rulesResponse.writeString(this.serverInterface.getPlayerCount().makeConcatWithConstants < invokedynamic > (this.serverInterface.getPlayerCount()));
            this.rulesResponse.writeString("maxplayers");
            this.rulesResponse.writeString(this.maxPlayers.makeConcatWithConstants < invokedynamic > (this.maxPlayers));
            this.rulesResponse.writeString("hostport");
            this.rulesResponse.writeString(this.serverPort.makeConcatWithConstants < invokedynamic > (this.serverPort));
            this.rulesResponse.writeString("hostip");
            this.rulesResponse.writeString(this.hostIp);
            this.rulesResponse.write(0);
            this.rulesResponse.write(1);
            this.rulesResponse.writeString("player_");
            this.rulesResponse.write(0);
            String[] astring = this.serverInterface.getPlayerNames();
            String[] astring1 = astring;
            int j = astring.length;

            for (int k = 0; k < j; ++k) {
                String s = astring1[k];

                this.rulesResponse.writeString(s);
            }

            this.rulesResponse.write(0);
            return this.rulesResponse.toByteArray();
        }
    }

    private byte[] getIdentBytes(SocketAddress socketaddress) {
        return ((RemoteStatusListener.RemoteStatusChallenge) this.validChallenges.get(socketaddress)).getIdentBytes();
    }

    private Boolean validChallenge(DatagramPacket datagrampacket) {
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        if (!this.validChallenges.containsKey(socketaddress)) {
            return false;
        } else {
            byte[] abyte = datagrampacket.getData();

            return ((RemoteStatusListener.RemoteStatusChallenge) this.validChallenges.get(socketaddress)).getChallenge() == StatusChallengeUtils.intFromNetworkByteArray(abyte, 7, datagrampacket.getLength());
        }
    }

    private void sendChallenge(DatagramPacket datagrampacket) throws IOException {
        RemoteStatusListener.RemoteStatusChallenge remotestatuslistener_remotestatuschallenge = new RemoteStatusListener.RemoteStatusChallenge(datagrampacket);

        this.validChallenges.put(datagrampacket.getSocketAddress(), remotestatuslistener_remotestatuschallenge);
        this.sendTo(remotestatuslistener_remotestatuschallenge.getChallengeBytes(), datagrampacket);
    }

    private void pruneChallenges() {
        if (this.running) {
            long i = SystemUtils.getMillis();

            if (i >= this.lastChallengeCheck + 30000L) {
                this.lastChallengeCheck = i;
                this.validChallenges.values().removeIf((remotestatuslistener_remotestatuschallenge) -> {
                    return remotestatuslistener_remotestatuschallenge.before(i);
                });
            }
        }
    }

    public void run() {
        RemoteStatusListener.LOGGER.info("Query running on {}:{}", this.serverIp, this.port);
        this.lastChallengeCheck = SystemUtils.getMillis();
        DatagramPacket datagrampacket = new DatagramPacket(this.buffer, this.buffer.length);

        try {
            while (this.running) {
                try {
                    this.socket.receive(datagrampacket);
                    this.pruneChallenges();
                    this.processPacket(datagrampacket);
                } catch (SocketTimeoutException sockettimeoutexception) {
                    this.pruneChallenges();
                } catch (PortUnreachableException portunreachableexception) {
                    ;
                } catch (IOException ioexception) {
                    this.recoverSocketError(ioexception);
                }
            }
        } finally {
            RemoteStatusListener.LOGGER.debug("closeSocket: {}:{}", this.serverIp, this.port);
            this.socket.close();
        }

    }

    @Override
    public boolean start() {
        return this.running ? true : (!this.initSocket() ? false : super.start());
    }

    private void recoverSocketError(Exception exception) {
        if (this.running) {
            RemoteStatusListener.LOGGER.warn("Unexpected exception", exception);
            if (!this.initSocket()) {
                RemoteStatusListener.LOGGER.error("Failed to recover from exception, shutting down!");
                this.running = false;
            }

        }
    }

    private boolean initSocket() {
        try {
            this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp));
            this.socket.setSoTimeout(500);
            return true;
        } catch (Exception exception) {
            RemoteStatusListener.LOGGER.warn("Unable to initialise query system on {}:{}", new Object[]{this.serverIp, this.port, exception});
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
            this.challenge = RandomSource.create().nextInt(16777216);
            this.challengeBytes = String.format(Locale.ROOT, "\t%s%d\u0000", this.ident, this.challenge).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean before(long i) {
            return this.time < i;
        }

        public int getChallenge() {
            return this.challenge;
        }

        public byte[] getChallengeBytes() {
            return this.challengeBytes;
        }

        public byte[] getIdentBytes() {
            return this.identBytes;
        }

        public String getIdent() {
            return this.ident;
        }
    }
}
