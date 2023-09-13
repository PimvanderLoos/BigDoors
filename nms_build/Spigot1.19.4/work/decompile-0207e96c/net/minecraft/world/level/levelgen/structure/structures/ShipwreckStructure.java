package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class ShipwreckStructure extends Structure {

    public static final Codec<ShipwreckStructure> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(settingsCodec(instance), Codec.BOOL.fieldOf("is_beached").forGetter((shipwreckstructure) -> {
            return shipwreckstructure.isBeached;
        })).apply(instance, ShipwreckStructure::new);
    });
    public final boolean isBeached;

    public ShipwreckStructure(Structure.c structure_c, boolean flag) {
        super(structure_c);
        this.isBeached = flag;
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        HeightMap.Type heightmap_type = this.isBeached ? HeightMap.Type.WORLD_SURFACE_WG : HeightMap.Type.OCEAN_FLOOR_WG;

        return onTopOfChunkCenter(structure_a, heightmap_type, (structurepiecesbuilder) -> {
            this.generatePieces(structurepiecesbuilder, structure_a);
        });
    }

    private void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(structure_a.random());
        BlockPosition blockposition = new BlockPosition(structure_a.chunkPos().getMinBlockX(), 90, structure_a.chunkPos().getMinBlockZ());

        ShipwreckPieces.addPieces(structure_a.structureTemplateManager(), blockposition, enumblockrotation, structurepiecesbuilder, structure_a.random(), this.isBeached);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.SHIPWRECK;
    }
}
