package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.world.level.block.EnumBlockRotation;

public class GameTestHarnessTestFunction {

    private final String batchName;
    private final String testName;
    private final String structureName;
    private final boolean required;
    private final int maxAttempts;
    private final int requiredSuccesses;
    private final Consumer<GameTestHarnessHelper> function;
    private final int maxTicks;
    private final long setupTicks;
    private final EnumBlockRotation rotation;

    public GameTestHarnessTestFunction(String s, String s1, String s2, int i, long j, boolean flag, Consumer<GameTestHarnessHelper> consumer) {
        this(s, s1, s2, EnumBlockRotation.NONE, i, j, flag, 1, 1, consumer);
    }

    public GameTestHarnessTestFunction(String s, String s1, String s2, EnumBlockRotation enumblockrotation, int i, long j, boolean flag, Consumer<GameTestHarnessHelper> consumer) {
        this(s, s1, s2, enumblockrotation, i, j, flag, 1, 1, consumer);
    }

    public GameTestHarnessTestFunction(String s, String s1, String s2, EnumBlockRotation enumblockrotation, int i, long j, boolean flag, int k, int l, Consumer<GameTestHarnessHelper> consumer) {
        this.batchName = s;
        this.testName = s1;
        this.structureName = s2;
        this.rotation = enumblockrotation;
        this.maxTicks = i;
        this.required = flag;
        this.requiredSuccesses = k;
        this.maxAttempts = l;
        this.function = consumer;
        this.setupTicks = j;
    }

    public void run(GameTestHarnessHelper gametestharnesshelper) {
        this.function.accept(gametestharnesshelper);
    }

    public String getTestName() {
        return this.testName;
    }

    public String getStructureName() {
        return this.structureName;
    }

    public String toString() {
        return this.testName;
    }

    public int getMaxTicks() {
        return this.maxTicks;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public long getSetupTicks() {
        return this.setupTicks;
    }

    public EnumBlockRotation getRotation() {
        return this.rotation;
    }

    public boolean isFlaky() {
        return this.maxAttempts > 1;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    public int getRequiredSuccesses() {
        return this.requiredSuccesses;
    }
}
