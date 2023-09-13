package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class WorldGenNether extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeBase.BiomeMeta> a = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.BLAZE, 10, 2, 3), new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_PIGMAN, 5, 4, 4), new BiomeBase.BiomeMeta(EntityTypes.WITHER_SKELETON, 8, 5, 5), new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 2, 5, 5), new BiomeBase.BiomeMeta(EntityTypes.MAGMA_CUBE, 3, 4, 4)});

    public WorldGenNether(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    public boolean a(BiomeManager biomemanager, ChunkGenerator<?> chunkgenerator, Random random, int i, int j, BiomeBase biomebase) {
        int k = i >> 4;
        int l = j >> 4;

        random.setSeed((long) (k ^ l << 4) ^ chunkgenerator.getSeed());
        random.nextInt();
        return random.nextInt(3) != 0 ? false : (i != (k << 4) + 4 + random.nextInt(8) ? false : (j != (l << 4) + 4 + random.nextInt(8) ? false : chunkgenerator.canSpawnStructure(biomebase, this)));
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenNether.a::new;
    }

    @Override
    public String b() {
        return "Fortress";
    }

    @Override
    public int c() {
        return 8;
    }

    @Override
    public List<BiomeBase.BiomeMeta> e() {
        return WorldGenNether.a;
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenNetherPieces.WorldGenNetherPiece15 worldgennetherpieces_worldgennetherpiece15 = new WorldGenNetherPieces.WorldGenNetherPiece15(this.d, (i << 4) + 2, (j << 4) + 2);

            this.b.add(worldgennetherpieces_worldgennetherpiece15);
            worldgennetherpieces_worldgennetherpiece15.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.b, (Random) this.d);
            List list = worldgennetherpieces_worldgennetherpiece15.d;

            while (!list.isEmpty()) {
                int k = this.d.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(k);

                structurepiece.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.b, (Random) this.d);
            }

            this.b();
            this.a(this.d, 48, 70);
        }
    }
}
