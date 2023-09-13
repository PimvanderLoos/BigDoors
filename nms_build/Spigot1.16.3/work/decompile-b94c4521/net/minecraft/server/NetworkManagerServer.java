package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkManagerServer extends NetworkManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final IChatBaseComponent h = new ChatMessage("disconnect.exceeded_packet_rate");
    private final int i;

    public NetworkManagerServer(int i) {
        super(EnumProtocolDirection.SERVERBOUND);
        this.i = i;
    }

    @Override
    protected void b() {
        super.b();
        float f = this.n();

        if (f > (float) this.i) {
            NetworkManagerServer.LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", f);
            this.sendPacket(new PacketPlayOutKickDisconnect(NetworkManagerServer.h), (future) -> {
                this.close(NetworkManagerServer.h);
            });
            this.stopReading();
        }

    }
}
