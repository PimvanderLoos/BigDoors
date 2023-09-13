package net.minecraft.world.level.storage;

import java.util.UUID;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimerQueue;

public class SecondaryWorldData implements IWorldDataServer {

    private final SaveData worldData;
    private final IWorldDataServer wrapped;

    public SecondaryWorldData(SaveData savedata, IWorldDataServer iworlddataserver) {
        this.worldData = savedata;
        this.wrapped = iworlddataserver;
    }

    @Override
    public int getXSpawn() {
        return this.wrapped.getXSpawn();
    }

    @Override
    public int getYSpawn() {
        return this.wrapped.getYSpawn();
    }

    @Override
    public int getZSpawn() {
        return this.wrapped.getZSpawn();
    }

    @Override
    public float getSpawnAngle() {
        return this.wrapped.getSpawnAngle();
    }

    @Override
    public long getGameTime() {
        return this.wrapped.getGameTime();
    }

    @Override
    public long getDayTime() {
        return this.wrapped.getDayTime();
    }

    @Override
    public String getLevelName() {
        return this.worldData.getLevelName();
    }

    @Override
    public int getClearWeatherTime() {
        return this.wrapped.getClearWeatherTime();
    }

    @Override
    public void setClearWeatherTime(int i) {}

    @Override
    public boolean isThundering() {
        return this.wrapped.isThundering();
    }

    @Override
    public int getThunderTime() {
        return this.wrapped.getThunderTime();
    }

    @Override
    public boolean isRaining() {
        return this.wrapped.isRaining();
    }

    @Override
    public int getRainTime() {
        return this.wrapped.getRainTime();
    }

    @Override
    public EnumGamemode getGameType() {
        return this.worldData.getGameType();
    }

    @Override
    public void setXSpawn(int i) {}

    @Override
    public void setYSpawn(int i) {}

    @Override
    public void setZSpawn(int i) {}

    @Override
    public void setSpawnAngle(float f) {}

    @Override
    public void setGameTime(long i) {}

    @Override
    public void setDayTime(long i) {}

    @Override
    public void setSpawn(BlockPosition blockposition, float f) {}

    @Override
    public void setThundering(boolean flag) {}

    @Override
    public void setThunderTime(int i) {}

    @Override
    public void setRaining(boolean flag) {}

    @Override
    public void setRainTime(int i) {}

    @Override
    public void setGameType(EnumGamemode enumgamemode) {}

    @Override
    public boolean isHardcore() {
        return this.worldData.isHardcore();
    }

    @Override
    public boolean getAllowCommands() {
        return this.worldData.getAllowCommands();
    }

    @Override
    public boolean isInitialized() {
        return this.wrapped.isInitialized();
    }

    @Override
    public void setInitialized(boolean flag) {}

    @Override
    public GameRules getGameRules() {
        return this.worldData.getGameRules();
    }

    @Override
    public WorldBorder.c getWorldBorder() {
        return this.wrapped.getWorldBorder();
    }

    @Override
    public void setWorldBorder(WorldBorder.c worldborder_c) {}

    @Override
    public EnumDifficulty getDifficulty() {
        return this.worldData.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.worldData.isDifficultyLocked();
    }

    @Override
    public CustomFunctionCallbackTimerQueue<MinecraftServer> getScheduledEvents() {
        return this.wrapped.getScheduledEvents();
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int i) {}

    @Override
    public int getWanderingTraderSpawnChance() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnChance(int i) {}

    @Override
    public UUID getWanderingTraderId() {
        return null;
    }

    @Override
    public void setWanderingTraderId(UUID uuid) {}

    @Override
    public void fillCrashReportCategory(CrashReportSystemDetails crashreportsystemdetails, LevelHeightAccessor levelheightaccessor) {
        crashreportsystemdetails.setDetail("Derived", (Object) true);
        this.wrapped.fillCrashReportCategory(crashreportsystemdetails, levelheightaccessor);
    }
}
