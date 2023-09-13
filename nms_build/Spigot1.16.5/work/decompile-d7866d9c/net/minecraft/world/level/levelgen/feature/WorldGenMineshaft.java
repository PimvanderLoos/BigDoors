package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.INamable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenMineshaftPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenMineshaft extends StructureGenerator<WorldGenMineshaftConfiguration> {

    public WorldGenMineshaft(Codec<WorldGenMineshaftConfiguration> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenMineshaftConfiguration worldgenmineshaftconfiguration) {
        seededrandom.c(i, j, k);
        double d0 = (double) worldgenmineshaftconfiguration.b;

        return seededrandom.nextDouble() < d0;
    }

    @Override
    public StructureGenerator.a<WorldGenMineshaftConfiguration> a() {
        return WorldGenMineshaft.a::new;
    }

    public static class a extends StructureStart<WorldGenMineshaftConfiguration> {

        public a(StructureGenerator<WorldGenMineshaftConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenMineshaftConfiguration worldgenmineshaftconfiguration) {
            WorldGenMineshaftPieces.WorldGenMineshaftRoom worldgenmineshaftpieces_worldgenmineshaftroom = new WorldGenMineshaftPieces.WorldGenMineshaftRoom(0, this.d, (i << 4) + 2, (j << 4) + 2, worldgenmineshaftconfiguration.c);

            this.b.add(worldgenmineshaftpieces_worldgenmineshaftroom);
            worldgenmineshaftpieces_worldgenmineshaftroom.a((StructurePiece) worldgenmineshaftpieces_worldgenmineshaftroom, this.b, (Random) this.d);
            this.b();
            if (worldgenmineshaftconfiguration.c == WorldGenMineshaft.Type.MESA) {
                boolean flag = true;
                int k = chunkgenerator.getSeaLevel() - this.c.e + this.c.e() / 2 - -5;

                this.c.a(0, k, 0);
                Iterator iterator = this.b.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    structurepiece.a(0, k, 0);
                }
            } else {
                this.a(chunkgenerator.getSeaLevel(), this.d, 10);
            }

        }
    }

    public static enum Type implements INamable {

        NORMAL("normal"), MESA("mesa");

        public static final Codec<WorldGenMineshaft.Type> c = INamable.a(WorldGenMineshaft.Type::values, WorldGenMineshaft.Type::a);
        private static final Map<String, WorldGenMineshaft.Type> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenMineshaft.Type::b, (worldgenmineshaft_type) -> {
            return worldgenmineshaft_type;
        }));
        private final String e;

        private Type(String s) {
            this.e = s;
        }

        public String b() {
            return this.e;
        }

        private static WorldGenMineshaft.Type a(String s) {
            return (WorldGenMineshaft.Type) WorldGenMineshaft.Type.d.get(s);
        }

        public static WorldGenMineshaft.Type a(int i) {
            return i >= 0 && i < values().length ? values()[i] : WorldGenMineshaft.Type.NORMAL;
        }

        @Override
        public String getName() {
            return this.e;
        }
    }
}
