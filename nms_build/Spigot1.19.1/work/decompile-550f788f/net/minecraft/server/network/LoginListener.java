package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.chat.ThrowingComponent;
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
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class LoginListener implements TickablePacketListener, PacketLoginInListener {

    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;
    private static final RandomSource RANDOM = RandomSource.create();
    private static final IChatBaseComponent MISSING_PROFILE_PUBLIC_KEY = IChatBaseComponent.translatable("multiplayer.disconnect.missing_public_key");
    private static final IChatBaseComponent INVALID_SIGNATURE = IChatBaseComponent.translatable("multiplayer.disconnect.invalid_public_key_signature");
    private static final IChatBaseComponent INVALID_PUBLIC_KEY = IChatBaseComponent.translatable("multiplayer.disconnect.invalid_public_key");
    private final byte[] nonce;
    final MinecraftServer server;
    public final NetworkManager connection;
    LoginListener.EnumProtocolState state;
    private int tick;
    @Nullable
    GameProfile gameProfile;
    private final String serverId;
    @Nullable
    private EntityPlayer delayedAcceptPlayer;
    @Nullable
    private ProfilePublicKey.a profilePublicKeyData;

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.state = LoginListener.EnumProtocolState.HELLO;
        this.serverId = "";
        this.server = minecraftserver;
        this.connection = networkmanager;
        this.nonce = Ints.toByteArray(LoginListener.RANDOM.nextInt());
    }

    @Override
    public void tick() {
        if (this.state == LoginListener.EnumProtocolState.READY_TO_ACCEPT) {
            this.handleAcceptedLogin();
        } else if (this.state == LoginListener.EnumProtocolState.DELAY_ACCEPT) {
            EntityPlayer entityplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

            if (entityplayer == null) {
                this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                this.placeNewPlayer(this.delayedAcceptPlayer);
                this.delayedAcceptPlayer = null;
            }
        }

        if (this.tick++ == 600) {
            this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.slow_login"));
        }

    }

    @Override
    public NetworkManager getConnection() {
        return this.connection;
    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        try {
            LoginListener.LOGGER.info("Disconnecting {}: {}", this.getUserName(), ichatbasecomponent.getString());
            this.connection.send(new PacketLoginOutDisconnect(ichatbasecomponent));
            this.connection.disconnect(ichatbasecomponent);
        } catch (Exception exception) {
            LoginListener.LOGGER.error("Error whilst disconnecting player", exception);
        }

    }

    public void handleAcceptedLogin() {
        ProfilePublicKey profilepublickey = null;

        if (!this.gameProfile.isComplete()) {
            this.gameProfile = this.createFakeProfile(this.gameProfile);
        } else {
            try {
                SignatureValidator signaturevalidator = this.server.getServiceSignatureValidator();

                profilepublickey = validatePublicKey(this.profilePublicKeyData, this.gameProfile.getId(), signaturevalidator, this.server.enforceSecureProfile());
            } catch (LoginListener.a loginlistener_a) {
                LoginListener.LOGGER.error(loginlistener_a.getMessage(), loginlistener_a.getCause());
                if (!this.connection.isMemoryConnection()) {
                    this.disconnect(loginlistener_a.getComponent());
                    return;
                }
            }
        }

        IChatBaseComponent ichatbasecomponent = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);

        if (ichatbasecomponent != null) {
            this.disconnect(ichatbasecomponent);
        } else {
            this.state = LoginListener.EnumProtocolState.ACCEPTED;
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new PacketLoginOutSetCompression(this.server.getCompressionThreshold()), PacketSendListener.thenRun(() -> {
                    this.connection.setupCompression(this.server.getCompressionThreshold(), true);
                }));
            }

            this.connection.send(new PacketLoginOutSuccess(this.gameProfile));
            EntityPlayer entityplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

            try {
                EntityPlayer entityplayer1 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile, profilepublickey);

                if (entityplayer != null) {
                    this.state = LoginListener.EnumProtocolState.DELAY_ACCEPT;
                    this.delayedAcceptPlayer = entityplayer1;
                } else {
                    this.placeNewPlayer(entityplayer1);
                }
            } catch (Exception exception) {
                LoginListener.LOGGER.error("Couldn't place player in world", exception);
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.translatable("multiplayer.disconnect.invalid_player_data");

                this.connection.send(new PacketPlayOutKickDisconnect(ichatmutablecomponent));
                this.connection.disconnect(ichatmutablecomponent);
            }
        }

    }

    private void placeNewPlayer(EntityPlayer entityplayer) {
        this.server.getPlayerList().placeNewPlayer(this.connection, entityplayer);
    }

    @Override
    public void onDisconnect(IChatBaseComponent ichatbasecomponent) {
        LoginListener.LOGGER.info("{} lost connection: {}", this.getUserName(), ichatbasecomponent.getString());
    }

    public String getUserName() {
        return this.gameProfile != null ? this.gameProfile + " (" + this.connection.getRemoteAddress() + ")" : String.valueOf(this.connection.getRemoteAddress());
    }

    @Nullable
    private static ProfilePublicKey validatePublicKey(@Nullable ProfilePublicKey.a profilepublickey_a, UUID uuid, SignatureValidator signaturevalidator, boolean flag) throws LoginListener.a {
        try {
            if (profilepublickey_a == null) {
                if (flag) {
                    throw new LoginListener.a(LoginListener.MISSING_PROFILE_PUBLIC_KEY);
                } else {
                    return null;
                }
            } else {
                return ProfilePublicKey.createValidated(signaturevalidator, uuid, profilepublickey_a);
            }
        } catch (MissingException missingexception) {
            if (flag) {
                throw new LoginListener.a(LoginListener.INVALID_SIGNATURE, missingexception);
            } else {
                return null;
            }
        } catch (CryptographyException cryptographyexception) {
            throw new LoginListener.a(LoginListener.INVALID_PUBLIC_KEY, cryptographyexception);
        } catch (Exception exception) {
            throw new LoginListener.a(LoginListener.INVALID_SIGNATURE, exception);
        }
    }

    @Override
    public void handleHello(PacketLoginInStart packetlogininstart) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet", new Object[0]);
        Validate.validState(isValidUsername(packetlogininstart.name()), "Invalid characters in username", new Object[0]);
        this.profilePublicKeyData = (ProfilePublicKey.a) packetlogininstart.publicKey().orElse((Object) null);
        GameProfile gameprofile = this.server.getSingleplayerProfile();

        if (gameprofile != null && packetlogininstart.name().equalsIgnoreCase(gameprofile.getName())) {
            this.gameProfile = gameprofile;
            this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
        } else {
            this.gameProfile = new GameProfile((UUID) null, packetlogininstart.name());
            if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
                this.state = LoginListener.EnumProtocolState.KEY;
                this.connection.send(new PacketLoginOutEncryptionBegin("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
            } else {
                this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
            }

        }
    }

    public static boolean isValidUsername(String s) {
        return s.chars().filter((i) -> {
            return i <= 32 || i >= 127;
        }).findAny().isEmpty();
    }

    @Override
    public void handleKey(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.KEY, "Unexpected key packet", new Object[0]);

        final String s;

        try {
            PrivateKey privatekey = this.server.getKeyPair().getPrivate();

            if (this.profilePublicKeyData != null) {
                ProfilePublicKey profilepublickey = ProfilePublicKey.createTrusted(this.profilePublicKeyData);

                if (!packetlogininencryptionbegin.isChallengeSignatureValid(this.nonce, profilepublickey)) {
                    throw new IllegalStateException("Protocol error");
                }
            } else if (!packetlogininencryptionbegin.isNonceValid(this.nonce, privatekey)) {
                throw new IllegalStateException("Protocol error");
            }

            SecretKey secretkey = packetlogininencryptionbegin.getSecretKey(privatekey);
            Cipher cipher = MinecraftEncryption.getCipher(2, secretkey);
            Cipher cipher1 = MinecraftEncryption.getCipher(1, secretkey);

            s = (new BigInteger(MinecraftEncryption.digestData("", this.server.getKeyPair().getPublic(), secretkey))).toString(16);
            this.state = LoginListener.EnumProtocolState.AUTHENTICATING;
            this.connection.setEncryptionKey(cipher, cipher1);
        } catch (CryptographyException cryptographyexception) {
            throw new IllegalStateException("Protocol error", cryptographyexception);
        }

        Thread thread = new Thread("User Authenticator #" + LoginListener.UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                GameProfile gameprofile = LoginListener.this.gameProfile;

                try {
                    LoginListener.this.gameProfile = LoginListener.this.server.getSessionService().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s, this.getAddress());
                    if (LoginListener.this.gameProfile != null) {
                        LoginListener.LOGGER.info("UUID of player {} is {}", LoginListener.this.gameProfile.getName(), LoginListener.this.gameProfile.getId());
                        LoginListener.this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                    } else if (LoginListener.this.server.isSingleplayer()) {
                        LoginListener.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        LoginListener.this.gameProfile = gameprofile;
                        LoginListener.this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                    } else {
                        LoginListener.this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.unverified_username"));
                        LoginListener.LOGGER.error("Username '{}' tried to join with an invalid session", gameprofile.getName());
                    }
                } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                    if (LoginListener.this.server.isSingleplayer()) {
                        LoginListener.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        LoginListener.this.gameProfile = gameprofile;
                        LoginListener.this.state = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                    } else {
                        LoginListener.this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.authservers_down"));
                        LoginListener.LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                }

            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress socketaddress = LoginListener.this.connection.getRemoteAddress();

                return LoginListener.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
            }
        };

        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LoginListener.LOGGER));
        thread.start();
    }

    @Override
    public void handleCustomQueryPacket(PacketLoginInCustomPayload packetloginincustompayload) {
        this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.unexpected_query_response"));
    }

    protected GameProfile createFakeProfile(GameProfile gameprofile) {
        UUID uuid = UUIDUtil.createOfflinePlayerUUID(gameprofile.getName());

        return new GameProfile(uuid, gameprofile.getName());
    }

    private static enum EnumProtocolState {

        HELLO, KEY, AUTHENTICATING, NEGOTIATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;

        private EnumProtocolState() {}
    }

    private static class a extends ThrowingComponent {

        public a(IChatBaseComponent ichatbasecomponent) {
            super(ichatbasecomponent);
        }

        public a(IChatBaseComponent ichatbasecomponent, Throwable throwable) {
            super(ichatbasecomponent, throwable);
        }
    }
}
