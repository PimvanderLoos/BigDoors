package net.minecraft.world.level.storage;

import java.util.UUID;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimerQueue;

public class SecondaryWorldData implements IWorldDataServer {

    private final SaveData a;
    private final IWorldDataServer b;

    public SecondaryWorldData(SaveData savedata, IWorldDataServer iworlddataserver) {
        this.a = savedata;
        this.b = iworlddataserver;
    }

    @Override
    public int a() {
        return this.b.a();
    }

    @Override
    public int b() {
        return this.b.b();
    }

    @Override
    public int c() {
        return this.b.c();
    }

    @Override
    public float d() {
        return this.b.d();
    }

    @Override
    public long getTime() {
        return this.b.getTime();
    }

    @Override
    public long getDayTime() {
        return this.b.getDayTime();
    }

    @Override
    public String getName() {
        return this.a.getName();
    }

    @Override
    public int getClearWeatherTime() {
        return this.b.getClearWeatherTime();
    }

    @Override
    public void setClearWeatherTime(int i) {}

    @Override
    public boolean isThundering() {
        return this.b.isThundering();
    }

    @Override
    public int getThunderDuration() {
        return this.b.getThunderDuration();
    }

    @Override
    public boolean hasStorm() {
        return this.b.hasStorm();
    }

    @Override
    public int getWeatherDuration() {
        return this.b.getWeatherDuration();
    }

    @Override
    public EnumGamemode getGameType() {
        return this.a.getGameType();
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
        return this.a.isHardcore();
    }

    @Override
    public boolean o() {
        return this.a.o();
    }

    @Override
    public boolean p() {
        return this.b.p();
    }

    @Override
    public void c(boolean flag) {}

    @Override
    public GameRules q() {
        return this.a.q();
    }

    @Override
    public WorldBorder.c r() {
        return this.b.r();
    }

    @Override
    public void a(WorldBorder.c worldborder_c) {}

    @Override
    public EnumDifficulty getDifficulty() {
        return this.a.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.a.isDifficultyLocked();
    }

    @Override
    public CustomFunctionCallbackTimerQueue<MinecraftServer> u() {
        return this.b.u();
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
    public void a(UUID uuid) {}

    @Override
    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Derived", (Object) true);
        this.b.a(crashreportsystemdetails);
    }
}
