package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenNetherPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenNether extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final WeightedRandomList<BiomeSettingsMobs.c> FORTRESS_ENEMIES = WeightedRandomList.a((WeightedEntry[])(new BiomeSettingsMobs.c(EntityTypes.BLAZE, 10, 2, 3), new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 5, 4, 4), new BiomeSettingsMobs.c(EntityTypes.WITHER_SKELETON, 8, 5, 5), new BiomeSettingsMobs.c(EntityTypes.SKELETON, 2, 5, 5), new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 3, 4, 4)));

    public WorldGenNether(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
        return seededrandom.nextInt(5) < 2;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenNether.a::new;
    }

    @Override
    public WeightedRandomList<BiomeSettingsMobs.c> c() {
        return WorldGenNether.FORTRESS_ENEMIES;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            WorldGenNetherPieces.WorldGenNetherPiece15 worldgennetherpieces_worldgennetherpiece15 = new WorldGenNetherPieces.WorldGenNetherPiece15(this.random, chunkcoordintpair.a(2), chunkcoordintpair.b(2));

            this.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15);
            worldgennetherpieces_worldgennetherpiece15.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, (StructurePieceAccessor) this, (Random) this.random);
            List list = worldgennetherpieces_worldgennetherpiece15.pendingChildren;

            while (!list.isEmpty()) {
                int i = this.random.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(i);

                structurepiece.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, (StructurePieceAccessor) this, (Random) this.random);
            }

            this.a(this.random, 48, 70);
        }
    }
}
