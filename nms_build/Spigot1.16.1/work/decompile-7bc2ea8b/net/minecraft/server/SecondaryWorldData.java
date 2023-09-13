package net.minecraft.server;

import java.util.UUID;

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
    public int g() {
        return this.b.g();
    }

    @Override
    public void a(int i) {}

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
    public void setTime(long i) {}

    @Override
    public void setDayTime(long i) {}

    @Override
    public void setSpawn(BlockPosition blockposition) {}

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
    public boolean n() {
        return this.a.n();
    }

    @Override
    public boolean o() {
        return this.b.o();
    }

    @Override
    public void c(boolean flag) {}

    @Override
    public GameRules p() {
        return this.a.p();
    }

    @Override
    public WorldBorder.c q() {
        return this.b.q();
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
    public CustomFunctionCallbackTimerQueue<MinecraftServer> t() {
        return this.b.t();
    }

    @Override
    public int u() {
        return 0;
    }

    @Override
    public void g(int i) {}

    @Override
    public int v() {
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
