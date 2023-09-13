package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;

public class GameTestHarnessBatch {

    private final String a;
    private final Collection<GameTestHarnessTestFunction> b;
    @Nullable
    private final Consumer<WorldServer> c;

    public GameTestHarnessBatch(String s, Collection<GameTestHarnessTestFunction> collection, @Nullable Consumer<WorldServer> consumer) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
        } else {
            this.a = s;
            this.b = collection;
            this.c = consumer;
        }
    }

    public String a() {
        return this.a;
    }

    public Collection<GameTestHarnessTestFunction> b() {
        return this.b;
    }

    public void a(WorldServer worldserver) {
        if (this.c != null) {
            this.c.accept(worldserver);
        }

    }
}
