package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;

public interface LevelHeightAccessor {

    int getHeight();

    int getMinBuildHeight();

    default int getMaxBuildHeight() {
        return this.getMinBuildHeight() + this.getHeight();
    }

    default int getSectionsCount() {
        return this.getMaxSection() - this.getMinSection();
    }

    default int getMinSection() {
        return SectionPosition.a(this.getMinBuildHeight());
    }

    default int getMaxSection() {
        return SectionPosition.a(this.getMaxBuildHeight() - 1) + 1;
    }

    default boolean isOutsideWorld(BlockPosition blockposition) {
        return this.d(blockposition.getY());
    }

    default boolean d(int i) {
        return i < this.getMinBuildHeight() || i >= this.getMaxBuildHeight();
    }

    default int getSectionIndex(int i) {
        return this.getSectionIndexFromSectionY(SectionPosition.a(i));
    }

    default int getSectionIndexFromSectionY(int i) {
        return i - this.getMinSection();
    }

    default int getSectionYFromSectionIndex(int i) {
        return i + this.getMinSection();
    }
}
