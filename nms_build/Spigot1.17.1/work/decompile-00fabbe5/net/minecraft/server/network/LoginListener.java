package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect;
import net.minecraft.network.protocol.login.PacketLoginInCustomPayload;
import net.minecraft.network.protocol.login.PacketLoginInEncryptionBegin;
import net.minecraft.network.protocol.login.PacketLoginInListener;
import net.minecraft.network.protocol.login.PacketLoginInStart;
import net.minecraft.network.protocol.login.PacketLoginOutDisconnect;
import net.minecraft.network.protocol.login.PacketLoginOutEncryptionBegin;
import net.minecraft.network.protocol.login.PacketLoginOutSetCompression;
import net.minecraft.network.protocol.login.PacketLoginOutSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.world.entity.player.EntityHuman;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginListener implements PacketLoginInListener {

    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;
    private static final Random RANDOM = new Random();
    private final byte[] nonce = new byte[4];
    final MinecraftServer server;
    public final NetworkManager connection;
    LoginListener.EnumProtocolState state;
    private int tick;
    @Nullable
    GameProfile gameProfile;
    private final String serverId;
    @Nullable
    private EntityPlayer delayedAcceptPlayer;

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.state = LoginListener.EnumProtocolState.HELLO;
        this.serverId = "";
        this.server = minecraftserver;
        this.connection = networkmanager;
        LoginListener.RANDOM.nextBytes(this.nonce);
    }

    public void tick() {
        if (this.state == LoginListener.EnumProtocolState.READY_TO_ACCEPT) {
            this.c();
        } else if (this.state == LoginListener.EnumProtocolState.DELAY_ACCEPT) {
            EntityPlayer entityplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

            if (entityplayer == null) {
                this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                this.a(this.delayedAcceptPlayer);
                this.delayedAcceptPlayer = null;
            }
        }

        if (this.tick++ == 600) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.slow_login"));
        }

    }

    @Override
    public NetworkManager a() {
        return this.connection;
    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        try {
            LoginListener.LOGGER.info("Disconnecting {}: {}", this.d(), ichatbasecomponent.getString());
            this.connection.sendPacket(new PacketLoginOutDisconnect(ichatbasecomponent));
            this.connection.close(ichatbasecomponent);
        } catch (Exception exception) {
            LoginListener.LOGGER.error("Error whilst disconnecting player", exception);
        }

    }

    public void c() {
        if (!this.gameProfile.isComplete()) {
            this.gameProfile = this.a(this.gameProfile);
        }

        IChatBaseComponent ichatbasecomponent = this.server.getPlayerList().attemptLogin(this.connection.getSocketAddress(), this.gameProfile);

        if (ichatbasecomponent != null) {
            this.disconnect(ichatbasecomponent);
        } else {
            this.state = LoginListener.EnumProtocolState.ACCEPTED;
            if (this.server.av() >= 0 && !this.connection.isLocal()) {
                this.connection.sendPacket(new PacketLoginOutSetCompression(this.server.av()), (channelfuture) -> {
                    this.connection.setCompressionLevel(this.server.av(), true);
                });
            }

            this.connection.sendPacket(new PacketLoginOutSuccess(this.gameProfile));
            EntityPlayer entityplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

            try {
                EntityPlayer entityplayer1 = this.server.getPlayerList().processLogin(this.gameProfile);

                if (entityplayer != null) {
                    this.state = LoginListener.EnumProtocolState.DELAY_ACCEPT;
                    this.delayedAcceptPlayer = entityplayer1;
                } else {
                    this.a(entityplayer1);
                }
            } catch (Exception exception) {
                ChatMessage chatmessage = new ChatMessage("multiplayer.disconnect.invalid_player_data");

                this.connection.sendPacket(new PacketPlayOutKickDisconnect(chatmessage));
                this.connection.close(chatmessage);
            }
        }

    }

    private void a(EntityPlayer entityplayer) {
        this.server.getPlayerList().a(this.connection, entityplayer);
    }

    @Override
    public void a(IChatBaseComponent ichatbasecomponent) {
        LoginListener.LOGGER.info("{} lost connection: {}", this.d(), ichatbasecomponent.getString());
    }

    public String d() {
        return this.gameProfile != null ? this.gameProfile + " (" + this.connection.getSocketAddress() + ")" : String.valueOf(this.connection.getSocketAddress());
    }

    @Override
    public void a(PacketLoginInStart packetlogininstart) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet", new Object[0]);
        this.gameProfile = packetlogininstart.b();
        if (this.server.getOnlineMode() && !this.connection.isLocal()) {
            this.state = LoginListener.EnumProtocolState.KEY;
            this.connection.sendPacket(new PacketLoginOutEncryptionBegin("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
        } else {
            this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
        }

    }

    @Override
    public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.KEY, "Unexpected key packet", new Object[0]);
        PrivateKey privatekey = this.server.getKeyPair().getPrivate();

        final String s;

        try {
            if (!Arrays.equals(this.nonce, packetlogininencryptionbegin.b(privatekey))) {
                throw new IllegalStateException("Protocol error");
            }

            SecretKey secretkey = packetlogininencryptionbegin.a(privatekey);
            Cipher cipher = MinecraftEncryption.a(2, secretkey);
            Cipher cipher1 = MinecraftEncryption.a(1, secretkey);

            s = (new BigInteger(MinecraftEncryption.a("", this.server.getKeyPair().getPublic(), secretkey))).toString(16);
            this.state = LoginListener.EnumProtocolState.AUTHENTICATING;
            this.connection.a(cipher, cipher1);
        } catch (CryptographyException cryptographyexception) {
            throw new IllegalStateException("Protocol error", cryptographyexception);
        }

        Thread thread = new Thread("User Authenticator #" + LoginListener.UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                GameProfile gameprofile = LoginListener.this.gameProfile;

                try {
                    LoginListener.this.gameProfile = LoginListener.this.server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s, this.a());
                    if (LoginListener.this.gameProfile != null) {
                        LoginListener.LOGGER.info("UUID of player {} is {}", LoginListener.this.gameProfile.getName(), LoginListener.this.gameProfile.getId());
                        LoginListener.this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                    } else if (LoginListener.this.server.isEmbeddedServer()) {
                        LoginListener.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        LoginListener.this.gameProfile = LoginListener.this.a(gameprofile);
                        LoginListener.this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                    } else {
                        LoginListener.this.disconnect(new ChatMessage("multiplayer.disconnect.unverified_username"));
                        LoginListener.LOGGER.error("Username '{}' tried to join with an invalid session", gameprofile.getName());
                    }
                } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                    if (LoginListener.this.server.isEmbeddedServer()) {
                        LoginListener.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        LoginListener.this.gameProfile = LoginListener.this.a(gameprofile);
                        LoginListener.this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                    } else {
                        LoginListener.this.disconnect(new ChatMessage("multiplayer.disconnect.authservers_down"));
                        LoginListener.LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                }

            }

            @Nullable
            private InetAddress a() {
                SocketAddress socketaddress = LoginListener.this.connection.getSocketAddress();

                return LoginListener.this.server.W() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
            }
        };

        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LoginListener.LOGGER));
        thread.start();
    }

    @Override
    public void a(PacketLoginInCustomPayload packetloginincustompayload) {
        this.disconnect(new ChatMessage("multiplayer.disconnect.unexpected_query_response"));
    }

    protected GameProfile a(GameProfile gameprofile) {
        UUID uuid = EntityHuman.getOfflineUUID(gameprofile.getName());

        return new GameProfile(uuid, gameprofile.getName());
    }

    private static enum EnumProtocolState {

        HELLO, KEY, AUTHENTICATING, NEGOTIATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;

        private EnumProtocolState() {}
    }
}
