package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

@FunctionalInterface
public interface PieceGeneratorSupplier<C extends WorldGenFeatureConfiguration> {

    Optional<PieceGenerator<C>> createGenerator(PieceGeneratorSupplier.a<C> piecegeneratorsupplier_a);

    static <C extends WorldGenFeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<PieceGeneratorSupplier.a<C>> predicate, PieceGenerator<C> piecegenerator) {
        Optional<PieceGenerator<C>> optional = Optional.of(piecegenerator);

        return (piecegeneratorsupplier_a) -> {
            return predicate.test(piecegeneratorsupplier_a) ? optional : Optional.empty();
        };
    }

    static <C extends WorldGenFeatureConfiguration> Predicate<PieceGeneratorSupplier.a<C>> checkForBiomeOnTop(HeightMap.Type heightmap_type) {
        return (piecegeneratorsupplier_a) -> {
            return piecegeneratorsupplier_a.validBiomeOnTop(heightmap_type);
        };
    }

    public static record a<C extends WorldGenFeatureConfiguration> (ChunkGenerator a, WorldChunkManager b, long c, ChunkCoordIntPair d, C e, LevelHeightAccessor f, Predicate<BiomeBase> g, DefinedStructureManager h, IRegistryCustom i) {

        private final ChunkGenerator chunkGenerator;
        private final WorldChunkManager biomeSource;
        private final long seed;
        private final ChunkCoordIntPair chunkPos;
        private final C config;
        private final LevelHeightAccessor heightAccessor;
        private final Predicate<BiomeBase> validBiome;
        private final DefinedStructureManager structureManager;
        private final IRegistryCustom registryAccess;

        public a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, ChunkCoordIntPair chunkcoordintpair, C c0, LevelHeightAccessor levelheightaccessor, Predicate<BiomeBase> predicate, DefinedStructureManager definedstructuremanager, IRegistryCustom iregistrycustom) {
            this.chunkGenerator = chunkgenerator;
            this.biomeSource = worldchunkmanager;
            this.seed = i;
            this.chunkPos = chunkcoordintpair;
            this.config = c0;
            this.heightAccessor = levelheightaccessor;
            this.validBiome = predicate;
            this.structureManager = definedstructuremanager;
            this.registryAccess = iregistrycustom;
        }

        public boolean validBiomeOnTop(HeightMap.Type heightmap_type) {
            int i = this.chunkPos.getMiddleBlockX();
            int j = this.chunkPos.getMiddleBlockZ();
            int k = this.chunkGenerator.getFirstOccupiedHeight(i, j, heightmap_type, this.heightAccessor);
            BiomeBase biomebase = this.chunkGenerator.getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j));

            return this.validBiome.test(biomebase);
        }

        public int[] getCornerHeights(int i, int j, int k, int l) {
            return new int[]{this.chunkGenerator.getFirstOccupiedHeight(i, k, HeightMap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(i, k + l, HeightMap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(i + j, k, HeightMap.Type.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(i + j, k + l, HeightMap.Type.WORLD_SURFACE_WG, this.heightAccessor)};
        }

        public int getLowestY(int i, int j) {
            int k = this.chunkPos.getMinBlockX();
            int l = this.chunkPos.getMinBlockZ();
            int[] aint = this.getCornerHeights(k, i, l, j);

            return Math.min(Math.min(aint[0], aint[1]), Math.min(aint[2], aint[3]));
        }

        public ChunkGenerator chunkGenerator() {
            return this.chunkGenerator;
        }

        public WorldChunkManager biomeSource() {
            return this.biomeSource;
        }

        public long seed() {
            return this.seed;
        }

        public ChunkCoordIntPair chunkPos() {
            return this.chunkPos;
        }

        public C config() {
            return this.config;
        }

        public LevelHeightAccessor heightAccessor() {
            return this.heightAccessor;
        }

        public Predicate<BiomeBase> validBiome() {
            return this.validBiome;
        }

        public DefinedStructureManager structureManager() {
            return this.structureManager;
        }

        public IRegistryCustom registryAccess() {
            return this.registryAccess;
        }
    }
}
