package net.minecraft.world.level.storage;

public class SavedFile {

    public static final SavedFile PLAYER_ADVANCEMENTS_DIR = new SavedFile("advancements");
    public static final SavedFile PLAYER_STATS_DIR = new SavedFile("stats");
    public static final SavedFile PLAYER_DATA_DIR = new SavedFile("playerdata");
    public static final SavedFile PLAYER_OLD_DATA_DIR = new SavedFile("players");
    public static final SavedFile LEVEL_DATA_FILE = new SavedFile("level.dat");
    public static final SavedFile OLD_LEVEL_DATA_FILE = new SavedFile("level.dat_old");
    public static final SavedFile ICON_FILE = new SavedFile("icon.png");
    public static final SavedFile LOCK_FILE = new SavedFile("session.lock");
    public static final SavedFile GENERATED_DIR = new SavedFile("generated");
    public static final SavedFile DATAPACK_DIR = new SavedFile("datapacks");
    public static final SavedFile MAP_RESOURCE_FILE = new SavedFile("resources.zip");
    public static final SavedFile ROOT = new SavedFile(".");
    private final String id;

    private SavedFile(String s) {
        this.id = s;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return "/" + this.id;
    }
}
