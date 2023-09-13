package net.minecraft.server;

public interface WorldDataMutable extends WorldData {

    void b(int i);

    void c(int i);

    void d(int i);

    default void setSpawn(BlockPosition blockposition) {
        this.b(blockposition.getX());
        this.c(blockposition.getY());
        this.d(blockposition.getZ());
    }
}
