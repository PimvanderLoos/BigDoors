package net.minecraft.server;

import javax.annotation.Nullable;

public class SecondaryWorldData extends WorldData {

    private final WorldData b;

    public SecondaryWorldData(WorldData worlddata) {
        this.b = worlddata;
    }

    public NBTTagCompound a(@Nullable NBTTagCompound nbttagcompound) {
        return this.b.a(nbttagcompound);
    }

    public long getSeed() {
        return this.b.getSeed();
    }

    public int b() {
        return this.b.b();
    }

    public int c() {
        return this.b.c();
    }

    public int d() {
        return this.b.d();
    }

    public long getTime() {
        return this.b.getTime();
    }

    public long getDayTime() {
        return this.b.getDayTime();
    }

    public NBTTagCompound h() {
        return this.b.h();
    }

    public String getName() {
        return this.b.getName();
    }

    public int k() {
        return this.b.k();
    }

    public boolean isThundering() {
        return this.b.isThundering();
    }

    public int getThunderDuration() {
        return this.b.getThunderDuration();
    }

    public boolean hasStorm() {
        return this.b.hasStorm();
    }

    public int getWeatherDuration() {
        return this.b.getWeatherDuration();
    }

    public EnumGamemode getGameType() {
        return this.b.getGameType();
    }

    public void setTime(long i) {}

    public void setDayTime(long i) {}

    public void setSpawn(BlockPosition blockposition) {}

    public void a(String s) {}

    public void d(int i) {}

    public void setThundering(boolean flag) {}

    public void setThunderDuration(int i) {}

    public void setStorm(boolean flag) {}

    public void setWeatherDuration(int i) {}

    public boolean shouldGenerateMapFeatures() {
        return this.b.shouldGenerateMapFeatures();
    }

    public boolean isHardcore() {
        return this.b.isHardcore();
    }

    public WorldType getType() {
        return this.b.getType();
    }

    public void a(WorldType worldtype) {}

    public boolean u() {
        return this.b.u();
    }

    public void c(boolean flag) {}

    public boolean v() {
        return this.b.v();
    }

    public void d(boolean flag) {}

    public GameRules w() {
        return this.b.w();
    }

    public EnumDifficulty getDifficulty() {
        return this.b.getDifficulty();
    }

    public void setDifficulty(EnumDifficulty enumdifficulty) {}

    public boolean isDifficultyLocked() {
        return this.b.isDifficultyLocked();
    }

    public void e(boolean flag) {}

    public void a(DimensionManager dimensionmanager, NBTTagCompound nbttagcompound) {
        this.b.a(dimensionmanager, nbttagcompound);
    }

    public NBTTagCompound a(DimensionManager dimensionmanager) {
        return this.b.a(dimensionmanager);
    }
}
