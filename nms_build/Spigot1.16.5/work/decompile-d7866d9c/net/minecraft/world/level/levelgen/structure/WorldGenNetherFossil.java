package net.minecraft.world.level.levelgen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;

public class WorldGenNetherFossil {

    private static final MinecraftKey[] a = new MinecraftKey[]{new MinecraftKey("nether_fossils/fossil_1"), new MinecraftKey("nether_fossils/fossil_2"), new MinecraftKey("nether_fossils/fossil_3"), new MinecraftKey("nether_fossils/fossil_4"), new MinecraftKey("nether_fossils/fossil_5"), new MinecraftKey("nether_fossils/fossil_6"), new MinecraftKey("nether_fossils/fossil_7"), new MinecraftKey("nether_fossils/fossil_8"), new MinecraftKey("nether_fossils/fossil_9"), new MinecraftKey("nether_fossils/fossil_10"), new MinecraftKey("nether_fossils/fossil_11"), new MinecraftKey("nether_fossils/fossil_12"), new MinecraftKey("nether_fossils/fossil_13"), new MinecraftKey("nether_fossils/fossil_14")};

    public static void a(DefinedStructureManager definedstructuremanager, List<StructurePiece> list, Random random, BlockPosition blockposition) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);

        list.add(new WorldGenNetherFossil.a(definedstructuremanager, (MinecraftKey) SystemUtils.a((Object[]) WorldGenNetherFossil.a, random), blockposition, enumblockrotation));
    }

    public static class a extends DefinedStructurePiece {

        private final MinecraftKey d;
        private final EnumBlockRotation e;

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
            super(WorldGenFeatureStructurePieceType.ac, 0);
            this.d = minecraftkey;
            this.c = blockposition;
            this.e = enumblockrotation;
            this.a(definedstructuremanager);
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.ac, nbttagcompound);
            this.d = new MinecraftKey(nbttagcompound.getString("Template"));
            this.e = EnumBlockRotation.valueOf(nbttagcompound.getString("Rot"));
            this.a(definedstructuremanager);
        }

        private void a(DefinedStructureManager definedstructuremanager) {
            DefinedStructure definedstructure = definedstructuremanager.a(this.d);
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(this.e).a(EnumBlockMirror.NONE).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.d);

            this.a(definedstructure, this.c, definedstructureinfo);
        }

        @Override
        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setString("Template", this.d.toString());
            nbttagcompound.setString("Rot", this.e.name());
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {}

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            structureboundingbox.c(this.a.b(this.b, this.c));
            return super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }
    }
}
