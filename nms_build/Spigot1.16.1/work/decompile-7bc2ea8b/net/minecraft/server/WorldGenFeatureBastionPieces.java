package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Random;

public class WorldGenFeatureBastionPieces {

    public static final ImmutableMap<String, Integer> a = ImmutableMap.builder().put("bastion/units/base", 60).put("bastion/hoglin_stable/origin", 60).put("bastion/treasure/starters", 60).put("bastion/bridge/start", 60).build();

    public static void a() {
        WorldGenFeatureBastionUnits.a();
        WorldGenFeatureBastionHoglinStable.a();
        WorldGenFeatureBastionTreasure.a();
        WorldGenFeatureBastionBridge.a();
        WorldGenFeatureBastionExtra.a();
    }

    public static void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, BlockPosition blockposition, List<StructurePiece> list, SeededRandom seededrandom, WorldGenFeatureBastionRemnantConfiguration worldgenfeaturebastionremnantconfiguration) {
        a();
        WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration = worldgenfeaturebastionremnantconfiguration.a((Random) seededrandom);

        WorldGenFeatureDefinedStructureJigsawPlacement.a(worldgenfeaturevillageconfiguration.b, worldgenfeaturevillageconfiguration.c, WorldGenFeatureBastionPieces.a::new, chunkgenerator, definedstructuremanager, blockposition, list, seededrandom, false, false);
    }

    public static class a extends WorldGenFeaturePillagerOutpostPoolPiece {

        public a(DefinedStructureManager definedstructuremanager, WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, BlockPosition blockposition, int i, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox) {
            super(WorldGenFeatureStructurePieceType.af, definedstructuremanager, worldgenfeaturedefinedstructurepoolstructure, blockposition, i, enumblockrotation, structureboundingbox);
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(definedstructuremanager, nbttagcompound, WorldGenFeatureStructurePieceType.af);
        }
    }
}
