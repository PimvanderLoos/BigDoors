package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public abstract class NoiseAffectingStructureFeature<C extends WorldGenFeatureConfiguration> extends StructureGenerator<C> {

    public NoiseAffectingStructureFeature(Codec<C> codec, PieceGeneratorSupplier<C> piecegeneratorsupplier) {
        super(codec, piecegeneratorsupplier);
    }

    public NoiseAffectingStructureFeature(Codec<C> codec, PieceGeneratorSupplier<C> piecegeneratorsupplier, PostPlacementProcessor postplacementprocessor) {
        super(codec, piecegeneratorsupplier, postplacementprocessor);
    }

    @Override
    public StructureBoundingBox adjustBoundingBox(StructureBoundingBox structureboundingbox) {
        return super.adjustBoundingBox(structureboundingbox).inflatedBy(12);
    }
}
