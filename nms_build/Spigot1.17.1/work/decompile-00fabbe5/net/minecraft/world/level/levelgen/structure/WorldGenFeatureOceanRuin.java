package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.INamable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureOceanRuin extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration> {

    public WorldGenFeatureOceanRuin(Codec<WorldGenFeatureOceanRuinConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureOceanRuinConfiguration> a() {
        return WorldGenFeatureOceanRuin.a::new;
    }

    public static enum Temperature implements INamable {

        WARM("warm"), COLD("cold");

        public static final Codec<WorldGenFeatureOceanRuin.Temperature> CODEC = INamable.a(WorldGenFeatureOceanRuin.Temperature::values, WorldGenFeatureOceanRuin.Temperature::a);
        private static final Map<String, WorldGenFeatureOceanRuin.Temperature> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureOceanRuin.Temperature::a, (worldgenfeatureoceanruin_temperature) -> {
            return worldgenfeatureoceanruin_temperature;
        }));
        private final String name;

        private Temperature(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        @Nullable
        public static WorldGenFeatureOceanRuin.Temperature a(String s) {
            return (WorldGenFeatureOceanRuin.Temperature) WorldGenFeatureOceanRuin.Temperature.BY_NAME.get(s);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static class a extends StructureStart<WorldGenFeatureOceanRuinConfiguration> {

        public a(StructureGenerator<WorldGenFeatureOceanRuinConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration, LevelHeightAccessor levelheightaccessor) {
            BlockPosition blockposition = new BlockPosition(chunkcoordintpair.d(), 90, chunkcoordintpair.e());
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.random);

            WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, blockposition, enumblockrotation, (StructurePieceAccessor) this, (Random) this.random, worldgenfeatureoceanruinconfiguration);
        }
    }
}
