package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructureJigsawPlacement;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureJigsaw extends StructureGenerator<WorldGenFeatureVillageConfiguration> {

    final int startY;
    final boolean doExpansionHack;
    final boolean projectStartToHeightmap;

    public WorldGenFeatureJigsaw(Codec<WorldGenFeatureVillageConfiguration> codec, int i, boolean flag, boolean flag1) {
        super(codec);
        this.startY = i;
        this.doExpansionHack = flag;
        this.projectStartToHeightmap = flag1;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureVillageConfiguration> a() {
        return (structuregenerator, chunkcoordintpair, i, j) -> {
            return new WorldGenFeatureJigsaw.a(this, chunkcoordintpair, i, j);
        };
    }

    public static class a extends NoiseAffectingStructureStart<WorldGenFeatureVillageConfiguration> {

        private final WorldGenFeatureJigsaw feature;

        public a(WorldGenFeatureJigsaw worldgenfeaturejigsaw, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(worldgenfeaturejigsaw, chunkcoordintpair, i, j);
            this.feature = worldgenfeaturejigsaw;
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration, LevelHeightAccessor levelheightaccessor) {
            BlockPosition blockposition = new BlockPosition(chunkcoordintpair.d(), this.feature.startY, chunkcoordintpair.e());

            WorldGenFeaturePieces.a();
            WorldGenFeatureDefinedStructureJigsawPlacement.a(iregistrycustom, worldgenfeaturevillageconfiguration, WorldGenFeaturePillagerOutpostPoolPiece::new, chunkgenerator, definedstructuremanager, blockposition, this, this.random, this.feature.doExpansionHack, this.feature.projectStartToHeightmap, levelheightaccessor);
        }
    }
}
