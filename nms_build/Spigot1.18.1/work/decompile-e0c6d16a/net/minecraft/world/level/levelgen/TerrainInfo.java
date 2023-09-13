package net.minecraft.world.level.levelgen;

public record TerrainInfo(double a, double b, double c) {

    private final double offset;
    private final double factor;
    private final double jaggedness;

    public TerrainInfo(double d0, double d1, double d2) {
        this.offset = d0;
        this.factor = d1;
        this.jaggedness = d2;
    }

    public double offset() {
        return this.offset;
    }

    public double factor() {
        return this.factor;
    }

    public double jaggedness() {
        return this.jaggedness;
    }
}
