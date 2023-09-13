package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;

public class GameTestHarnessTicker {

    public static final GameTestHarnessTicker SINGLETON = new GameTestHarnessTicker();
    private final Collection<GameTestHarnessInfo> testInfos = Lists.newCopyOnWriteArrayList();

    public GameTestHarnessTicker() {}

    public void a(GameTestHarnessInfo gametestharnessinfo) {
        this.testInfos.add(gametestharnessinfo);
    }

    public void a() {
        this.testInfos.clear();
    }

    public void b() {
        this.testInfos.forEach(GameTestHarnessInfo::b);
        this.testInfos.removeIf(GameTestHarnessInfo::k);
    }
}
