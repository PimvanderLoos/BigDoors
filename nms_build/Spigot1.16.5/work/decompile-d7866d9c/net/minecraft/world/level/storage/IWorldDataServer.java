package net.minecraft.world.level.storage;

import java.util.UUID;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimerQueue;

public interface IWorldDataServer extends WorldDataMutable {

    String getName();

    void setThundering(boolean flag);

    int getWeatherDuration();

    void setWeatherDuration(int i);

    void setThunderDuration(int i);

    int getThunderDuration();

    @Override
    default void a(CrashReportSystemDetails crashreportsystemdetails) {
        WorldDataMutable.super.a(crashreportsystemdetails);
        crashreportsystemdetails.a("Level name", this::getName);
        crashreportsystemdetails.a("Level game mode", () -> {
            return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().b(), this.getGameType().getId(), this.isHardcore(), this.o());
        });
        crashreportsystemdetails.a("Level weather", () -> {
            return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getWeatherDuration(), this.hasStorm(), this.getThunderDuration(), this.isThundering());
        });
    }

    int getClearWeatherTime();

    void setClearWeatherTime(int i);

    int v();

    void g(int i);

    int w();

    void h(int i);

    void a(UUID uuid);

    EnumGamemode getGameType();

    void a(WorldBorder.c worldborder_c);

    WorldBorder.c r();

    boolean p();

    void c(boolean flag);

    boolean o();

    void setGameType(EnumGamemode enumgamemode);

    CustomFunctionCallbackTimerQueue<MinecraftServer> u();

    void setTime(long i);

    void setDayTime(long i);
}
