package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenDesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureDesertPyramid extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureDesertPyramid(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureDesertPyramid.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            WorldGenDesertPyramidPiece worldgendesertpyramidpiece = new WorldGenDesertPyramidPiece(this.random, chunkcoordintpair.d(), chunkcoordintpair.e());

            this.a((StructurePiece) worldgendesertpyramidpiece);
        }
    }
}
