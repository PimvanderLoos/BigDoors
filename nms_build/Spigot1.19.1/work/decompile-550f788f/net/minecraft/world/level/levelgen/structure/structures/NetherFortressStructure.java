package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFortressStructure extends Structure {

    public static final WeightedRandomList<BiomeSettingsMobs.c> FORTRESS_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new BiomeSettingsMobs.c(EntityTypes.BLAZE, 10, 2, 3), new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 5, 4, 4), new BiomeSettingsMobs.c(EntityTypes.WITHER_SKELETON, 8, 5, 5), new BiomeSettingsMobs.c(EntityTypes.SKELETON, 2, 5, 5), new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 3, 4, 4)));
    public static final Codec<NetherFortressStructure> CODEC = simpleCodec(NetherFortressStructure::new);

    public NetherFortressStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMinBlockX(), 64, chunkcoordintpair.getMinBlockZ());

        return Optional.of(new Structure.b(blockposition, (structurepiecesbuilder) -> {
            generatePieces(structurepiecesbuilder, structure_a);
        }));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        NetherFortressPieces.q netherfortresspieces_q = new NetherFortressPieces.q(structure_a.random(), structure_a.chunkPos().getBlockX(2), structure_a.chunkPos().getBlockZ(2));

        structurepiecesbuilder.addPiece(netherfortresspieces_q);
        netherfortresspieces_q.addChildren(netherfortresspieces_q, structurepiecesbuilder, structure_a.random());
        List list = netherfortresspieces_q.pendingChildren;

        while (!list.isEmpty()) {
            int i = structure_a.random().nextInt(list.size());
            StructurePiece structurepiece = (StructurePiece) list.remove(i);

            structurepiece.addChildren(netherfortresspieces_q, structurepiecesbuilder, structure_a.random());
        }

        structurepiecesbuilder.moveInsideHeights(structure_a.random(), 48, 70);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.FORTRESS;
    }
}
