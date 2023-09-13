package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.NibbleArray;

public interface LightEngineLayerEventListener extends ILightEngine {

    @Nullable
    NibbleArray getDataLayerData(SectionPosition sectionposition);

    int getLightValue(BlockPosition blockposition);

    public static enum Void implements LightEngineLayerEventListener {

        INSTANCE;

        private Void() {}

        @Nullable
        @Override
        public NibbleArray getDataLayerData(SectionPosition sectionposition) {
            return null;
        }

        @Override
        public int getLightValue(BlockPosition blockposition) {
            return 0;
        }

        @Override
        public void checkBlock(BlockPosition blockposition) {}

        @Override
        public void onBlockEmissionIncrease(BlockPosition blockposition, int i) {}

        @Override
        public boolean hasLightWork() {
            return false;
        }

        @Override
        public int runUpdates(int i, boolean flag, boolean flag1) {
            return i;
        }

        @Override
        public void updateSectionStatus(SectionPosition sectionposition, boolean flag) {}

        @Override
        public void enableLightSources(ChunkCoordIntPair chunkcoordintpair, boolean flag) {}
    }
}
