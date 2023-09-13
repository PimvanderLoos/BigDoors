package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureJigsaw extends StructureGenerator<WorldGenFeatureVillageConfiguration> {

    private final int u;
    private final boolean v;
    private final boolean w;

    public WorldGenFeatureJigsaw(Codec<WorldGenFeatureVillageConfiguration> codec, int i, boolean flag, boolean flag1) {
        super(codec);
        this.u = i;
        this.v = flag;
        this.w = flag1;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureVillageConfiguration> a() {
        return (structuregenerator, i, j, structureboundingbox, k, l) -> {
            return new WorldGenFeatureJigsaw.a(this, i, j, structureboundingbox, k, l);
        };
    }

    public static class a extends StructureAbstract<WorldGenFeatureVillageConfiguration> {

        private final WorldGenFeatureJigsaw e;

        public a(WorldGenFeatureJigsaw worldgenfeaturejigsaw, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(worldgenfeaturejigsaw, i, j, structureboundingbox, k, l);
            this.e = worldgenfeaturejigsaw;
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration) {
            BlockPosition blockposition = new BlockPosition(i * 16, this.e.u, j * 16);

            WorldGenFeaturePieces.a();
            WorldGenFeatureDefinedStructureJigsawPlacement.a(iregistrycustom, worldgenfeaturevillageconfiguration, WorldGenFeaturePillagerOutpostPoolPiece::new, chunkgenerator, definedstructuremanager, blockposition, this.b, this.d, this.e.v, this.e.w);
            this.b();
        }
    }
}
