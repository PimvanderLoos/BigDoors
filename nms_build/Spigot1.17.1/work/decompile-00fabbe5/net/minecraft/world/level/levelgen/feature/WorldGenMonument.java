package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenMonumentPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenMonument extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final WeightedRandomList<BiomeSettingsMobs.c> MONUMENT_ENEMIES = WeightedRandomList.a((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.GUARDIAN, 1, 2, 4)));

    public WorldGenMonument(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean b() {
        return false;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
        int j = chunkcoordintpair.a(9);
        int k = chunkcoordintpair.b(9);
        Set<BiomeBase> set = worldchunkmanager.a(j, chunkgenerator.getSeaLevel(), k, 16);
        Iterator iterator = set.iterator();

        BiomeBase biomebase1;

        do {
            if (!iterator.hasNext()) {
                Set<BiomeBase> set1 = worldchunkmanager.a(j, chunkgenerator.getSeaLevel(), k, 29);
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
    public WeightedRandomList<BiomeSettingsMobs.c> c() {
        return WorldGenMonument.MONUMENT_ENEMIES;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        private boolean isCreated;

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            this.a(chunkcoordintpair);
        }

        private void a(ChunkCoordIntPair chunkcoordintpair) {
            int i = chunkcoordintpair.d() - 29;
            int j = chunkcoordintpair.e() - 29;
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(this.random);

            this.a((StructurePiece) (new WorldGenMonumentPieces.WorldGenMonumentPiece1(this.random, i, j, enumdirection)));
            this.isCreated = true;
        }

        @Override
        public void a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (!this.isCreated) {
                this.pieces.clear();
                this.a(this.f());
            }

            super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair);
        }
    }
}
