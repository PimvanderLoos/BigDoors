package net.minecraft.world.level.storage;

import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimerQueue;

public interface IWorldDataServer extends WorldDataMutable {

    String getLevelName();

    void setThundering(boolean flag);

    int getRainTime();

    void setRainTime(int i);

    void setThunderTime(int i);

    int getThunderTime();

    @Override
    default void fillCrashReportCategory(CrashReportSystemDetails crashreportsystemdetails, LevelHeightAccessor levelheightaccessor) {
        WorldDataMutable.super.fillCrashReportCategory(crashreportsystemdetails, levelheightaccessor);
        crashreportsystemdetails.setDetail("Level name", this::getLevelName);
        crashreportsystemdetails.setDetail("Level game mode", () -> {
            return String.format(Locale.ROOT, "Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.getAllowCommands());
        });
        crashreportsystemdetails.setDetail("Level weather", () -> {
            return String.format(Locale.ROOT, "Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering());
        });
    }

    int getClearWeatherTime();

    void setClearWeatherTime(int i);

    int getWanderingTraderSpawnDelay();

    void setWanderingTraderSpawnDelay(int i);

    int getWanderingTraderSpawnChance();

    void setWanderingTraderSpawnChance(int i);

    @Nullable
    UUID getWanderingTraderId();

    void setWanderingTraderId(UUID uuid);

    EnumGamemode getGameType();

    void setWorldBorder(WorldBorder.c worldborder_c);

    WorldBorder.c getWorldBorder();

    boolean isInitialized();

    void setInitialized(boolean flag);

    boolean getAllowCommands();

    void setGameType(EnumGamemode enumgamemode);

    CustomFunctionCallbackTimerQueue<MinecraftServer> getScheduledEvents();

    void setGameTime(long i);

    void setDayTime(long i);
}
