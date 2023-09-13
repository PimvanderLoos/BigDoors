package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;

public class LevelVersion {

    private final int a;
    private final long b;
    private final String c;
    private final int d;
    private final boolean e;

    public LevelVersion(int i, long j, String s, int k, boolean flag) {
        this.a = i;
        this.b = j;
        this.c = s;
        this.d = k;
        this.e = flag;
    }

    public static LevelVersion a(Dynamic<?> dynamic) {
        int i = dynamic.get("version").asInt(0);
        long j = dynamic.get("LastPlayed").asLong(0L);
        OptionalDynamic<?> optionaldynamic = dynamic.get("Version");

        return optionaldynamic.result().isPresent() ? new LevelVersion(i, j, optionaldynamic.get("Name").asString(SharedConstants.getGameVersion().getName()), optionaldynamic.get("Id").asInt(SharedConstants.getGameVersion().getWorldVersion()), optionaldynamic.get("Snapshot").asBoolean(!SharedConstants.getGameVersion().isStable())) : new LevelVersion(i, j, "", 0, false);
    }

    public int a() {
        return this.a;
    }

    public long b() {
        return this.b;
    }
}
