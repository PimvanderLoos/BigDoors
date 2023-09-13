package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenMonumentPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenMonument extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeSettingsMobs.c> u = ImmutableList.of(new BiomeSettingsMobs.c(EntityTypes.GUARDIAN, 1, 2, 4));

    public WorldGenMonument(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean b() {
        return false;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        Set<BiomeBase> set = worldchunkmanager.a(j * 16 + 9, chunkgenerator.getSeaLevel(), k * 16 + 9, 16);
        Iterator iterator = set.iterator();

        BiomeBase biomebase1;

        do {
            if (!iterator.hasNext()) {
                Set<BiomeBase> set1 = worldchunkmanager.a(j * 16 + 9, chunkgenerator.getSeaLevel(), k * 16 + 9, 29);
                Iterator iterator1 = set1.iterator();

                BiomeBase biomebase2;

                do {
                    if (!iterator1.hasNext()) {
                        return true;
                    }

                    biomebase2 = (BiomeBase) iterator1.next();
                } while (biomebase2.t() == BiomeBase.Geography.OCEAN || biomebase2.t() == BiomeBase.Geography.RIVER);

                return false;
            }

            biomebase1 = (BiomeBase) iterator.next();
        } while (biomebase1.e().a((StructureGenerator) this));

        return false;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenMonument.a::new;
    }

    @Override
    public List<BiomeSettingsMobs.c> c() {
        return WorldGenMonument.u;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        private boolean e;

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            this.b(i, j);
        }

        private void b(int i, int j) {
            int k = i * 16 - 29;
            int l = j * 16 - 29;
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(this.d);

            this.b.add(new WorldGenMonumentPieces.WorldGenMonumentPiece1(this.d, k, l, enumdirection));
            this.b();
            this.e = true;
        }

        @Override
        public void a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (!this.e) {
                this.b.clear();
                this.b(this.f(), this.g());
            }

            super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair);
        }
    }
}
