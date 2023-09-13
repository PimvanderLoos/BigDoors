package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;

public class LevelVersion {

    private final int levelDataVersion;
    private final long lastPlayed;
    private final String minecraftVersionName;
    private final int minecraftVersion;
    private final boolean snapshot;

    public LevelVersion(int i, long j, String s, int k, boolean flag) {
        this.levelDataVersion = i;
        this.lastPlayed = j;
        this.minecraftVersionName = s;
        this.minecraftVersion = k;
        this.snapshot = flag;
    }

    public static LevelVersion a(Dynamic<?> dynamic) {
        int i = dynamic.get("version").asInt(0);
        long j = dynamic.get("LastPlayed").asLong(0L);
        OptionalDynamic<?> optionaldynamic = dynamic.get("Version");

        return optionaldynamic.result().isPresent() ? new LevelVersion(i, j, optionaldynamic.get("Name").asString(SharedConstants.getGameVersion().getName()), optionaldynamic.get("Id").asInt(SharedConstants.getGameVersion().getWorldVersion()), optionaldynamic.get("Snapshot").asBoolean(!SharedConstants.getGameVersion().isStable())) : new LevelVersion(i, j, "", 0, false);
    }

    public int a() {
        return this.levelDataVersion;
    }

    public long b() {
        return this.lastPlayed;
    }

    public String c() {
        return this.minecraftVersionName;
    }

    public int d() {
        return this.minecraftVersion;
    }

    public boolean e() {
        return this.snapshot;
    }
}
