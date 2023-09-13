package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureNetherFossil extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureNetherFossil(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureNetherFossil.a::new;
    }

    public static class a extends StructureAbstract<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
            int k = chunkcoordintpair.d() + this.d.nextInt(16);
            int l = chunkcoordintpair.e() + this.d.nextInt(16);
            int i1 = chunkgenerator.getSeaLevel();
            int j1 = i1 + this.d.nextInt(chunkgenerator.getGenerationDepth() - 2 - i1);
            IBlockAccess iblockaccess = chunkgenerator.a(k, l);

            for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(k, j1, l); j1 > i1; --j1) {
                IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

                blockposition_mutableblockposition.c(EnumDirection.DOWN);
                IBlockData iblockdata1 = iblockaccess.getType(blockposition_mutableblockposition);

                if (iblockdata.isAir() && (iblockdata1.a(Blocks.SOUL_SAND) || iblockdata1.d(iblockaccess, blockposition_mutableblockposition, EnumDirection.UP))) {
                    break;
                }
            }

            if (j1 > i1) {
                WorldGenNetherFossil.a(definedstructuremanager, this.b, this.d, new BlockPosition(k, j1, l));
                this.b();
            }
        }
    }
}
