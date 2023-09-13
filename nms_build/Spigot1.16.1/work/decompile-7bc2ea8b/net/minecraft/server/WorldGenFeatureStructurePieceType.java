package net.minecraft.server;

import java.util.Locale;

public interface WorldGenFeatureStructurePieceType {

    WorldGenFeatureStructurePieceType a = a(WorldGenMineshaftPieces.WorldGenMineshaftCorridor::new, "MSCorridor");
    WorldGenFeatureStructurePieceType b = a(WorldGenMineshaftPieces.WorldGenMineshaftCross::new, "MSCrossing");
    WorldGenFeatureStructurePieceType c = a(WorldGenMineshaftPieces.WorldGenMineshaftRoom::new, "MSRoom");
    WorldGenFeatureStructurePieceType d = a(WorldGenMineshaftPieces.WorldGenMineshaftStairs::new, "MSStairs");
    WorldGenFeatureStructurePieceType e = a(WorldGenFeaturePillagerOutpostPieces.a::new, "PCP");
    WorldGenFeatureStructurePieceType f = a(NewVillagePieces.a::new, "NVi");
    WorldGenFeatureStructurePieceType g = a(WorldGenNetherPieces.WorldGenNetherPiece1::new, "NeBCr");
    WorldGenFeatureStructurePieceType h = a(WorldGenNetherPieces.WorldGenNetherPiece2::new, "NeBEF");
    WorldGenFeatureStructurePieceType i = a(WorldGenNetherPieces.WorldGenNetherPiece3::new, "NeBS");
    WorldGenFeatureStructurePieceType j = a(WorldGenNetherPieces.WorldGenNetherPiece4::new, "NeCCS");
    WorldGenFeatureStructurePieceType k = a(WorldGenNetherPieces.WorldGenNetherPiece5::new, "NeCTB");
    WorldGenFeatureStructurePieceType l = a(WorldGenNetherPieces.WorldGenNetherPiece6::new, "NeCE");
    WorldGenFeatureStructurePieceType m = a(WorldGenNetherPieces.WorldGenNetherPiece7::new, "NeSCSC");
    WorldGenFeatureStructurePieceType n = a(WorldGenNetherPieces.WorldGenNetherPiece8::new, "NeSCLT");
    WorldGenFeatureStructurePieceType o = a(WorldGenNetherPieces.WorldGenNetherPiece9::new, "NeSC");
    WorldGenFeatureStructurePieceType p = a(WorldGenNetherPieces.WorldGenNetherPiece10::new, "NeSCRT");
    WorldGenFeatureStructurePieceType q = a(WorldGenNetherPieces.WorldGenNetherPiece11::new, "NeCSR");
    WorldGenFeatureStructurePieceType r = a(WorldGenNetherPieces.WorldGenNetherPiece12::new, "NeMT");
    WorldGenFeatureStructurePieceType s = a(WorldGenNetherPieces.WorldGenNetherPiece13::new, "NeRC");
    WorldGenFeatureStructurePieceType t = a(WorldGenNetherPieces.WorldGenNetherPiece14::new, "NeSR");
    WorldGenFeatureStructurePieceType u = a(WorldGenNetherPieces.WorldGenNetherPiece15::new, "NeStart");
    WorldGenFeatureStructurePieceType v = a(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor::new, "SHCC");
    WorldGenFeatureStructurePieceType w = a(WorldGenStrongholdPieces.WorldGenStrongholdCorridor::new, "SHFC");
    WorldGenFeatureStructurePieceType x = a(WorldGenStrongholdPieces.WorldGenStrongholdCrossing::new, "SH5C");
    WorldGenFeatureStructurePieceType y = a(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn::new, "SHLT");
    WorldGenFeatureStructurePieceType z = a(WorldGenStrongholdPieces.WorldGenStrongholdLibrary::new, "SHLi");
    WorldGenFeatureStructurePieceType A = a(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom::new, "SHPR");
    WorldGenFeatureStructurePieceType B = a(WorldGenStrongholdPieces.WorldGenStrongholdPrison::new, "SHPH");
    WorldGenFeatureStructurePieceType C = a(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn::new, "SHRT");
    WorldGenFeatureStructurePieceType D = a(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing::new, "SHRC");
    WorldGenFeatureStructurePieceType E = a(WorldGenStrongholdPieces.WorldGenStrongholdStairs2::new, "SHSD");
    WorldGenFeatureStructurePieceType F = a(WorldGenStrongholdPieces.WorldGenStrongholdStart::new, "SHStart");
    WorldGenFeatureStructurePieceType G = a(WorldGenStrongholdPieces.WorldGenStrongholdStairs::new, "SHS");
    WorldGenFeatureStructurePieceType H = a(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight::new, "SHSSD");
    WorldGenFeatureStructurePieceType I = a(WorldGenJunglePyramidPiece::new, "TeJP");
    WorldGenFeatureStructurePieceType J = a(WorldGenFeatureOceanRuinPieces.a::new, "ORP");
    WorldGenFeatureStructurePieceType K = a(WorldGenIglooPiece.a::new, "Iglu");
    WorldGenFeatureStructurePieceType L = a(WorldGenFeatureRuinedPortalPieces::new, "RUPO");
    WorldGenFeatureStructurePieceType M = a(WorldGenWitchHut::new, "TeSH");
    WorldGenFeatureStructurePieceType N = a(WorldGenDesertPyramidPiece::new, "TeDP");
    WorldGenFeatureStructurePieceType O = a(WorldGenMonumentPieces.WorldGenMonumentPiece1::new, "OMB");
    WorldGenFeatureStructurePieceType P = a(WorldGenMonumentPieces.WorldGenMonumentPiece2::new, "OMCR");
    WorldGenFeatureStructurePieceType Q = a(WorldGenMonumentPieces.WorldGenMonumentPiece3::new, "OMDXR");
    WorldGenFeatureStructurePieceType R = a(WorldGenMonumentPieces.WorldGenMonumentPiece4::new, "OMDXYR");
    WorldGenFeatureStructurePieceType S = a(WorldGenMonumentPieces.WorldGenMonumentPiece5::new, "OMDYR");
    WorldGenFeatureStructurePieceType T = a(WorldGenMonumentPieces.WorldGenMonumentPiece6::new, "OMDYZR");
    WorldGenFeatureStructurePieceType U = a(WorldGenMonumentPieces.WorldGenMonumentPiece7::new, "OMDZR");
    WorldGenFeatureStructurePieceType V = a(WorldGenMonumentPieces.WorldGenMonumentPieceEntry::new, "OMEntry");
    WorldGenFeatureStructurePieceType W = a(WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse::new, "OMPenthouse");
    WorldGenFeatureStructurePieceType X = a(WorldGenMonumentPieces.WorldGenMonumentPieceSimple::new, "OMSimple");
    WorldGenFeatureStructurePieceType Y = a(WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT::new, "OMSimpleT");
    WorldGenFeatureStructurePieceType Z = a(WorldGenMonumentPieces.WorldGenMonumentPiece8::new, "OMWR");
    WorldGenFeatureStructurePieceType aa = a(WorldGenEndCityPieces.Piece::new, "ECP");
    WorldGenFeatureStructurePieceType ab = a(WorldGenWoodlandMansionPieces.i::new, "WMP");
    WorldGenFeatureStructurePieceType ac = a(WorldGenBuriedTreasurePieces.a::new, "BTP");
    WorldGenFeatureStructurePieceType ad = a(WorldGenShipwreck.a::new, "Shipwreck");
    WorldGenFeatureStructurePieceType ae = a(WorldGenNetherFossil.a::new, "NeFos");
    WorldGenFeatureStructurePieceType af = a(WorldGenFeatureBastionPieces.a::new, "BastionRemnant");
    WorldGenFeatureStructurePieceType ag = a(TileEntityJigsaw.b::new, "Runtime");

    StructurePiece load(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound);

    static WorldGenFeatureStructurePieceType a(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, String s) {
        return (WorldGenFeatureStructurePieceType) IRegistry.a(IRegistry.STRUCTURE_PIECE, s.toLowerCase(Locale.ROOT), (Object) worldgenfeaturestructurepiecetype);
    }
}
