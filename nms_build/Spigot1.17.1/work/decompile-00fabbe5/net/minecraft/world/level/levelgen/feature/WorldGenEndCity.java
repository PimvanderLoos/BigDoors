package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenEndCityPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenEndCity extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final int RANDOM_SALT = 10387313;

    public WorldGenEndCity(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean b() {
        return false;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
        return a(chunkcoordintpair, chunkgenerator, levelheightaccessor) >= 60;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenEndCity.a::new;
    }

    static int a(ChunkCoordIntPair chunkcoordintpair, ChunkGenerator chunkgenerator, LevelHeightAccessor levelheightaccessor) {
        Random random = new Random((long) (chunkcoordintpair.x + chunkcoordintpair.z * 10387313));
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

        int i = chunkcoordintpair.a(7);
        int j = chunkcoordintpair.b(7);
        int k = chunkgenerator.c(i, j, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
        int l = chunkgenerator.c(i, j + b1, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
        int i1 = chunkgenerator.c(i + b0, j, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
        int j1 = chunkgenerator.c(i + b0, j + b1, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);

        return Math.min(Math.min(k, l), Math.min(i1, j1));
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.random);
            int i = WorldGenEndCity.a(chunkcoordintpair, chunkgenerator, levelheightaccessor);

            if (i >= 60) {
                BlockPosition blockposition = chunkcoordintpair.c(i);
                List<StructurePiece> list = Lists.newArrayList();

                WorldGenEndCityPieces.a(definedstructuremanager, blockposition, enumblockrotation, list, this.random);
                list.forEach(this::a);
            }
        }
    }
}
