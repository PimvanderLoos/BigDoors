package net.minecraft.server;

import java.util.Random;

public class WorldGenBuriedTreasure extends StructureGenerator<WorldGenBuriedTreasureConfiguration> {

    public WorldGenBuriedTreasure() {}

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), (BiomeBase) null);

        if (chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.r)) {
            ((SeededRandom) random).a(chunkgenerator.getSeed(), i, j, 10387320);
            WorldGenBuriedTreasureConfiguration worldgenburiedtreasureconfiguration = (WorldGenBuriedTreasureConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.r);

            return random.nextFloat() < worldgenburiedtreasureconfiguration.a;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), (BiomeBase) null);

        return new WorldGenBuriedTreasure.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected String a() {
        return "Buried_Treasure";
    }

    public int b() {
        return 1;
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k + 9, 90, l + 9);

            this.a.add(new WorldGenBuriedTreasurePieces.a(blockposition));
            this.a((IBlockAccess) generatoraccess);
        }

        public BlockPosition a() {
            return new BlockPosition((this.c << 4) + 9, 0, (this.d << 4) + 9);
        }
    }
}
