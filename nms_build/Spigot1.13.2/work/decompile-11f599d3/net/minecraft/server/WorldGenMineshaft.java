package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenMineshaft extends StructureGenerator<WorldGenMineshaftConfiguration> {

    public WorldGenMineshaft() {}

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ((SeededRandom) random).c(chunkgenerator.getSeed(), i, j);
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        if (chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.f)) {
            WorldGenMineshaftConfiguration worldgenmineshaftconfiguration = (WorldGenMineshaftConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.f);
            double d0 = worldgenmineshaftconfiguration.a;

            return random.nextDouble() < d0;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        return new WorldGenMineshaft.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected String a() {
        return "Mineshaft";
    }

    public int b() {
        return 8;
    }

    public static class a extends StructureStart {

        private WorldGenMineshaft.Type e;

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenMineshaftConfiguration worldgenmineshaftconfiguration = (WorldGenMineshaftConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.f);

            this.e = worldgenmineshaftconfiguration.b;
            WorldGenMineshaftPieces.WorldGenMineshaftRoom worldgenmineshaftpieces_worldgenmineshaftroom = new WorldGenMineshaftPieces.WorldGenMineshaftRoom(0, seededrandom, (i << 4) + 2, (j << 4) + 2, this.e);

            this.a.add(worldgenmineshaftpieces_worldgenmineshaftroom);
            worldgenmineshaftpieces_worldgenmineshaftroom.a((StructurePiece) worldgenmineshaftpieces_worldgenmineshaftroom, this.a, (Random) seededrandom);
            this.a((IBlockAccess) generatoraccess);
            if (worldgenmineshaftconfiguration.b == WorldGenMineshaft.Type.MESA) {
                boolean flag = true;
                int k = generatoraccess.getSeaLevel() - this.b.e + this.b.d() / 2 - -5;

                this.b.a(0, k, 0);
                Iterator iterator = this.a.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    structurepiece.a(0, k, 0);
                }
            } else {
                this.a(generatoraccess, seededrandom, 10);
            }

        }
    }

    public static enum Type {

        NORMAL, MESA;

        private Type() {}

        public static WorldGenMineshaft.Type a(int i) {
            return i >= 0 && i < values().length ? values()[i] : WorldGenMineshaft.Type.NORMAL;
        }
    }
}
