package net.minecraft.world.level.storage;

public class DataVersion {

    private final int version;
    private final String series;
    public static String MAIN_SERIES = "main";

    public DataVersion(int i) {
        this(i, DataVersion.MAIN_SERIES);
    }

    public DataVersion(int i, String s) {
        this.version = i;
        this.series = s;
    }

    public boolean isSideSeries() {
        return !this.series.equals(DataVersion.MAIN_SERIES);
    }

    public String getSeries() {
        return this.series;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean isCompatible(DataVersion dataversion) {
        return this.getSeries().equals(dataversion.getSeries());
    }
}
