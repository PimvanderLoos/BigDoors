package net.minecraft.server.rcon.thread;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.IMinecraftServer;
import net.minecraft.server.rcon.StatusChallengeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoteControlSession extends RemoteConnectionThread {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SERVERDATA_AUTH = 3;
    private static final int SERVERDATA_EXECCOMMAND = 2;
    private static final int SERVERDATA_RESPONSE_VALUE = 0;
    private static final int SERVERDATA_AUTH_RESPONSE = 2;
    private static final int SERVERDATA_AUTH_FAILURE = -1;
    private boolean authed;
    private final Socket client;
    private final byte[] buf = new byte[1460];
    private final String rconPassword;
    private final IMinecraftServer serverInterface;

    RemoteControlSession(IMinecraftServer iminecraftserver, String s, Socket socket) {
        super("RCON Client " + socket.getInetAddress());
        this.serverInterface = iminecraftserver;
        this.client = socket;

        try {
            this.client.setSoTimeout(0);
        } catch (Exception exception) {
            this.running = false;
        }

        this.rconPassword = s;
    }

    public void run() {
        while (true) {
            try {
                if (!this.running) {
                    return;
                }

                BufferedInputStream bufferedinputstream = new BufferedInputStream(this.client.getInputStream());
                int i = bufferedinputstream.read(this.buf, 0, 1460);

                if (10 > i) {
                    return;
                }

                byte b0 = 0;
                int j = StatusChallengeUtils.b(this.buf, 0, i);

                if (j == i - 4) {
                    int k = b0 + 4;
                    int l = StatusChallengeUtils.b(this.buf, k, i);

                    k += 4;
                    int i1 = StatusChallengeUtils.a(this.buf, k);

                    k += 4;
                    switch (i1) {
                        case 2:
                            if (this.authed) {
                                String s = StatusChallengeUtils.a(this.buf, k, i);

                                try {
                                    this.a(l, this.serverInterface.executeRemoteCommand(s));
                                } catch (Exception exception) {
                                    this.a(l, "Error executing: " + s + " (" + exception.getMessage() + ")");
                                }
                                continue;
                            }

                            this.d();
                            continue;
                        case 3:
                            String s1 = StatusChallengeUtils.a(this.buf, k, i);
                            int j1 = k + s1.length();

                            if (!s1.isEmpty() && s1.equals(this.rconPassword)) {
                                this.authed = true;
                                this.a(l, 2, "");
                                continue;
                            }

                            this.authed = false;
                            this.d();
                            continue;
                        default:
                            this.a(l, String.format("Unknown request %s", Integer.toHexString(i1)));
                            continue;
                    }
                }
            } catch (IOException ioexception) {
                return;
            } catch (Exception exception1) {
                RemoteControlSession.LOGGER.error("Exception whilst parsing RCON input", exception1);
                return;
            } finally {
                this.e();
                RemoteControlSession.LOGGER.info("Thread {} shutting down", this.name);
                this.running = false;
            }

            return;
        }
    }

    private void a(int i, int j, String s) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1248);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);

        dataoutputstream.writeInt(Integer.reverseBytes(abyte.length + 10));
        dataoutputstream.writeInt(Integer.reverseBytes(i));
        dataoutputstream.writeInt(Integer.reverseBytes(j));
        dataoutputstream.write(abyte);
        dataoutputstream.write(0);
        dataoutputstream.write(0);
        this.client.getOutputStream().write(bytearrayoutputstream.toByteArray());
    }

    private void d() throws IOException {
        this.a(-1, 2, "");
    }

    private void a(int i, String s) throws IOException {
        int j = s.length();

        do {
            int k = 4096 <= j ? 4096 : j;

            this.a(i, 0, s.substring(0, k));
            s = s.substring(k);
            j = s.length();
        } while (0 != j);

    }

    @Override
    public void b() {
        this.running = false;
        this.e();
        super.b();
    }

    private void e() {
        try {
            this.client.close();
        } catch (IOException ioexception) {
            RemoteControlSession.LOGGER.warn("Failed to close socket", ioexception);
        }

    }
}
