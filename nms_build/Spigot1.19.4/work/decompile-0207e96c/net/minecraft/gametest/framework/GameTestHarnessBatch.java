package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;

public class GameTestHarnessBatch {

    public static final String DEFAULT_BATCH_NAME = "defaultBatch";
    private final String name;
    private final Collection<GameTestHarnessTestFunction> testFunctions;
    @Nullable
    private final Consumer<WorldServer> beforeBatchFunction;
    @Nullable
    private final Consumer<WorldServer> afterBatchFunction;

    public GameTestHarnessBatch(String s, Collection<GameTestHarnessTestFunction> collection, @Nullable Consumer<WorldServer> consumer, @Nullable Consumer<WorldServer> consumer1) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
        } else {
            this.name = s;
            this.testFunctions = collection;
            this.beforeBatchFunction = consumer;
            this.afterBatchFunction = consumer1;
        }
    }

    public String getName() {
        return this.name;
    }

    public Collection<GameTestHarnessTestFunction> getTestFunctions() {
        return this.testFunctions;
    }

    public void runBeforeBatchFunction(WorldServer worldserver) {
        if (this.beforeBatchFunction != null) {
            this.beforeBatchFunction.accept(worldserver);
        }

    }

    public void runAfterBatchFunction(WorldServer worldserver) {
        if (this.afterBatchFunction != null) {
            this.afterBatchFunction.accept(worldserver);
        }

    }
}
