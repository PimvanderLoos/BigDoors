package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestHarnessBatchRunner {

    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPosition b;
    private final WorldServer c;
    private final GameTestHarnessTicker d;
    private final int e;
    private final List<GameTestHarnessInfo> f = Lists.newArrayList();
    private final Map<GameTestHarnessInfo, BlockPosition> g = Maps.newHashMap();
    private final List<Pair<GameTestHarnessBatch, Collection<GameTestHarnessInfo>>> h = Lists.newArrayList();
    private GameTestHarnessCollector i;
    private int j = 0;
    private BlockPosition.MutableBlockPosition k;

    public GameTestHarnessBatchRunner(Collection<GameTestHarnessBatch> collection, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver, GameTestHarnessTicker gametestharnessticker, int i) {
        this.k = blockposition.i();
        this.b = blockposition;
        this.c = worldserver;
        this.d = gametestharnessticker;
        this.e = i;
        collection.forEach((gametestharnessbatch) -> {
            Collection<GameTestHarnessInfo> collection1 = Lists.newArrayList();
            Collection<GameTestHarnessTestFunction> collection2 = gametestharnessbatch.b();
            Iterator iterator = collection2.iterator();

            while (iterator.hasNext()) {
                GameTestHarnessTestFunction gametestharnesstestfunction = (GameTestHarnessTestFunction) iterator.next();
                GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, enumblockrotation, worldserver);

                collection1.add(gametestharnessinfo);
                this.f.add(gametestharnessinfo);
            }

            this.h.add(Pair.of(gametestharnessbatch, collection1));
        });
    }

    public List<GameTestHarnessInfo> a() {
        return this.f;
    }

    public void b() {
        this.a(0);
    }

    private void a(int i) {
        this.j = i;
        this.i = new GameTestHarnessCollector();
        if (i < this.h.size()) {
            Pair<GameTestHarnessBatch, Collection<GameTestHarnessInfo>> pair = (Pair) this.h.get(this.j);
            GameTestHarnessBatch gametestharnessbatch = (GameTestHarnessBatch) pair.getFirst();
            Collection<GameTestHarnessInfo> collection = (Collection) pair.getSecond();

            this.a(collection);
            gametestharnessbatch.a(this.c);
            String s = gametestharnessbatch.a();

            GameTestHarnessBatchRunner.LOGGER.info("Running test batch '" + s + "' (" + collection.size() + " tests)...");
            collection.forEach((gametestharnessinfo) -> {
                this.i.a(gametestharnessinfo);
                this.i.a(new GameTestHarnessListener() {
                    @Override
                    public void a(GameTestHarnessInfo gametestharnessinfo1) {}

                    @Override
                    public void c(GameTestHarnessInfo gametestharnessinfo1) {
                        GameTestHarnessBatchRunner.this.a(gametestharnessinfo1);
                    }
                });
                BlockPosition blockposition = (BlockPosition) this.g.get(gametestharnessinfo);

                GameTestHarnessRunner.a(gametestharnessinfo, blockposition, this.d);
            });
        }
    }

    private void a(GameTestHarnessInfo gametestharnessinfo) {
        if (this.i.i()) {
            this.a(this.j + 1);
        }

    }

    private void a(Collection<GameTestHarnessInfo> collection) {
        int i = 0;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.k);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameTestHarnessInfo gametestharnessinfo = (GameTestHarnessInfo) iterator.next();
            BlockPosition blockposition = new BlockPosition(this.k);
            TileEntityStructure tileentitystructure = GameTestHarnessStructures.a(gametestharnessinfo.s(), blockposition, gametestharnessinfo.t(), 2, this.c, true);
            AxisAlignedBB axisalignedbb1 = GameTestHarnessStructures.a(tileentitystructure);

            gametestharnessinfo.a(tileentitystructure.getPosition());
            this.g.put(gametestharnessinfo, new BlockPosition(this.k));
            axisalignedbb = axisalignedbb.b(axisalignedbb1);
            this.k.e((int) axisalignedbb1.b() + 5, 0, 0);
            if (i++ % this.e == this.e - 1) {
                this.k.e(0, 0, (int) axisalignedbb.d() + 6);
                this.k.o(this.b.getX());
                axisalignedbb = new AxisAlignedBB(this.k);
            }
        }

    }
}
