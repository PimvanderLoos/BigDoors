package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;

public class WorldGenNetherFossil {

    private static final MinecraftKey[] FOSSILS = new MinecraftKey[]{new MinecraftKey("nether_fossils/fossil_1"), new MinecraftKey("nether_fossils/fossil_2"), new MinecraftKey("nether_fossils/fossil_3"), new MinecraftKey("nether_fossils/fossil_4"), new MinecraftKey("nether_fossils/fossil_5"), new MinecraftKey("nether_fossils/fossil_6"), new MinecraftKey("nether_fossils/fossil_7"), new MinecraftKey("nether_fossils/fossil_8"), new MinecraftKey("nether_fossils/fossil_9"), new MinecraftKey("nether_fossils/fossil_10"), new MinecraftKey("nether_fossils/fossil_11"), new MinecraftKey("nether_fossils/fossil_12"), new MinecraftKey("nether_fossils/fossil_13"), new MinecraftKey("nether_fossils/fossil_14")};

    public WorldGenNetherFossil() {}

    public static void a(DefinedStructureManager definedstructuremanager, StructurePieceAccessor structurepieceaccessor, Random random, BlockPosition blockposition) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);

        structurepieceaccessor.a((StructurePiece) (new WorldGenNetherFossil.a(definedstructuremanager, (MinecraftKey) SystemUtils.a((Object[]) WorldGenNetherFossil.FOSSILS, random), blockposition, enumblockrotation)));
    }

    public static class a extends DefinedStructurePiece {

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
            super(WorldGenFeatureStructurePieceType.NETHER_FOSSIL, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), a(enumblockrotation), blockposition);
        }

        public a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FOSSIL, nbttagcompound, worldserver, (minecraftkey) -> {
                return a(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
        }

        private static DefinedStructureInfo a(EnumBlockRotation enumblockrotation) {
            return (new DefinedStructureInfo()).a(enumblockrotation).a(EnumBlockMirror.NONE).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setString("Rot", this.placeSettings.d().name());
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {}

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            structureboundingbox.b(this.template.b(this.placeSettings, this.templatePosition));
            return super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }
    }
}
