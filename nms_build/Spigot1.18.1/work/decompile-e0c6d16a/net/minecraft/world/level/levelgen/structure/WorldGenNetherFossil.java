package net.minecraft.world.level.levelgen.structure;

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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;

public class WorldGenNetherFossil {

    private static final MinecraftKey[] FOSSILS = new MinecraftKey[]{new MinecraftKey("nether_fossils/fossil_1"), new MinecraftKey("nether_fossils/fossil_2"), new MinecraftKey("nether_fossils/fossil_3"), new MinecraftKey("nether_fossils/fossil_4"), new MinecraftKey("nether_fossils/fossil_5"), new MinecraftKey("nether_fossils/fossil_6"), new MinecraftKey("nether_fossils/fossil_7"), new MinecraftKey("nether_fossils/fossil_8"), new MinecraftKey("nether_fossils/fossil_9"), new MinecraftKey("nether_fossils/fossil_10"), new MinecraftKey("nether_fossils/fossil_11"), new MinecraftKey("nether_fossils/fossil_12"), new MinecraftKey("nether_fossils/fossil_13"), new MinecraftKey("nether_fossils/fossil_14")};

    public WorldGenNetherFossil() {}

    public static void addPieces(DefinedStructureManager definedstructuremanager, StructurePieceAccessor structurepieceaccessor, Random random, BlockPosition blockposition) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(random);

        structurepieceaccessor.addPiece(new WorldGenNetherFossil.a(definedstructuremanager, (MinecraftKey) SystemUtils.getRandom((Object[]) WorldGenNetherFossil.FOSSILS, random), blockposition, enumblockrotation));
    }

    public static class a extends DefinedStructurePiece {

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
            super(WorldGenFeatureStructurePieceType.NETHER_FOSSIL, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), makeSettings(enumblockrotation), blockposition);
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FOSSIL, nbttagcompound, definedstructuremanager, (minecraftkey) -> {
                return makeSettings(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
        }

        private static DefinedStructureInfo makeSettings(EnumBlockRotation enumblockrotation) {
            return (new DefinedStructureInfo()).setRotation(enumblockrotation).setMirror(EnumBlockMirror.NONE).addProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putString("Rot", this.placeSettings.getRotation().name());
        }

        @Override
        protected void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {}

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            structureboundingbox.encapsulate(this.template.getBoundingBox(this.placeSettings, this.templatePosition));
            super.postProcess(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }
    }
}
