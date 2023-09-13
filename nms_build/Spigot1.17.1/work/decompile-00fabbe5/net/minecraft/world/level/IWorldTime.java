package net.minecraft.world.level;

import net.minecraft.world.level.dimension.DimensionManager;

public interface IWorldTime extends IWorldReader {

    long ae();

    default float ak() {
        return DimensionManager.MOON_BRIGHTNESS_PER_PHASE[this.getDimensionManager().b(this.ae())];
    }

    default float f(float f) {
        return this.getDimensionManager().a(this.ae());
    }

    default int al() {
        return this.getDimensionManager().b(this.ae());
    }
}
