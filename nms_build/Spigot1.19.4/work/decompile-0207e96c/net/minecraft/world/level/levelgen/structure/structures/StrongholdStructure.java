package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class StrongholdStructure extends Structure {

    public static final Codec<StrongholdStructure> CODEC = simpleCodec(StrongholdStructure::new);

    public StrongholdStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        return Optional.of(new Structure.b(structure_a.chunkPos().getWorldPosition(), (structurepiecesbuilder) -> {
            generatePieces(structurepiecesbuilder, structure_a);
        }));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        int i = 0;

        StrongholdPieces.m strongholdpieces_m;

        do {
            structurepiecesbuilder.clear();
            structure_a.random().setLargeFeatureSeed(structure_a.seed() + (long) (i++), structure_a.chunkPos().x, structure_a.chunkPos().z);
            StrongholdPieces.resetPieces();
            strongholdpieces_m = new StrongholdPieces.m(structure_a.random(), structure_a.chunkPos().getBlockX(2), structure_a.chunkPos().getBlockZ(2));
            structurepiecesbuilder.addPiece(strongholdpieces_m);
            strongholdpieces_m.addChildren(strongholdpieces_m, structurepiecesbuilder, structure_a.random());
            List list = strongholdpieces_m.pendingChildren;

            while (!list.isEmpty()) {
                int j = structure_a.random().nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(j);

                structurepiece.addChildren(strongholdpieces_m, structurepiecesbuilder, structure_a.random());
            }

            structurepiecesbuilder.moveBelowSeaLevel(structure_a.chunkGenerator().getSeaLevel(), structure_a.chunkGenerator().getMinY(), structure_a.random(), 10);
        } while (structurepiecesbuilder.isEmpty() || strongholdpieces_m.portalRoomPiece == null);

    }

    @Override
    public StructureType<?> type() {
        return StructureType.STRONGHOLD;
    }
}
