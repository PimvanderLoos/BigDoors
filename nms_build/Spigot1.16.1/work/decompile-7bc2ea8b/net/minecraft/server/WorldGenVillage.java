package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenVillage extends StructureGenerator<WorldGenFeatureVillageConfiguration> {

    public WorldGenVillage(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureVillageConfiguration> a() {
        return WorldGenVillage.a::new;
    }

    public static class a extends StructureAbstract<WorldGenFeatureVillageConfiguration> {

        public a(StructureGenerator<WorldGenFeatureVillageConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration) {
            BlockPosition blockposition = new BlockPosition(i * 16, 0, j * 16);

            NewVillagePieces.a(chunkgenerator, definedstructuremanager, blockposition, this.b, this.d, worldgenfeaturevillageconfiguration);
            this.b();
        }
    }
}
