package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {

    private static final Logger a = LogManager.getLogger();
    private static final ThreadFactory b = (new ThreadFactoryBuilder()).setDaemon(true).build();
    private final IDataManager c;
    private final PersistentCollection d;
    private final Thread e;
    private boolean f = true;
    private boolean g = false;
    private float h;
    private float i;
    private float j;
    private int k;
    private int l;
    private int m;
    private float n;
    private int o;
    private int p = 0;
    private int q = 0;
    private IChatBaseComponent r = new ChatMessage("optimizeWorld.stage.counting", new Object[0]);
    private final String s;

    public WorldUpgrader(String s, Convertable convertable, WorldData worlddata) {
        this.s = worlddata.getName();
        this.c = convertable.a(s, (MinecraftServer) null);
        this.c.saveWorldData(worlddata);
        this.d = new PersistentCollection(this.c);
        this.e = WorldUpgrader.b.newThread(this::o);
        this.e.setUncaughtExceptionHandler(this::a);
        this.e.start();
    }

    private void a(Thread thread, Throwable throwable) {
        WorldUpgrader.a.error("Error upgrading world", throwable);
        this.f = false;
        this.r = new ChatMessage("optimizeWorld.stage.failed", new Object[0]);
    }

    public void a() {
        this.f = false;

        try {
            this.e.join();
        } catch (InterruptedException interruptedexception) {
            ;
        }

    }

    private void o() {
        File file = this.c.getDirectory();
        WorldUpgraderIterator worldupgraderiterator = new WorldUpgraderIterator(file);

        worldupgraderiterator.a();
        ChunkRegionLoader chunkregionloader = new ChunkRegionLoader(file, this.c.i());
        ChunkRegionLoader chunkregionloader1 = new ChunkRegionLoader(new File(file, "DIM-1"), this.c.i());
        ChunkRegionLoader chunkregionloader2 = new ChunkRegionLoader(new File(file, "DIM1"), this.c.i());
        long i = SystemUtils.b();
        List list = worldupgraderiterator.b();
        List list1 = worldupgraderiterator.c();
        List list2 = worldupgraderiterator.d();

        this.o = worldupgraderiterator.b().size() + worldupgraderiterator.c().size() + worldupgraderiterator.d().size();
        float f = (float) (list.size() + list1.size() + list2.size());

        this.r = new ChatMessage("optimizeWorld.stage.upgrading", new Object[0]);

        while (this.f) {
            boolean flag = false;

            if (this.k < list.size()) {
                if (chunkregionloader.a((ChunkCoordIntPair) list.get(this.k++), DimensionManager.OVERWORLD, this.d)) {
                    ++this.p;
                } else {
                    ++this.q;
                }

                flag = true;
            }

            if (this.l < list1.size()) {
                if (chunkregionloader1.a((ChunkCoordIntPair) list1.get(this.l++), DimensionManager.NETHER, this.d)) {
                    ++this.p;
                } else {
                    ++this.q;
                }

                flag = true;
            }

            if (this.m < list2.size()) {
                if (chunkregionloader2.a((ChunkCoordIntPair) list2.get(this.m++), DimensionManager.THE_END, this.d)) {
                    ++this.p;
                } else {
                    ++this.q;
                }

                flag = true;
            }

            if (f > 0.0F) {
                this.h = (float) this.k / f;
                this.i = (float) this.l / f;
                this.j = (float) this.m / f;
            } else {
                this.h = 0.33333334F;
                this.i = 0.33333334F;
                this.j = 0.33333334F;
            }

            this.n = this.h + this.i + this.j;
            if (!flag) {
                this.f = false;
            }
        }

        this.r = new ChatMessage("optimizeWorld.stage.finished", new Object[0]);
        i = SystemUtils.b() - i;
        WorldUpgrader.a.info("World optimizaton finished after {} ms", Long.valueOf(i));
        chunkregionloader.c();
        chunkregionloader1.c();
        chunkregionloader2.c();
        this.d.a();
        this.c.a();
        this.g = true;
    }

    public boolean b() {
        return this.g;
    }

    public int j() {
        return this.o;
    }

    public int k() {
        return this.p;
    }

    public int l() {
        return this.q;
    }

    public IChatBaseComponent m() {
        return this.r;
    }
}
