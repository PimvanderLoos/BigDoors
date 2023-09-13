package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureStart;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.WorldGenStrongholdPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenStronghold extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenStronghold(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenStronghold.a::new;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
        return chunkgenerator.a(chunkcoordintpair);
    }

    public static class a extends NoiseAffectingStructureStart<WorldGenFeatureEmptyConfiguration> {

        private final long seed;

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
            this.seed = j;
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            int i = 0;

            WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart;

            do {
                this.l();
                this.random.c(this.seed + (long) (i++), chunkcoordintpair.x, chunkcoordintpair.z);
                WorldGenStrongholdPieces.a();
                worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(this.random, chunkcoordintpair.a(2), chunkcoordintpair.b(2));
                this.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart);
                worldgenstrongholdpieces_worldgenstrongholdstart.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, (StructurePieceAccessor) this, (Random) this.random);
                List list = worldgenstrongholdpieces_worldgenstrongholdstart.pendingChildren;

                while (!list.isEmpty()) {
                    int j = this.random.nextInt(list.size());
                    StructurePiece structurepiece = (StructurePiece) list.remove(j);

                    structurepiece.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, (StructurePieceAccessor) this, (Random) this.random);
                }

                this.a(chunkgenerator.getSeaLevel(), chunkgenerator.getMinY(), this.random, 10);
            } while (this.m() || worldgenstrongholdpieces_worldgenstrongholdstart.portalRoomPiece == null);

        }
    }
}
