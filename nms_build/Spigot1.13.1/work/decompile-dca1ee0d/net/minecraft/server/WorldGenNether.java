package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class WorldGenNether extends StructureGenerator<WorldGenNetherConfiguration> {

    private static final List<BiomeBase.BiomeMeta> b = Lists.newArrayList(new BiomeBase.BiomeMeta[] { new BiomeBase.BiomeMeta(EntityTypes.BLAZE, 10, 2, 3), new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_PIGMAN, 5, 4, 4), new BiomeBase.BiomeMeta(EntityTypes.WITHER_SKELETON, 8, 5, 5), new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 2, 5, 5), new BiomeBase.BiomeMeta(EntityTypes.MAGMA_CUBE, 3, 4, 4)});

    public WorldGenNether() {}

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        int k = i >> 4;
        int l = j >> 4;

        random.setSeed((long) (k ^ l << 4) ^ chunkgenerator.getSeed());
        random.nextInt();
        if (random.nextInt(3) != 0) {
            return false;
        } else if (i != (k << 4) + 4 + random.nextInt(8)) {
            return false;
        } else if (j != (l << 4) + 4 + random.nextInt(8)) {
            return false;
        } else {
            BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

            return chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.p);
        }
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        return new WorldGenNether.a(generatoraccess, seededrandom, i, j, biomebase);
    }

    protected String a() {
        return "Fortress";
    }

    public int b() {
        return 8;
    }

    public List<BiomeBase.BiomeMeta> d() {
        return WorldGenNether.b;
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenNetherPieces.WorldGenNetherPiece15 worldgennetherpieces_worldgennetherpiece15 = new WorldGenNetherPieces.WorldGenNetherPiece15(seededrandom, (i << 4) + 2, (j << 4) + 2);

            this.a.add(worldgennetherpieces_worldgennetherpiece15);
            worldgennetherpieces_worldgennetherpiece15.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.a, (Random) seededrandom);
            List list = worldgennetherpieces_worldgennetherpiece15.d;

            while (!list.isEmpty()) {
                int k = seededrandom.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(k);

                structurepiece.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.a, (Random) seededrandom);
            }

            this.a((IBlockAccess) generatoraccess);
            this.a(generatoraccess, seededrandom, 48, 70);
        }
    }
}
