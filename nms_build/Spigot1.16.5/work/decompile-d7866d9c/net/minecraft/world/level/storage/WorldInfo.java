package net.minecraft.world.level.storage;

import java.io.File;
import net.minecraft.world.level.WorldSettings;

public class WorldInfo implements Comparable<WorldInfo> {

    private final WorldSettings a;
    private final LevelVersion b;
    private final String c;
    private final boolean d;
    private final boolean e;
    private final File f;

    public WorldInfo(WorldSettings worldsettings, LevelVersion levelversion, String s, boolean flag, boolean flag1, File file) {
        this.a = worldsettings;
        this.b = levelversion;
        this.c = s;
        this.e = flag1;
        this.f = file;
        this.d = flag;
    }

    public int compareTo(WorldInfo worldinfo) {
        return this.b.b() < worldinfo.b.b() ? 1 : (this.b.b() > worldinfo.b.b() ? -1 : this.c.compareTo(worldinfo.c));
    }

    public LevelVersion k() {
        return this.b;
    }
}
