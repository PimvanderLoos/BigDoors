package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
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

        public a(StructureGenerator<WorldGenFeatureShipwreckConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.d);
            BlockPosition blockposition = new BlockPosition(i * 16, 90, j * 16);

            WorldGenShipwreck.a(definedstructuremanager, blockposition, enumblockrotation, this.b, this.d, worldgenfeatureshipwreckconfiguration);
            this.b();
        }
    }
}
