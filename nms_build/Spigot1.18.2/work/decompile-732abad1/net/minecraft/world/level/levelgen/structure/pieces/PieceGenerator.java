package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

@FunctionalInterface
public interface PieceGenerator<C extends WorldGenFeatureConfiguration> {

    void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<C> piecegenerator_a);

    public static record a<C extends WorldGenFeatureConfiguration> (C a, ChunkGenerator b, DefinedStructureManager c, ChunkCoordIntPair d, LevelHeightAccessor e, SeededRandom f, long g) {

        private final C config;
        private final ChunkGenerator chunkGenerator;
        private final DefinedStructureManager structureManager;
        private final ChunkCoordIntPair chunkPos;
        private final LevelHeightAccessor heightAccessor;
        private final SeededRandom random;
        private final long seed;

        public a(C c0, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, LevelHeightAccessor levelheightaccessor, SeededRandom seededrandom, long i) {
            this.config = c0;
            this.chunkGenerator = chunkgenerator;
            this.structureManager = definedstructuremanager;
            this.chunkPos = chunkcoordintpair;
            this.heightAccessor = levelheightaccessor;
            this.random = seededrandom;
            this.seed = i;
        }

        public C config() {
            return this.config;
        }

        public ChunkGenerator chunkGenerator() {
            return this.chunkGenerator;
        }

        public DefinedStructureManager structureManager() {
            return this.structureManager;
        }

        public ChunkCoordIntPair chunkPos() {
            return this.chunkPos;
        }

        public LevelHeightAccessor heightAccessor() {
            return this.heightAccessor;
        }

        public SeededRandom random() {
            return this.random;
        }

        public long seed() {
            return this.seed;
        }
    }
}
