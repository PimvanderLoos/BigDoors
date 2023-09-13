package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class WorldGenBuriedTreasure extends StructureGenerator<WorldGenBuriedTreasureConfiguration> {

    public WorldGenBuriedTreasure(Function<Dynamic<?>, ? extends WorldGenBuriedTreasureConfiguration> function) {
        super(function);
    }

    @Override
    public boolean a(BiomeManager biomemanager, ChunkGenerator<?> chunkgenerator, Random random, int i, int j, BiomeBase biomebase) {
        if (chunkgenerator.canSpawnStructure(biomebase, this)) {
            ((SeededRandom) random).a(chunkgenerator.getSeed(), i, j, 10387320);
            WorldGenBuriedTreasureConfiguration worldgenburiedtreasureconfiguration = (WorldGenBuriedTreasureConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, this);

            return random.nextFloat() < worldgenburiedtreasureconfiguration.a;
        } else {
            return false;
        }
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenBuriedTreasure.a::new;
    }

    @Override
    public String b() {
        return "Buried_Treasure";
    }

    @Override
    public int c() {
        return 1;
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k + 9, 90, l + 9);

            this.b.add(new WorldGenBuriedTreasurePieces.a(blockposition));
            this.b();
        }

        @Override
        public BlockPosition a() {
            return new BlockPosition((this.f() << 4) + 9, 0, (this.g() << 4) + 9);
        }
    }
}
