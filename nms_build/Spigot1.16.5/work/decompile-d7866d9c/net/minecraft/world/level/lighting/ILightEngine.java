package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;

public interface ILightEngine {

    default void a(BlockPosition blockposition, boolean flag) {
        this.a(SectionPosition.a(blockposition), flag);
    }

    void a(SectionPosition sectionposition, boolean flag);
}
