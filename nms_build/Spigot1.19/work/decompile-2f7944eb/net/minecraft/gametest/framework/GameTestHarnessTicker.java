package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;

public class GameTestHarnessTicker {

    public static final GameTestHarnessTicker SINGLETON = new GameTestHarnessTicker();
    private final Collection<GameTestHarnessInfo> testInfos = Lists.newCopyOnWriteArrayList();

    public GameTestHarnessTicker() {}

    public void add(GameTestHarnessInfo gametestharnessinfo) {
        this.testInfos.add(gametestharnessinfo);
    }

    public void clear() {
        this.testInfos.clear();
    }

    public void tick() {
        this.testInfos.forEach(GameTestHarnessInfo::tick);
        this.testInfos.removeIf(GameTestHarnessInfo::isDone);
    }
}
