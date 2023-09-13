package net.minecraft.world.level.levelgen.feature;

import java.util.Locale;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
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

public interface WorldGenFeatureStructurePieceType {

    WorldGenFeatureStructurePieceType MINE_SHAFT_CORRIDOR = a(WorldGenMineshaftPieces.WorldGenMineshaftCorridor::new, "MSCorridor");
    WorldGenFeatureStructurePieceType MINE_SHAFT_CROSSING = a(WorldGenMineshaftPieces.WorldGenMineshaftCross::new, "MSCrossing");
    WorldGenFeatureStructurePieceType MINE_SHAFT_ROOM = a(WorldGenMineshaftPieces.WorldGenMineshaftRoom::new, "MSRoom");
    WorldGenFeatureStructurePieceType MINE_SHAFT_STAIRS = a(WorldGenMineshaftPieces.WorldGenMineshaftStairs::new, "MSStairs");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = a(WorldGenNetherPieces.WorldGenNetherPiece1::new, "NeBCr");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_END_FILLER = a(WorldGenNetherPieces.WorldGenNetherPiece2::new, "NeBEF");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_STRAIGHT = a(WorldGenNetherPieces.WorldGenNetherPiece3::new, "NeBS");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS = a(WorldGenNetherPieces.WorldGenNetherPiece4::new, "NeCCS");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY = a(WorldGenNetherPieces.WorldGenNetherPiece5::new, "NeCTB");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_ENTRANCE = a(WorldGenNetherPieces.WorldGenNetherPiece6::new, "NeCE");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING = a(WorldGenNetherPieces.WorldGenNetherPiece7::new, "NeSCSC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN = a(WorldGenNetherPieces.WorldGenNetherPiece8::new, "NeSCLT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR = a(WorldGenNetherPieces.WorldGenNetherPiece9::new, "NeSC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = a(WorldGenNetherPieces.WorldGenNetherPiece10::new, "NeSCRT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_STALK_ROOM = a(WorldGenNetherPieces.WorldGenNetherPiece11::new, "NeCSR");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_MONSTER_THRONE = a(WorldGenNetherPieces.WorldGenNetherPiece12::new, "NeMT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_ROOM_CROSSING = a(WorldGenNetherPieces.WorldGenNetherPiece13::new, "NeRC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_STAIRS_ROOM = a(WorldGenNetherPieces.WorldGenNetherPiece14::new, "NeSR");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_START = a(WorldGenNetherPieces.WorldGenNetherPiece15::new, "NeStart");
    WorldGenFeatureStructurePieceType STRONGHOLD_CHEST_CORRIDOR = a(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor::new, "SHCC");
    WorldGenFeatureStructurePieceType STRONGHOLD_FILLER_CORRIDOR = a(WorldGenStrongholdPieces.WorldGenStrongholdCorridor::new, "SHFC");
    WorldGenFeatureStructurePieceType STRONGHOLD_FIVE_CROSSING = a(WorldGenStrongholdPieces.WorldGenStrongholdCrossing::new, "SH5C");
    WorldGenFeatureStructurePieceType STRONGHOLD_LEFT_TURN = a(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn::new, "SHLT");
    WorldGenFeatureStructurePieceType STRONGHOLD_LIBRARY = a(WorldGenStrongholdPieces.WorldGenStrongholdLibrary::new, "SHLi");
    WorldGenFeatureStructurePieceType STRONGHOLD_PORTAL_ROOM = a(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom::new, "SHPR");
    WorldGenFeatureStructurePieceType STRONGHOLD_PRISON_HALL = a(WorldGenStrongholdPieces.WorldGenStrongholdPrison::new, "SHPH");
    WorldGenFeatureStructurePieceType STRONGHOLD_RIGHT_TURN = a(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn::new, "SHRT");
    WorldGenFeatureStructurePieceType STRONGHOLD_ROOM_CROSSING = a(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing::new, "SHRC");
    WorldGenFeatureStructurePieceType STRONGHOLD_STAIRS_DOWN = a(WorldGenStrongholdPieces.WorldGenStrongholdStairs2::new, "SHSD");
    WorldGenFeatureStructurePieceType STRONGHOLD_START = a(WorldGenStrongholdPieces.WorldGenStrongholdStart::new, "SHStart");
    WorldGenFeatureStructurePieceType STRONGHOLD_STRAIGHT = a(WorldGenStrongholdPieces.WorldGenStrongholdStairs::new, "SHS");
    WorldGenFeatureStructurePieceType STRONGHOLD_STRAIGHT_STAIRS_DOWN = a(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight::new, "SHSSD");
    WorldGenFeatureStructurePieceType JUNGLE_PYRAMID_PIECE = a(WorldGenJunglePyramidPiece::new, "TeJP");
    WorldGenFeatureStructurePieceType OCEAN_RUIN = a(WorldGenFeatureOceanRuinPieces.a::new, "ORP");
    WorldGenFeatureStructurePieceType IGLOO = a(WorldGenIglooPiece.a::new, "Iglu");
    WorldGenFeatureStructurePieceType RUINED_PORTAL = a(WorldGenFeatureRuinedPortalPieces::new, "RUPO");
    WorldGenFeatureStructurePieceType SWAMPLAND_HUT = a(WorldGenWitchHut::new, "TeSH");
    WorldGenFeatureStructurePieceType DESERT_PYRAMID_PIECE = a(WorldGenDesertPyramidPiece::new, "TeDP");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_BUILDING = a(WorldGenMonumentPieces.WorldGenMonumentPiece1::new, "OMB");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_CORE_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece2::new, "OMCR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece3::new, "OMDXR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_XY_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece4::new, "OMDXYR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece5::new, "OMDYR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_YZ_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece6::new, "OMDYZR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece7::new, "OMDZR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPieceEntry::new, "OMEntry");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_PENTHOUSE = a(WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse::new, "OMPenthouse");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPieceSimple::new, "OMSimple");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT::new, "OMSimpleT");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_WING_ROOM = a(WorldGenMonumentPieces.WorldGenMonumentPiece8::new, "OMWR");
    WorldGenFeatureStructurePieceType END_CITY_PIECE = a(WorldGenEndCityPieces.Piece::new, "ECP");
    WorldGenFeatureStructurePieceType WOODLAND_MANSION_PIECE = a(WorldGenWoodlandMansionPieces.i::new, "WMP");
    WorldGenFeatureStructurePieceType BURIED_TREASURE_PIECE = a(WorldGenBuriedTreasurePieces.a::new, "BTP");
    WorldGenFeatureStructurePieceType SHIPWRECK_PIECE = a(WorldGenShipwreck.a::new, "Shipwreck");
    WorldGenFeatureStructurePieceType NETHER_FOSSIL = a(WorldGenNetherFossil.a::new, "NeFos");
    WorldGenFeatureStructurePieceType JIGSAW = a(WorldGenFeaturePillagerOutpostPoolPiece::new, "jigsaw");

    StructurePiece load(WorldServer worldserver, NBTTagCompound nbttagcompound);

    static WorldGenFeatureStructurePieceType a(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, String s) {
        return (WorldGenFeatureStructurePieceType) IRegistry.a(IRegistry.STRUCTURE_PIECE, s.toLowerCase(Locale.ROOT), (Object) worldgenfeaturestructurepiecetype);
    }
}
