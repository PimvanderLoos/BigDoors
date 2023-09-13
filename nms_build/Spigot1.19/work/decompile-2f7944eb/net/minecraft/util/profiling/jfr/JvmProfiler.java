package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import org.slf4j.Logger;

public interface JvmProfiler {

    JvmProfiler INSTANCE = Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent() ? JfrProfiler.getInstance() : new JvmProfiler.a();

    boolean start(Environment environment);

    Path stop();

    boolean isRunning();

    boolean isAvailable();

    void onServerTick(float f);

    void onPacketReceived(int i, int j, SocketAddress socketaddress, int k);

    void onPacketSent(int i, int j, SocketAddress socketaddress, int k);

    @Nullable
    ProfiledDuration onWorldLoadedStarted();

    @Nullable
    ProfiledDuration onChunkGenerate(ChunkCoordIntPair chunkcoordintpair, ResourceKey<World> resourcekey, String s);

    public static class a implements JvmProfiler {

        private static final Logger LOGGER = LogUtils.getLogger();
        static final ProfiledDuration noOpCommit = () -> {
        };

        public a() {}

        @Override
        public boolean start(Environment environment) {
            JvmProfiler.a.LOGGER.warn("Attempted to start Flight Recorder, but it's not supported on this JVM");
            return false;
        }

        @Override
        public Path stop() {
            throw new IllegalStateException("Attempted to stop Flight Recorder, but it's not supported on this JVM");
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void onPacketReceived(int i, int j, SocketAddress socketaddress, int k) {}

        @Override
        public void onPacketSent(int i, int j, SocketAddress socketaddress, int k) {}

        @Override
        public void onServerTick(float f) {}

        @Override
        public ProfiledDuration onWorldLoadedStarted() {
            return JvmProfiler.a.noOpCommit;
        }

        @Nullable
        @Override
        public ProfiledDuration onChunkGenerate(ChunkCoordIntPair chunkcoordintpair, ResourceKey<World> resourcekey, String s) {
            return null;
        }
    }
}
