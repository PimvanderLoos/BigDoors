package net.minecraft.server;

public interface WorldDataMutable extends WorldData {

    void b(int i);

    void c(int i);

    void d(int i);

    void a(float f);

    default void setSpawn(BlockPosition blockposition, float f) {
        this.b(blockposition.getX());
        this.c(blockposition.getY());
        this.d(blockposition.getZ());
        this.a(f);
    }
}
