package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenBuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenBuriedTreasure extends StructureGenerator<WorldGenFeatureConfigurationChance> {

    private static final int RANDOM_SALT = 10387320;

    public WorldGenBuriedTreasure(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance, LevelHeightAccessor levelheightaccessor) {
        seededrandom.b(i, chunkcoordintpair.x, chunkcoordintpair.z, 10387320);
        return seededrandom.nextFloat() < worldgenfeatureconfigurationchance.probability;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureConfigurationChance> a() {
        return WorldGenBuriedTreasure.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureConfigurationChance> {

        public a(StructureGenerator<WorldGenFeatureConfigurationChance> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance, LevelHeightAccessor levelheightaccessor) {
            BlockPosition blockposition = new BlockPosition(chunkcoordintpair.a(9), 90, chunkcoordintpair.b(9));

            this.a((StructurePiece) (new WorldGenBuriedTreasurePieces.a(blockposition)));
        }

        @Override
        public BlockPosition a() {
            ChunkCoordIntPair chunkcoordintpair = this.f();

            return new BlockPosition(chunkcoordintpair.a(9), 0, chunkcoordintpair.b(9));
        }
    }
}
