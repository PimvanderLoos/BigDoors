package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;

public interface ILightEngine {

    void a(BlockPosition blockposition);

    void a(BlockPosition blockposition, int i);

    boolean z_();

    int a(int i, boolean flag, boolean flag1);

    default void a(BlockPosition blockposition, boolean flag) {
        this.a(SectionPosition.a(blockposition), flag);
    }

    void a(SectionPosition sectionposition, boolean flag);

    void a(ChunkCoordIntPair chunkcoordintpair, boolean flag);
}
