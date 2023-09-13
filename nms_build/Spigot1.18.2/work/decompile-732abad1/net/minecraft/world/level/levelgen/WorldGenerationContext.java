package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WorldGenerationContext {

    private final int minY;
    private final int height;

    public WorldGenerationContext(ChunkGenerator chunkgenerator, LevelHeightAccessor levelheightaccessor) {
        this.minY = Math.max(levelheightaccessor.getMinBuildHeight(), chunkgenerator.getMinY());
        this.height = Math.min(levelheightaccessor.getHeight(), chunkgenerator.getGenDepth());
    }

    public int getMinGenY() {
        return this.minY;
    }

    public int getGenDepth() {
        return this.height;
    }
}
