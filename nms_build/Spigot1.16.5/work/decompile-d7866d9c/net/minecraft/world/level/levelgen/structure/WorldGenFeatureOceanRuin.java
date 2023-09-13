package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.INamable;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureOceanRuin extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration> {

    public WorldGenFeatureOceanRuin(Codec<WorldGenFeatureOceanRuinConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureOceanRuinConfiguration> a() {
        return WorldGenFeatureOceanRuin.a::new;
    }

    public static enum Temperature implements INamable {

        WARM("warm"), COLD("cold");

        public static final Codec<WorldGenFeatureOceanRuin.Temperature> c = INamable.a(WorldGenFeatureOceanRuin.Temperature::values, WorldGenFeatureOceanRuin.Temperature::a);
        private static final Map<String, WorldGenFeatureOceanRuin.Temperature> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureOceanRuin.Temperature::b, (worldgenfeatureoceanruin_temperature) -> {
            return worldgenfeatureoceanruin_temperature;
        }));
        private final String e;

        private Temperature(String s) {
            this.e = s;
        }

        public String b() {
            return this.e;
        }

        @Nullable
        public static WorldGenFeatureOceanRuin.Temperature a(String s) {
            return (WorldGenFeatureOceanRuin.Temperature) WorldGenFeatureOceanRuin.Temperature.d.get(s);
        }

        @Override
        public String getName() {
            return this.e;
        }
    }

    public static class a extends StructureStart<WorldGenFeatureOceanRuinConfiguration> {

        public a(StructureGenerator<WorldGenFeatureOceanRuinConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration) {
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k, 90, l);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.a((Random) this.d);

            WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, blockposition, enumblockrotation, this.b, (Random) this.d, worldgenfeatureoceanruinconfiguration);
            this.b();
        }
    }
}
