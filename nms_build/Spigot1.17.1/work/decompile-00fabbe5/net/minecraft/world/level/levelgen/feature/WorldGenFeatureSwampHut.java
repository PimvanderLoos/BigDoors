package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenWitchHut;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureSwampHut extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final WeightedRandomList<BiomeSettingsMobs.c> SWAMPHUT_ENEMIES = WeightedRandomList.a((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.WITCH, 1, 1, 1)));
    private static final WeightedRandomList<BiomeSettingsMobs.c> SWAMPHUT_ANIMALS = WeightedRandomList.a((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.CAT, 1, 1, 1)));

    public WorldGenFeatureSwampHut(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureSwampHut.a::new;
    }

    @Override
    public WeightedRandomList<BiomeSettingsMobs.c> c() {
        return WorldGenFeatureSwampHut.SWAMPHUT_ENEMIES;
    }

    @Override
    public WeightedRandomList<BiomeSettingsMobs.c> h() {
        return WorldGenFeatureSwampHut.SWAMPHUT_ANIMALS;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration, LevelHeightAccessor levelheightaccessor) {
            WorldGenWitchHut worldgenwitchhut = new WorldGenWitchHut(this.random, chunkcoordintpair.d(), chunkcoordintpair.e());

            this.a((StructurePiece) worldgenwitchhut);
        }
    }
}
