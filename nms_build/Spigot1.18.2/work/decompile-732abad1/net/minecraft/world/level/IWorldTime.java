package net.minecraft.world.level;

import net.minecraft.world.level.dimension.DimensionManager;

public interface IWorldTime extends IWorldReader {

    long dayTime();

    default float getMoonBrightness() {
        return DimensionManager.MOON_BRIGHTNESS_PER_PHASE[this.dimensionType().moonPhase(this.dayTime())];
    }

    default float getTimeOfDay(float f) {
        return this.dimensionType().timeOfDay(this.dayTime());
    }

    default int getMoonPhase() {
        return this.dimensionType().moonPhase(this.dayTime());
    }
}
