package net.minecraft.server;

import java.util.Random;

public class WorldGenEndCity extends StructureGenerator<WorldGenEndCityConfiguration> {

    public WorldGenEndCity() {}

    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = chunkgenerator.getSettings().n();
        int j1 = chunkgenerator.getSettings().o();
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, 10387313);
        k2 *= i1;
        l2 *= i1;
        k2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        l2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        return new ChunkCoordIntPair(k2, l2);
    }

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

            if (!chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.q)) {
                return false;
            } else {
                int k = b(i, j, chunkgenerator);

                return k >= 60;
            }
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        return new WorldGenEndCity.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected String a() {
        return "EndCity";
    }

    public int b() {
        return 9;
    }

    private static int b(int i, int j, ChunkGenerator<?> chunkgenerator) {
        Random random = new Random((long) (i + j * 10387313));
        EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[random.nextInt(EnumBlockRotation.values().length)];
        ProtoChunk protochunk = new ProtoChunk(new ChunkCoordIntPair(i, j), ChunkConverter.a);

        chunkgenerator.createChunk(protochunk);
        byte b0 = 5;
        byte b1 = 5;

        if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            b0 = -5;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            b0 = -5;
            b1 = -5;
        } else if (enumblockrotation == EnumBlockRotation.COUNTERCLOCKWISE_90) {
            b1 = -5;
        }

        int k = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7, 7);
        int l = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7, 7 + b1);
        int i1 = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7 + b0, 7);
        int j1 = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7 + b0, 7 + b1);

        return Math.min(Math.min(k, l), Math.min(i1, j1));
    }

    public static class a extends StructureStart {

        private boolean e;

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[seededrandom.nextInt(EnumBlockRotation.values().length)];
            int k = WorldGenEndCity.b(i, j, chunkgenerator);

            if (k < 60) {
                this.e = false;
            } else {
                BlockPosition blockposition = new BlockPosition(i * 16 + 8, k, j * 16 + 8);

                WorldGenEndCityPieces.a(generatoraccess.getDataManager().h(), blockposition, enumblockrotation, this.a, seededrandom);
                this.a((IBlockAccess) generatoraccess);
                this.e = true;
            }
        }

        public boolean b() {
            return this.e;
        }
    }
}
