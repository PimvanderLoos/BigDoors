package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;

public class WorldGenFeaturePillagerOutpost extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeBase.BiomeMeta> u = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.PILLAGER, 1, 1, 1)});

    public WorldGenFeaturePillagerOutpost(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public List<BiomeBase.BiomeMeta> c() {
        return WorldGenFeaturePillagerOutpost.u;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        int l = j >> 4;
        int i1 = k >> 4;

        seededrandom.setSeed((long) (l ^ i1 << 4) ^ i);
        seededrandom.nextInt();
        if (seededrandom.nextInt(5) != 0) {
            return false;
        } else {
            for (int j1 = j - 10; j1 <= j + 10; ++j1) {
                for (int k1 = k - 10; k1 <= k + 10; ++k1) {
                    ChunkCoordIntPair chunkcoordintpair1 = StructureGenerator.VILLAGE.a(chunkgenerator.getSettings().a(StructureGenerator.VILLAGE), i, seededrandom, j1, k1);

                    if (j1 == chunkcoordintpair1.x && k1 == chunkcoordintpair1.z) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeaturePillagerOutpost.a::new;
    }

    public static class a extends StructureAbstract<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            BlockPosition blockposition = new BlockPosition(i * 16, 0, j * 16);

            WorldGenFeaturePillagerOutpostPieces.a(chunkgenerator, definedstructuremanager, blockposition, this.b, this.d);
            this.b();
        }
    }
}
