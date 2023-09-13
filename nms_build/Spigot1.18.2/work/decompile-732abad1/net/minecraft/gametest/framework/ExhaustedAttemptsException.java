package net.minecraft.gametest.framework;

class ExhaustedAttemptsException extends Throwable {

    public ExhaustedAttemptsException(int i, int j, GameTestHarnessInfo gametestharnessinfo) {
        super("Not enough successes: " + j + " out of " + i + " attempts. Required successes: " + gametestharnessinfo.requiredSuccesses() + ". max attempts: " + gametestharnessinfo.maxAttempts() + ".", gametestharnessinfo.getError());
    }
}
