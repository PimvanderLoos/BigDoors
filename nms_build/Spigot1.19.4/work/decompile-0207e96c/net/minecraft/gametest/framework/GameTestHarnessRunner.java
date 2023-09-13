package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class GameTestHarnessRunner {

    private static final int MAX_TESTS_PER_BATCH = 100;
    public static final int PADDING_AROUND_EACH_STRUCTURE = 2;
    public static final int SPACE_BETWEEN_COLUMNS = 5;
    public static final int SPACE_BETWEEN_ROWS = 6;
    public static final int DEFAULT_TESTS_PER_ROW = 8;

    public GameTestHarnessRunner() {}

    public static void runTest(GameTestHarnessInfo gametestharnessinfo, BlockPosition blockposition, GameTestHarnessTicker gametestharnessticker) {
        gametestharnessinfo.startExecution();
        gametestharnessticker.add(gametestharnessinfo);
        gametestharnessinfo.addListener(new ReportGameListener(gametestharnessinfo, gametestharnessticker, blockposition));
        gametestharnessinfo.spawnStructure(blockposition, 2);
    }

    public static Collection<GameTestHarnessInfo> runTestBatches(Collection<GameTestHarnessBatch> collection, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver, GameTestHarnessTicker gametestharnessticker, int i) {
        GameTestHarnessBatchRunner gametestharnessbatchrunner = new GameTestHarnessBatchRunner(collection, blockposition, enumblockrotation, worldserver, gametestharnessticker, i);

        gametestharnessbatchrunner.start();
        return gametestharnessbatchrunner.getTestInfos();
    }

    public static Collection<GameTestHarnessInfo> runTests(Collection<GameTestHarnessTestFunction> collection, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver, GameTestHarnessTicker gametestharnessticker, int i) {
        return runTestBatches(groupTestsIntoBatches(collection), blockposition, enumblockrotation, worldserver, gametestharnessticker, i);
    }

    public static Collection<GameTestHarnessBatch> groupTestsIntoBatches(Collection<GameTestHarnessTestFunction> collection) {
        Map<String, List<GameTestHarnessTestFunction>> map = (Map) collection.stream().collect(Collectors.groupingBy(GameTestHarnessTestFunction::getBatchName));

        return (Collection) map.entrySet().stream().flatMap((entry) -> {
            String s = (String) entry.getKey();
            Consumer<WorldServer> consumer = GameTestHarnessRegistry.getBeforeBatchFunction(s);
            Consumer<WorldServer> consumer1 = GameTestHarnessRegistry.getAfterBatchFunction(s);
            MutableInt mutableint = new MutableInt();
            Collection<GameTestHarnessTestFunction> collection1 = (Collection) entry.getValue();

            return Streams.stream(Iterables.partition(collection1, 100)).map((list) -> {
                return new GameTestHarnessBatch(s + ":" + mutableint.incrementAndGet(), ImmutableList.copyOf(list), consumer, consumer1);
            });
        }).collect(ImmutableList.toImmutableList());
    }

    public static void clearAllTests(WorldServer worldserver, BlockPosition blockposition, GameTestHarnessTicker gametestharnessticker, int i) {
        gametestharnessticker.clear();
        BlockPosition blockposition1 = blockposition.offset(-i, 0, -i);
        BlockPosition blockposition2 = blockposition.offset(i, 0, i);

        BlockPosition.betweenClosedStream(blockposition1, blockposition2).filter((blockposition3) -> {
            return worldserver.getBlockState(blockposition3).is(Blocks.STRUCTURE_BLOCK);
        }).forEach((blockposition3) -> {
            TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity(blockposition3);
            BlockPosition blockposition4 = tileentitystructure.getBlockPos();
            StructureBoundingBox structureboundingbox = GameTestHarnessStructures.getStructureBoundingBox(tileentitystructure);

            GameTestHarnessStructures.clearSpaceForStructure(structureboundingbox, blockposition4.getY(), worldserver);
        });
    }

    public static void clearMarkers(WorldServer worldserver) {
        PacketDebug.sendGameTestClearPacket(worldserver);
    }
}
