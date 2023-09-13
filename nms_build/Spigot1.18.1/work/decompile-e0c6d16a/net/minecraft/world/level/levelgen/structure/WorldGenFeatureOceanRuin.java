package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenFeatureOceanRuin extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration> {

    public WorldGenFeatureOceanRuin(Codec<WorldGenFeatureOceanRuinConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(HeightMap.Type.OCEAN_FLOOR_WG), WorldGenFeatureOceanRuin::generatePieces));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureOceanRuinConfiguration> piecegenerator_a) {
        BlockPosition blockposition = new BlockPosition(piecegenerator_a.chunkPos().getMinBlockX(), 90, piecegenerator_a.chunkPos().getMinBlockZ());
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(piecegenerator_a.random());

        WorldGenFeatureOceanRuinPieces.addPieces(piecegenerator_a.structureManager(), blockposition, enumblockrotation, structurepiecesbuilder, piecegenerator_a.random(), (WorldGenFeatureOceanRuinConfiguration) piecegenerator_a.config());
    }

    public static enum Temperature implements INamable {

        WARM("warm"), COLD("cold");

        public static final Codec<WorldGenFeatureOceanRuin.Temperature> CODEC = INamable.fromEnum(WorldGenFeatureOceanRuin.Temperature::values, WorldGenFeatureOceanRuin.Temperature::byName);
        private static final Map<String, WorldGenFeatureOceanRuin.Temperature> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureOceanRuin.Temperature::getName, (worldgenfeatureoceanruin_temperature) -> {
            return worldgenfeatureoceanruin_temperature;
        }));
        private final String name;

        private Temperature(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static WorldGenFeatureOceanRuin.Temperature byName(String s) {
            return (WorldGenFeatureOceanRuin.Temperature) WorldGenFeatureOceanRuin.Temperature.BY_NAME.get(s);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
