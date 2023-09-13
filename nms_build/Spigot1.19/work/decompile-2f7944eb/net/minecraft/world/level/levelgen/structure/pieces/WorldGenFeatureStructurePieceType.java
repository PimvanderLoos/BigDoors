package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Locale;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import net.minecraft.world.level.levelgen.structure.structures.JungleTemplePiece;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilPieces;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public interface WorldGenFeatureStructurePieceType {

    WorldGenFeatureStructurePieceType MINE_SHAFT_CORRIDOR = setPieceId(MineshaftPieces.a::new, "MSCorridor");
    WorldGenFeatureStructurePieceType MINE_SHAFT_CROSSING = setPieceId(MineshaftPieces.b::new, "MSCrossing");
    WorldGenFeatureStructurePieceType MINE_SHAFT_ROOM = setPieceId(MineshaftPieces.d::new, "MSRoom");
    WorldGenFeatureStructurePieceType MINE_SHAFT_STAIRS = setPieceId(MineshaftPieces.e::new, "MSStairs");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = setPieceId(NetherFortressPieces.a::new, "NeBCr");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_END_FILLER = setPieceId(NetherFortressPieces.b::new, "NeBEF");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_BRIDGE_STRAIGHT = setPieceId(NetherFortressPieces.c::new, "NeBS");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS = setPieceId(NetherFortressPieces.d::new, "NeCCS");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY = setPieceId(NetherFortressPieces.e::new, "NeCTB");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_ENTRANCE = setPieceId(NetherFortressPieces.f::new, "NeCE");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING = setPieceId(NetherFortressPieces.g::new, "NeSCSC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN = setPieceId(NetherFortressPieces.h::new, "NeSCLT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR = setPieceId(NetherFortressPieces.i::new, "NeSC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = setPieceId(NetherFortressPieces.j::new, "NeSCRT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_CASTLE_STALK_ROOM = setPieceId(NetherFortressPieces.k::new, "NeCSR");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_MONSTER_THRONE = setPieceId(NetherFortressPieces.l::new, "NeMT");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_ROOM_CROSSING = setPieceId(NetherFortressPieces.o::new, "NeRC");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_STAIRS_ROOM = setPieceId(NetherFortressPieces.p::new, "NeSR");
    WorldGenFeatureStructurePieceType NETHER_FORTRESS_START = setPieceId(NetherFortressPieces.q::new, "NeStart");
    WorldGenFeatureStructurePieceType STRONGHOLD_CHEST_CORRIDOR = setPieceId(StrongholdPieces.a::new, "SHCC");
    WorldGenFeatureStructurePieceType STRONGHOLD_FILLER_CORRIDOR = setPieceId(StrongholdPieces.b::new, "SHFC");
    WorldGenFeatureStructurePieceType STRONGHOLD_FIVE_CROSSING = setPieceId(StrongholdPieces.c::new, "SH5C");
    WorldGenFeatureStructurePieceType STRONGHOLD_LEFT_TURN = setPieceId(StrongholdPieces.d::new, "SHLT");
    WorldGenFeatureStructurePieceType STRONGHOLD_LIBRARY = setPieceId(StrongholdPieces.e::new, "SHLi");
    WorldGenFeatureStructurePieceType STRONGHOLD_PORTAL_ROOM = setPieceId(StrongholdPieces.g::new, "SHPR");
    WorldGenFeatureStructurePieceType STRONGHOLD_PRISON_HALL = setPieceId(StrongholdPieces.h::new, "SHPH");
    WorldGenFeatureStructurePieceType STRONGHOLD_RIGHT_TURN = setPieceId(StrongholdPieces.i::new, "SHRT");
    WorldGenFeatureStructurePieceType STRONGHOLD_ROOM_CROSSING = setPieceId(StrongholdPieces.j::new, "SHRC");
    WorldGenFeatureStructurePieceType STRONGHOLD_STAIRS_DOWN = setPieceId(StrongholdPieces.l::new, "SHSD");
    WorldGenFeatureStructurePieceType STRONGHOLD_START = setPieceId(StrongholdPieces.m::new, "SHStart");
    WorldGenFeatureStructurePieceType STRONGHOLD_STRAIGHT = setPieceId(StrongholdPieces.n::new, "SHS");
    WorldGenFeatureStructurePieceType STRONGHOLD_STRAIGHT_STAIRS_DOWN = setPieceId(StrongholdPieces.o::new, "SHSSD");
    WorldGenFeatureStructurePieceType JUNGLE_PYRAMID_PIECE = setPieceId(JungleTemplePiece::new, "TeJP");
    WorldGenFeatureStructurePieceType OCEAN_RUIN = setTemplatePieceId(OceanRuinPieces.a::new, "ORP");
    WorldGenFeatureStructurePieceType IGLOO = setTemplatePieceId(IglooPieces.a::new, "Iglu");
    WorldGenFeatureStructurePieceType RUINED_PORTAL = setTemplatePieceId(RuinedPortalPiece::new, "RUPO");
    WorldGenFeatureStructurePieceType SWAMPLAND_HUT = setPieceId(SwampHutPiece::new, "TeSH");
    WorldGenFeatureStructurePieceType DESERT_PYRAMID_PIECE = setPieceId(DesertPyramidPiece::new, "TeDP");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_BUILDING = setPieceId(OceanMonumentPieces.h::new, "OMB");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_CORE_ROOM = setPieceId(OceanMonumentPieces.j::new, "OMCR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = setPieceId(OceanMonumentPieces.k::new, "OMDXR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_XY_ROOM = setPieceId(OceanMonumentPieces.l::new, "OMDXYR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = setPieceId(OceanMonumentPieces.m::new, "OMDYR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_YZ_ROOM = setPieceId(OceanMonumentPieces.n::new, "OMDYZR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = setPieceId(OceanMonumentPieces.o::new, "OMDZR");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = setPieceId(OceanMonumentPieces.p::new, "OMEntry");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_PENTHOUSE = setPieceId(OceanMonumentPieces.q::new, "OMPenthouse");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = setPieceId(OceanMonumentPieces.s::new, "OMSimple");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = setPieceId(OceanMonumentPieces.t::new, "OMSimpleT");
    WorldGenFeatureStructurePieceType OCEAN_MONUMENT_WING_ROOM = setPieceId(OceanMonumentPieces.u::new, "OMWR");
    WorldGenFeatureStructurePieceType END_CITY_PIECE = setTemplatePieceId(EndCityPieces.a::new, "ECP");
    WorldGenFeatureStructurePieceType WOODLAND_MANSION_PIECE = setTemplatePieceId(WoodlandMansionPieces.i::new, "WMP");
    WorldGenFeatureStructurePieceType BURIED_TREASURE_PIECE = setPieceId(BuriedTreasurePieces.a::new, "BTP");
    WorldGenFeatureStructurePieceType SHIPWRECK_PIECE = setTemplatePieceId(ShipwreckPieces.a::new, "Shipwreck");
    WorldGenFeatureStructurePieceType NETHER_FOSSIL = setTemplatePieceId(NetherFossilPieces.a::new, "NeFos");
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

        StructurePiece load(StructureTemplateManager structuretemplatemanager, NBTTagCompound nbttagcompound);

        @Override
        default StructurePiece load(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            return this.load(structurepieceserializationcontext.structureTemplateManager(), nbttagcompound);
        }
    }
}
