package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class EndCityStructure extends Structure {

    public static final Codec<EndCityStructure> CODEC = simpleCodec(EndCityStructure::new);

    public EndCityStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(structure_a.random());
        BlockPosition blockposition = this.getLowestYIn5by5BoxOffset7Blocks(structure_a, enumblockrotation);

        return blockposition.getY() < 60 ? Optional.empty() : Optional.of(new Structure.b(blockposition, (structurepiecesbuilder) -> {
            this.generatePieces(structurepiecesbuilder, blockposition, enumblockrotation, structure_a);
        }));
    }

    private void generatePieces(StructurePiecesBuilder structurepiecesbuilder, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Structure.a structure_a) {
        List<StructurePiece> list = Lists.newArrayList();

        EndCityPieces.startHouseTower(structure_a.structureTemplateManager(), blockposition, enumblockrotation, list, structure_a.random());
        Objects.requireNonNull(structurepiecesbuilder);
        list.forEach(structurepiecesbuilder::addPiece);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.END_CITY;
    }
}
