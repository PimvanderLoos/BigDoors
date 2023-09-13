package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestHarnessBatchRunner {

    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPosition b;
    private final WorldServer c;
    private final GameTestHarnessTicker d;
    private final List<GameTestHarnessInfo> e = Lists.newArrayList();
    private final List<Pair<GameTestHarnessBatch, Collection<GameTestHarnessInfo>>> f = Lists.newArrayList();
    private GameTestHarnessCollector g;
    private int h = 0;
    private BlockPosition.MutableBlockPosition i;
    private int j = 0;

    public GameTestHarnessBatchRunner(Collection<GameTestHarnessBatch> collection, BlockPosition blockposition, WorldServer worldserver, GameTestHarnessTicker gametestharnessticker) {
        this.i = new BlockPosition.MutableBlockPosition(blockposition);
        this.b = blockposition;
        this.c = worldserver;
        this.d = gametestharnessticker;
        collection.forEach((gametestharnessbatch) -> {
            Collection<GameTestHarnessInfo> collection1 = Lists.newArrayList();
            Collection<GameTestHarnessTestFunction> collection2 = gametestharnessbatch.b();
            Iterator iterator = collection2.iterator();

            while (iterator.hasNext()) {
                GameTestHarnessTestFunction gametestharnesstestfunction = (GameTestHarnessTestFunction) iterator.next();
                GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, worldserver);

                collection1.add(gametestharnessinfo);
                this.e.add(gametestharnessinfo);
            }

            this.f.add(Pair.of(gametestharnessbatch, collection1));
        });
    }

    public List<GameTestHarnessInfo> a() {
        return this.e;
    }

    public void b() {
        this.a(0);
    }

    private void a(int i) {
        this.h = i;
        this.g = new GameTestHarnessCollector();
        if (i < this.f.size()) {
            Pair<GameTestHarnessBatch, Collection<GameTestHarnessInfo>> pair = (Pair) this.f.get(this.h);
            GameTestHarnessBatch gametestharnessbatch = (GameTestHarnessBatch) pair.getFirst();
            Collection<GameTestHarnessInfo> collection = (Collection) pair.getSecond();

            this.a(collection);
            gametestharnessbatch.a(this.c);
            String s = gametestharnessbatch.a();

            GameTestHarnessBatchRunner.LOGGER.info("Running test batch '" + s + "' (" + collection.size() + " tests)...");
            collection.forEach((gametestharnessinfo) -> {
                this.g.a(gametestharnessinfo);
                this.g.a(new GameTestHarnessListener() {
                    @Override
                    public void a(GameTestHarnessInfo gametestharnessinfo1) {}

                    @Override
                    public void c(GameTestHarnessInfo gametestharnessinfo1) {
                        GameTestHarnessBatchRunner.this.a(gametestharnessinfo1);
                    }
                });
                GameTestHarnessRunner.a(gametestharnessinfo, this.d);
            });
        }
    }

    private void a(GameTestHarnessInfo gametestharnessinfo) {
        if (this.g.i()) {
            this.a(this.h + 1);
        }

    }

    private void a(Collection<GameTestHarnessInfo> collection) {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameTestHarnessInfo gametestharnessinfo = (GameTestHarnessInfo) iterator.next();
            BlockPosition blockposition = new BlockPosition(this.i);

            gametestharnessinfo.a(blockposition);
            GameTestHarnessStructures.a(gametestharnessinfo.s(), blockposition, 2, this.c, true);
            BlockPosition blockposition1 = gametestharnessinfo.e();
            int j = blockposition1 == null ? 1 : blockposition1.getX();
            int k = blockposition1 == null ? 1 : blockposition1.getZ();

            this.j = Math.max(this.j, k);
            this.i.e(j + 4, 0, 0);
            if (i++ % 8 == 0) {
                this.i.e(0, 0, this.j + 5);
                this.i.o(this.b.getX());
                this.j = 0;
            }
        }

    }
}
