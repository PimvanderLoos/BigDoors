package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskScheduler extends Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk> {

    private static final Logger b = LogManager.getLogger();
    private final World c;
    private final ChunkGenerator<?> d;
    private final IChunkLoader e;
    private final IAsyncTaskHandler f;
    private final Long2ObjectMap<Scheduler.a> g = Long2ObjectMaps.synchronize(new ExpiringMap(8192, 5000) {
        protected boolean a(Scheduler.a scheduler_a) {
            ProtoChunk protochunk = (ProtoChunk) scheduler_a.a();

            return !protochunk.ab_() && !protochunk.h();
        }
    });

    public ChunkTaskScheduler(int i, World world, ChunkGenerator<?> chunkgenerator, IChunkLoader ichunkloader, IAsyncTaskHandler iasynctaskhandler) {
        super("WorldGen", i, ChunkStatus.FINALIZED, () -> {
            return new EnumMap(ChunkStatus.class);
        }, () -> {
            return new EnumMap(ChunkStatus.class);
        });
        this.c = world;
        this.d = chunkgenerator;
        this.e = ichunkloader;
        this.f = iasynctaskhandler;
    }

    protected Scheduler.a a(ChunkCoordIntPair chunkcoordintpair) {
        return (Scheduler.a) this.g.computeIfAbsent(Long.valueOf(chunkcoordintpair.a()), (olong) -> {
            ProtoChunk protochunk = this.a(chunkcoordintpair.x, chunkcoordintpair.z);

            if (protochunk != null) {
                protochunk.setLastSaved(this.c.getTime());
                return new Scheduler.a(chunkcoordintpair, protochunk, protochunk.i());
            } else {
                return new Scheduler.a(chunkcoordintpair, new ProtoChunk(chunkcoordintpair, ChunkConverter.a), ChunkStatus.EMPTY);
            }
        });
    }

    protected ProtoChunk a(ChunkCoordIntPair chunkcoordintpair, ChunkStatus chunkstatus, Map<ChunkCoordIntPair, ProtoChunk> map) {
        return chunkstatus.a(this.c, this.d, map, chunkcoordintpair.x, chunkcoordintpair.z);
    }

    @Nullable
    private ProtoChunk a(int i, int j) {
        try {
            return this.e.b(this.c, i, j, (ichunkaccess) -> {
            });
        } catch (ReportedException reportedexception) {
            throw reportedexception;
        } catch (Exception exception) {
            ChunkTaskScheduler.b.error("Couldn\'t load protochunk", exception);
            return null;
        }
    }

    protected Scheduler.a a(ChunkCoordIntPair chunkcoordintpair, Scheduler.a scheduler_a) {
        ((ProtoChunk) scheduler_a.a()).a(1);
        return scheduler_a;
    }

    protected void b(ChunkCoordIntPair chunkcoordintpair, Scheduler.a scheduler_a) {
        ((ProtoChunk) scheduler_a.a()).a(-1);
    }

    public void a() {
        this.g.values().forEach((scheduler_a) -> {
            ProtoChunk protochunk = (ProtoChunk) scheduler_a.a();

            if (protochunk.h() && protochunk.i().d() == ChunkStatus.Type.PROTOCHUNK) {
                try {
                    protochunk.setLastSaved(this.c.getTime());
                    this.e.saveChunk(this.c, protochunk);
                    protochunk.a(false);
                } catch (IOException ioexception) {
                    ChunkTaskScheduler.b.error("Couldn\'t save chunk", ioexception);
                } catch (ExceptionWorldConflict exceptionworldconflict) {
                    ChunkTaskScheduler.b.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
                }
            }

        });
    }
}
