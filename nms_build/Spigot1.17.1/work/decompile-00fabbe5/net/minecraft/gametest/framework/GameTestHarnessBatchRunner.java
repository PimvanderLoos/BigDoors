package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestHarnessBatchRunner {

    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPosition firstTestNorthWestCorner;
    final WorldServer level;
    private final GameTestHarnessTicker testTicker;
    private final int testsPerRow;
    private final List<GameTestHarnessInfo> allTestInfos;
    private final List<Pair<GameTestHarnessBatch, Collection<GameTestHarnessInfo>>> batches;
    private final BlockPosition.MutableBlockPosition nextTestNorthWestCorner;

    public GameTestHarnessBatchRunner(Collection<GameTestHarnessBatch> collection, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver, GameTestHarnessTicker gametestharnessticker, int i) {
        this.nextTestNorthWestCorner = blockposition.i();
        this.firstTestNorthWestCorner = blockposition;
        this.level = worldserver;
        this.testTicker = gametestharnessticker;
        this.testsPerRow = i;
        this.batches = (List) collection.stream().map((gametestharnessbatch) -> {
            Collection<GameTestHarnessInfo> collection1 = (Collection) gametestharnessbatch.b().stream().map((gametestharnesstestfunction) -> {
                return new GameTestHarnessInfo(gametestharnesstestfunction, enumblockrotation, worldserver);
            }).collect(ImmutableList.toImmutableList());

            return Pair.of(gametestharnessbatch, collection1);
        }).collect(ImmutableList.toImmutableList());
        this.allTestInfos = (List) this.batches.stream().flatMap((pair) -> {
            return ((Collection) pair.getSecond()).stream();
        }).collect(ImmutableList.toImmutableList());
    }

    public List<GameTestHarnessInfo> a() {
        return this.allTestInfos;
    }

    public void b() {
        this.a(0);
    }

    void a(final int i) {
        if (i < this.batches.size()) {
            Pair<GameTestHarnessBatch, Collection<GameTestHarnessInfo>> pair = (Pair) this.batches.get(i);
            final GameTestHarnessBatch gametestharnessbatch = (GameTestHarnessBatch) pair.getFirst();
            Collection<GameTestHarnessInfo> collection = (Collection) pair.getSecond();
            Map<GameTestHarnessInfo, BlockPosition> map = this.a(collection);
            String s = gametestharnessbatch.a();

            GameTestHarnessBatchRunner.LOGGER.info("Running test batch '{}' ({} tests)...", s, collection.size());
            gametestharnessbatch.a(this.level);
            final GameTestHarnessCollector gametestharnesscollector = new GameTestHarnessCollector();

            Objects.requireNonNull(gametestharnesscollector);
            collection.forEach(gametestharnesscollector::a);
            gametestharnesscollector.a(new GameTestHarnessListener() {
                private void a() {
                    if (gametestharnesscollector.i()) {
                        gametestharnessbatch.b(GameTestHarnessBatchRunner.this.level);
                        GameTestHarnessBatchRunner.this.a(i + 1);
                    }

                }

                @Override
                public void a(GameTestHarnessInfo gametestharnessinfo) {}

                @Override
                public void b(GameTestHarnessInfo gametestharnessinfo) {
                    this.a();
                }

                @Override
                public void c(GameTestHarnessInfo gametestharnessinfo) {
                    this.a();
                }
            });
            collection.forEach((gametestharnessinfo) -> {
                BlockPosition blockposition = (BlockPosition) map.get(gametestharnessinfo);

                GameTestHarnessRunner.a(gametestharnessinfo, blockposition, this.testTicker);
            });
        }
    }

    private Map<GameTestHarnessInfo, BlockPosition> a(Collection<GameTestHarnessInfo> collection) {
        Map<GameTestHarnessInfo, BlockPosition> map = Maps.newHashMap();
        int i = 0;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.nextTestNorthWestCorner);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameTestHarnessInfo gametestharnessinfo = (GameTestHarnessInfo) iterator.next();
            BlockPosition blockposition = new BlockPosition(this.nextTestNorthWestCorner);
            TileEntityStructure tileentitystructure = GameTestHarnessStructures.a(gametestharnessinfo.t(), blockposition, gametestharnessinfo.u(), 2, this.level, true);
            AxisAlignedBB axisalignedbb1 = GameTestHarnessStructures.a(tileentitystructure);

            gametestharnessinfo.a(tileentitystructure.getPosition());
            map.put(gametestharnessinfo, new BlockPosition(this.nextTestNorthWestCorner));
            axisalignedbb = axisalignedbb.b(axisalignedbb1);
            this.nextTestNorthWestCorner.e((int) axisalignedbb1.b() + 5, 0, 0);
            if (i++ % this.testsPerRow == this.testsPerRow - 1) {
                this.nextTestNorthWestCorner.e(0, 0, (int) axisalignedbb.d() + 6);
                this.nextTestNorthWestCorner.u(this.firstTestNorthWestCorner.getX());
                axisalignedbb = new AxisAlignedBB(this.nextTestNorthWestCorner);
            }
        }

        return map;
    }
}
