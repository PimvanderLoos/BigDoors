package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public abstract class NoiseAffectingStructureStart<C extends WorldGenFeatureConfiguration> extends StructureStart<C> {

    public NoiseAffectingStructureStart(StructureGenerator<C> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
        super(structuregenerator, chunkcoordintpair, i, j);
    }

    @Override
    protected StructureBoundingBox b() {
        return super.b().a(12);
    }
}
