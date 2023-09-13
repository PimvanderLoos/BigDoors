package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureDesertPyramid extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureDesertPyramid(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureDesertPyramid.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            WorldGenDesertPyramidPiece worldgendesertpyramidpiece = new WorldGenDesertPyramidPiece(this.d, i * 16, j * 16);

            this.b.add(worldgendesertpyramidpiece);
            this.b();
        }
    }
}
