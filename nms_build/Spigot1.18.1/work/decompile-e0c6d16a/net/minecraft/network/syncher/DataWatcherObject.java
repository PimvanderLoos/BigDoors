package net.minecraft.network.syncher;

public class DataWatcherObject<T> {

    private final int id;
    private final DataWatcherSerializer<T> serializer;

    public DataWatcherObject(int i, DataWatcherSerializer<T> datawatcherserializer) {
        this.id = i;
        this.serializer = datawatcherserializer;
    }

    public int getId() {
        return this.id;
    }

    public DataWatcherSerializer<T> getSerializer() {
        return this.serializer;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            DataWatcherObject<?> datawatcherobject = (DataWatcherObject) object;

            return this.id == datawatcherobject.id;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "<entity data: " + this.id + ">";
    }
}
