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
        return SectionPosition.blockToSectionCoord(this.getMinBuildHeight());
    }

    default int getMaxSection() {
        return SectionPosition.blockToSectionCoord(this.getMaxBuildHeight() - 1) + 1;
    }

    default boolean isOutsideBuildHeight(BlockPosition blockposition) {
        return this.isOutsideBuildHeight(blockposition.getY());
    }

    default boolean isOutsideBuildHeight(int i) {
        return i < this.getMinBuildHeight() || i >= this.getMaxBuildHeight();
    }

    default int getSectionIndex(int i) {
        return this.getSectionIndexFromSectionY(SectionPosition.blockToSectionCoord(i));
    }

    default int getSectionIndexFromSectionY(int i) {
        return i - this.getMinSection();
    }

    default int getSectionYFromSectionIndex(int i) {
        return i + this.getMinSection();
    }

    static LevelHeightAccessor create(final int i, final int j) {
        return new LevelHeightAccessor() {
            @Override
            public int getHeight() {
                return j;
            }

            @Override
            public int getMinBuildHeight() {
                return i;
            }
        };
    }
}
