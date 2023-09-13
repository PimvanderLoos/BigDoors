package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;

public class GameTestHarnessTicker {

    public static final GameTestHarnessTicker a = new GameTestHarnessTicker();
    private final Collection<GameTestHarnessInfo> b = Lists.newCopyOnWriteArrayList();

    public GameTestHarnessTicker() {}

    public void a(GameTestHarnessInfo gametestharnessinfo) {
        this.b.add(gametestharnessinfo);
    }

    public void a() {
        this.b.clear();
    }

    public void b() {
        this.b.forEach(GameTestHarnessInfo::b);
        this.b.removeIf(GameTestHarnessInfo::k);
    }
}
