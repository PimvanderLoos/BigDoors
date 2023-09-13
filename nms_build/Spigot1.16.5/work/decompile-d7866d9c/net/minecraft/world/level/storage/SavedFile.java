package net.minecraft.world.level.storage;

public class SavedFile {

    public static final SavedFile ADVANCEMENTS = new SavedFile("advancements");
    public static final SavedFile STATS = new SavedFile("stats");
    public static final SavedFile PLAYERDATA = new SavedFile("playerdata");
    public static final SavedFile PLAYERS = new SavedFile("players");
    public static final SavedFile LEVEL_DAT = new SavedFile("level.dat");
    public static final SavedFile GENERATED = new SavedFile("generated");
    public static final SavedFile DATAPACKS = new SavedFile("datapacks");
    public static final SavedFile RESOURCES_ZIP = new SavedFile("resources.zip");
    public static final SavedFile ROOT = new SavedFile(".");
    private final String j;

    private SavedFile(String s) {
        this.j = s;
    }

    public String a() {
        return this.j;
    }

    public String toString() {
        return "/" + this.j;
    }
}
