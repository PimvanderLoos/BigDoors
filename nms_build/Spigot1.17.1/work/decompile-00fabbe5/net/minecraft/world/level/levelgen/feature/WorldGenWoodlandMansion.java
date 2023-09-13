package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenWoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenWoodlandMansion extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenWoodlandMansion(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean b() {
        return false;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
        Set<BiomeBase> set = worldchunkmanager.a(chunkcoordintpair.a(9), chunkgenerator.getSeaLevel(), chunkcoordintpair.b(9), 32);
        Iterator iterator = set.iterator();

        BiomeBase biomebase1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            biomebase1 = (BiomeBase) iterator.next();
        } while (biomebase1.e().a((StructureGenerator) this));

        return false;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenWoodlandMansion.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.random);
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
            int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));

            if (k1 >= 60) {
                BlockPosition blockposition = new BlockPosition(chunkcoordintpair.a(8), k1 + 1, chunkcoordintpair.b(8));
                List<WorldGenWoodlandMansionPieces.i> list = Lists.newLinkedList();

                WorldGenWoodlandMansionPieces.a(definedstructuremanager, blockposition, enumblockrotation, list, this.random);
                list.forEach(this::a);
            }
        }

        @Override
        public void a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair);
            StructureBoundingBox structureboundingbox1 = this.c();
            int i = structureboundingbox1.h();

            for (int j = structureboundingbox.g(); j <= structureboundingbox.j(); ++j) {
                for (int k = structureboundingbox.i(); k <= structureboundingbox.l(); ++k) {
                    BlockPosition blockposition = new BlockPosition(j, i, k);

                    if (!generatoraccessseed.isEmpty(blockposition) && structureboundingbox1.b((BaseBlockPosition) blockposition) && this.a(blockposition)) {
                        for (int l = i - 1; l > 1; --l) {
                            BlockPosition blockposition1 = new BlockPosition(j, l, k);

                            if (!generatoraccessseed.isEmpty(blockposition1) && !generatoraccessseed.getType(blockposition1).getMaterial().isLiquid()) {
                                break;
                            }

                            generatoraccessseed.setTypeAndData(blockposition1, Blocks.COBBLESTONE.getBlockData(), 2);
                        }
                    }
                }
            }

        }
    }
}
