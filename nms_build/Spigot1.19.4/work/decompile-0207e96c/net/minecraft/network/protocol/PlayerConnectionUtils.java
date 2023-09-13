package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
import net.minecraft.network.PacketListener;
import net.minecraft.server.CancelledPacketHandleException;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.thread.IAsyncTaskHandler;
import org.slf4j.Logger;

public class PlayerConnectionUtils {

    private static final Logger LOGGER = LogUtils.getLogger();

    public PlayerConnectionUtils() {}

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T t0, WorldServer worldserver) throws CancelledPacketHandleException {
        ensureRunningOnSameThread(packet, t0, (IAsyncTaskHandler) worldserver.getServer());
    }

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T t0, IAsyncTaskHandler<?> iasynctaskhandler) throws CancelledPacketHandleException {
        if (!iasynctaskhandler.isSameThread()) {
            iasynctaskhandler.executeIfPossible(() -> {
                if (t0.isAcceptingMessages()) {
                    try {
                        packet.handle(t0);
                    } catch (Exception exception) {
                        if (t0.shouldPropagateHandlingExceptions()) {
                            throw exception;
                        }

                        PlayerConnectionUtils.LOGGER.error("Failed to handle packet {}, suppressing error", packet, exception);
                    }
                } else {
                    PlayerConnectionUtils.LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
                }

            });
            throw CancelledPacketHandleException.RUNNING_ON_DIFFERENT_THREAD;
        }
    }
}
