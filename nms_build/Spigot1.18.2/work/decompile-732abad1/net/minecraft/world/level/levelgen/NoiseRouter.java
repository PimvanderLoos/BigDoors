package net.minecraft.world.level.levelgen;

import java.util.List;
import net.minecraft.world.level.biome.Climate;

public record NoiseRouter(DensityFunction a, DensityFunction b, DensityFunction c, DensityFunction d, PositionalRandomFactory e, PositionalRandomFactory f, DensityFunction g, DensityFunction h, DensityFunction i, DensityFunction j, DensityFunction k, DensityFunction l, DensityFunction m, DensityFunction n, DensityFunction o, DensityFunction p, DensityFunction q, List<Climate.d> r) {

    private final DensityFunction barrierNoise;
    private final DensityFunction fluidLevelFloodednessNoise;
    private final DensityFunction fluidLevelSpreadNoise;
    private final DensityFunction lavaNoise;
    private final PositionalRandomFactory aquiferPositionalRandomFactory;
    private final PositionalRandomFactory oreVeinsPositionalRandomFactory;
    private final DensityFunction temperature;
    private final DensityFunction humidity;
    private final DensityFunction continents;
    private final DensityFunction erosion;
    private final DensityFunction depth;
    private final DensityFunction ridges;
    private final DensityFunction initialDensityWithoutJaggedness;
    private final DensityFunction finalDensity;
    private final DensityFunction veinToggle;
    private final DensityFunction veinRidged;
    private final DensityFunction veinGap;
    private final List<Climate.d> spawnTarget;

    public NoiseRouter(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, DensityFunction densityfunction3, PositionalRandomFactory positionalrandomfactory, PositionalRandomFactory positionalrandomfactory1, DensityFunction densityfunction4, DensityFunction densityfunction5, DensityFunction densityfunction6, DensityFunction densityfunction7, DensityFunction densityfunction8, DensityFunction densityfunction9, DensityFunction densityfunction10, DensityFunction densityfunction11, DensityFunction densityfunction12, DensityFunction densityfunction13, DensityFunction densityfunction14, List<Climate.d> list) {
        this.barrierNoise = densityfunction;
        this.fluidLevelFloodednessNoise = densityfunction1;
        this.fluidLevelSpreadNoise = densityfunction2;
        this.lavaNoise = densityfunction3;
        this.aquiferPositionalRandomFactory = positionalrandomfactory;
        this.oreVeinsPositionalRandomFactory = positionalrandomfactory1;
        this.temperature = densityfunction4;
        this.humidity = densityfunction5;
        this.continents = densityfunction6;
        this.erosion = densityfunction7;
        this.depth = densityfunction8;
        this.ridges = densityfunction9;
        this.initialDensityWithoutJaggedness = densityfunction10;
        this.finalDensity = densityfunction11;
        this.veinToggle = densityfunction12;
        this.veinRidged = densityfunction13;
        this.veinGap = densityfunction14;
        this.spawnTarget = list;
    }

    public DensityFunction barrierNoise() {
        return this.barrierNoise;
    }

    public DensityFunction fluidLevelFloodednessNoise() {
        return this.fluidLevelFloodednessNoise;
    }

    public DensityFunction fluidLevelSpreadNoise() {
        return this.fluidLevelSpreadNoise;
    }

    public DensityFunction lavaNoise() {
        return this.lavaNoise;
    }

    public PositionalRandomFactory aquiferPositionalRandomFactory() {
        return this.aquiferPositionalRandomFactory;
    }

    public PositionalRandomFactory oreVeinsPositionalRandomFactory() {
        return this.oreVeinsPositionalRandomFactory;
    }

    public DensityFunction temperature() {
        return this.temperature;
    }

    public DensityFunction humidity() {
        return this.humidity;
    }

    public DensityFunction continents() {
        return this.continents;
    }

    public DensityFunction erosion() {
        return this.erosion;
    }

    public DensityFunction depth() {
        return this.depth;
    }

    public DensityFunction ridges() {
        return this.ridges;
    }

    public DensityFunction initialDensityWithoutJaggedness() {
        return this.initialDensityWithoutJaggedness;
    }

    public DensityFunction finalDensity() {
        return this.finalDensity;
    }

    public DensityFunction veinToggle() {
        return this.veinToggle;
    }

    public DensityFunction veinRidged() {
        return this.veinRidged;
    }

    public DensityFunction veinGap() {
        return this.veinGap;
    }

    public List<Climate.d> spawnTarget() {
        return this.spawnTarget;
    }
}
