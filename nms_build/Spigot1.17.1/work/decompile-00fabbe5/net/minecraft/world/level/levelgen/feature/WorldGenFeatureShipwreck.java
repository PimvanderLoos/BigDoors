package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenShipwreck;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureShipwreck extends StructureGenerator<WorldGenFeatureShipwreckConfiguration> {

    public WorldGenFeatureShipwreck(Codec<WorldGenFeatureShipwreckConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureShipwreckConfiguration> a() {
        return WorldGenFeatureShipwreck.a::new;
    }

    public static class a extends StructureStart<WorldGenFeatureShipwreckConfiguration> {

        public a(StructureGenerator<WorldGenFeatureShipwreckConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration, LevelHeightAccessor levelheightaccessor) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.random);
            BlockPosition blockposition = new BlockPosition(chunkcoordintpair.d(), 90, chunkcoordintpair.e());

            WorldGenShipwreck.a(definedstructuremanager, blockposition, enumblockrotation, this, this.random, worldgenfeatureshipwreckconfiguration);
        }
    }
}
