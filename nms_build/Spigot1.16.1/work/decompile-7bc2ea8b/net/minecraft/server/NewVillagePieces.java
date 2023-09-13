package net.minecraft.server;

import java.util.List;

public class NewVillagePieces {

    public static void a() {
        WorldGenFeatureVillagePlain.a();
        WorldGenFeatureVillageSnowy.a();
        WorldGenFeatureVillageSavanna.a();
        WorldGenFeatureDesertVillage.a();
        WorldGenFeatureVillageTaiga.a();
    }

    public static void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, BlockPosition blockposition, List<StructurePiece> list, SeededRandom seededrandom, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration) {
        a();
        WorldGenFeatureDefinedStructureJigsawPlacement.a(worldgenfeaturevillageconfiguration.b, worldgenfeaturevillageconfiguration.c, NewVillagePieces.a::new, chunkgenerator, definedstructuremanager, blockposition, list, seededrandom, true, true);
    }

    public static class a extends WorldGenFeaturePillagerOutpostPoolPiece {

        public a(DefinedStructureManager definedstructuremanager, WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, BlockPosition blockposition, int i, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox) {
            super(WorldGenFeatureStructurePieceType.f, definedstructuremanager, worldgenfeaturedefinedstructurepoolstructure, blockposition, i, enumblockrotation, structureboundingbox);
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(definedstructuremanager, nbttagcompound, WorldGenFeatureStructurePieceType.f);
        }
    }
}
