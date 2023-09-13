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
    public int a() {
        return this.wrapped.a();
    }

    @Override
    public int b() {
        return this.wrapped.b();
    }

    @Override
    public int c() {
        return this.wrapped.c();
    }

    @Override
    public float d() {
        return this.wrapped.d();
    }

    @Override
    public long getTime() {
        return this.wrapped.getTime();
    }

    @Override
    public long getDayTime() {
        return this.wrapped.getDayTime();
    }

    @Override
    public String getName() {
        return this.worldData.getName();
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
    public int getThunderDuration() {
        return this.wrapped.getThunderDuration();
    }

    @Override
    public boolean hasStorm() {
        return this.wrapped.hasStorm();
    }

    @Override
    public int getWeatherDuration() {
        return this.wrapped.getWeatherDuration();
    }

    @Override
    public EnumGamemode getGameType() {
        return this.worldData.getGameType();
    }

    @Override
    public void b(int i) {}

    @Override
    public void c(int i) {}

    @Override
    public void d(int i) {}

    @Override
    public void a(float f) {}

    @Override
    public void setTime(long i) {}

    @Override
    public void setDayTime(long i) {}

    @Override
    public void setSpawn(BlockPosition blockposition, float f) {}

    @Override
    public void setThundering(boolean flag) {}

    @Override
    public void setThunderDuration(int i) {}

    @Override
    public void setStorm(boolean flag) {}

    @Override
    public void setWeatherDuration(int i) {}

    @Override
    public void setGameType(EnumGamemode enumgamemode) {}

    @Override
    public boolean isHardcore() {
        return this.worldData.isHardcore();
    }

    @Override
    public boolean o() {
        return this.worldData.o();
    }

    @Override
    public boolean p() {
        return this.wrapped.p();
    }

    @Override
    public void c(boolean flag) {}

    @Override
    public GameRules q() {
        return this.worldData.q();
    }

    @Override
    public WorldBorder.c r() {
        return this.wrapped.r();
    }

    @Override
    public void a(WorldBorder.c worldborder_c) {}

    @Override
    public EnumDifficulty getDifficulty() {
        return this.worldData.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.worldData.isDifficultyLocked();
    }

    @Override
    public CustomFunctionCallbackTimerQueue<MinecraftServer> u() {
        return this.wrapped.u();
    }

    @Override
    public int v() {
        return 0;
    }

    @Override
    public void g(int i) {}

    @Override
    public int w() {
        return 0;
    }

    @Override
    public void h(int i) {}

    @Override
    public UUID x() {
        return null;
    }

    @Override
    public void a(UUID uuid) {}

    @Override
    public void a(CrashReportSystemDetails crashreportsystemdetails, LevelHeightAccessor levelheightaccessor) {
        crashreportsystemdetails.a("Derived", (Object) true);
        this.wrapped.a(crashreportsystemdetails, levelheightaccessor);
    }
}
