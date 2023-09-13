package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenWitchHut;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureSwampHut extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeSettingsMobs.c> u = ImmutableList.of(new BiomeSettingsMobs.c(EntityTypes.WITCH, 1, 1, 1));
    private static final List<BiomeSettingsMobs.c> v = ImmutableList.of(new BiomeSettingsMobs.c(EntityTypes.CAT, 1, 1, 1));

    public WorldGenFeatureSwampHut(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenFeatureSwampHut.a::new;
    }

    @Override
    public List<BiomeSettingsMobs.c> c() {
        return WorldGenFeatureSwampHut.u;
    }

    @Override
    public List<BiomeSettingsMobs.c> j() {
        return WorldGenFeatureSwampHut.v;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            WorldGenWitchHut worldgenwitchhut = new WorldGenWitchHut(this.d, i * 16, j * 16);

            this.b.add(worldgenwitchhut);
            this.b();
        }
    }
}
