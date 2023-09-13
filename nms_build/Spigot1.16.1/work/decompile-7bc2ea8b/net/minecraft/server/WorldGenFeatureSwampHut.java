package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;

public class WorldGenFeatureSwampHut extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeBase.BiomeMeta> u = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.WITCH, 1, 1, 1)});
    private static final List<BiomeBase.BiomeMeta> v = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.CAT, 1, 1, 1)});

    public WorldGenFeatureSwampHut(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureSwampHut.a::new;
    }

    @Override
    public List<BiomeBase.BiomeMeta> c() {
        return WorldGenFeatureSwampHut.u;
    }

    @Override
    public List<BiomeBase.BiomeMeta> j() {
        return WorldGenFeatureSwampHut.v;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            WorldGenWitchHut worldgenwitchhut = new WorldGenWitchHut(this.d, i * 16, j * 16);

            this.b.add(worldgenwitchhut);
            this.b();
        }
    }
}
