package net.minecraft.world.level.levelgen.feature;

import java.util.Locale;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenBuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.WorldGenDesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.WorldGenEndCityPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureRuinedPortalPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenIglooPiece;
import net.minecraft.world.level.levelgen.structure.WorldGenJunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.WorldGenMineshaftPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenMonumentPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenNetherFossil;
import net.minecraft.world.level.levelgen.structure.WorldGenNetherPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenShipwreck;
import net.minecraft.world.level.levelgen.structure.WorldGenStrongholdPieces;
import net.minecraft.world.level.levelgen.structure.WorldGenWitchHut;
import net.minecraft.world.level.levelgen.structure.WorldGenWoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public interface WorldGenFeatureStructurePieceType {

    WorldGenFeatureStructurePieceType MINE_SHAFT_CORRIDOR = setPieceId(WorldGenMineshaftPieces.WorldGenMineshaftCorridor::new, "MSCorridor");
    WorldGenFeatureStructurePieceType MINE_SHAFT_CROSSING = setPieceId(WorldGenMineshaftPieces.WorldGenMineshaftCross::new, "MSCrossing");
    WorldGenFeatureStructurePieceType MINE_SHAFT_ROOM = setPieceId(WorldGenMineshaftPieces.WorldGenMineshaftRoom::new, "MSRoom");
    WorldGenFeatureStructurePieceType MINE_SHAFT_STAIRS = setPieceId(WorldGenMineshaftPieces.WorldGenMineshaftStairs::new, "MSStairs");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece1::new, "NeBCr");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_END_FILLER = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece2::new, "NeBEF");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_STRAIGHT = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece3::new, "NeBS");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece4::new, "NeCCS");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece5::new, "NeCTB");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_ENTRANCE = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece6::new, "NeCE");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece7::new, "NeSCSC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece8::new, "NeSCLT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece9::new, "NeSC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece10::new, "NeSCRT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_STALK_ROOM = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece11::new, "NeCSR");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_MONSTER_THRONE = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece12::new, "NeMT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_ROOM_CROSSING = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece13::new, "NeRC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_STAIRS_ROOM = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece14::new, "NeSR");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_START = setPieceId(WorldGenNetherPieces.WorldGenNetherPiece15::new, "NeStart");
    WorldGenFeatureStructurePieceType STRONGHOLD_CHEST_CORRIDOR = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor::new, "SHCC");
    WorldGenFeatureStructurePieceType STRONGHOLD_FILLER_CORRIDOR = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdCorridor::new, "SHFC");
    WorldGenFeatureStructurePieceType STRONGHOLD_FIVE_CROSSING = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdCrossing::new, "SH5C");
    WorldGenFeatureStructurePieceType STRONGHOLD_LEFT_TURN = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn::new, "SHLT");
    WorldGenFeatureStructurePieceType STRONGHOLD_LIBRARY = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdLibrary::new, "SHLi");
    WorldGenFeatureStructurePieceType STRONGHOLD_PORTAL_ROOM = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom::new, "SHPR");
    WorldGenFeatureStructurePieceType STRONGHOLD_PRISON_HALL = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdPrison::new, "SHPH");
    WorldGenFeatureStructurePieceType STRONGHOLD_RIGHT_TURN = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn::new, "SHRT");
    WorldGenFeatureStructurePieceType STRONGHOLD_ROOM_CROSSING = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing::new, "SHRC");
    WorldGenFeatureStructurePieceType STRONGHOLD_STAIRS_DOWN = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdStairs2::new, "SHSD");
    WorldGenFeatureStructurePieceType STRONGHOLD_START = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdStart::new, "SHStart");
    WorldGenFeatureStructurePieceType STRONGHOLD_STRAIGHT = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdStairs::new, "SHS");
    WorldGenFeatureStructurePieceType STRONGHOLD_STRAIGHT_STAIRS_DOWN = setPieceId(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight::new, "SHSSD");
    WorldGenFeatureStructurePieceType JUNGLE_PYRAMID_PIECE = setPieceId(WorldGenJunglePyramidPiece::new, "TeJP");
    WorldGenFeatureStructurePieceType OCEAN_RUIN = setTemplatePieceId(WorldGenFeatureOceanRuinPieces.a::new, "ORP");
    WorldGenFeatureStructurePieceType IGLOO = setTemplatePieceId(WorldGenIglooPiece.a::new, "Iglu");
    WorldGenFeatureStructurePieceType RUINED_PORTAL = setTemplatePieceId(WorldGenFeatureRuinedPortalPieces::new, "RUPO");
    WorldGenFeatureStructurePieceType SWAMPLAND_HUT = setPieceId(WorldGenWitchHut::new, "TeSH");
    WorldGenFeatureStructurePieceType DESERT_PYRAMID_PIECE = setPieceId(WorldGenDesertPyramidPiece::new, "TeDP");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_BUILDING = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece1::new, "OMB");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_CORE_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece2::new, "OMCR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece3::new, "OMDXR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_XY_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece4::new, "OMDXYR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece5::new, "OMDYR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_YZ_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece6::new, "OMDYZR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece7::new, "OMDZR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPieceEntry::new, "OMEntry");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_PENTHOUSE = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse::new, "OMPenthouse");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPieceSimple::new, "OMSimple");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT::new, "OMSimpleT");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_WING_ROOM = setPieceId(WorldGenMonumentPieces.WorldGenMonumentPiece8::new, "OMWR");
    WorldGenFeatureStructurePieceType END_CITY_PIECE = setTemplatePieceId(WorldGenEndCityPieces.Piece::new, "ECP");
    WorldGenFeatureStructurePieceType WOODLAND_MANSION_PIECE = setTemplatePieceId(WorldGenWoodlandMansionPieces.i::new, "WMP");
    WorldGenFeatureStructurePieceType BURIED_TREASURE_PIECE = setPieceId(WorldGenBuriedTreasurePieces.a::new, "BTP");
    WorldGenFeatureStructurePieceType SHIPWRECK_PIECE = setTemplatePieceId(WorldGenShipwreck.a::new, "Shipwreck");
    WorldGenFeatureStructurePieceType NETHER_FOSSIL = setTemplatePieceId(WorldGenNetherFossil.a::new, "NeFos");
    WorldGenFeatureStructurePieceType JIGSAW = setFullContextPieceId(WorldGenFeaturePillagerOutpostPoolPiece::new, "jigsaw");

    StructurePiece load(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound);

    private static WorldGenFeatureStructurePieceType setFullContextPieceId(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, String s) {
        return (WorldGenFeatureStructurePieceType) IRegistry.register(IRegistry.STRUCTURE_PIECE, s.toLowerCase(Locale.ROOT), worldgenfeaturestructurepiecetype);
    }

    private static WorldGenFeatureStructurePieceType setPieceId(WorldGenFeatureStructurePieceType.a worldgenfeaturestructurepiecetype_a, String s) {
        return setFullContextPieceId(worldgenfeaturestructurepiecetype_a, s);
    }

    private static WorldGenFeatureStructurePieceType setTemplatePieceId(WorldGenFeatureStructurePieceType.b worldgenfeaturestructurepiecetype_b, String s) {
        return setFullContextPieceId(worldgenfeaturestructurepiecetype_b, s);
    }

    public interface a extends WorldGenFeatureStructurePieceType {

        StructurePiece load(NBTTagCompound nbttagcompound);

        @Override
        default StructurePiece load(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            return this.load(nbttagcompound);
        }
    }

    public interface b extends WorldGenFeatureStructurePieceType {

        StructurePiece load(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound);

        @Override
        default StructurePiece load(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            return this.load(structurepieceserializationcontext.structureManager(), nbttagcompound);
        }
    }
}
