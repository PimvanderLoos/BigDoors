package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;

public class LevelVersion {

    private final int levelDataVersion;
    private final long lastPlayed;
    private final String minecraftVersionName;
    private final DataVersion minecraftVersion;
    private final boolean snapshot;

    private LevelVersion(int i, long j, String s, int k, String s1, boolean flag) {
        this.levelDataVersion = i;
        this.lastPlayed = j;
        this.minecraftVersionName = s;
        this.minecraftVersion = new DataVersion(k, s1);
        this.snapshot = flag;
    }

    public static LevelVersion parse(Dynamic<?> dynamic) {
        int i = dynamic.get("version").asInt(0);
        long j = dynamic.get("LastPlayed").asLong(0L);
        OptionalDynamic<?> optionaldynamic = dynamic.get("Version");

        return optionaldynamic.result().isPresent() ? new LevelVersion(i, j, optionaldynamic.get("Name").asString(SharedConstants.getCurrentVersion().getName()), optionaldynamic.get("Id").asInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()), optionaldynamic.get("Series").asString(DataVersion.MAIN_SERIES), optionaldynamic.get("Snapshot").asBoolean(!SharedConstants.getCurrentVersion().isStable())) : new LevelVersion(i, j, "", 0, DataVersion.MAIN_SERIES, false);
    }

    public int levelDataVersion() {
        return this.levelDataVersion;
    }

    public long lastPlayed() {
        return this.lastPlayed;
    }

    public String minecraftVersionName() {
        return this.minecraftVersionName;
    }

    public DataVersion minecraftVersion() {
        return this.minecraftVersion;
    }

    public boolean snapshot() {
        return this.snapshot;
    }
}
