package net.minecraft.server;

public interface IWorldTime extends IWorldReader {

    long ab();

    default float ae() {
        return DimensionManager.e[this.getDimensionManager().b(this.ab())];
    }

    default float f(float f) {
        return this.getDimensionManager().a(this.ab());
    }
}
