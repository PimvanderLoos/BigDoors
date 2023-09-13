package net.minecraft.world.level.storage;

import net.minecraft.CrashReportSystemDetails;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.GameRules;

public interface WorldData {

    int a();

    int b();

    int c();

    float d();

    long getTime();

    long getDayTime();

    boolean isThundering();

    boolean hasStorm();

    void setStorm(boolean flag);

    boolean isHardcore();

    GameRules q();

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
