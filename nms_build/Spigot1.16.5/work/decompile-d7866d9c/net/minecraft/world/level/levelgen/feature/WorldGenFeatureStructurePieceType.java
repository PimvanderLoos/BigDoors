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
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public interface WorldGenFeatureStructurePieceType {

    WorldGenFeatureStructurePieceType a = a(WorldGenMineshaftPieces.WorldGenMineshaftCorridor::new, "MSCorridor");
    WorldGenFeatureStructurePieceType b = a(WorldGenMineshaftPieces.WorldGenMineshaftCross::new, "MSCrossing");
    WorldGenFeatureStructurePieceType c = a(WorldGenMineshaftPieces.WorldGenMineshaftRoom::new, "MSRoom");
    WorldGenFeatureStructurePieceType d = a(WorldGenMineshaftPieces.WorldGenMineshaftStairs::new, "MSStairs");
    WorldGenFeatureStructurePieceType e = a(WorldGenNetherPieces.WorldGenNetherPiece1::new, "NeBCr");
    WorldGenFeatureStructurePieceType f = a(WorldGenNetherPieces.WorldGenNetherPiece2::new, "NeBEF");
    WorldGenFeatureStructurePieceType g = a(WorldGenNetherPieces.WorldGenNetherPiece3::new, "NeBS");
    WorldGenFeatureStructurePieceType h = a(WorldGenNetherPieces.WorldGenNetherPiece4::new, "NeCCS");
    WorldGenFeatureStructurePieceType i = a(WorldGenNetherPieces.WorldGenNetherPiece5::new, "NeCTB");
    WorldGenFeatureStructurePieceType j = a(WorldGenNetherPieces.WorldGenNetherPiece6::new, "NeCE");
    WorldGenFeatureStructurePieceType k = a(WorldGenNetherPieces.WorldGenNetherPiece7::new, "NeSCSC");
    WorldGenFeatureStructurePieceType l = a(WorldGenNetherPieces.WorldGenNetherPiece8::new, "NeSCLT");
    WorldGenFeatureStructurePieceType m = a(WorldGenNetherPieces.WorldGenNetherPiece9::new, "NeSC");
    WorldGenFeatureStructurePieceType n = a(WorldGenNetherPieces.WorldGenNetherPiece10::new, "NeSCRT");
    WorldGenFeatureStructurePieceType o = a(WorldGenNetherPieces.WorldGenNetherPiece11::new, "NeCSR");
    WorldGenFeatureStructurePieceType p = a(WorldGenNetherPieces.WorldGenNetherPiece12::new, "NeMT");
    WorldGenFeatureStructurePieceType q = a(WorldGenNetherPieces.WorldGenNetherPiece13::new, "NeRC");
    WorldGenFeatureStructurePieceType r = a(WorldGenNetherPieces.WorldGenNetherPiece14::new, "NeSR");
    WorldGenFeatureStructurePieceType s = a(WorldGenNetherPieces.WorldGenNetherPiece15::new, "NeStart");
    WorldGenFeatureStructurePieceType t = a(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor::new, "SHCC");
    WorldGenFeatureStructurePieceType u = a(WorldGenStrongholdPieces.WorldGenStrongholdCorridor::new, "SHFC");
    WorldGenFeatureStructurePieceType v = a(WorldGenStrongholdPieces.WorldGenStrongholdCrossing::new, "SH5C");
    WorldGenFeatureStructurePieceType w = a(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn::new, "SHLT");
    WorldGenFeatureStructurePieceType x = a(WorldGenStrongholdPieces.WorldGenStrongholdLibrary::new, "SHLi");
    WorldGenFeatureStructurePieceType y = a(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom::new, "SHPR");
    WorldGenFeatureStructurePieceType z = a(WorldGenStrongholdPieces.WorldGenStrongholdPrison::new, "SHPH");
    WorldGenFeatureStructurePieceType A = a(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn::new, "SHRT");
    WorldGenFeatureStructurePieceType B = a(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing::new, "SHRC");
    WorldGenFeatureStructurePieceType C = a(WorldGenStrongholdPieces.WorldGenStrongholdStairs2::new, "SHSD");
    WorldGenFeatureStructurePieceType D = a(WorldGenStrongholdPieces.WorldGenStrongholdStart::new, "SHStart");
    WorldGenFeatureStructurePieceType E = a(WorldGenStrongholdPieces.WorldGenStrongholdStairs::new, "SHS");
    WorldGenFeatureStructurePieceType F = a(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight::new, "SHSSD");
    WorldGenFeatureStructurePieceType G = a(WorldGenJunglePyramidPiece::new, "TeJP");
    WorldGenFeatureStructurePieceType H = a(WorldGenFeatureOceanRuinPieces.a::new, "ORP");
    WorldGenFeatureStructurePieceType I = a(WorldGenIglooPiece.a::new, "Iglu");
    WorldGenFeatureStructurePieceType J = a(WorldGenFeatureRuinedPortalPieces::new, "RUPO");
    WorldGenFeatureStructurePieceType K = a(WorldGenWitchHut::new, "TeSH");
    WorldGenFeatureStructurePieceType L = a(WorldGenDesertPyramidPiece::new, "TeDP");
    WorldGenFeatureStructurePieceType M = a(WorldGenMonumentPieces.WorldGenMonumentPiece1::new, "OMB");
    WorldGenFeatureStructurePieceType N = a(WorldGenMonumentPieces.WorldGenMonumentPiece2::new, "OMCR");
    WorldGenFeatureStructurePieceType O = a(WorldGenMonumentPieces.WorldGenMonumentPiece3::new, "OMDXR");
    WorldGenFeatureStructurePieceType P = a(WorldGenMonumentPieces.WorldGenMonumentPiece4::new, "OMDXYR");
    WorldGenFeatureStructurePieceType Q = a(WorldGenMonumentPieces.WorldGenMonumentPiece5::new, "OMDYR");
    WorldGenFeatureStructurePieceType R = a(WorldGenMonumentPieces.WorldGenMonumentPiece6::new, "OMDYZR");
    WorldGenFeatureStructurePieceType S = a(WorldGenMonumentPieces.WorldGenMonumentPiece7::new, "OMDZR");
    WorldGenFeatureStructurePieceType T = a(WorldGenMonumentPieces.WorldGenMonumentPieceEntry::new, "OMEntry");
    WorldGenFeatureStructurePieceType U = a(WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse::new, "OMPenthouse");
    WorldGenFeatureStructurePieceType V = a(WorldGenMonumentPieces.WorldGenMonumentPieceSimple::new, "OMSimple");
    WorldGenFeatureStructurePieceType W = a(WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT::new, "OMSimpleT");
    WorldGenFeatureStructurePieceType X = a(WorldGenMonumentPieces.WorldGenMonumentPiece8::new, "OMWR");
    WorldGenFeatureStructurePieceType Y = a(WorldGenEndCityPieces.Piece::new, "ECP");
    WorldGenFeatureStructurePieceType Z = a(WorldGenWoodlandMansionPieces.i::new, "WMP");
    WorldGenFeatureStructurePieceType aa = a(WorldGenBuriedTreasurePieces.a::new, "BTP");
    WorldGenFeatureStructurePieceType ab = a(WorldGenShipwreck.a::new, "Shipwreck");
    WorldGenFeatureStructurePieceType ac = a(WorldGenNetherFossil.a::new, "NeFos");
    WorldGenFeatureStructurePieceType ad = a(WorldGenFeaturePillagerOutpostPoolPiece::new, "jigsaw");

    StructurePiece load(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound);

    static WorldGenFeatureStructurePieceType a(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, String s) {
        return (WorldGenFeatureStructurePieceType) IRegistry.a(IRegistry.STRUCTURE_PIECE, s.toLowerCase(Locale.ROOT), (Object) worldgenfeaturestructurepiecetype);
    }
}
