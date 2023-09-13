package net.minecraft.world.level.storage;

import net.minecraft.core.BlockPosition;

public interface WorldDataMutable extends WorldData {

    void setXSpawn(int i);

    void setYSpawn(int i);

    void setZSpawn(int i);

    void setSpawnAngle(float f);

    default void setSpawn(BlockPosition blockposition, float f) {
        this.setXSpawn(blockposition.getX());
        this.setYSpawn(blockposition.getY());
        this.setZSpawn(blockposition.getZ());
        this.setSpawnAngle(f);
    }
}
