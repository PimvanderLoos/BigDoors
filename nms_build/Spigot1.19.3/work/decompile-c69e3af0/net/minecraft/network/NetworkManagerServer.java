package net.minecraft.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect;
import org.slf4j.Logger;

public class NetworkManagerServer extends NetworkManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final IChatBaseComponent EXCEED_REASON = IChatBaseComponent.translatable("disconnect.exceeded_packet_rate");
    private final int rateLimitPacketsPerSecond;

    public NetworkManagerServer(int i) {
        super(EnumProtocolDirection.SERVERBOUND);
        this.rateLimitPacketsPerSecond = i;
    }

    @Override
    protected void tickSecond() {
        super.tickSecond();
        float f = this.getAverageReceivedPackets();

        if (f > (float) this.rateLimitPacketsPerSecond) {
            NetworkManagerServer.LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", f);
            this.send(new PacketPlayOutKickDisconnect(NetworkManagerServer.EXCEED_REASON), PacketSendListener.thenRun(() -> {
                this.disconnect(NetworkManagerServer.EXCEED_REASON);
            }));
            this.setReadOnly();
        }

    }
}
