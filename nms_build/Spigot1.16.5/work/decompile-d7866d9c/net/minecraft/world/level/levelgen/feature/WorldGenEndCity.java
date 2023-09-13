package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenEndCityPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenEndCity extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenEndCity(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean b() {
        return false;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return b(j, k, chunkgenerator) >= 60;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenEndCity.a::new;
    }

    private static int b(int i, int j, ChunkGenerator chunkgenerator) {
        Random random = new Random((long) (i + j * 10387313));
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);
        byte b0 = 5;
        byte b1 = 5;

        if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            b0 = -5;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            b0 = -5;
            b1 = -5;
        } else if (enumblockrotation == EnumBlockRotation.COUNTERCLOCKWISE_90) {
            b1 = -5;
        }

        int k = (i << 4) + 7;
        int l = (j << 4) + 7;
        int i1 = chunkgenerator.c(k, l, HeightMap.Type.WORLD_SURFACE_WG);
        int j1 = chunkgenerator.c(k, l + b1, HeightMap.Type.WORLD_SURFACE_WG);
        int k1 = chunkgenerator.c(k + b0, l, HeightMap.Type.WORLD_SURFACE_WG);
        int l1 = chunkgenerator.c(k + b0, l + b1, HeightMap.Type.WORLD_SURFACE_WG);

        return Math.min(Math.min(i1, j1), Math.min(k1, l1));
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.d);
            int k = WorldGenEndCity.b(i, j, chunkgenerator);

            if (k >= 60) {
                BlockPosition blockposition = new BlockPosition(i * 16 + 8, k, j * 16 + 8);

                WorldGenEndCityPieces.a(definedstructuremanager, blockposition, enumblockrotation, this.b, this.d);
                this.b();
            }
        }
    }
}
