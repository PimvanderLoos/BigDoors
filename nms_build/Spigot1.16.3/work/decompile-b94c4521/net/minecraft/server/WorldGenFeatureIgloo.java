package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureIgloo extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureIgloo(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureIgloo.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k, 90, l);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.d);

            WorldGenIglooPiece.a(definedstructuremanager, blockposition, enumblockrotation, this.b, this.d);
            this.b();
        }
    }
}
