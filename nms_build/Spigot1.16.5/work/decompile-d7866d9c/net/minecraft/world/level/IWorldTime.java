package net.minecraft.world.level;

import net.minecraft.world.level.dimension.DimensionManager;

public interface IWorldTime extends IWorldReader {

    long ac();

    default float af() {
        return DimensionManager.e[this.getDimensionManager().b(this.ac())];
    }

    default float f(float f) {
        return this.getDimensionManager().a(this.ac());
    }
}
