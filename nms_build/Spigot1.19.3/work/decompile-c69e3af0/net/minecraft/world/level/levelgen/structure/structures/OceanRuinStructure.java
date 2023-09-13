package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanRuinStructure extends Structure {

    public static final Codec<OceanRuinStructure> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(settingsCodec(instance), OceanRuinStructure.a.CODEC.fieldOf("biome_temp").forGetter((oceanruinstructure) -> {
            return oceanruinstructure.biomeTemp;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter((oceanruinstructure) -> {
            return oceanruinstructure.largeProbability;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter((oceanruinstructure) -> {
            return oceanruinstructure.clusterProbability;
        })).apply(instance, OceanRuinStructure::new);
    });
    public final OceanRuinStructure.a biomeTemp;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinStructure(Structure.c structure_c, OceanRuinStructure.a oceanruinstructure_a, float f, float f1) {
        super(structure_c);
        this.biomeTemp = oceanruinstructure_a;
        this.largeProbability = f;
        this.clusterProbability = f1;
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        return onTopOfChunkCenter(structure_a, HeightMap.Type.OCEAN_FLOOR_WG, (structurepiecesbuilder) -> {
            this.generatePieces(structurepiecesbuilder, structure_a);
        });
    }

    private void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        BlockPosition blockposition = new BlockPosition(structure_a.chunkPos().getMinBlockX(), 90, structure_a.chunkPos().getMinBlockZ());
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(structure_a.random());

        OceanRuinPieces.addPieces(structure_a.structureTemplateManager(), blockposition, enumblockrotation, structurepiecesbuilder, structure_a.random(), this);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.OCEAN_RUIN;
    }

    public static enum a implements INamable {

        WARM("warm"), COLD("cold");

        public static final Codec<OceanRuinStructure.a> CODEC = INamable.fromEnum(OceanRuinStructure.a::values);
        private final String name;

        private a(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
