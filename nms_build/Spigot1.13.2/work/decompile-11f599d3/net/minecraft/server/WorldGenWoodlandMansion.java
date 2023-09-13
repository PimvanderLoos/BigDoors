package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenWoodlandMansion extends StructureGenerator<WorldGenMansionConfiguration> {

    public WorldGenWoodlandMansion() {}

    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = chunkgenerator.getSettings().p();
        int j1 = chunkgenerator.getSettings().q();
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, 10387319);
        k2 *= i1;
        l2 *= i1;
        k2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        l2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        return new ChunkCoordIntPair(k2, l2);
    }

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            Set<BiomeBase> set = chunkgenerator.getWorldChunkManager().a(i * 16 + 9, j * 16 + 9, 32);
            Iterator iterator = set.iterator();

            BiomeBase biomebase;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                biomebase = (BiomeBase) iterator.next();
            } while (chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.g));

            return false;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        return new WorldGenWoodlandMansion.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected String a() {
        return "Mansion";
    }

    public int b() {
        return 8;
    }

    public static class a extends StructureStart {

        private boolean e;

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[seededrandom.nextInt(EnumBlockRotation.values().length)];
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

            ProtoChunk protochunk = new ProtoChunk(new ChunkCoordIntPair(i, j), ChunkConverter.a);

            chunkgenerator.createChunk(protochunk);
            int k = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7, 7);
            int l = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7, 7 + b1);
            int i1 = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7 + b0, 7);
            int j1 = protochunk.a(HeightMap.Type.MOTION_BLOCKING, 7 + b0, 7 + b1);
            int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));

            if (k1 < 60) {
                this.e = false;
            } else {
                BlockPosition blockposition = new BlockPosition(i * 16 + 8, k1 + 1, j * 16 + 8);
                List<WorldGenWoodlandMansionPieces.i> list = Lists.newLinkedList();

                WorldGenWoodlandMansionPieces.a(generatoraccess.getDataManager().h(), blockposition, enumblockrotation, list, seededrandom);
                this.a.addAll(list);
                this.a((IBlockAccess) generatoraccess);
                this.e = true;
            }
        }

        public void a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            super.a(generatoraccess, random, structureboundingbox, chunkcoordintpair);
            int i = this.b.b;

            for (int j = structureboundingbox.a; j <= structureboundingbox.d; ++j) {
                for (int k = structureboundingbox.c; k <= structureboundingbox.f; ++k) {
                    BlockPosition blockposition = new BlockPosition(j, i, k);

                    if (!generatoraccess.isEmpty(blockposition) && this.b.b((BaseBlockPosition) blockposition)) {
                        boolean flag = false;
                        Iterator iterator = this.a.iterator();

                        while (iterator.hasNext()) {
                            StructurePiece structurepiece = (StructurePiece) iterator.next();

                            if (structurepiece.d().b((BaseBlockPosition) blockposition)) {
                                flag = true;
                                break;
                            }
                        }

                        if (flag) {
                            for (int l = i - 1; l > 1; --l) {
                                BlockPosition blockposition1 = new BlockPosition(j, l, k);

                                if (!generatoraccess.isEmpty(blockposition1) && !generatoraccess.getType(blockposition1).getMaterial().isLiquid()) {
                                    break;
                                }

                                generatoraccess.setTypeAndData(blockposition1, Blocks.COBBLESTONE.getBlockData(), 2);
                            }
                        }
                    }
                }
            }

        }

        public boolean b() {
            return this.e;
        }
    }
}
