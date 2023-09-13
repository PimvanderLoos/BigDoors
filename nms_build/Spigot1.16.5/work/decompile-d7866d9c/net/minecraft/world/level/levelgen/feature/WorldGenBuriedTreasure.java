package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenBuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenBuriedTreasure extends StructureGenerator<WorldGenFeatureConfigurationChance> {

    public WorldGenBuriedTreasure(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
        seededrandom.a(i, j, k, 10387320);
        return seededrandom.nextFloat() < worldgenfeatureconfigurationchance.c;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureConfigurationChance> a() {
        return WorldGenBuriedTreasure.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureConfigurationChance> {

        public a(StructureGenerator<WorldGenFeatureConfigurationChance> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k + 9, 90, l + 9);

            this.b.add(new WorldGenBuriedTreasurePieces.a(blockposition));
            this.b();
        }

        @Override
        public BlockPosition a() {
            return new BlockPosition((this.f() << 4) + 9, 0, (this.g() << 4) + 9);
        }
    }
}
