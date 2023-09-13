package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.chunk.NibbleArray;

public interface LightEngineLayerEventListener extends ILightEngine {

    @Nullable
    NibbleArray a(SectionPosition sectionposition);

    int b(BlockPosition blockposition);

    public static enum Void implements LightEngineLayerEventListener {

        INSTANCE;

        private Void() {}

        @Nullable
        @Override
        public NibbleArray a(SectionPosition sectionposition) {
            return null;
        }

        @Override
        public int b(BlockPosition blockposition) {
            return 0;
        }

        @Override
        public void a(SectionPosition sectionposition, boolean flag) {}
    }
}
