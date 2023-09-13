package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.INamable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenMineshaftPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenMineshaft extends StructureGenerator<WorldGenMineshaftConfiguration> {

    public WorldGenMineshaft(Codec<WorldGenMineshaftConfiguration> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenMineshaftConfiguration worldgenmineshaftconfiguration, LevelHeightAccessor levelheightaccessor) {
        seededrandom.c(i, chunkcoordintpair.x, chunkcoordintpair.z);
        double d0 = (double) worldgenmineshaftconfiguration.probability;

        return seededrandom.nextDouble() < d0;
    }

    @Override
    public StructureGenerator.a<WorldGenMineshaftConfiguration> a() {
        return WorldGenMineshaft.a::new;
    }

    public static class a extends StructureStart<WorldGenMineshaftConfiguration> {

        public a(StructureGenerator<WorldGenMineshaftConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenMineshaftConfiguration worldgenmineshaftconfiguration, LevelHeightAccessor levelheightaccessor) {
            WorldGenMineshaftPieces.WorldGenMineshaftRoom worldgenmineshaftpieces_worldgenmineshaftroom = new WorldGenMineshaftPieces.WorldGenMineshaftRoom(0, this.random, chunkcoordintpair.a(2), chunkcoordintpair.b(2), worldgenmineshaftconfiguration.type);

            this.a((StructurePiece) worldgenmineshaftpieces_worldgenmineshaftroom);
            worldgenmineshaftpieces_worldgenmineshaftroom.a((StructurePiece) worldgenmineshaftpieces_worldgenmineshaftroom, (StructurePieceAccessor) this, (Random) this.random);
            if (worldgenmineshaftconfiguration.type == WorldGenMineshaft.Type.MESA) {
                boolean flag = true;
                StructureBoundingBox structureboundingbox = this.c();
                int i = chunkgenerator.getSeaLevel() - structureboundingbox.k() + structureboundingbox.d() / 2 - -5;

                this.a(i);
            } else {
                this.a(chunkgenerator.getSeaLevel(), chunkgenerator.getMinY(), this.random, 10);
            }

        }
    }

    public static enum Type implements INamable {

        NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE), MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

        public static final Codec<WorldGenMineshaft.Type> CODEC = INamable.a(WorldGenMineshaft.Type::values, WorldGenMineshaft.Type::a);
        private static final Map<String, WorldGenMineshaft.Type> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenMineshaft.Type::a, (worldgenmineshaft_type) -> {
            return worldgenmineshaft_type;
        }));
        private final String name;
        private final IBlockData woodState;
        private final IBlockData planksState;
        private final IBlockData fenceState;

        private Type(String s, Block block, Block block1, Block block2) {
            this.name = s;
            this.woodState = block.getBlockData();
            this.planksState = block1.getBlockData();
            this.fenceState = block2.getBlockData();
        }

        public String a() {
            return this.name;
        }

        private static WorldGenMineshaft.Type a(String s) {
            return (WorldGenMineshaft.Type) WorldGenMineshaft.Type.BY_NAME.get(s);
        }

        public static WorldGenMineshaft.Type a(int i) {
            return i >= 0 && i < values().length ? values()[i] : WorldGenMineshaft.Type.NORMAL;
        }

        public IBlockData b() {
            return this.woodState;
        }

        public IBlockData d() {
            return this.planksState;
        }

        public IBlockData e() {
            return this.fenceState;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
