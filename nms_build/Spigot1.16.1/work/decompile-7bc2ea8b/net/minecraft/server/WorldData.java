package net.minecraft.server;

public interface WorldData {

    int a();

    int b();

    int c();

    long getTime();

    long getDayTime();

    boolean isThundering();

    boolean hasStorm();

    void setStorm(boolean flag);

    boolean isHardcore();

    GameRules p();

    EnumDifficulty getDifficulty();

    boolean isDifficultyLocked();

    default void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Level spawn location", () -> {
            return CrashReportSystemDetails.a(this.a(), this.b(), this.c());
        });
        crashreportsystemdetails.a("Level time", () -> {
            return String.format("%d game time, %d day time", this.getTime(), this.getDayTime());
        });
    }
}
