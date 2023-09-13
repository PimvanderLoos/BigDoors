package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureBastionRemnant extends StructureGenerator<WorldGenFeatureBastionRemnantConfiguration> {

    public WorldGenFeatureBastionRemnant(Codec<WorldGenFeatureBastionRemnantConfiguration> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureBastionRemnantConfiguration worldgenfeaturebastionremnantconfiguration) {
        return seededrandom.nextInt(5) >= 2;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureBastionRemnantConfiguration> a() {
        return WorldGenFeatureBastionRemnant.a::new;
    }

    public static class a extends StructureAbstract<WorldGenFeatureBastionRemnantConfiguration> {

        public a(StructureGenerator<WorldGenFeatureBastionRemnantConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureBastionRemnantConfiguration worldgenfeaturebastionremnantconfiguration) {
            BlockPosition blockposition = new BlockPosition(i * 16, 33, j * 16);

            WorldGenFeatureBastionPieces.a(chunkgenerator, definedstructuremanager, blockposition, this.b, this.d, worldgenfeaturebastionremnantconfiguration);
            this.b();
        }
    }
}
