package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;

public interface ILightEngine {

    void checkBlock(BlockPosition blockposition);

    void onBlockEmissionIncrease(BlockPosition blockposition, int i);

    boolean hasLightWork();

    int runUpdates(int i, boolean flag, boolean flag1);

    default void updateSectionStatus(BlockPosition blockposition, boolean flag) {
        this.updateSectionStatus(SectionPosition.of(blockposition), flag);
    }

    void updateSectionStatus(SectionPosition sectionposition, boolean flag);

    void enableLightSources(ChunkCoordIntPair chunkcoordintpair, boolean flag);
}
