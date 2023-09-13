package net.minecraft.server;

import java.util.UUID;

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
            return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().b(), this.getGameType().getId(), this.isHardcore(), this.n());
        });
        crashreportsystemdetails.a("Level weather", () -> {
            return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getWeatherDuration(), this.hasStorm(), this.getThunderDuration(), this.isThundering());
        });
    }

    int g();

    void a(int i);

    int u();

    void g(int i);

    int v();

    void h(int i);

    void a(UUID uuid);

    EnumGamemode getGameType();

    void a(WorldBorder.c worldborder_c);

    WorldBorder.c q();

    boolean o();

    void c(boolean flag);

    boolean n();

    void setGameType(EnumGamemode enumgamemode);

    CustomFunctionCallbackTimerQueue<MinecraftServer> t();

    void setTime(long i);

    void setDayTime(long i);
}
