package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {

    private static final Logger a = LogManager.getLogger();
    private static final ThreadFactory b = (new ThreadFactoryBuilder()).setDaemon(true).build();
    private final String c;
    private final IDataManager d;
    private final PersistentCollection e;
    private final Thread f;
    private volatile boolean g = true;
    private volatile boolean h = false;
    private volatile float i;
    private volatile int j;
    private volatile int k = 0;
    private volatile int l = 0;
    private final Object2FloatMap<DimensionManager> m = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(SystemUtils.g()));
    private volatile IChatBaseComponent n = new ChatMessage("optimizeWorld.stage.counting", new Object[0]);

    public WorldUpgrader(String s, Convertable convertable, WorldData worlddata) {
        this.c = worlddata.getName();
        this.d = convertable.a(s, (MinecraftServer) null);
        this.d.saveWorldData(worlddata);
        this.e = new PersistentCollection(this.d);
        this.f = WorldUpgrader.b.newThread(this::i);
        this.f.setUncaughtExceptionHandler(this::a);
        this.f.start();
    }

    private void a(Thread thread, Throwable throwable) {
        WorldUpgrader.a.error("Error upgrading world", throwable);
        this.g = false;
        this.n = new ChatMessage("optimizeWorld.stage.failed", new Object[0]);
    }

    public void a() {
        this.g = false;

        try {
            this.f.join();
        } catch (InterruptedException interruptedexception) {
            ;
        }

    }

    private void i() {
        File file = this.d.getDirectory();
        WorldUpgraderIterator worldupgraderiterator = new WorldUpgraderIterator(file);
        Builder<DimensionManager, ChunkRegionLoader> builder = ImmutableMap.builder();
        Iterator iterator = DimensionManager.b().iterator();

        while (iterator.hasNext()) {
            DimensionManager dimensionmanager = (DimensionManager) iterator.next();

            builder.put(dimensionmanager, new ChunkRegionLoader(dimensionmanager.a(file), this.d.i()));
        }

        Map<DimensionManager, ChunkRegionLoader> map = builder.build();
        long i = SystemUtils.getMonotonicMillis();

        this.j = 0;
        Builder<DimensionManager, ListIterator<ChunkCoordIntPair>> builder1 = ImmutableMap.builder();

        List list;

        for (Iterator iterator1 = DimensionManager.b().iterator(); iterator1.hasNext(); this.j += list.size()) {
            DimensionManager dimensionmanager1 = (DimensionManager) iterator1.next();

            list = worldupgraderiterator.a(dimensionmanager1);
            builder1.put(dimensionmanager1, list.listIterator());
        }

        ImmutableMap<DimensionManager, ListIterator<ChunkCoordIntPair>> immutablemap = builder1.build();
        float f = (float) this.j;

        this.n = new ChatMessage("optimizeWorld.stage.structures", new Object[0]);
        Iterator iterator2 = map.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry<DimensionManager, ChunkRegionLoader> entry = (Entry) iterator2.next();

            ((ChunkRegionLoader) entry.getValue()).a((DimensionManager) entry.getKey(), this.e);
        }

        this.e.a();
        this.n = new ChatMessage("optimizeWorld.stage.upgrading", new Object[0]);
        if (f <= 0.0F) {
            iterator2 = DimensionManager.b().iterator();

            while (iterator2.hasNext()) {
                DimensionManager dimensionmanager2 = (DimensionManager) iterator2.next();

                this.m.put(dimensionmanager2, 1.0F / (float) map.size());
            }
        }

        while (this.g) {
            boolean flag = false;
            float f1 = 0.0F;
            Iterator iterator3 = DimensionManager.b().iterator();

            while (iterator3.hasNext()) {
                DimensionManager dimensionmanager3 = (DimensionManager) iterator3.next();
                ListIterator<ChunkCoordIntPair> listiterator = (ListIterator) immutablemap.get(dimensionmanager3);

                flag |= this.a((ChunkRegionLoader) map.get(dimensionmanager3), listiterator, dimensionmanager3);
                if (f > 0.0F) {
                    float f2 = (float) listiterator.nextIndex() / f;

                    this.m.put(dimensionmanager3, f2);
                    f1 += f2;
                }
            }

            this.i = f1;
            if (!flag) {
                this.g = false;
            }
        }

        this.n = new ChatMessage("optimizeWorld.stage.finished", new Object[0]);
        i = SystemUtils.getMonotonicMillis() - i;
        WorldUpgrader.a.info("World optimizaton finished after {} ms", i);
        map.values().forEach(ChunkRegionLoader::b);
        this.e.a();
        this.d.a();
        this.h = true;
    }

    private boolean a(ChunkRegionLoader chunkregionloader, ListIterator<ChunkCoordIntPair> listiterator, DimensionManager dimensionmanager) {
        if (listiterator.hasNext()) {
            boolean flag;

            synchronized (chunkregionloader) {
                flag = chunkregionloader.a((ChunkCoordIntPair) listiterator.next(), dimensionmanager, this.e);
            }

            if (flag) {
                ++this.k;
            } else {
                ++this.l;
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean b() {
        return this.h;
    }

    public int d() {
        return this.j;
    }

    public int e() {
        return this.k;
    }

    public int f() {
        return this.l;
    }

    public IChatBaseComponent g() {
        return this.n;
    }
}
