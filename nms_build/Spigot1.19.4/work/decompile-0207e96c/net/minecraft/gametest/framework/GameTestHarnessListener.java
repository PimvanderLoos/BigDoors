package net.minecraft.gametest.framework;

public interface GameTestHarnessListener {

    void testStructureLoaded(GameTestHarnessInfo gametestharnessinfo);

    void testPassed(GameTestHarnessInfo gametestharnessinfo);

    void testFailed(GameTestHarnessInfo gametestharnessinfo);
}
