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
import net.minecraft.world.level.levelgen.structure.StructurePiece;
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

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        Set<BiomeBase> set = worldchunkmanager.a(j * 16 + 9, chunkgenerator.getSeaLevel(), k * 16 + 9, 32);
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

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.d);
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
            int i2 = Math.min(Math.min(i1, j1), Math.min(k1, l1));

            if (i2 >= 60) {
                BlockPosition blockposition = new BlockPosition(i * 16 + 8, i2 + 1, j * 16 + 8);
                List<WorldGenWoodlandMansionPieces.i> list = Lists.newLinkedList();

                WorldGenWoodlandMansionPieces.a(definedstructuremanager, blockposition, enumblockrotation, list, this.d);
                this.b.addAll(list);
                this.b();
            }
        }

        @Override
        public void a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair);
            int i = this.c.b;

            for (int j = structureboundingbox.a; j <= structureboundingbox.d; ++j) {
                for (int k = structureboundingbox.c; k <= structureboundingbox.f; ++k) {
                    BlockPosition blockposition = new BlockPosition(j, i, k);

                    if (!generatoraccessseed.isEmpty(blockposition) && this.c.b((BaseBlockPosition) blockposition)) {
                        boolean flag = false;
                        Iterator iterator = this.b.iterator();

                        while (iterator.hasNext()) {
                            StructurePiece structurepiece = (StructurePiece) iterator.next();

                            if (structurepiece.g().b((BaseBlockPosition) blockposition)) {
                                flag = true;
                                break;
                            }
                        }

                        if (flag) {
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
}
